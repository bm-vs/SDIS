package SubProtocols;

import Header.Type;
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

    public Backup(PeerId peer, int fileId, String filePath, int replDegree, MulticastSocket socket, InetAddress address, int port){
        super(peer, fileId);
        this.filePath = filePath;
        this.replDegree = replDegree;
        this.socket = socket;
        this.address = address;
        this.port = port;
        try {
            in = new RandomAccessFile(filePath, "r");
        } catch (FileNotFoundException err){
            System.err.println("File not found");
        }


    }

    public void transfer(){

        int i = 0, repeats = 0;
        int numChunks = 0;
        int timeout = 1000; //in miliseconds
        byte[] body = new byte[MAX_SIZE];
        byte[] buf;
        DatagramPacket receive = new DatagramPacket(body, body.length);
        String message;
        do {
            try {
                i = in.read(body, 0, MAX_SIZE);
                numChunks++;
                System.out.println(i);
            } catch (IOException err) {
                System.err.println(err);
            }
            //join data with header
            String header = createHeader(numChunks);
            byte[] headerArray = header.getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(headerArray);
                outputStream.write(body);
            } catch(IOException err){
                System.err.println(err);
            }
            buf = outputStream.toByteArray();

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            int confirmations;

            do{
                confirmations = 0;
                try{
                    socket.send(packet);
                    try {
                        Thread.sleep(timeout);
                        socket.setSoTimeout(timeout);

                        //count confirmation messages during timeout after sending packet
                        while (confirmations < replDegree) {
                            socket.receive(receive);
                            message = new String(receive.getData());
                            if (getReply(message)) {
                                confirmations++;
                            }
                        }
                    } catch (InterruptedException err){
                        if(confirmations < replDegree) {
                            timeout *=2;
                            repeats++;
                        }
                    }
                }catch (IOException err){

                }


            } while(confirmations < replDegree && repeats < SEND_REPEAT);

        } while(i == 64000 || repeats < SEND_REPEAT);
    }

    //see if message is STORED
    public boolean getReply(String receive){
        return false;
    }


    public String createHeader(int chunkNo){
        String common = super.getCommonHeader();
        String header = "PUTCHUNK " + common + " " + chunkNo + " " + replDegree + " CRLFCRLF";

        return header;
    };
}
