package opt.data.control;

import error.OTMException;
import jaxb.Controller;
import opt.data.Scenario;
import opt.data.control.AbstractController;

public class ControllerPolicyHOT extends AbstractController {

	public ControllerPolicyHOT(Controller j, Scenario scn) throws OTMException {
		super(j, scn);
	}
}
