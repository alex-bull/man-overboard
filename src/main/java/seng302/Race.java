package seng302;

/**
 * Created by mgo65 on 3/03/17.
 */
public interface Race {
    void start();
    void setCompetitors(Competitor comp1, Competitor comp2);
    Competitor getCompetitor1();
    Competitor getCompetitor2();
}
