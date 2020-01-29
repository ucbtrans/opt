package opt.simulation;

import api.OTMdev;
import error.OTMException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import opt.AppMainController;
import opt.data.FreewayScenario;

public class OTMTask  extends Task {

	private OTMdev otmdev;
	private Exception exception;

	private float start_time;
	private float duration;
	private int numsteps;
	private int step_per_update;
	private AppMainController mainController;
	private float simdt;


	public OTMTask(AppMainController mainController, FreewayScenario fwyscenario,float start_time, float duration, int progbar_steps){

		this.mainController = mainController;
		this.start_time = start_time;
		this.duration = duration;

		// bind the progress bar and make it visible
		mainController.bindSimProgress(progressProperty());

		// create a runnable OTM scenario
		try {
			jaxb.Scenario jscenario = fwyscenario.get_scenario().to_jaxb();
			api.OTM otm = new api.OTM();
			otm.load_from_jaxb(jscenario,true);
			this.otmdev = new OTMdev(otm);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// number of time steps in the simulation
		this.simdt = 2f;
		this.numsteps = (int) Math.ceil(duration/simdt);

		// number of steps per progrbar update
		this.step_per_update = Math.max( numsteps / progbar_steps , 1);

	}

	@Override
	protected Object call()  {
		this.run_simulation();
		return null;
	}

	@Override
	protected void done() {
		super.done();

		// unbind progress bar and make it invisible.
		mainController.unbindSimProgress();
	}

	public void run_simulation(){

		try {

			otmdev.otm.initialize(start_time);

			int steps_taken = 0;

			while(steps_taken<numsteps){

				if (isCancelled())
					break;

//				Thread.sleep(sim_delay);

				// advance otm, get back information
				int steps_to_take = Math.min( step_per_update , numsteps-steps_taken );


				otmdev.otm.advance(steps_to_take*simdt);
				steps_taken += steps_to_take;

				// retrieve animation info
				//final AnimationInfo info = otmdev.otm.scenario().get_animation_info();

				// ui manipulations
				final int ii = steps_taken;
				Platform.runLater(new Runnable() {
                    @Override public void run() {
                        updateProgress(ii, numsteps);
                    }
                });

			}

		} catch (OTMException e) {
			this.exception = e;
			failed();
		}

	}

}