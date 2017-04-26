package seng302.Parsers;

import org.junit.Before;
import org.junit.Test;
import seng302.Model.Boat;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by khe60 on 27/04/17.
 */
public class BoatXMLParserTest {
    BoatXMLParser boatXMLParser;
    @Before
    public void setUp() throws Exception {
        boatXMLParser=new BoatXMLParser("<BoatConfig>\n" +
                "\t<Modified>2015-08-28T17:32:59+0100</Modified>\n" +
                "\t<Version>12</Version>\n" +
                "\t<Snapshot>219</Snapshot>\n" +
                "\t<Settings>\n" +
                "\t\t<RaceBoatType Type=\"AC45\"/>\n" +
                "\t\t<BoatDimension BoatLength=\"14.019\" HullLength=\"13.449\"/>\n" +
                "\t\t<ZoneSize MarkZoneSize=\"40.347\" CourseZoneSize=\"53.796\"/>\n" +
                "\t\t<ZoneLimits Limit1=\"200\" Limit2=\"100\" Limit3=\"53.796\" Limit4=\"0\" Limit5=\"-100\"/>\n" +
                "\t</Settings>\n" +
                "\t<BoatShapes>\n" +
                "\t\t<BoatShape ShapeID=\"0\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"25\" X=\"0\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"14\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"0.75\" X=\"-1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"0.75\" X=\"-0.25\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"3.5\" X=\"-0.25\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"4.5\" X=\"-1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"6.5\" X=\"-1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"7\" Y=\"7\" X=\"-0.5\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"8\" Y=\"7\" X=\"0.5\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"9\" Y=\"6.5\" X=\"1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"10\" Y=\"4.5\" X=\"1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"11\" Y=\"3.5\" X=\"0.25\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"12\" Y=\"0.75\" X=\"0.25\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"13\" Y=\"0.75\" X=\"1\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"14\" Y=\"0\" X=\"1\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"15\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-3.46\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"13.449\" X=\"-3.46\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"14.019\" X=\"0\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"13.449\" X=\"3.46\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"0\" X=\"3.46\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t\t<Catamaran>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"1.769\" X=\"-2.752\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"0\" X=\"-2.813\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"0\" X=\"-3.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"5.351\" X=\"-3.46\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"10.544\" X=\"-3.387\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"13.449\" X=\"-3.075\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"7\" Y=\"10.851\" X=\"-2.793\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"8\" Y=\"6.669\" X=\"-2.699\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"9\" Y=\"6.669\" X=\"2.699\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"10\" Y=\"10.851\" X=\"2.793\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"11\" Y=\"13.449\" X=\"3.075\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"12\" Y=\"10.544\" X=\"3.387\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"13\" Y=\"5.351\" X=\"3.46\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"14\" Y=\"0\" X=\"3.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"15\" Y=\"0\" X=\"2.813\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"16\" Y=\"1.769\" X=\"2.752\"/>\n" +
                "\t\t\t</Catamaran>\n" +
                "\t\t\t<Bowsprit>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"6.669\" X=\"-0.2\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"11.377\" X=\"-0.2\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"14.019\" X=\"0\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"11.377\" X=\"0.2\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"6.669\" X=\"0.2\"/>\n" +
                "\t\t\t</Bowsprit>\n" +
                "\t\t\t<Trampoline>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"2\" X=\"-2.699\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"6.438\" X=\"-2.699\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"6.438\" X=\"2.699\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"2\" X=\"2.699\"/>\n" +
                "\t\t\t</Trampoline>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"18\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-1.04\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"0.11\" X=\"-1.18\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"0.42\" X=\"-1.28\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"3.74\" X=\"-1.29\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"5.36\" X=\"-1.21\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"6.29\" X=\"-1.08\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"7\" Y=\"7.15\" X=\"-0.84\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"8\" Y=\"7.63\" X=\"-0.62\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"9\" Y=\"7.94\" X=\"-0.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"10\" Y=\"8.06\" X=\"0\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"11\" Y=\"7.94\" X=\"0.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"12\" Y=\"7.63\" X=\"0.62\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"13\" Y=\"7.15\" X=\"0.84\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"14\" Y=\"6.29\" X=\"1.08\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"15\" Y=\"5.36\" X=\"1.21\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"16\" Y=\"3.74\" X=\"1.29\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"17\" Y=\"0.42\" X=\"1.28\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"18\" Y=\"0.11\" X=\"1.18\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"19\" Y=\"0\" X=\"1.04\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"24\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-2.5\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"7\" X=\"-2.5\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"12.6\" X=\"-2.2\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"12.6\" X=\"2.2\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"7\" X=\"2.5\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"0\" X=\"2.5\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"34\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-1.16\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"5.51\" X=\"-1.16\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"5.846\" X=\"-0.84\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"5.846\" X=\"0.84\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"5.51\" X=\"1.16\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"0\" X=\"1.16\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t\t<BoatShape ShapeID=\"35\">\n" +
                "\t\t\t<Vertices>\n" +
                "\t\t\t\t<Vtx Seq=\"1\" Y=\"0\" X=\"-1.461\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"2\" Y=\"6\" X=\"-1.461\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"3\" Y=\"7\" X=\"-1.44\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"4\" Y=\"8\" X=\"-1.38\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"5\" Y=\"9\" X=\"-1.17\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"6\" Y=\"10\" X=\"-0.76\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"7\" Y=\"10.6\" X=\"-0.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"8\" Y=\"10.61\" X=\"0\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"9\" Y=\"10.6\" X=\"0.34\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"10\" Y=\"10\" X=\"0.76\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"11\" Y=\"9\" X=\"1.17\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"12\" Y=\"8\" X=\"1.38\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"13\" Y=\"7\" X=\"1.44\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"14\" Y=\"6\" X=\"1.461\"/>\n" +
                "\t\t\t\t<Vtx Seq=\"15\" Y=\"0\" X=\"1.461\"/>\n" +
                "\t\t\t</Vertices>\n" +
                "\t\t</BoatShape>\n" +
                "\t</BoatShapes>\n" +
                "\t<Boats>\n" +
                "\t\t<Boat Type=\"RC\" SourceID=\"121\" ShapeID=\"35\" StoweName=\"PRO\" ShortName=\"PRO\" ShorterName=\"PRO\" BoatName=\"REGARDLESS\" HullNum=\"RG02\" Skipper=\"Iain Murray\" Helmsman=\"Iain Murray\" PeliID=\"121\" RadioIP=\"172.20.2.121\">\n" +
                "\t\t\t<GPSposition Z=\"3.8\" Y=\"4.15\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"3.77\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"122\" ShapeID=\"34\" StoweName=\"SL1\" ShortName=\"SL1\" ShorterName=\"SL1\" BoatName=\"Start Line 1\" HullNum=\"Mark-02\" Skipper=\"\" PeliID=\"122\" RadioIP=\"172.20.2.122\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"123\" ShapeID=\"34\" StoweName=\"SL2\" ShortName=\"SL2\" ShorterName=\"SL2\" BoatName=\"Start Line 2\" HullNum=\"Mark-03\" Skipper=\"\" PeliID=\"123\" RadioIP=\"172.20.2.123\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"124\" ShapeID=\"34\" StoweName=\"LG1\" ShortName=\"LG1\" ShorterName=\"LG1\" BoatName=\"Lee Gate 1\" HullNum=\"Mark-04\" Skipper=\"\" PeliID=\"124\" RadioIP=\"172.20.2.124\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"125\" ShapeID=\"34\" StoweName=\"LG2\" ShortName=\"LG2\" ShorterName=\"LG2\" BoatName=\"Lee Gate 2\" HullNum=\"Mark-05\" Skipper=\"\" PeliID=\"125\" RadioIP=\"172.20.2.125\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"126\" ShapeID=\"34\" StoweName=\"WG1\" ShortName=\"WG1\" ShorterName=\"WG1\" BoatName=\"Wind Gate 1\" HullNum=\"Mark-06\" Skipper=\"\" PeliID=\"126\" RadioIP=\"172.20.2.126\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"127\" ShapeID=\"34\" StoweName=\"WG2\" ShortName=\"WG2\" ShorterName=\"WG2\" BoatName=\"Wind Gate 2\" HullNum=\"Mark-07\" Skipper=\"\" PeliID=\"127\" RadioIP=\"172.20.2.127\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"128\" ShapeID=\"34\" StoweName=\"FL1\" ShortName=\"FL1\" ShorterName=\"FL1\" BoatName=\"Finish Line 1\" HullNum=\"Mark-08\" Skipper=\"\" PeliID=\"128\" RadioIP=\"172.20.2.128\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"129\" ShapeID=\"34\" StoweName=\"FL2\" ShortName=\"FL2\" ShorterName=\"FL2\" BoatName=\"Finish Line 2\" HullNum=\"Mark-09\" Skipper=\"\" PeliID=\"129\" RadioIP=\"172.20.2.129\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"130\" ShapeID=\"34\" StoweName=\"SP1\" ShortName=\"SP1\" ShorterName=\"Sp1\" BoatName=\"Spare\" HullNum=\"Mark-10\" Skipper=\"\" PeliID=\"130\" RadioIP=\"172.20.2.130\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Mark\" SourceID=\"131\" ShapeID=\"34\" StoweName=\"M1\" ShortName=\"M1\" ShorterName=\"M1\" BoatName=\"Mark1\" HullNum=\"Mark-01\" Skipper=\"\" PeliID=\"131\" RadioIP=\"172.20.2.131\">\n" +
                "\t\t\t<GPSposition Z=\"5.445\" Y=\"1.12\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0.74\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"101\" ShapeID=\"15\" StoweName=\"USA\" ShortName=\"ORACLE\" ShorterName=\"USA\" BoatName=\"ORACLE TEAM USA\" HullNum=\"AC4515\" Skipper=\"SPITHILL\" Helmsman=\"SPITHILL\" Country=\"USA\" PeliID=\"101\" RadioIP=\"172.20.2.101\">\n" +
                "\t\t\t<GPSposition Z=\"1.78\" Y=\"-0.331\" X=\"-0.006\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"102\" ShapeID=\"15\" StoweName=\"SWE\" ShortName=\"ARTEMIS\" ShorterName=\"SWE\" BoatName=\"ARTEMIS RACING\" HullNum=\"AC4517\" Skipper=\"OUTTERIDGE\" Helmsman=\"OUTTERIDGE\" Country=\"SWE\" PeliID=\"102\" RadioIP=\"172.20.2.102\">\n" +
                "\t\t\t<GPSposition Z=\"1.727\" Y=\"-0.359\" X=\"-0.0121\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"103\" ShapeID=\"15\" StoweName=\"NZL\" ShortName=\"ETNZ\" ShorterName=\"NZL\" BoatName=\"EMIRATES TEAM NZ\" HullNum=\"AC4503\" Skipper=\"ASHBY\" Helmsman=\"BURLING\" Country=\"NZL\" PeliID=\"103\" RadioIP=\"172.20.2.103\">\n" +
                "\t\t\t<GPSposition Z=\"1.881\" Y=\"-0.291\" X=\"-0.003\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"104\" ShapeID=\"15\" StoweName=\"JPN\" ShortName=\"JAPAN\" ShorterName=\"JPN\" BoatName=\"SOFTBANK TEAM JAPAN\" HullNum=\"AC4504\" Skipper=\"BARKER\" Helmsman=\"BARKER\" Country=\"JPN\" PeliID=\"104\" RadioIP=\"172.20.2.104\">\n" +
                "\t\t\t<GPSposition Z=\"1.805\" Y=\"-0.322\" X=\"-0.003\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"105\" ShapeID=\"15\" StoweName=\"FRA\" ShortName=\"FRANCE\" ShorterName=\"FRA\" BoatName=\"GROUPAMA TEAM FRANCE\" HullNum=\"AC4505\" Skipper=\"CAMMAS\" Helmsman=\"CAMMAS\" Country=\"FRA\" PeliID=\"105\" RadioIP=\"172.20.2.105\">\n" +
                "\t\t\t<GPSposition Z=\"1.863\" Y=\"-0.3\" X=\"-0.003\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Yacht\" SourceID=\"106\" ShapeID=\"15\" StoweName=\"GBR\" ShortName=\"GBR\" ShorterName=\"GBR\" BoatName=\"LAND ROVER BAR\" HullNum=\"AC4516\" Skipper=\"ANSLIE\" Helmsman=\"ANSLIE\" Country=\"GBR\" PeliID=\"106\" RadioIP=\"172.20.2.106\">\n" +
                "\t\t\t<GPSposition Z=\"1.734\" Y=\"-0.352\" X=\"0\"/>\n" +
                "\t\t\t<MastTop Z=\"21.496\" Y=\"3.7\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"6.2\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Marshall\" SourceID=\"109\" ShapeID=\"24\" StoweName=\"CAM\" ShortName=\"CAM\" ShorterName=\"CAM\" BoatName=\"Cambria\" HullNum=\"TV01\" Skipper=\" \" Helmsman=\" \" PeliID=\"109\" RadioIP=\"172.20.2.109\">\n" +
                "\t\t\t<GPSposition Z=\"0\" Y=\"0\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"0\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Marshall\" SourceID=\"110\" ShapeID=\"18\" StoweName=\"BYS\" ShortName=\"BYSTANDER\" ShorterName=\"BYS\" BoatName=\"BYSTANDER\" HullNum=\"XR09\" Skipper=\"Stan Gibbs\" PeliID=\"110\" RadioIP=\"172.20.2.110\">\n" +
                "\t\t\t<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"3.426\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Marshall\" SourceID=\"111\" ShapeID=\"18\" StoweName=\"SHA\" ShortName=\"SHA\" ShorterName=\"SHA\" BoatName=\"SHAMROCK\" HullNum=\"XR01\" Skipper=\"\" PeliID=\"111\" RadioIP=\"172.20.2.111\">\n" +
                "\t\t\t<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0\"/>\n" +
                "\t\t\t<FlagPosition Z=\"0\" Y=\"3.426\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Umpire\" SourceID=\"113\" ShapeID=\"18\" StoweName=\"U1\" ShortName=\"U1\" ShorterName=\"U1\" BoatName=\"VIGILENT\" HullNum=\"XR02\" Skipper=\"\" PeliID=\"113\" RadioIP=\"172.20.2.113\">\n" +
                "\t\t\t<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Umpire\" SourceID=\"114\" ShapeID=\"18\" StoweName=\"U2\" ShortName=\"U2\" ShorterName=\"U2\" BoatName=\"RESOLUTE\" HullNum=\"XR03\" Skipper=\"\" PeliID=\"114\" RadioIP=\"172.20.2.114\">\n" +
                "\t\t\t<GPSposition Z=\"5.334\" Y=\"3.804\" X=\"0\"/>\n" +
                "\t\t</Boat>\n" +
                "\t\t<Boat Type=\"Helicopter\" SourceID=\"140\" ShapeID=\"14\" StoweName=\"HL1\" ShortName=\"HEL1\" ShorterName=\"HL1\" BoatName=\"HELICOPTER\" PeliID=\"140\" RadioIP=\"172.20.2.140\"/>\n" +
                "\t</Boats>\n" +
                "</BoatConfig>");
    }

    @Test
    public void testParser() throws Exception{
        List<Boat> competitors=boatXMLParser.getBoats();
        assertEquals(6,competitors.size());
        assertEquals("ORACLE TEAM USA",competitors.get(0).getTeamName());
        assertEquals("ARTEMIS RACING",competitors.get(1).getTeamName());
        assertEquals("EMIRATES TEAM NZ",competitors.get(2).getTeamName());
        assertEquals("SOFTBANK TEAM JAPAN",competitors.get(3).getTeamName());
        assertEquals("GROUPAMA TEAM FRANCE",competitors.get(4).getTeamName());
        assertEquals("LAND ROVER BAR",competitors.get(5).getTeamName());
    }

}