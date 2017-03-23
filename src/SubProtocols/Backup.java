package SubProtocols;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class Backup {

    private final int MAX_SIZE = 64000;
    private final int SEND_REPEAT = 5;
    public String filePath;
    public int replDegree;
    RandomAccessFile in;
    MulticastSocket socket;
    InetAddress address;
    int port;

    public Backup(String filePath, int replDegree, MulticastSocket socket, InetAddress address, int port){
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
        int timeout = 1000; //in miliseconds
        byte[] buf = new byte[MAX_SIZE];
        DatagramPacket receive = new DatagramPacket(buf, buf.length);
        String message;
        do {
            try {
                i = in.read(buf, 0, MAX_SIZE);
                System.out.println(i);
            } catch (IOException err) {

            }
            //join data with header


            //create packet
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            int confirmations;

            do{
                confirmations = 0;
                try{
                    socket.send(packet);
                    try {
                        socket.setSoTimeout(timeout);

                        //count confirmation messages during timeout after sending packet
                        while (confirmations < replDegree) {
                            socket.receive(receive);
                            message = new String(receive.getData());
                            if (getReply(message)) {
                                confirmations++;
                            }
                        }
                    } catch (SocketTimeoutException err){
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
}
