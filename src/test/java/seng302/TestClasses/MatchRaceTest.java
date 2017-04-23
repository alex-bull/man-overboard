package seng302.TestClasses;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;
import seng302.Model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by mgo65 on 3/03/17.
 */
public class MatchRaceTest {

    @Test
    public void testGenerateTimeline() {
        List<Competitor> competitors = new ArrayList<>();
        List<CourseFeature> features = new ArrayList<>();

        features.add(new Mark("MarkOne", new MutablePoint(0.0, 0.0), new MutablePoint(0.0, 0.0), 0));
        features.add(new Mark("MarkTwo", new MutablePoint(1.0, 1.0), new MutablePoint(10.0, 10.0), 0));
        competitors.add(new Boat("A", 10, new MutablePoint(0.0, 0.0), Color.ALICEBLUE, "A"));
        competitors.add(new Boat("B", 10, new MutablePoint(0.0, 0.0), Color.ALICEBLUE, "B"));


        Course raceCourse = mock(Course.class);
        when(raceCourse.getPoints()).thenReturn(features);
        when(raceCourse.distanceBetweenGPSPoints(any(), any())).thenReturn(50.0);


        MatchRace matchRace = new MatchRace(10, raceCourse, competitors);

        Timeline timeline = matchRace.generateTimeline();
        ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

        Assert.assertEquals(4, keyFrames.size());
        Assert.assertEquals(0.0, keyFrames.get(0).getTime().toSeconds(), 0.0001);
        Assert.assertEquals(5, keyFrames.get(1).getTime().toSeconds(), 0.0001);
        Assert.assertEquals(0.0, keyFrames.get(2).getTime().toSeconds(), 0.0001);
        Assert.assertEquals(5, keyFrames.get(3).getTime().toSeconds(), 0.0001);

    }


}
