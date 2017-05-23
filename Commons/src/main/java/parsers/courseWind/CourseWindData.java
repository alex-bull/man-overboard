package parsers.courseWind;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by psu43 on 19/05/17.
 * Course wind data from the AC35 streaming data interface specification
 */
public class CourseWindData {
    private int messageVersionNumber;
    private int selectedWindID;
    private Map<Integer, WindStatus> windStatuses = new HashMap<>();

    public CourseWindData(int messageVersionNumber, int selectedWindID, Map<Integer, WindStatus> windStatuses) {
        this.messageVersionNumber = messageVersionNumber;
        this.selectedWindID = selectedWindID;
        this.windStatuses = windStatuses;
    }


    public int getMessageVersionNumber() {
        return messageVersionNumber;
    }

    public int getSelectedWindID() {
        return selectedWindID;
    }

    public Map<Integer, WindStatus> getWindStatuses() {
        return windStatuses;
    }


}
