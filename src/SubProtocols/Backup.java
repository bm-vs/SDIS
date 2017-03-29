package SubProtocols;

import Client.TestClient;
import Server.Peer;
import Server.PeerId;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Backup extends SubProtocol implements Runnable {

    private final int MAX_SIZE = 64000;
    private final int SEND_REPEAT = 5;
    private int replDegree;
    RandomAccessFile in;
    MulticastSocket socket;
    InetAddress address;
    int port;

    public Backup(PeerId peerId, String filePath, int replDegree, MulticastSocket socket, InetAddress address, int port){
        super(peerId, filePath);
        this.replDegree = replDegree;
        this.socket = socket;
        this.address = address;
        this.port = port;
        try {
            in = new RandomAccessFile(filePath, "r");
        } catch (IOException err){
            System.err.println("File not found");
        }


    }

    public void run(){

        int i = 0, repeats = 0, confirmations;
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

            do{
                confirmations = 0;
                timeout *= 2;
                try{
                    Peer.mcChannel.startStoredCount(fileId, numChunks, replDegree);
                    socket.send(packet);
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException err){

                    }
                }catch (IOException err){

                }
                confirmations = Peer.mcChannel.getStoredMessages(fileId, numChunks);

            } while(confirmations < replDegree && repeats < SEND_REPEAT);
            if(confirmations >= replDegree){
                timeout = 500;
            }
        } while(i == 64000);

        System.out.println("Backup completed");
    }

    public String createHeader(int chunkNo){
        String common = super.getCommonHeader();
        String header = "PUTCHUNK " + common + " " + chunkNo + " " + replDegree + " \r\n\r\n";

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
