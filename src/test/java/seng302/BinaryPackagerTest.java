package seng302;

import javafx.scene.paint.Color;
import org.junit.Test;
import seng302.Model.Boat;
import seng302.Model.Competitor;
import seng302.Model.MutablePoint;
import seng302.TestMockDatafeed.BinaryPackager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by mgo65 on 1/05/17.
 */
public class BinaryPackagerTest {

    @Test
    public void testPackageBoatLocation() {
        BinaryPackager a = new BinaryPackager();
        byte[] b = a.packageBoatLocation(12, 123.444, 234.434, 65535.0, 20.3, 1);

        assertEquals(75, b.length);
        assertEquals(71, b[0]);
    }


    @Test
    public void testPackageXML() {
        BinaryPackager a = new BinaryPackager();
        BufferedReader br;
        String g;

        try {
            br = new BufferedReader(new FileReader(new File("src/main/resources/mockXML/mock_boats.xml")));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            g = sb.toString();
        } catch (Exception e) {
            System.out.println("File not found");
            return;
        }

        try {
            byte[] b = a.packageXML(g.length(), g, 23);
            assertEquals(9693, b.length);
            assertEquals(71, b[0]);

        } catch (IOException e) {
            System.out.println("Failed to package XML");
        }

    }


    @Test
    public void testRaceStatusHeader() {
        BinaryPackager a = new BinaryPackager();
        byte[] status = a.raceStatusHeader(1, 0, ZonedDateTime.now());

        assertEquals(0, status[11]);
        assertEquals(24, status.length);
    }


    @Test
    public void testPackageEachBoat() {
        List<Competitor> competitorList = new ArrayList<>();
        competitorList.add(new Boat("A", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "ABC"));
        competitorList.add(new Boat("B", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "BCA"));
        competitorList.add(new Boat("C", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "CAB"));
        BinaryPackager a = new BinaryPackager();
        byte[] boats = a.packageEachBoat(competitorList);
        assertEquals(60, boats.length);

    }


    @Test
    public void testPackageRaceStatus() {

        BinaryPackager a = new BinaryPackager();
        byte[] status = a.raceStatusHeader(1, 0, ZonedDateTime.now());
        List<Competitor> competitorList = new ArrayList<>();
        competitorList.add(new Boat("A", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "ABC"));
        competitorList.add(new Boat("B", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "BCA"));
        competitorList.add(new Boat("C", 10, new MutablePoint(10.0, 29.0), Color.BLACK, "CAB"));
        byte[] boats = a.packageEachBoat(competitorList);

        byte[] b = a.packageRaceStatus(status, boats);
        assertEquals(19 + status.length + boats.length, b.length);
        assertEquals(71, b[0]);
    }

}