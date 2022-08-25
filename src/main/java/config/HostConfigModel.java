package config;

/**
 * Data class for host configuration files
 */
public class HostConfigModel {
    private String topic;
    private String brokerLocation;
    private String id;
    private int port;
    private int pollDelay;
    private String hostType;
    private int startingPos;
    private String dataFilePath;

    /**
     * Constructor for the host configuration class
     * @param topic
     * @param brokerLocation
     * @param id
     * @param port
     * @param pollDelay
     * @param hostType
     * @param startingPos
     * @param dataFilePath
     */
    public HostConfigModel(String topic, String brokerLocation, String id, int port, int pollDelay, String hostType, int startingPos, String dataFilePath) {
        this.topic = topic;
        this.brokerLocation = brokerLocation;
        this.id = id;
        this.port = port;
        this.pollDelay = pollDelay;
        this.hostType = hostType;
        this.startingPos = startingPos;
        this.dataFilePath = dataFilePath;
    }

    /**
     * Get method for topic
     * @return String
     */
    public String getTopic() {
        return topic;
    }

    /**
     * get method for broker location
     * @return String
     */
    public String getBrokerLocation() {
        return brokerLocation;
    }

    /**
     * get method for id
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * get method for port number
     * @return int
     */
    public int getPort() {
        return port;
    }

    /**
     * get method for poll delay
     * @return int
     */
    public int getPollDelay() {
        return pollDelay;
    }

    /**
     * get method for host type
     * @return String
     */
    public String getHostType() {
        return hostType;
    }

    /**
     * get method for startign position
     * @return int
     */
    public int getStartingPos() {
        return startingPos;
    }

    /**
     * get method for the data file
     * @return String
     */
    public String getDataFilePath(){
        return dataFilePath;
    }
}
