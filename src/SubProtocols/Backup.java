package SubProtocols;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Backup {

    public final int MAX_SIZE = 64000;
    public String filePath;
    public int replDegree;
    BufferedReader in;
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
            in = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException err){
            System.err.println("File not found");
        }


    }

    public void transfer(){

        int i = 0;
        char[] buf = new char[MAX_SIZE];
        do {
            try {
                i = in.read(buf, 0, MAX_SIZE);
                System.out.println(i);
            } catch (IOException err) {

            }
            //join data with header


            //create packet
            String msg = new String(buf);
            byte[] byteArray = new byte[MAX_SIZE];
            byteArray = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, address, port);
            int confirmations;

            do{
                confirmations = 0;
                try{
                    socket.send(packet);
                }catch (IOException err){

                }

                //collect stored messages during one second after sending packet

            } while(confirmations < replDegree);

        } while(i == 64000);
    }
}
