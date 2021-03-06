package ge.edu.freeuni.sdp.iot.service.scheduler.sprinkler.data;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by GM on 6/9/2016.
 */
@XmlRootElement
public class Status {

    @javax.xml.bind.annotation.XmlElement
    private String lastCommand;

    public Status(){}

    public Status(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public String getLastCommand() {
        return lastCommand;
    }
}
