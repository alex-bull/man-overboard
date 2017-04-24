package seng302;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static seng302.Parsers.Converter.hexByteArrayToInt;

/**
 * Created by psu43 on 13/04/17.
 */
public class ConverterTest {

//    @Test
//    public void testHexListToDecimal() {
//        String values1 = "68, 00, 00, 00";
//        List<String> test1 = Arrays.asList(values1.split("\\s*,\\s*"));
//        Assert.assertTrue(104 == hexByteArrayToInt(test1));
//
//        String values2 = "68, AD, DA, 2E";
//        List<String> test2 = Arrays.asList(values2.split("\\s*,\\s*"));
//        Assert.assertTrue(786083176 == hexByteArrayToInt(test2));
//
//        String values3 = "00, 00, 00, 00";
//        List<String> test3 = Arrays.asList(values3.split("\\s*,\\s*"));
//        Assert.assertTrue(0 == hexByteArrayToInt(test3));
//
//        String values4 = "00, 99, D0, D3";
//        List<String> test4 = Arrays.asList(values4.split("\\s*,\\s*"));
//        Assert.assertTrue(Long.valueOf(3553663232l).equals(hexByteArrayToInt(test4)));
//
//        String values5 = "FF, FF, FF";
//        List<String> test5 = Arrays.asList(values5.split("\\s*,\\s*"));
//        Assert.assertTrue(Long.valueOf(16777215).equals(hexByteArrayToInt(test5)));
//
//        String values6 = "FF, FF, FC, 00";
//        List<String> test6 = Arrays.asList(values6.split("\\s*,\\s*"));
//        Assert.assertTrue(Long.valueOf(16580607).equals(hexByteArrayToInt(test6)));
//
//        String values7 = "FF, FF, FF, FF, FF, FF";
//        List<String> test7 = Arrays.asList(values7.split("\\s*,\\s*"));
//        Assert.assertTrue(Long.valueOf(281474976710655L).equals(hexByteArrayToInt(test7)));
//
//    }
}
