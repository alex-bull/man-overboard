package parsers.xml.race;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Integer> participantIDs = new HashSet<>();
    private List<MarkData> startMarks = new ArrayList<>();
    private List<MarkData> finishMarks = new ArrayList<>();
    private List<CompoundMarkData> course = new ArrayList<>();
    private List<CornerData> compoundMarkSequence = new ArrayList<>();
    private Set<Integer> compoundMarkIDs = new HashSet<>();
    private Set<Integer> markIDs = new HashSet<>();
    private List<LimitData> courseLimit = new ArrayList<>();


    public RaceData() {
    }
    public List<Integer> getStartMarksID(){
        List<Integer> returnList=new ArrayList<>();
        for(MarkData mark:startMarks){
            returnList.add(mark.getSourceID());
        }
        return returnList;
    }
    public List<Integer> getFinishMarksID(){
        List<Integer> returnList=new ArrayList<>();
        for(MarkData mark:finishMarks){
            returnList.add(mark.getSourceID());
        }
        return returnList;
    }
    public List<MarkData> getStartMarks() {
        return this.startMarks;
    }

    public void setStartMarks(List<MarkData> startMarks) {
        this.startMarks = startMarks;
    }

    public List<MarkData> getFinishMarks() {
        return this.finishMarks;
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

    public void setParticipantIDs(Set<Integer> participantIDs) {
        this.participantIDs = participantIDs;
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

    public void addCourseLimit(LimitData limitData) {
        this.courseLimit.add(limitData);
    }

    public void setCourseLimit(List<LimitData> courseLimit) {
        this.courseLimit = courseLimit;
    }

    public Set<Integer> getParticipantIDs() {
        return participantIDs;
    }

    public Set<Integer> getCompoundMarkIDs() {
        return compoundMarkIDs;
    }

    public void setCompoundMarkIDs(Set<Integer> compoundMarkIDs) {
        this.compoundMarkIDs = compoundMarkIDs;
    }

    public void addCompoundMarkID(Integer id) {
        this.compoundMarkIDs.add(id);
    }

    public Set<Integer> getMarkIDs() {
        return markIDs;
    }

    public void setMarkIDs(Set<Integer> markIDs) {
        this.markIDs = markIDs;
    }

    public void addMarkID(Integer markId) {
        this.markIDs.add(markId);
    }
}
