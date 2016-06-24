package ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.shchedule.updater;


import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.Utility;
import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.shchedule.data.Schedule;
import ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.sprinkler.comunicator.SprinklerCommunicator;

import java.util.Set;

public class SprinklerStatusUpdater extends Thread {

    private Utility utility;
    private SprinklerCommunicator communicator;

    public SprinklerStatusUpdater(Utility utility ,SprinklerCommunicator communicator){
        this.utility = utility;

        this.communicator = communicator;
    }


    @Override
    public void run() {
        while (true){
            Set<Integer> houseIDS = utility.getHouseIDS();
            for (Integer houseId: houseIDS){
                if (utility.timeForSprinkler(houseId)){
                    communicator.setStatus(true,houseId+"");
                }

            }
            try {
                sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
