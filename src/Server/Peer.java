package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Peer {

    public static MulticastSocket socket;
    static DatagramPacket packet;

    public static InetAddress address;

    public static int port;

    static byte[] buf = new byte[256];

    public static void main(String[] args) {

        if(args.length != 6){
            printUsage(args);
            return;
        } else{
            init(args);
        }
        boolean finish = false;
        do {
            try {
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //analyze

            } catch (IOException err) {
                err.printStackTrace();
            }
        } while(!finish);

        try{
            socket.leaveGroup(address);

        }catch (IOException err) {
            err.printStackTrace();
        }


    }

    public static void init(String[] args){
        try{
            address = InetAddress.getByName(args[1]);
            port = Integer.parseInt(args[2]);
            socket = new MulticastSocket(port);
            socket.joinGroup(address);

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println("Usage: ./peer mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
}
