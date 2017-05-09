package parsers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static parsers.Converter.hexByteArrayToInt;
import static parsers.Converter.hexByteArrayToLong;

/**
 * Created by psu43 on 13/04/17.
 */
public class ConverterTest {
    @Test
    public void hexByteArrayToIntTest() throws Exception {
        byte[] testByteArray={0x68,0x00,0x00,0x00};
        assertEquals(104,hexByteArrayToInt(testByteArray));


        byte[] testByteArray2={0x68,-83,-38,0x2E};
        assertEquals(786083176,hexByteArrayToInt(testByteArray2));
    }

    @Test
    public void hexByteArrayToLongTest() throws Exception {
        byte[] testByteArray={0x68,83,127,-127,12,12};
        assertEquals(13247851746152l,hexByteArrayToLong(testByteArray));


        byte[] testByteArray2={0x68,-83,-38,0x2E,00,00};
        assertEquals(786083176l,hexByteArrayToLong(testByteArray2));
    }


}
