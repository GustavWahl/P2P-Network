package connection;

import java.io.*;
import java.net.Socket;

/**
 * Class connection
 * Implements Receiver and Sender
 */
public class Connection {
    private Socket socket;
    private int port;
    private boolean initiated = false;
    private DataInputStream dataInputStream;
    private DataOutputStream out;
    private String id;

    /**
     * Default constructor for Connection class
     * @param socket
     */
    public Connection(Socket socket, String id) {
        this.socket = socket;
        this.port = socket.getPort();
        this.id = id;
        try {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException exception) {
            System.out.println("IO exception getting socket IO channel");
        }
    }



    /**
     * Method to recieve message through socket
     * @return byte[] message
     */
    public byte[] receive() {
        // Used this code for reference https://stackoverflow.com/questions/1176135/socket-send-and-receive-byte-array
        try {
            int length = dataInputStream.readInt();

            if (length > 0) {
                byte[] message = new byte[length];
                dataInputStream.readFully(message, 0, message.length);
                return message;
            }

        } catch (IOException exception) {
            System.out.println("IO exception Recieve" + id);
            try {
                dataInputStream.close();
                socket.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.out.println("failed closing connection");
            }
        }
        return null;
    }

    /**
     * Method to send byte[] over sockets
     * @param message
     * @return boolean
     */
    public boolean send(byte[] message) {
        try {
            out.writeInt(message.length);
            out.write(message);
            out.flush();

            return true;
        } catch (IOException exception) {
            System.out.println("IO exception send");
            try {
                out.close();
                socket.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.out.println("failed closing connection");
            }
        }
        return false;
    }

    /**
     * get Scoket method
     * @return
     */
    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            out.close();
            dataInputStream.close();
        } catch (IOException exception) {
            System.out.println("IO exception closing stream");
        }
    }
}
