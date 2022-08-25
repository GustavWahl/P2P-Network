package tracker;

import java.util.concurrent.ConcurrentHashMap;

public class SwarmList {
    private volatile ConcurrentHashMap<String, Swarm> swarmList;

    public SwarmList() {
        this.swarmList = new ConcurrentHashMap<>();
    }

    public Swarm getSwarm(String wantedFile) {
        return swarmList.get(wantedFile);
    }

    public synchronized void put(String wantedFile, Swarm swarm) {
        swarmList.putIfAbsent(wantedFile, swarm);
    }

    public boolean hasSeed(String wantedFile) {
        return swarmList.containsKey(wantedFile);
    }
}
