package opt;

import api.OTM;
import javafx.application.Platform;
import javafx.concurrent.Task;
import opt.data.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OTMTask  extends Task {

	private AppMainController mainController;
	private FreewayScenario fwyscenario;
	private OTM otmdev;

	private boolean celloutput;
	private boolean lgoutput;

	private int simsteps;
	private float outdt;
	private int step_per_progbar_update;

	private Exception exception;
	private SimDataScenario simdata;

	public OTMTask(AppMainController mainController, FreewayScenario fwyscenario, float outdt, int progbar_steps, boolean celloutput, boolean lgoutput, Benchmarker logger) throws Exception {

		this.mainController = mainController;
		this.fwyscenario = fwyscenario;
		this.outdt = outdt;
		this.celloutput = celloutput;
		this.lgoutput = lgoutput;

		assert(outdt==300f);

		if(!celloutput && !lgoutput)
			throw new Exception("No data requested");

		// bind the progress bar and make it visible
		if(mainController!=null)
			mainController.bindProgressBar(progressProperty());

		try {

			// add ghost pieces
			fwyscenario.add_ghost_pieces();

			final float nominal_duration = fwyscenario.get_sim_duration();
			final float nominal_simdt = fwyscenario.get_sim_dt_sec();

			if(nominal_simdt<1f)
				throw new Exception("Simulation dt too small. This is likely caused by very short segments.");
			if(nominal_simdt>300f)
				throw new Exception("Simulation dt too large.");

			List<Float> factors_300 = List.of(1f, 2f, 3f, 4f, 5f, 6f, 9f, 10f, 12f, 15f, 20f, 25f, 30f, 50f, 60f, 75f, 100f, 150f, 300f);

			float simdt = nominal_simdt;
			if(!factors_300.contains(simdt))
				simdt = factors_300.stream().filter(x->x>nominal_simdt).findFirst().get();

			fwyscenario.set_sim_dt_sec(simdt);
			this.simsteps = (int) Math.ceil(nominal_duration/simdt);

			this.step_per_progbar_update = Math.max( simsteps / progbar_steps , 1);

			// check outdt is multiple of simdt
			if(outdt%simdt > 0.01)
				throw new Exception("Reporting time step should be a multiple of simulation time step.");

			jaxb.Scenario jscenario = fwyscenario.get_scenario().to_jaxb();
			if(logger!=null)
				logger.write("to_jaxb");

			this.otmdev = new api.OTM();
			otmdev.load_from_jaxb(jscenario,false);
			if(logger!=null)
				logger.write("otm_load");

		} catch (Exception e) {
			fwyscenario.remove_ghost_pieces();
			throw new Exception(e);
		}
	}

	@Override
	protected Object call()  {
		this.run_simulation(null);
		return null;
	}

	@Override
	protected void done() {
		super.done();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// unbind progress bar and make it invisible.
				if (mainController!=null) {
					mainController.unbindProgressBar();
					mainController.completeSimulation();
				}
			}
		});
	}

	public SimDataScenario run_simulation(Benchmarker logger){

		simdata = null;
		exception = null;
		boolean is_canceled = false;

		try {

			float sim_dt = fwyscenario.get_sim_dt_sec();

			Set<Long> linkids = fwyscenario.get_links().stream()
					.map(x->x.id).collect(Collectors.toSet());

			if(celloutput) {
				for(Long commid : fwyscenario.get_commodities().keySet()){
					otmdev.output.request_cell_flw(null,null,commid, linkids, outdt);
					otmdev.output.request_cell_sum_veh(null,null,commid, linkids, outdt);
					otmdev.output.request_cell_sum_veh_dwn(null,null,commid, linkids, outdt);
				}
			}

			if(lgoutput) {
				for(Long commid : fwyscenario.get_commodities().keySet()) {
					otmdev.output.request_lanegroup_flw(null,null,commid, linkids, outdt);
					otmdev.output.request_lanegroup_sum_veh(null,null,commid, linkids, outdt);
				}
			}

			if(logger!=null)
				logger.write("requests");

			otmdev.initialize(fwyscenario.get_start_time());

			if(logger!=null)
				logger.write("initialize");

			otmdev.advance(fwyscenario.get_start_time());

			int steps_taken = 0;
			while(steps_taken<simsteps){

				if (isCancelled()) {
					is_canceled = true;
					break;
				}

				// advance otm, get back information
				otmdev.advance(sim_dt);
				steps_taken += 1;

				// progress bar
				if(mainController!=null && steps_taken%step_per_progbar_update==0){
					final int ii = steps_taken;
					Platform.runLater(new Runnable() {
						@Override public void run() {
							updateProgress(ii, simsteps);
						}
					});
				}
			}

			otmdev.terminate();

			if(logger!=null) {
				if(is_canceled)
					logger.write("run canceled");
				else
					logger.write("run");
			}

			simdata = is_canceled ? null : new SimDataScenario(fwyscenario,otmdev,outdt,celloutput,lgoutput);

			if(logger!=null)
				logger.write("output");

		} catch (Exception e) {
			this.exception = e;
			failed();
		} finally {
			fwyscenario.remove_ghost_pieces();
			if(mainController!=null)
				Platform.runLater(() -> { mainController.attachSimDataToScenario(simdata,exception); } );
		}

		return simdata;
	}

}
