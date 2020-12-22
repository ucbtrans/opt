package opt.tests;

import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class AbstractTest {

    protected static String output_folder = "temp/";
    protected static HashMap<String,String> test_configs;

    static {
        test_configs = new HashMap<>();
        test_configs.put("demand","demand.opt");
        test_configs.put("on_ramp_at_cap","on_ramp_at_cap.opt");
        test_configs.put("on_ramp_above_cap","on_ramp_above_cap.opt");
        test_configs.put("on_ramp_AUX","on_ramp_AUX.opt");
        test_configs.put("on_off_ramp_AUX","on_off_ramp_AUX.opt");
        test_configs.put("off_ramp_AUX","off_ramp_AUX.opt");
        test_configs.put("off_ramp_AUX_split_ratio","off_ramp_AUX_split_ratio.opt");
        test_configs.put("on_off_ramp_AUX_split_ratio","on_off_ramp_AUX_split_ratio.opt");
        test_configs.put("multiple_off_ramps_split","multiple_off_ramps_split.opt");
        test_configs.put("multiple_off_ramps_hov_split","multiple_off_ramps_hov_split.opt");
        test_configs.put("inner_outer_off_ramp","inner_outer_off_ramp.opt");
        test_configs.put("on_ramp_fixed_meter","on_ramp_fixed_meter.opt");
        test_configs.put("on_ramp_fixed_meter_queue_override","on_ramp_fixed_meter_queue_override.opt");
        test_configs.put("on_ramp_alinea_meter","on_ramp_alinea_meter.opt");
    }

    static protected String get_test_fullpath(String testname){
        return (new File("src/test/resources/test_configs/" + test_configs.get(testname))).getAbsolutePath();
    }

    public static Collection<String> get_test_config_names() {
        return test_configs.keySet();
    }

    @Parameterized.Parameters
    public static Collection getConfigs(){
        ArrayList<String []> x = new ArrayList<>();
        Collection<String> all_configs = get_test_config_names();
        for(String s : all_configs)
            x.add(new String[]{s});
        return x;
    }

}
