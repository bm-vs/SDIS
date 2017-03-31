package Channel;

import Server.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Channel implements Runnable {
    public int port;
    public InetAddress address;
    MulticastSocket socket;
    String[] packetHeader;
    byte[] packetBody;
    Peer peer;

    public Channel(int port, String address){
        try {
            this.port = port;
            this.address = InetAddress.getByName(address);

        } catch(IOException err){
            System.err.println(err);
        }
    }

    public void run(){
        try {
            socket = new MulticastSocket(port);
            socket.joinGroup(this.address);
        } catch(IOException err){
            System.err.println(err);
        }
        byte[] buf = new byte[64000];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while(true){
            try {
                socket.receive(packet);
                handle(packet);
            } catch(IOException err){
                System.err.println(err);
            }
        }
    }

    public void handle(DatagramPacket packet){
        byte[] raw = packet.getData();
        String pac = new String(raw);
        String[] packetData = pac.split("\r\n\r\n", 2);
        packetHeader = packetData[0].split("\\s+");
        packetBody = Arrays.copyOfRange(raw, packetData[0].length()+4, raw.length);
    }

    public void startStoredCount(String fileId, int chunkNo, int replDegree){
    }

    public int getStoredMessages(String fileId, int chunkNo){
        return 5;
    }
}
