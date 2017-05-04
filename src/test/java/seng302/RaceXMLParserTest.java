package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.Parsers.RaceXMLParser;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 27/04/17.
 */
public class RaceXMLParserTest {
    RaceXMLParser raceXMLParser;

    @Before
    public void setUp() throws Exception {
        raceXMLParser = new RaceXMLParser("<Race>\n" +
                "  <CreationTimeDate>2015-08-29T13:12:40+02:00</CreationTimeDate>\n" +
                "  <RaceStartTime Start=\"2015-08-29T13:10:00+02:00\" Postpone=\"False\" />\n" +
                "  <RaceID>15082901</RaceID>\n" +
                "  <RaceType>Fleet</RaceType>\n" +
                "  <Participants>\n" +
                "    <Yacht SourceID=\"101\" />\n" +
                "    <Yacht SourceID=\"102\" />\n" +
                "    <Yacht SourceID=\"103\" />\n" +
                "    <Yacht SourceID=\"104\" />\n" +
                "    <Yacht SourceID=\"105\" />\n" +
                "    <Yacht SourceID=\"106\" />\n" +
                "  </Participants>\n" +
                "  <Course>\n" +
                "    <CompoundMark CompoundMarkID=\"1\" Name=\"Mark0\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Start Line 1\" TargetLat=\"57.6703330\" TargetLng=\"11.8278330\" SourceID=\"122\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Start Line 2\" TargetLat=\"57.6703330\" TargetLng=\"11.8278330\" SourceID=\"123\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"2\" Name=\"Mark1\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Mark1\" TargetLat=\"57.6675700\" TargetLng=\"11.8359880\" SourceID=\"131\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"3\" Name=\"Mark2\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"124\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"125\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"4\" Name=\"Mark3\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Wind Gate 1\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"126\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Wind Gate 2\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"127\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"5\" Name=\"Mark2\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"124\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"125\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"6\" Name=\"Mark3\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Wind Gate 1\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"126\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Wind Gate 2\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"127\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"7\" Name=\"Mark2\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"124\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"125\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"8\" Name=\"Mark3\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Wind Gate 1\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"126\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Wind Gate 2\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"127\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"9\" Name=\"Mark2\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Lee Gate 1\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"124\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Lee Gate 2\" TargetLat=\"57.6708220\" TargetLng=\"11.8433900\" SourceID=\"125\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"10\" Name=\"Mark3\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Wind Gate 1\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"126\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Wind Gate 2\" TargetLat=\"57.6650170\" TargetLng=\"11.8279170\" SourceID=\"127\" />\n" +
                "    </CompoundMark>\n" +
                "    <CompoundMark CompoundMarkID=\"11\" Name=\"Mark4\">\n" +
                "      <Mark SeqID=\"1\" Name=\"Finish Line 1\" TargetLat=\"57.6715240\" TargetLng=\"11.8444950\" SourceID=\"128\" />\n" +
                "      <Mark SeqID=\"2\" Name=\"Finish Line 2\" TargetLat=\"57.6715240\" TargetLng=\"11.8444950\" SourceID=\"129\" />\n" +
                "    </CompoundMark>\n" +
                "  </Course>\n" +
                "  <CompoundMarkSequence>\n" +
                "    <Corner SeqID=\"1\" CompoundMarkID=\"1\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"2\" CompoundMarkID=\"2\" Rounding=\"Port\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"3\" CompoundMarkID=\"3\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"4\" CompoundMarkID=\"4\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"5\" CompoundMarkID=\"5\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"6\" CompoundMarkID=\"6\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"7\" CompoundMarkID=\"7\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"8\" CompoundMarkID=\"8\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"9\" CompoundMarkID=\"9\" Rounding=\"SP\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"10\" CompoundMarkID=\"10\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "    <Corner SeqID=\"11\" CompoundMarkID=\"11\" Rounding=\"PS\" ZoneSize=\"3\" />\n" +
                "  </CompoundMarkSequence>\n" +
                "  <CourseLimit>\n" +
                "    <Limit SeqID=\"1\" Lat=\"57.6739450\" Lon=\"11.8417100\" />\n" +
                "    <Limit SeqID=\"2\" Lat=\"57.6709520\" Lon=\"11.8485010\" />\n" +
                "    <Limit SeqID=\"3\" Lat=\"57.6690260\" Lon=\"11.8472790\" />\n" +
                "    <Limit SeqID=\"4\" Lat=\"57.6693140\" Lon=\"11.8457610\" />\n" +
                "    <Limit SeqID=\"5\" Lat=\"57.6665370\" Lon=\"11.8432910\" />\n" +
                "    <Limit SeqID=\"6\" Lat=\"57.6641400\" Lon=\"11.8385840\" />\n" +
                "    <Limit SeqID=\"7\" Lat=\"57.6629430\" Lon=\"11.8332030\" />\n" +
                "    <Limit SeqID=\"8\" Lat=\"57.6629480\" Lon=\"11.8249660\" />\n" +
                "    <Limit SeqID=\"9\" Lat=\"57.6686890\" Lon=\"11.8250920\" />\n" +
                "    <Limit SeqID=\"10\" Lat=\"57.6708220\" Lon=\"11.8321340\" />\n" +
                "  </CourseLimit>\n" +
                "</Race>", 1000, 1000);
    }

    @Test
    public void parserTest() throws  Exception{
        System.out.println(raceXMLParser.getCourseBoundary());
        System.out.println(raceXMLParser.getCourseFeatures());
    }

}