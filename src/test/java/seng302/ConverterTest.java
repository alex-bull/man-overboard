//package seng302;
//
//import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import seng302.Parsers.Converter;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static seng302.Parsers.Converter.hexByteArrayToInt;
//
///**
// * Created by psu43 on 13/04/17.
// */
//public class ConverterTest {
//
//    Converter testConverter = new Converter();
//    byte[] aByteArray = {0, 0, 0, 0};
//    byte[] anotherByteArray = {(byte)244, 1, 0, 0};
//    byte[] yetAnotherByteArray = {64, (byte)226, 1, 0};
//
//
////    @Before
////    public void setUp(){
////        aByteArray
////    }
//    @Test
//    public void hexByteArrayToInt(){
//        assertEquals(testConverter.hexByteArrayToInt(aByteArray), 0);
//    }
//
//    @Test
//    public void hexByteArrayToInt2(){
//        assertEquals(testConverter.hexByteArrayToInt(anotherByteArray), 500);
//    }
//
//    @Test
//    public void hexByteArrayToInt3(){
//        assertEquals(testConverter.hexByteArrayToInt(yetAnotherByteArray), 123456);
//    }
//
//    @Test
//    public void hexByteArrayToLong(){
//        assertEquals(testConverter.hexByteArrayToLong(aByteArray), 0);
//    }
//
//    @Test
//    public void hexByteArrayToLong2(){
//        assertEquals(testConverter.hexByteArrayToLong(anotherByteArray), 500);
//    }
//
//
//    @Test
//    public void hexByteArrayToLong3(){
//        assertEquals(testConverter.hexByteArrayToLong(yetAnotherByteArray), 123456);
//    }
//
//
//    @Test
//    public void printInt(){
//        System.out.println(testConverter.hexByteArrayToInt(aByteArray));
//        System.out.println(testConverter.hexByteArrayToInt(anotherByteArray));
//        System.out.println(testConverter.hexByteArrayToInt(yetAnotherByteArray));
//        System.out.println(testConverter.hexByteArrayToLong(aByteArray));
//        System.out.println(testConverter.hexByteArrayToLong(anotherByteArray));
//        System.out.println(testConverter.hexByteArrayToLong(yetAnotherByteArray));
//    }
//
//
//}
