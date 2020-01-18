package opt.tests;

import error.OTMException;
import opt.data.Schedule;
import opt.data.control.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestControl extends AbstractTest{
	float dt = 3f;
	float start_time = 0f;
	Float end_time = 34f;

	/////////////////////////////////////
	// create controllers
	/////////////////////////////////////8

	@Test
	public void test_create_controller_alinea(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Set<Long> link_ids = new HashSet<>();
			link_ids.add(8l);
			ControllerRampMeterAlinea cntrl = ControlFactory.create_controller_alinea(dt,start_time,end_time, link_ids,X.scenario);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_tod(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Set<Long> link_ids = new HashSet<>();
			link_ids.add(8l);
			ControllerRampMeterTOD cntrl = ControlFactory.create_controller_tod(dt,start_time,end_time, link_ids,X.scenario);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_hov(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Set<Long> link_ids = new HashSet<>();
			link_ids.add(2l);
			link_ids.add(3l);
			link_ids.add(4l);
			ControllerPolicyHOV cntrl = ControlFactory.create_controller_hov(dt,start_time,end_time, link_ids,X.scenario);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_hot(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Set<Long> link_ids = new HashSet<>();
			link_ids.add(2l);
			link_ids.add(3l);
			link_ids.add(4l);
			ControllerPolicyHOT cntrl = ControlFactory.create_controller_hot(dt,start_time,end_time, link_ids,X.scenario);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////
	// create a schedule
	/////////////////////////////////////

	@Test
	public void test_schedule_get(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Schedule schedule = X.scenario.get_controller_schedule();
			System.out.println(schedule);
			assertNotNull(schedule);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test_schedule_clear(){
		try {
			TestData X = new TestData("project2_rm.opt");
			X.scenario.clear_controller_schedule();
			Schedule schedule = X.scenario.get_controller_schedule();
			System.out.println(schedule);
			assertTrue(schedule.get_num_items()==0);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test_add_controller(){
		try {
			TestData X = new TestData("project2.opt");
			X.scenario.clear_controller_schedule();

			float dt = 300f;   // update time in seconds
			float start_time = 0f; // start time in seconds after midnight
			Float end_time = null; // end time in seconds after midnight, or null for 'never'
			Set<Long> link_ids = new HashSet<>(); // set of onramp link ids
			link_ids.add(8l);

			ControllerRampMeterAlinea alinea = ControlFactory.create_controller_alinea(dt,start_time,end_time,link_ids,X.scenario);
			X.scenario.add_controller(alinea);

			Schedule schedule = X.scenario.get_controller_schedule();
			System.out.println(schedule);
			assertTrue(schedule.get_num_items()==1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test_delete_controller(){
		try {
			TestData X = new TestData("project2_rm.opt");


			Schedule schedule = X.scenario.get_controller_schedule();
			System.out.println(schedule);

			long controller_id = schedule.items.get(0).getId();
			X.scenario.delete_controller(controller_id);

			System.out.println();
			System.out.println(schedule);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
