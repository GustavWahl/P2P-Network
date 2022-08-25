package tracker;

import java.util.ArrayList;
import java.util.List;

public class SeedList {
    private List<String> peerList;

    public SeedList(){
        this.peerList = new ArrayList();
    }

    public boolean isInList(String data) {
        return peerList.contains(data);
    }

    public boolean hasSeed(String wantedFile) {
        return peerList.contains(wantedFile);
    }

    public void addSeed(Seed seed) {

    }

    public List<Seed> getAllSeeds(String wantedFile) {
        return null;
    }
}
