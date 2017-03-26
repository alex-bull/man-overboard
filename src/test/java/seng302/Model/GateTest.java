//package seng302.Model;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
///**
// * Created by khe60 on 24/03/17.
// */
//public class GateTest {
//
//    Gate gate;
//    Gate finishGate;
//
//    @Before
//    public void setUp() throws Exception {
//        gate = new Gate("Test Gate", new MutablePoint(0.0, 0.0), new MutablePoint(1.0, 1.0), false, false,0);
//        gate.setExitHeading(45.0);
//        finishGate = new Gate("Finish Gate", new MutablePoint(90.0, 100.0), new MutablePoint(100.0, 100.0), true, true,1);
//        finishGate.setExitHeading(0.0);
//    }
//
//    @Test
//    public void isLine() throws Exception {
//        assertFalse(gate.isLine());
//        assertTrue(finishGate.isLine());
//    }
//
//    @Test
//    public void isFinish() throws Exception {
//        assertFalse(gate.isFinish());
//        assertTrue(finishGate.isFinish());
//    }
//
//    @Test
//    public void getName() throws Exception {
//        assertEquals(gate.getName(),"Test Gate");
//        assertEquals(finishGate.getName(),"Finish Gate");
//
//    }
//
//    @Test
//    public void getIndex() throws Exception {
//        assertEquals(gate.getIndex(),0);
//        assertEquals(finishGate.getIndex(),1);
//    }
//
//
//    @Test
//    public void getLocations() throws Exception {
//        assertEquals(gate.getLocations().get(0),new MutablePoint(0.0,0.0));
//        assertEquals(finishGate.getLocations().get(1),new MutablePoint(100.0,100.0));
//    }
//
//    @Test
//    public void getCentre() throws Exception {
//        assertEquals(gate.getCentre(),new MutablePoint(0.5,0.5));
//        assertEquals(finishGate.getCentre(),new MutablePoint(95.0,100.0));
//    }
//
//
//    @Test
//    public void equals() throws Exception {
//        assertNotEquals(gate,finishGate);
//    }
//
//    @Test
//    public void getExitHeading() throws Exception {
//        assertEquals(gate.getExitHeading(),45.0,0.01);
//        assertEquals(finishGate.getExitHeading(),0.0,0.01);
//
//    }
//
//}