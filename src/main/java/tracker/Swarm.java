package tracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Swarm implements Serializable {
    private String file;
    private List<Seed> seedsList;
    private List<String> peerList;
    private int numChunks;

    private Swarm(String file, int numChunks) {
        this.file = file;
        this.seedsList = new ArrayList<>();
        this.peerList = new ArrayList<>();
        this.numChunks = numChunks;
    }

    public static Swarm createSwarm(String fileName, int numChunks) {
        return new Swarm(fileName, numChunks);
    }

    public synchronized void addPeerToSwarm(String hostLocation) {
        peerList.add(hostLocation);
    }

    public synchronized void addSeed(Seed seed) {
        peerList.remove(seed.getLocation());
        seedsList.add(seed);
    }

    public String getSwarmFile() {
        return file;
    }

    public List<Seed> getSeedsList() {
        return seedsList;
    }

    public List<String> getPeerList() {
        return peerList;
    }

    public int getNumChunks() {
        return numChunks;
    }
}
