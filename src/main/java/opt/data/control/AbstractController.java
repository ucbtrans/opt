package opt.data.control;

import java.util.*;

public abstract class AbstractController {

	public enum Type { RampMetering , LgPolicy}

	protected long id;
	protected Type type;
	protected Float dt;
	protected control.AbstractController.Algorithm algorithm;

	abstract public Collection<jaxb.Parameter> jaxb_parameters();
	abstract public Set<Sensor> get_sensors();

	////////////////////////////////
	// construction
	////////////////////////////////

	public AbstractController(Type type, Float dt, control.AbstractController.Algorithm algorithm) {
		this.type = type;
		this.dt = dt;
		this.algorithm = algorithm;
	}

	////////////////////////////////
	// public final
	////////////////////////////////

	public final String getName(){
		return ControlFactory.cntrl_alg_name.AtoB(algorithm);
	}
        
        public final Type getType() {
            return type;
        }

	public final control.AbstractController.Algorithm getAlgorithm(){
		return algorithm;
	}

	public final static boolean is_ramp_metering(control.AbstractController.Algorithm a){
		return a==control.AbstractController.Algorithm.rm_alinea ||
				a==control.AbstractController.Algorithm.rm_closed ||
				a==control.AbstractController.Algorithm.rm_open ||
				a==control.AbstractController.Algorithm.rm_fixed_rate;
	}

	public final static boolean is_lg_restrict(control.AbstractController.Algorithm a){
		return a==control.AbstractController.Algorithm.lg_restrict;
	}

	////////////////////////////////
	// API
	////////////////////////////////

	public long getId() {
		return id;
	}

	public Float getDt() {
		return dt;
	}

	public void setDt(Float dt) {
		this.dt = dt;
	}

//	public Map<Long,Sensor> get_sensors(){
//		return sensors;
//	}

	public void setId(long id){
		this.id = id;
	}

}
