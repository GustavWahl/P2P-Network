package peer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import fileIO.FileIO;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import connection.Connection;
import protos.Packet;
import tracker.Seed;
import tracker.SeedList;
import tracker.Swarm;
import helpers.Util;
import tracker.SwarmList;

public class Peer {
    private final String id;
    private final int port;
    private final String hostName;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private boolean hasConnectedToTracker;
    private BlockingQueue<byte[]> messageQeueSending;
    private BlockingQueue<byte[]> recievedMessageQeue;
    private String trackerHostName;
    private int trackerPort;
    private SwarmList swarmList;
    private FileReconstructer fileReconstructer;
    private FileSelection fileSelection;
    private volatile boolean stillReceiving;
    private volatile boolean stillSending;
    private volatile boolean stillListening;
    private List<File> listOfFiles;


    private static final Logger logger = LogManager.getLogger(Peer.class);

    /**
     * Constructor for host
     * initializes executor srvice for thread handling
     * and also initializes queues for outgoing and incomming messages.
     * @param id
     * @param port
     */
    public Peer(String id, int port, String hostName, String trackerLocation) {
        this.id = id;
        this.port = port;
        this.hostName = hostName;
        this.threadPool = Executors.newFixedThreadPool(500);
        this.stillListening = true;
        this.hasConnectedToTracker = false;
        this.swarmList = new SwarmList();
        AbstractMap.SimpleEntry<String, Integer> kv = Util.splitLocationString(trackerLocation);
        this.fileReconstructer = new FileReconstructer();
        this.fileSelection = new FileSelection();

        this.trackerHostName = kv.getKey();
        this.trackerPort = kv.getValue();
        this.listOfFiles = new ArrayList<File>();
        // this.stillReceiving = true;
       // this.stillSending = true;

        this.messageQeueSending = new LinkedBlockingDeque<>();
        this.recievedMessageQeue = new LinkedBlockingDeque<>();
    }


    /**
     * Method to add messages to sending queue
     * @param data
     * @return
     */
    public boolean fillMessages(byte[] data) {
        return this.messageQeueSending.add(data);
    }

    /**
     * Method to establish a connection to another peer
     * And requests seed file
     * @param fileName
     */
    public void startRequestingSeed(String fileName) {
        try {
            Socket socket = new Socket(trackerHostName, trackerPort);
            Connection connection = new Connection(socket, id);
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
            AtomicReference<Boolean> hasReceivedSwarm = new AtomicReference<>(false);
          //  Lock lock = new ReentrantLock();

                scheduledExecutorService.scheduleAtFixedRate(() -> {
                    if (connection.getSocket().isConnected()) {
                        connection.send(Packet.newBuilder()
                                        .setType("leach")
                                        .setFileName(fileName)
                                        .setHostLocation(Util.getStringMemberLocation(new AbstractMap.SimpleEntry<String, Integer>(
                                                hostName,port
                                        )))
                                .build().toByteArray());
                    }
                    if (hasReceivedSwarm.get()) scheduledExecutorService.shutdown();
                }, 1000,2000, TimeUnit.MILLISECONDS);

            threadPool.execute(() -> {
                boolean stillReceiving = true;
                while (stillReceiving) {
                    try {
                        byte[] message = connection.receive();
                        if (message != null) {
                            Packet packet = Packet.parseFrom(message);
                            String type = packet.getType();

                            if (type.equals("tracker")) {
                                logger.info("Seed/swarm locations recieved from tracker");
                                hasReceivedSwarm.set(true);
                                swarmList.put(fileName, SerializationUtils.deserialize(
                                        packet.getData().toByteArray()));
                                startDownloading(fileName);
                                stillReceiving = false;
                            } else if (type.equals("NOSEED")) {
                                logger.info("peer requested seed does not exists");
                            }
                        }
                    } catch (InvalidProtocolBufferException e) {
                        logger.info("peer failed to recieve message");
                    }
                }
            });
        } catch (IOException e) {
            logger.info(String.format("peer %s failed to connect to Tracker", id));
        }
    }

    /**
     * Method that handles download from multiple peers
     * @param fileName
     */
    private void startDownloading(String fileName) {
        try {
            Swarm currentSwarm = swarmList.getSwarm(fileName);
            fileReconstructer.put(id+fileName, currentSwarm.getNumChunks());

            CountDownLatch cdl = new CountDownLatch(2);
            if (fileSelection.select(fileName) == null) {
                 Random random = new Random();
                 int randomSelectedChunk = random.nextInt(currentSwarm.getNumChunks());

                 threadPool.execute(() -> connectToPeers(currentSwarm.getSeedsList(), fileName, currentSwarm, cdl, randomSelectedChunk));
            } else {
                FileSelection.SelectedPeer chosen = fileSelection.select(fileName);

                threadPool.execute(() -> connectToPeers(currentSwarm.getSeedsList(), fileName, currentSwarm, cdl, 0));
                threadPool.execute(() -> connectToPeers(currentSwarm.getPeerList(), fileName, currentSwarm, cdl, 0));
            }

            cdl.await();
        } catch (InterruptedException e) {
            logger.info("failed to await countdownlatch");
        }
    }


    public void connectToPeers(List list, String fileName, Swarm currentSwarm, CountDownLatch cdl, int selected) {
        try {

            for (Object peer : list) {
                AbstractMap.SimpleEntry<String, Integer> entry;
                if (peer instanceof Seed) {
                    entry = Util.splitLocationString(((Seed) peer).getLocation());
                } else {
                    entry = Util.splitLocationString((String) peer);
                }

                String peerHostName = entry.getKey();
                int peerPort = entry.getValue();

                if (!(peerHostName + ":" + peerPort).equals(hostName + ":" + port)) {

                    Socket socket = new Socket(peerHostName, peerPort);
                    Connection connection = new Connection(socket, id);
                    logger.info(String.format("peer : %s:%s connecting to seed : %s:%s",hostName, port, peerHostName, peerPort));

                    if (socket.isConnected()) {

                            getFileChunks(connection, fileName, currentSwarm, cdl, selected);

                    }
                }
            }
        } catch (IOException e) {
            logger.info("Could not connect to download from peer");
        }
    }


    /**
     * Mthod that downloads file chunks and push updates too swarm
     * @param connection
     * @param fileName
     */
    private void getFileChunks(Connection connection, String fileName, Swarm currentSwarm, CountDownLatch cdl, int selectedChunk) {
        threadPool.execute(() -> {
            connection.send(Packet.newBuilder()
                            .setType("FILEREQ")
                            .setFileName(fileName)
                           // .setSequenceNum(selectedChunk)
                    .build().toByteArray());
        });
        threadPool.execute(() -> {
            boolean stillReceiving = true;

            while (stillReceiving) {
                try {
                    byte[] message = connection.receive();
                    if (message != null) {
                        Packet packet = Packet.parseFrom(message);
                        String type = packet.getType();
                        if (type.equals("DONE")) {
                            logger.info("recieved done");
                        } else if (type.equals("FILECHUNK")) {
                            byte[] data = packet.getData().toByteArray();
                            int sequence = packet.getSequenceNum();
                            String chunkName = sequence+";"+id+fileName+".tmp";
                            fileReconstructer.putChunk(id+fileName, chunkName, sequence, data, currentSwarm.getNumChunks());

                            logger.info(String.format("peer %s recieved file chunk", id));


                            pushDownloadedChunkUpdate(connection, currentSwarm, fileName);

                            if (fileReconstructer.gotAllChunks(id+fileName)) {
                                FileIO.mergeTmpFiles(id+fileName, fileReconstructer, 512000);
                                stillReceiving = false;

                                logger.info(String.format("file %s downloaded", fileName));
                                cdl.countDown();
                                cdl.await();
                                sendSeed(fileName);
                                break;
                            }
                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    logger.info("peer failed to recieve message");
                } catch (InterruptedException e) {
                    logger.info("failed to await countdownlatch");
                }
            }

        });
    }

    private void pushDownloadedChunkUpdate(Connection connection, Swarm currentSwarm, String fileName) {
        try {
            List<Integer> chunks = fileReconstructer.getIndexNum(id+fileName);

            for (String peer : currentSwarm.getPeerList()) {
                AbstractMap.SimpleEntry<String, Integer> entry;
                entry = Util.splitLocationString(peer);

                String peerHostName = entry.getKey();
                int peerPort = entry.getValue();

                if (!(peerHostName + ":" + peerPort).equals(hostName + ":" + port)) {
                    Socket socket = new Socket(peerHostName, peerPort);
                    Connection c = new Connection(socket, id);
                    threadPool.execute( () -> {
                        c.send(Packet.newBuilder()
                                .setType("UPDATE")
                                        .setFileName(fileName)
                                        .setHostLocation(hostName + ":" + port)
                                        .setData(ByteString.copyFrom(
                                                SerializationUtils.serialize((Serializable) chunks)))
                                .build().toByteArray());
                    });

                }
            }
            logger.info("Broadcast chunk update to peer");

        } catch (IOException e) {
            logger.info("Could not broadcast chunk update");
        }
    }

    /**
     * Method that sends message to Tracker that it is a seed
     * @param fileName
     */
    public void sendSeed(String fileName) {
        try {
            Socket socket = new Socket(trackerHostName, trackerPort);
            Connection connection = new Connection(socket, id);

            threadPool.execute(() -> {
                int size = FileIO.getFileLength(fileName);
                System.out.println(size);
                connection.send(Packet.newBuilder()
                        .setType("SEED")
                        .setFileName(fileName)
                        .setSize(size)
                        .setHostLocation(Util.getStringMemberLocation(new AbstractMap.SimpleEntry<String, Integer>(
                                hostName, port
                        )))
                        .build().toByteArray());
            });
        } catch (Exception e) {
            logger.info("could not connect to tracker as seed");
        }
    }


    /**
     * Method that handles incomming requests
     */
    public void startAcceptingRequests() {
        try {
            logger.info(String.format("%s waiting for connection", this.id));

            this.serverSocket = new ServerSocket(port);
            Thread listeningThread = new Thread(() -> {
                while (true) {
                    try {
                        Connection connection = new Connection(serverSocket.accept(), id);
                        logger.info(String.format("Peer %s established connection", this.id));

                        threadPool.execute( () -> {
                            boolean stillReceiving = true;

                            while (stillReceiving) {
                                try {
                                    byte[] message = connection.receive();
                                    if (message != null) {
                                        Packet packet = Packet.parseFrom(message);
                                        String type = packet.getType();

                                        if (type.equals("FILEREQ")){
                                            logger.info(String.format("Peer %s received a file request", this.id));

                                            String wantedFile = packet.getFileName();

                                            int chunkSize = 512000;
                                            int chunks = 0;
                                            byte[] data;

                                            while ((data = FileIO.readByOffset(wantedFile, chunks * chunkSize, chunkSize)) != null){
                                                connection.send(Packet.newBuilder()
                                                        .setType("FILECHUNK")
                                                        .setSequenceNum(chunks)
                                                        .setData(ByteString.copyFrom(data))
                                                        .build().toByteArray());
                                                chunks++;
                                            }


                                            connection.send(Packet.newBuilder()
                                                    .setType("DONE")
                                                    .build().toByteArray());
                                            stillReceiving = false;
                                            break;
                                        } else if (type.equals("UPDATE")) {
                                            logger.info("Got update from peer");
                                            fileSelection.update(SerializationUtils.deserialize(packet.getData().toByteArray())
                                                    ,packet.getHostLocation(), packet.getFileName());
                                            stillReceiving = false;
                                            break;
                                        }
                                    }

                                } catch (InvalidProtocolBufferException e) {
                                    System.out.println("error occurred in Peer");
                                }
                            }
                        });
                    } catch (IOException e) {
                        logger.info(String.format("Peer %s Could not establish connection", this.id));
                    }
                }
            });
            listeningThread.start();
        } catch (IOException e) {
            logger.info(String.format("Peer %s could not start listening for requests", this.id));
        }
    }


    /**
     * Method to stop sending and recieving
     * @return
     */
    public boolean close() {
        this.stillListening = false;
        threadPool.shutdown();
        return true;
    }


}
