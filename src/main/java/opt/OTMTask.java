package opt;

import api.OTMdev;
import error.OTMException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import opt.data.AbstractLink;
import opt.data.FreewayScenario;
import opt.data.ProjectFactory;
import opt.data.SimDataScenario;
import output.AbstractOutput;
import output.OutputCellFlow;
import output.OutputCellVehicles;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class OTMTask  extends Task {

	private AppMainController mainController;
	private FreewayScenario fwyscenario;
	private OTMdev otmdev;

	private int simsteps;
	private float outdt;
	private int step_per_progbar_update;

	public OTMTask(AppMainController mainController, FreewayScenario fwyscenario,float start_time, float duration, int outsteps, int progbar_steps) throws Exception {

		this.mainController = mainController;
		this.fwyscenario = fwyscenario;

		fwyscenario.set_start_time(start_time);
		fwyscenario.set_sim_duration(duration);
		fwyscenario.set_sim_dt_sec(2f);

		// number of time steps in the simulation
		float simdt = fwyscenario.get_sim_dt_sec();
		this.simsteps = (int) Math.ceil(duration/simdt);
		this.outdt = duration/outsteps;
		this.step_per_progbar_update = Math.max( simsteps / progbar_steps , 1);

		// check outdt is multiple of simdt
		if(outdt%simdt > 0.01)
			throw new Exception("Reporting time step should be a multiple of simulation time step.");

		// bind the progress bar and make it visible
		if(mainController!=null)
			mainController.bindProgressBar(progressProperty());

		// create a runnable OTM scenario
		fwyscenario.add_ghost_pieces();

		try {
			jaxb.Scenario jscenario = fwyscenario.get_scenario().to_jaxb();

			api.OTM otm = new api.OTM();
			otm.load_from_jaxb(jscenario,false);
			this.otmdev = new OTMdev(otm);
		} catch (Exception e) {
			e.printStackTrace();
			fwyscenario.remove_ghost_pieces();
		}
	}

	@Override
	protected Object call()  {
		this.run_simulation();
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

	public SimDataScenario run_simulation(){

		SimDataScenario simdata;

		try {

			Set<Long> linkids = fwyscenario.get_links().stream()
					.filter(link->link.get_type()!= AbstractLink.Type.ghost)
					.map(x->x.id).collect(Collectors.toSet());
			for(Long commid : otmdev.scenario.commodities.keySet()){
				otmdev.otm.output().request_cell_flw(commid,linkids,outdt);
				otmdev.otm.output().request_cell_veh(commid,linkids,outdt);
			}

			otmdev.otm.initialize(fwyscenario.get_start_time());

			int steps_taken = 0;
			while(steps_taken<simsteps){

				if (isCancelled())
					break;

				// advance otm, get back information
				otmdev.otm.advance(fwyscenario.get_sim_dt_sec());
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

		} catch (Exception e) {
			failed();
		} finally {
			simdata = new SimDataScenario(fwyscenario,otmdev);
			fwyscenario.remove_ghost_pieces();
			if(mainController!=null)
				mainController.attachSimDataToScenario(simdata);
		}

		return simdata;
	}

}
