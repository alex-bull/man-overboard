package utilities;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by khe60 on 30/05/17.
 * utility class for custom operations
 */
public class Utility {

    public static String fileToString(String filePath) throws IOException {
       return CharStreams.toString(new InputStreamReader(Utility.class.getResourceAsStream(filePath)));
    }

}
