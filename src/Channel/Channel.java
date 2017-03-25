package Channel;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Channel implements Runnable {
    int port;
    InetAddress address;
    MulticastSocket socket;
    String[] packetData;

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
                System.out.println("Im here");
                socket.receive(packet);

                handle(packet);
            } catch(IOException err){
                System.err.println(err);
            }
        }
    }

    public void handle(DatagramPacket packet){
        String pac = new String(packet.getData());
        packetData = pac.split("CRLFCRLF", 2);
        String header = packetData[0];
    }
}
