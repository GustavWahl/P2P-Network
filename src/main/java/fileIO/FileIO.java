package fileIO;

import peer.FileReconstructer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * FileIo class that handles file reads
 */
public class FileIO {

    /**
     * Method to read logs from file that producer sends
     * @param file
     * @param topic
     * @param sequence
     * @return
     */
    public static Queue<byte[]> readLogs(String file, String topic, int sequence) {
        Queue<byte[]> queue = new LinkedList<>();
        int s = sequence;
        // https://howtodoinjava.com/java/io/read-file-from-resources-folder/
        // used for reference to read from resource


        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(FileIO.class.getClassLoader().getResourceAsStream(file))))) {
            String newLine;
            while ((newLine = bufferedReader.readLine()) != null) {
                s++;
                queue.add(newLine.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.out.println("could not read logs");
        }
        return queue;
    }


    public static boolean write(File file, byte[] data) {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, true))){

            outputStream.write(data);
            return true;
        }  catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static boolean mergeTmpFiles(String file, FileReconstructer fr, int byteChunkSize) {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file, true))){
            for (String tmpFileName : fr.getAllTmpFiles(file)) {
                File tmp = new File(tmpFileName);

                if (tmp.exists()) {
                    InputStream inputStream = new BufferedInputStream(new FileInputStream(tmp));

                    byte[] data = inputStream.readAllBytes();

                    if (data != null) {
                        inputStream.close();
                        outputStream.write(data);
                    }

                    tmp.delete();
                }
            }

            return true;
        }  catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    // https://stackoverflow.com/questions/3405195/divide-array-into-smaller-parts/26695737
    // used above link as reference for method
    public static List<byte[]> read(String file, int byteChunkSize) {
        // File file = new File(fileName);
        // int numBytes = (int) file.length();
        //  int partitions = (int) Math.ceil(((double) numBytes / (double) byteChunkSize));

        // System.out.println(partitions);

        List<byte[]> data = new ArrayList<byte[]>();
        int count = 0;
        int bytesRead = 0;
        byte[] myBuffer = new byte[byteChunkSize];
        InputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(Objects.requireNonNull(FileIO.class.getClassLoader().getResourceAsStream(file)));
            while ((bytesRead = inputStream.read(myBuffer,0,byteChunkSize)) != -1) {
                //   write("test2.mp4", myBuffer);
                data.add(Arrays.copyOf(myBuffer, byteChunkSize));
            }

            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static int getFileLength(String file) {
        //return FileIO.class.getClassLoader().getResource(file).getFile().length();
        try {
            return ClassLoader.getSystemResource(file).openConnection().getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static byte[] readByOffset(String file, int skip, int byteChunkSize) {
        int bytesRead = 0;
        byte[] myBuffer = new byte[byteChunkSize];
        InputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(Objects.requireNonNull(FileIO.class.getClassLoader().getResourceAsStream(file)));
            inputStream.skip(skip);
            bytesRead = inputStream.read(myBuffer,0,byteChunkSize);

            if (bytesRead  != -1 ) {
                inputStream.close();

                return myBuffer;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }



    public static boolean checkIntegrity(File file1, File file2) {
        String cSum1 = getChecksum(file1);
        String cSum2 = getChecksum(file2);

        if (cSum1 == null || cSum2 == null) return false;

        return cSum1.equals(cSum2);
    }

    //reference
    //https://howtodoinjava.com/java/java-security/sha-md5-file-checksum-hash/
    public static String getChecksum(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            DigestInputStream inputStream = new DigestInputStream(new FileInputStream(file),digest);


            byte[] fileBytes = new byte[(int) file.length()];
            inputStream.read(fileBytes);
            inputStream.close();

            byte[] bytes = digest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getChunkChecksum(byte[] chunk) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(chunk).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean checkChunkIntegrity(byte[] chunk1, byte[] chunk2) {
        return getChunkChecksum(chunk1).equals(getChunkChecksum(chunk2));
    }
}
