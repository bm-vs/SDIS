package SubProtocols;

import Server.Peer;
import Server.PeerId;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Backup extends SubProtocol {

    private final int MAX_SIZE = 64000;
    private final int SEND_REPEAT = 5;
    public String filePath;
    public int replDegree;
    RandomAccessFile in;
    MulticastSocket socket;
    InetAddress address;
    int port;
    Peer peer;
    Thread thread;

    public Backup(PeerId peer, int fileId, String filePath, int replDegree, MulticastSocket socket, InetAddress address, int port, Thread thread){
        super(peer, fileId);
        this.filePath = filePath;
        this.replDegree = replDegree;
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.thread = thread;
        try {
            in = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException err){
            System.err.println("File not found");
        }


    }

    public void transfer(){

        int i = 0, repeats = 0;
        int numChunks = 0, timeout = 500; //in miliseconds
        byte[] buf, body = new byte[MAX_SIZE];
        do {
            //read bytes from file
            try {
                i = in.read(body, 0, MAX_SIZE);
                numChunks++;
            } catch (IOException err) {
                System.err.println(err);
            }

            buf = createPacket(body, numChunks);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            int confirmations;

            do{
                timeout *= 2;
                try{
                    peer.mcChannel.startStoredCount(thread, fileId, numChunks, replDegree);
                    socket.send(packet);
                    try {
                        Thread.sleep(timeout);
                        socket.setSoTimeout(timeout);
                    } catch (InterruptedException err){
                    }
                }catch (IOException err){

                }
                confirmations = peer.mcChannel.getStoredMessages(fileId, numChunks);

            } while(confirmations < replDegree && repeats < SEND_REPEAT);

        } while(i == 64000 || repeats < SEND_REPEAT);
    }

    public String createHeader(int chunkNo){
        String common = super.getCommonHeader();
        String header = "PUTCHUNK " + common + " " + chunkNo + " " + replDegree + " CRLFCRLF";

        return header;
    };

    private byte[] createPacket(byte[]body, int numChunks){
        String header = createHeader(numChunks);
        byte[] headerArray = header.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(headerArray);
            outputStream.write(body);
        } catch(IOException err){
            System.err.println(err);
        }
        return outputStream.toByteArray();
    }
}
