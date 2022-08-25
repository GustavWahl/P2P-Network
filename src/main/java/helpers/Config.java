package Util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.HostConfigModel;

import java.io.*;


/**
 * Used code from principales of software design project as reference
 * Config class to handle configuration file
 */
public class Config {
    private static Gson gson = new Gson();

    /** Helper Method that reads file and returns JSON object
     * @param file
     * @return JsonObject
     * **/
    private static JsonObject getJSONObject(String file){
        try  {
            ClassLoader classLoader = Config.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(file);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");

            return gson.fromJson(reader, JsonObject.class);

        } catch (UnsupportedEncodingException e) {
            System.out.println("file not found");
            System.exit(0);        }
        return null;
    }

    /** *Method that parses json and returns config object
     * @param file
     * @return HostConfigModel
     * */
    public static HostConfigModel parseConfig(String file){
        JsonObject hostConfigModel = getJSONObject(file);

        String topic = hostConfigModel.get("topic").getAsString();
        String brokerLocation = hostConfigModel.get("brokerLocation").getAsString();
        String id = hostConfigModel.get("id").getAsString();
        int port = hostConfigModel.get("port").getAsInt();
        int pollDelay = hostConfigModel.get("pollDelay").getAsInt();
        String hostType = hostConfigModel.get("hostType").getAsString();
        int startingPos = hostConfigModel.get("startingPos").getAsInt();
        String dataFilePath = hostConfigModel.get("dataFilePath").getAsString();

        return new HostConfigModel(topic, brokerLocation, id, port, pollDelay, hostType, startingPos, dataFilePath);
    }
}
