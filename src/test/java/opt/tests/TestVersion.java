package opt.tests;

import opt.utils.Version;
import org.junit.Test;

public class TestVersion {

    @Test
    public void test_get_version(){
        System.out.println("otm-sim: " + Version.getOTMSimGitHash());
        System.out.println("opt: " + Version.getOPTGitHash());
    }

}
