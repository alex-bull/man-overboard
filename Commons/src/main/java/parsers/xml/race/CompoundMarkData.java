package parsers.xml.race;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by psu43 on 26/04/17.
 * A compound mark given in race.xml in a course
 */
public class CompoundMarkData {
    private int ID;
    private String name;
    private List<MarkData> marks = new ArrayList<>();

    /**
     * Compound mark data
     * @param ID int ID
     * @param name String name
     * @param marks List marks
     */
    CompoundMarkData(int ID, String name, List<MarkData> marks) {
        this.ID = ID;
        this.name = name;
        this.marks = marks;
    }

    List<MarkData> getMarks() {
        return marks;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CompoundMarkData{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", marks=" + marks +
                '}';
    }
}
