package parsers.courseWind;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by psu43 on 19/05/17.
 * Course wind data from the AC35 streaming data interface specification
 */
public class CourseWindData {
    private Map<Integer, WindStatus> windStatuses = new HashMap<>();

    /**
     * Constructs course wind data parsed from data source
     * @param windStatuses Map a map of wind statuses
     */
    CourseWindData(Map<Integer, WindStatus> windStatuses) {
        this.windStatuses = windStatuses;
    }

    public Map<Integer, WindStatus> getWindStatuses() {
        return windStatuses;
    }


}
