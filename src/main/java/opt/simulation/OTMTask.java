package opt.simulation;

import api.OTMdev;
import error.OTMException;
import javafx.concurrent.Task;

public class OTMTask  extends Task {

	private OTMdev otm;
	private OTMException exception;
	private final float start_time = 0f;
	private float duration;
	private long sim_delay;
	private float refresh_seconds;
//	private AbstractColormap colormap;
//	private MenuController menuController;
//	private GraphPaneController graphPaneController;
//	private StatusBarController statusBarController;

//	public OTMTask(OTMdev otm, GlobalParameters params, MenuController menuController,GraphPaneController graphPaneController, StatusBarController statusBarController){
//		this.otm = otm;
//		this.start_time = params.start_time.floatValue();
//		this.duration = params.duration.floatValue();
//		this.sim_dt = params.sim_dt.floatValue();
//		this.sim_delay = params.sim_delay.longValue();
//		this.colormap = params.get_colormap();
//		this.menuController = menuController;
//		this.graphPaneController = graphPaneController;
//		this.statusBarController = statusBarController;
//	}

	public OTMTask(OTMdev otm,float duration,long sim_delay,float refresh_seconds){
		this.otm = otm;
		this.duration = duration;
		this.sim_delay = sim_delay;
		this.refresh_seconds = refresh_seconds;
	}

	@Override
	protected Object call()  {
		try {

//			menuController.disableRun();
//			menuController.enablePause();
//			menuController.enableRewind();
//			menuController.disableParameters();
//			menuController.disablePlots();

			float start_time = 0f;
			otm.otm.initialize(start_time);
			final int steps = (int) (duration / refresh_seconds);
			for (int i=1; i<=steps; i++) {

				if (isCancelled())
					break;

				// delay simulation for better visualization
				Thread.sleep(sim_delay);

				// advance otm, get back information
				otm.otm.advance(refresh_seconds);

				// TODO : PUT THIS BACK
//				final AnimationInfo info = otm.otm.get_animation_info();
//				final int ii = i;
//                Platform.runLater(new Runnable() {
//                    @Override public void run() {
//                        graphPaneController.draw_link_state(info,colormap);
//                        updateProgress(ii, steps);
//                        updateMessage(String.format("%.0f",info.timestamp));
//                    }
//                });

			}

		} catch (OTMException e) {
			this.exception = e;
			failed();
		} catch (InterruptedException e) {
			this.exception = new OTMException(e);
			failed();
		} finally {
//			statusBarController.unbind_progress();
//			statusBarController.unbind_text();
//			menuController.disablePause();
//			menuController.enableRewind();
//			menuController.enableParameters();
//			menuController.enablePlots();
		}
		return null;
	}

	@Override
	protected void failed() {
		super.failed();
		System.err.println(exception);
	}
}
