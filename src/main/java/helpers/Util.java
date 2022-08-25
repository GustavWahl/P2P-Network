package helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap;

/**
 * Utility class
 */
public class Util {

    /**
     * Method that takes a string with hostname and port and returns a key, value entry
     * @param brokerLocation
     * @return
     */
    public static AbstractMap.SimpleEntry<String, Integer> splitLocationString(String brokerLocation) {
        String[] split = brokerLocation.split(":");
        return new AbstractMap.SimpleEntry<String, Integer>(split[0], Integer.parseInt(split[1]));
    }

    public static String getStringMemberLocation(AbstractMap.SimpleEntry<String, Integer> location) {
        return location.getKey() + ":" + location.getValue();
    }
}
