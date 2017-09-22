package utilities;

/**
 * Created by khe60 on 12/05/17.
 */
public enum Annotation {
    TEAM_NAME, BOAT_SPEED, EST_TIME_TO_NEXT_MARK, TIME_FROM_LAST_MARK;

    public static Annotation stringToAnnotation(String annotationString) {
        switch (annotationString) {
            case "Team Name":
                return TEAM_NAME;
            case "Speed":
                return BOAT_SPEED;
            case "Est Time to Next Mark":
                return EST_TIME_TO_NEXT_MARK;
            case "Time from Last Mark":
                return TIME_FROM_LAST_MARK;
            default:
                return null;
        }
    }
}
