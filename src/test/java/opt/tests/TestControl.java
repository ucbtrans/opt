package opt.tests;

import opt.data.ControlFactory;
import opt.data.FreewayScenario;
import opt.data.LaneGroupType;
import opt.data.Schedule;
import opt.data.control.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestControl extends AbstractTest{
	float dt = 3f;
	float start_time = 0f;
	Float end_time = 34f;

	/////////////////////////////////////
	// create controllers
	/////////////////////////////////////

	@Test
	public void test_create_controller_alinea(){
		try {
			TestData X = new TestData("project2_rm.opt");
			boolean has_queue_control = false;
			float min_rate_vph = 100f;
			float max_rate_vph = 900f;
			long sensor_link_id = 9l;
			float sensor_offset = 100f;
			long ramp_link_id = 8l;
			LaneGroupType lgtype = LaneGroupType.gp;
			ControllerRampMeterAlinea cntrl = ControlFactory.create_controller_alinea(X.scenario,dt,start_time,end_time,has_queue_control,min_rate_vph,max_rate_vph,sensor_link_id,sensor_offset,ramp_link_id,lgtype);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_tod(){
		try {
			TestData X = new TestData("project2_rm.opt");
			boolean has_queue_control = false;
			float min_rate_vph = 100f;
			float max_rate_vph = 900f;
			long ramp_link_id = 8l;
			LaneGroupType lgtype = LaneGroupType.gp;
			ControllerRampMeterTOD cntrl = ControlFactory.create_controller_tod(X.scenario,dt,start_time,end_time,has_queue_control,min_rate_vph,max_rate_vph,ramp_link_id,lgtype);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_hov(){
		try {
			TestData X = new TestData("project2_rm.opt");
			ControllerPolicyHOV cntrl = ControlFactory.create_controller_hov(X.scenario,dt,start_time,end_time);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test_create_controller_hot(){
		try {
			TestData X = new TestData("project2_rm.opt");
			ControllerPolicyHOT cntrl = ControlFactory.create_controller_hot(X.scenario,dt,start_time,end_time);
			assertNotNull(cntrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/////////////////////////////////////
	// delete controllers
	/////////////////////////////////////

	@Test
	public void test_delete_add_controller(){
		try {
			TestData X = new TestData("project2_rm.opt");
			Schedule schedule = X.scenario.get_controller_schedule();
			AbstractController c =  schedule.items.get(0);
			long ramp_link_id = c.get_actuators().values().iterator().next().link_id;
			assertEquals(2,schedule.items.size());
			schedule.delete_controller(c);

			assertEquals(1,schedule.items.size());
			AbstractController n = ControlFactory.create_controller_alinea(X.scenario,1f,2f,3f,false,4f,5f,3l,6f,ramp_link_id,LaneGroupType.gp);
			schedule.add_item(n);

			assertEquals(2,schedule.items.size());

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

//	@Test
//	public void test_schedule_clear(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//			X.scenario.clear_controller_schedule();
//			Schedule schedule = X.scenario.get_controller_schedule();
//			System.out.println(schedule);
//			assertTrue(schedule.get_num_items()==0);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

//	@Test
//	public void test_add_controller(){
//		try {
//			TestData X = new TestData("project2.opt");
//			X.scenario.clear_controller_schedule();
//
//			boolean has_queue_control = false;
//			float min_rate_vph = 100f;
//			float max_rate_vph = 900f;
//			long sensor_link_id = 9l;
//			float sensor_offset = 100f;
//			long ramp_link_id = 8l;
//			ControllerRampMeterAlinea alinea = ControlFactory.create_controller_alinea(X.scenario,dt,start_time,end_time,has_queue_control,min_rate_vph,max_rate_vph,sensor_link_id,sensor_offset,ramp_link_id);
//			X.scenario.add_controller(alinea);
//
//			Schedule schedule = X.scenario.get_controller_schedule();
//			System.out.println(schedule);
//			assertTrue(schedule.get_num_items()==1);
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

//	@Test
//	public void test_delete_controller(){
//		try {
//			TestData X = new TestData("project2_rm.opt");
//
//
//			Schedule schedule = X.scenario.get_controller_schedule();
//			System.out.println(schedule);
//
//			long controller_id = schedule.items.get(0).getId();
//			X.scenario.delete_controller(controller_id);
//
//			System.out.println();
//			System.out.println(schedule);
//
//		} catch (Exception e) {
//			fail(e.getMessage());
//		}
//	}

}
