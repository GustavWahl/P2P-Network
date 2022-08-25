package peer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileSelection {
    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<Integer>>> peerUpdates;

    public FileSelection() {
        this.peerUpdates = new ConcurrentHashMap<>();
    }

    public synchronized void update(List<Integer> chunkData, String peerLocation, String file) {
        if (peerUpdates.containsKey(file)) {
            if (peerUpdates.get(file).containsKey(peerLocation)) {
                peerUpdates.get(file).put(peerLocation, chunkData);
            } else {
                peerUpdates.get(file).put(peerLocation, chunkData);
            }
        } else {
            ConcurrentHashMap<String, List<Integer>> m = new ConcurrentHashMap<>();
            m.put(peerLocation, chunkData);
            peerUpdates.put(file,m);
        }
    }

    public synchronized SelectedPeer select(String file) {
        if (peerUpdates.isEmpty()) return null;
        if (peerUpdates.get(file) == null) {
            return null;
        } else {
            if (peerUpdates.get(file).isEmpty()) {
                return null;
            } else {
                HashMap<Integer, List<SelectedPeer>> map = new HashMap();

                for (Map.Entry<String,List<Integer>> entry : peerUpdates.get(file).entrySet()) {
                    String peer = entry.getKey();

                    for (Integer chunk: entry.getValue()) {
                        if (map.containsKey(chunk)) {
                            map.get(chunk).add(new SelectedPeer(peer, chunk));
                        } else {
                            List<SelectedPeer> l = new ArrayList<>();
                            l.add(new SelectedPeer(peer, chunk));
                            map.put(chunk, l);
                        }
                    }
                }

                int min = Integer.MAX_VALUE;
                int key = 0;

                for (Map.Entry<Integer, List<SelectedPeer>> entry : map.entrySet()) {
                    int count = entry.getValue().size();

                    if (count < min) {
                        min = count;
                        key = entry.getKey();
                    }
                }
                List<SelectedPeer> candidates = map.get(key);
                Random random = new Random();

                return candidates.get(random.nextInt(candidates.size()));
            }
        }
    }

    static class SelectedPeer {
        public String location;
        public int chunkIndex;

        public SelectedPeer(String loc, int chunk) {
            this.chunkIndex = chunk;
            this.location = loc;
        }
    }
}
