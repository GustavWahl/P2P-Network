package tracker;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import protos.Packet;
import connection.Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tracker {
    private final ExecutorService threadPool;
    private static final Logger logger = LogManager.getLogger(Tracker.class);
    private int port;
    private final String id;
    private ServerSocket serverSocket;
    private SwarmList swarmList;
    /**
     * Constructor for Discovery Service
     *  @param id
     * @param port
     */
    public Tracker(String id, int port) {
        this.id = id;
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(300);
        this.swarmList = new SwarmList();
    }



    /**
     * Method that handles incomming requests
     */
    public void startAcceptingRequests() {
        try {
            logger.info(String.format("Tracker %s waiting for connection", this.id));

            this.serverSocket = new ServerSocket(port);
            Thread listeningThread = new Thread(() -> {
                while (true) {
                    try {
                        Connection connection = new Connection(serverSocket.accept(), "track");
                        logger.info(String.format("Tracker %s established connection", this.id));

                        threadPool.execute( () -> {
                            boolean stillReceiving = true;

                            while (stillReceiving) {
                                try {
                                    byte[] message = connection.receive();
                                    if (message != null) {
                                        Packet packet = Packet.parseFrom(message);
                                        String type = packet.getType();

                                        if (type.equals("leach")){
                                            String wantedFile = packet.getFileName();
                                            Swarm swarm;

                                            if (swarmList.hasSeed(wantedFile)) {
                                                swarm = swarmList.getSwarm(wantedFile);
                                                if (swarm != null) {
                                                    swarm.addPeerToSwarm(packet.getHostLocation());
                                                    connection.send(
                                                            Packet.newBuilder()
                                                                    .setType("tracker")
                                                                    .setData(ByteString.copyFrom(
                                                                            SerializationUtils.serialize(swarm)))
                                                                    .build().toByteArray()
                                                    );
                                                    logger.info(String.format("Tracker sent info about a current swarm"));
                                                } else {
                                                    sendACK(connection, "NOSEED");
                                                    logger.info(String.format("Seed does not exist yet"));

                                                }
                                            } else {
                                                sendACK(connection, "NOSEED");
                                                logger.info(String.format("Seed does not exist"));
                                            }
                                        } else if (type.equals("SEED")) {
                                            String fileName = packet.getFileName();
                                            Swarm swarm = swarmList.getSwarm(fileName);
                                            int numChunks = (int) Math.ceil(packet.getSize() / 512000.0);
                                            Seed seed = new Seed(packet.getHostLocation(), packet.getFileName(), numChunks);

                                            if (swarm != null) {
                                                swarm.addSeed(seed);
                                            } else {
                                                swarm = Swarm.createSwarm(fileName, numChunks);
                                                swarmList.put(fileName, swarm);
                                                swarm.addSeed(seed);
                                            }
                                            sendACK(connection, "SEEDADDED");
                                            logger.info(String.format("Seed %s added", fileName));
                                            stillReceiving = false;
                                        }
                                    }

                                } catch (InvalidProtocolBufferException e) {
                                   System.out.println("error occurred in Tracker");
                                }
                            }
                        });
                    } catch (IOException e) {
                        logger.info(String.format("Tracker %s Could not establish connection", this.id));
                    }
                }
            });
            listeningThread.start();
        } catch (IOException e) {
            logger.info(String.format("Tracker %s could not start listening for requests", this.id));
        }
    }

    public void sendACK(Connection connection, String type) {
        connection.send(
                Packet.newBuilder()
                        .setType(type)
                        .build().toByteArray()
        );
    }
}

