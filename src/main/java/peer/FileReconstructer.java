package peer;

import fileIO.FileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FileReconstructer {
    private volatile ConcurrentHashMap<String, List<String>> fileChunkMap;
    private volatile ConcurrentHashMap<String, Integer> totalAmountChunks;

    public FileReconstructer() {
        this.fileChunkMap = new ConcurrentHashMap<>();
        this.totalAmountChunks = new ConcurrentHashMap<>();
    }

    public synchronized void put(String file, int amount) {
        totalAmountChunks.putIfAbsent(file, amount);
       // List<String> l = new ArrayList<String>(amount);
       // fileChunkMap.put(file, l);
    }

    public synchronized List<Integer> getIndexNum(String file) {
        List<Integer> chunks = new ArrayList<>();
        if (fileChunkMap.containsKey(file)) {
            if (fileChunkMap.get(file) != null) {
                fileChunkMap.get(file).forEach((element) -> {
                    if (!element.equals("")) {
                        chunks.add(Integer.parseInt(element.split(";")[0]));
                    }
                });
            }
        }
        return chunks;
    }

    public synchronized boolean gotAllChunks(String file) {
        if (fileChunkMap.containsKey(file) && totalAmountChunks.get(file) != null) {
            if (fileChunkMap.get(file).size() == totalAmountChunks.get(file)) {
              //  boolean allChunk = true;

              //  for (String f : fileChunkMap.get(file)) {
               //     if (f.equals("")) allChunk = false;
             //   }
            //    if (allChunk) {
                    totalAmountChunks.put(file, Integer.MAX_VALUE);
                    return true;
               // } return false;
            }
        }
        return false;
    }

    public synchronized void putChunk(String file, String chunkFile, int index, byte[] data, int maxIndex) {

        if (totalAmountChunks.get(file) != null) {
            if (totalAmountChunks.get(file) == Integer.MAX_VALUE) return;
        }

        if (fileChunkMap.containsKey(file)) {
            List<String> list = fileChunkMap.get(file);

        /*    if (list.size() < index) {
                for (int i = list.size(); i < index; i++) {
                    list.add("");
                }
            }*/

                if (!list.contains(chunkFile)) {
                    list.add(index, chunkFile);
                    FileIO.write(new File(chunkFile), data);
                }
        } else {
            List<String> chunkList = new ArrayList<>();
            chunkList.add(index, chunkFile);
            FileIO.write(new File(chunkFile), data);

            fileChunkMap.put(file, chunkList);
        }
    }


    public synchronized List<String> getAllTmpFiles(String file) {
        return fileChunkMap.get(file);
    }
}
