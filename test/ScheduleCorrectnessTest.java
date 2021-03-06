import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.SchedulerService;
import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.Utility;
import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.shchedule.data.Schedule;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by GM on 6/10/2016.
 */
public class ScheduleCorrectnessTest extends JerseyTest {
    Utility utility;

    @Before
    public void setUpUtility(){

        utility = Utility.getInstance();
        utility.init();
        utility.setNewScheduleForHouse(new Schedule(),"1");
    }
    @Override
    protected Application configure()
    {
        return new ResourceConfig(SchedulerService.class);
    }

    @Test
    public void TestSchedule() throws Exception {
        Schedule result =
                target("houses/1/schedule")
                        .request()
                        .get(Schedule.class);
        assertEquals(4, (int)result.getStartMonth());
        assertEquals(10, (int)result.getEndMonth());
    }

    @Test
    public void TestSchedule2() throws Exception {
        Schedule mySchedule = new Schedule();
        mySchedule.setAfterSunRise(3.0);
        mySchedule.setBeforeSunSet(2.0);
        mySchedule.setEndMonth(7);
        mySchedule.setStartMonth(1);
        utility.setNewScheduleForHouse(mySchedule,"1");
        Schedule result =
                target("houses/1/schedule")
                        .request()
                        .get(Schedule.class);
        assertEquals(1, (int)result.getStartMonth());
        assertEquals(7, (int)result.getEndMonth());
        assertEquals(3.0, result.getAfterSunRise(), 0.000001);
        assertEquals(2.0, result.getBeforeSunSet(), 0.000001);
    }


    @Test
    public void TestSchedule3() throws Exception {
        Schedule mySchedule = new Schedule();
        mySchedule.addExcluded("28/8/1995");
        utility.setNewScheduleForHouse(mySchedule,"1");
        Schedule result =
                target("houses/1/schedule")
                        .request()
                        .get(Schedule.class);
        assertEquals("28/8/1995", result.getExcluded().get(0));
    }


    @Test
    public void TestSchedule4() throws Exception {
        Schedule mySchedule = new Schedule();
        mySchedule.setExcluded(new ArrayList<String>());
        for (int i=0; i<5; i++){
            mySchedule.addExcluded(i+"/"+i+"/"+2000);
        }
        utility.setNewScheduleForHouse(mySchedule,"1");
        Schedule result =
                target("houses/1/schedule")
                        .request()
                        .get(Schedule.class);
        for (int i=0; i<5; i++){
            assertEquals(i+"/"+i+"/"+2000, result.getExcluded().get(i));
        }
    }

    @Test
    public void TestSchedule5() throws Exception {
        Schedule mySchedule = new Schedule();
        List<String> list = new ArrayList<>();
        for (int i=0; i<5; i++){
            list.add(i+"/"+i+"/"+2000);
        }
        mySchedule.setExcluded(list);
        utility.setNewScheduleForHouse(mySchedule,"1");
        Schedule result =
                target("houses/1/schedule")
                        .request()
                        .get(Schedule.class);
        for (int i=0; i<5; i++){
            assertEquals(i+"/"+i+"/"+2000, result.getExcluded().get(i));
        }
    }


}
