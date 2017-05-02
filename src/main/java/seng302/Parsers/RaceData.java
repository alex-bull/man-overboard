package seng302.Parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by psu43 on 26/04/17.
 * Race data contains course boundaries, race participants, race type, and the course description.
 */
public class RaceData {

    private int raceID;
    private String raceType;
    private String creationTimeDate;
    private String raceStartTime;
    private boolean raceStartTimePostpone;
    private List<YachtData> participants = new ArrayList<>();
    private List<MarkData> startMarks = new ArrayList<>();
    private List<MarkData> finishMarks = new ArrayList<>();


    //    private Map<Integer, CompoundMarkData> course = new HashMap<>();
    private List<CompoundMarkData> course = new ArrayList<>();
    private List<CornerData> compoundMarkSequence = new ArrayList<>();
    private List<LimitData> courseLimit = new ArrayList<>();

//    public RaceData(int raceID, String raceType, String creationTimeDate, String raceStartTime,
//                    boolean raceStartTimePostpone, List<YachtData> participants, List<CompoundMarkData> course,
//                    List<CornerData> compoundMarkSequence, List<LimitData> courseLimit) {
//        this.raceID = raceID;
//        this.raceType = raceType;
//        this.creationTimeDate = creationTimeDate;
//        this.raceStartTime = raceStartTime;
//        this.raceStartTimePostpone = raceStartTimePostpone;
//        this.participants = participants;
//        this.course = course;
//        this.compoundMarkSequence = compoundMarkSequence;
//        this.courseLimit = courseLimit;
//    }

    public RaceData() {
    }

    public List<MarkData> getStartMarks() {return this.startMarks;}
    public List<MarkData> getFinishMarks() {return this.finishMarks;}
    public void setStartMarks(List<MarkData> startMarks) {
        this.startMarks = startMarks;
    }
    public void setFinishMarks(List<MarkData> finishMarks) {
        this.finishMarks = finishMarks;
    }

    public List<CompoundMarkData> getCourse() {
        return course;
    }

    public void setCourse(List<CompoundMarkData> course) {
        this.course = course;
    }

//    public Map<Integer, CompoundMarkData> getCourse() {
//        return course;
//    }
//
//    public void setCourse(Map<Integer, CompoundMarkData> course) {
//        this.course = course;
//    }

    public int getRaceID() {
        return raceID;
    }

    public void setRaceID(int raceID) {
        this.raceID = raceID;
    }

    public String getRaceType() {
        return raceType;
    }

    public void setRaceType(String raceType) {
        this.raceType = raceType;
    }

    public String getCreationTimeDate() {
        return creationTimeDate;
    }

    public void setCreationTimeDate(String creationTimeDate) {
        this.creationTimeDate = creationTimeDate;
    }

    public String getRaceStartTime() {
        return raceStartTime;
    }

    public void setRaceStartTime(String raceStartTime) {
        this.raceStartTime = raceStartTime;
    }

    public boolean isRaceStartTimePostpone() {
        return raceStartTimePostpone;
    }

    public void setRaceStartTimePostpone(boolean raceStartTimePostpone) {
        this.raceStartTimePostpone = raceStartTimePostpone;
    }

    public List<YachtData> getParticipants() {
        return participants;
    }

    public void setParticipants(List<YachtData> participants) {
        this.participants = participants;
    }


    public List<CornerData> getCompoundMarkSequence() {
        return compoundMarkSequence;
    }

    public void setCompoundMarkSequence(List<CornerData> compoundMarkSequence) {
        this.compoundMarkSequence = compoundMarkSequence;
    }

    public List<LimitData> getCourseLimit() {
        return courseLimit;
    }

    public void setCourseLimit(List<LimitData> courseLimit) {
        this.courseLimit = courseLimit;
    }

}
