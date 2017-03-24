package Channel;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Channel {
    int port;
    InetAddress address;
    MulticastSocket socket;

    public Channel(int port, String address){
        try {
            this.port = port;
            this.address = InetAddress.getByName(address);

        } catch(IOException err){
            System.err.println(err);
        }
    }

    public void receive(){
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

    }
}
