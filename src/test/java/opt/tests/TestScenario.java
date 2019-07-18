package opt.tests;

import opt.data.Commodity;
import opt.data.FreewayScenario;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TestScenario extends AbstractTest {

    @Ignore
    @Test
    public void test_deep_copy_scenario(){

    }

    @Ignore
    @Test
    public void test_get_road_params(){

    }

    @Test
    public void test_commodities(){
        try {
            TestData X = new TestData();
            FreewayScenario scenario = X.project.get_scenario_with_name("scenarioA");

            long comm_id = scenario.get_commodities().keySet().iterator().next();
            System.out.println(scenario.get_commodity_by_id(comm_id));

            Commodity new_comm = scenario.create_commodity("new commodity");
            System.out.println(new_comm);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
