package opt.tests;

import opt.data.*;
import opt.data.control.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestControl extends AbstractTest{

	/////////////////////////////////////
	// create controllers
	/////////////////////////////////////

	@Test
	public void test_create_controller_alinea(){
		try {
			TestData X = new TestData("project2_rm.opt");
			ControllerRampMeterAlinea cntrl = ControlFactory.create_controller_alinea(X.scenario,
					null,
					3f,
					false,
					100f,
					900f,
					null,
					9l,
					100);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_fixedrate(){
		try {
			TestData X = new TestData("project2_rm.opt");
			ControllerRampMeterFixedRate cntrl = ControlFactory.create_controller_fixed_rate(X.scenario,
					null,
					3f,
					false,
					100f,
					900f,
					500f);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
//	public void test_create_controller_hov(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			ControllerPolicyHOV cntrl = ControlFactory.create_controller_hov(X.scenario,
//					null,
//					3f);
//			assertNotNull(cntrl);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@Test
	public void test_create_controller_hot(){
		try {
			TestData X = new TestData("project2_rm.opt");
			ControllerPolicyHOT cntrl = ControlFactory.create_controller_hot(X.scenario,
					null,
					3f);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////
	// delete controllers
	/////////////////////////////////////

//	@Test
//	public void test_add_controllers(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			Schedule schedule = X.scenario.get_controller_schedule();
//
//			schedule.add_item(ControlFactory.create_controller_alinea(X.scenario,
//					null,
//					3f,
//					false,
//					100f,
//					900f,
//					null,
//					9l,
//					100f,
//					null,
//					13l,
//					LaneGroupType.gp));
//
//			schedule.add_item(ControlFactory.create_controller_tod(X.scenario,
//					null,
//					3f,
//					false,
//					100f,
//					900f,
//					null,
//					13l,
//					LaneGroupType.gp));
//
//			assertEquals(4,schedule.items.size());
//
//			ProjectFactory.save_project(X.project,get_test_fullpath("project_saved.opt"));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

//	@Test
//	public void test_delete_add_controller(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			Schedule schedule = X.scenario.get_controller_schedule();
//			AbstractController c =  schedule.items.get(0);
//
//			assertEquals(2,schedule.items.size());
//			schedule.delete_controller(c);
//
//			assertEquals(1,schedule.items.size());
//			ControllerRampMeterAlinea cntrl = ControlFactory.create_controller_alinea(X.scenario,
//					null,
//					3f,
//					false,
//					100f,
//					900f,
//					null,
//					9l,
//					100f,
//					null,
//					8l,
//					LaneGroupType.gp);
//			schedule.add_item(cntrl);
//
//			assertEquals(2,schedule.items.size());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/////////////////////////////////////
	// create a schedule
	/////////////////////////////////////

//	@Test
//	public void test_schedule_get(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			Schedule schedule = X.scenario.get_controller_schedule();
//			System.out.println(schedule);
//			assertNotNull(schedule);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

	/////////////////////////////////////
	// save with controllers
	/////////////////////////////////////

//	@Test
//	public void test_save_controller(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			Schedule schedule = X.scenario.get_controller_schedule();
//
//			// save
//			String filename = get_test_fullpath("project_saved.opt");
//			ProjectFactory.save_project(X.project,filename);
//			Project project_saved = ProjectFactory.load_project(filename,true);
//
//			// test
////			assertTrue(X.project.equals(project_saved));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
