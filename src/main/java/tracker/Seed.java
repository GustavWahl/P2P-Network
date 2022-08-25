package tracker;

import java.io.Serializable;

public class Seed implements Serializable {
    private String location;
    private String file;
    private String checksum;
    private int numChunks;

    public Seed(String location, String file, int numChunks) {
        this.location = location;
        this.file = file;
        this.numChunks = numChunks;
     //   this.checksum = FileIO.getChecksum(new File(file));
    }

    public String getLocation() {
        return location;
    }

    public String getFile() {
        return file;
    }

    public String getChecksum() {
        return checksum;
    }
}
