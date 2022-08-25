package Main;

import fileIO.FileIO;
import peer.Peer;
import tracker.Tracker;


//import protos.Log;


/**
 * Class driver that handles setting up the different host
 */
public class Driver {

    /**
     * Main method, entry point for execution
     * handles starting all the different host based on config file
     * @param args
     */
    public static void main(String[] args) {
       /* String conf = args[0];
        int port = Integer.parseInt(args[1]);
        String id = args[2];
        String trackerLoc = args[3];

        if (conf.equals("leacher")) {
            Peer peer2 = new Peer(id, port, "127.0.0.1", trackerLoc);
            peer2.startAcceptingRequests();
            peer2.startRequestingSeed("testVideo.mp4");

        } else if (conf.equals("seed")) {
            Peer peer1 = new Peer(id, port, "127.0.0.1", trackerLoc);
            peer1.startAcceptingRequests();
            peer1.sendSeed("testVideo.mp4");
            peer1.startRequestingSeed("veryLongVideo2.mp4");

        } else if (conf.equals("seed2")) {
            Peer peer3 = new Peer(id, port, "127.0.0.1", trackerLoc);
            peer3.startAcceptingRequests();
            peer3.sendSeed("veryLongVideo2.mp4");
            peer3.startRequestingSeed("testVideo.mp4");
        }
        else if (conf.equals("tracker")) {
            Tracker tracker = new Tracker("TRACKER", port);
            tracker.startAcceptingRequests();
        }*/ //127.0.0.1:8080

        Peer peer1 = new Peer("peer1", 8081, "127.0.0.1", "127.0.0.1:8080");
        Peer peer2 = new Peer("peer2", 8082, "127.0.0.1", "127.0.0.1:8080");
        Peer peer3 = new Peer("peeer3", 8083, "127.0.0.1", "127.0.0.1:8080");
        Peer peer4 = new Peer("peeer4", 8084, "127.0.0.1", "127.0.0.1:8080");
        Peer peer5 = new Peer("peeer5", 8085, "127.0.0.1", "127.0.0.1:8080");
        Peer peer6 = new Peer("peeer6", 8086, "127.0.0.1", "127.0.0.1:8080");

        peer1.startAcceptingRequests();
        peer2.startAcceptingRequests();
        peer3.startAcceptingRequests();

        Tracker tracker = new Tracker("TRACKER", 8080);
        tracker.startAcceptingRequests();

        peer2.sendSeed("testVideo.mp4");
        peer1.sendSeed("veryLongVideo2.mp4");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        peer1.startRequestingSeed("testVideo.mp4");
        peer3.startRequestingSeed("testVideo.mp4");
        peer2.startRequestingSeed("veryLongVideo2.mp4");


    }
}
