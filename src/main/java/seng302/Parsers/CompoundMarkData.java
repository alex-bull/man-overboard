package seng302.Parsers;

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

    public CompoundMarkData(int ID, String name, List<MarkData> marks) {
        this.ID = ID;
        this.name = name;
        this.marks = marks;
    }


    public List<MarkData> getMarks() {
        return marks;
    }

    public void setMarks(List<MarkData> marks) {
        this.marks = marks;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




}
