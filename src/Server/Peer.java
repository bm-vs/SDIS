package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Peer {

    public static MulticastSocket socket;
    static DatagramPacket packet;

    public static InetAddress mcAddress;
    public static InetAddress mdbAddress;
    public static InetAddress mdrAddress;

    public static int mcPort;
    public static int mdbPort;
    public static int mdrPort;

    public static String protVersion;
    public static String id;
    public static String remObj;


    static byte[] buf = new byte[64000];

    public static void main(String[] args) {

//        if(args.length != 6){
//            printUsage(args);
//            return;
//        } else{
            init(args);
//        }
        boolean finish = false;
        do {
            try {
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                int recPort = packet.getPort();
                String receive = new String(packet.getData(), 0, packet.getLength());
                System.out.println("message from port " + recPort);
                System.out.println(receive.length());
                System.out.println(receive);

                //analyze

            } catch (IOException err) {
                err.printStackTrace();
            }
        } while(!finish);

        try{
            socket.leaveGroup(mcAddress);

        }catch (IOException err) {
            err.printStackTrace();
        }


    }

    public static void init(String[] args){
        try{
            mcAddress = InetAddress.getByName(args[0]);
            mcPort = Integer.parseInt(args[1]);
            socket = new MulticastSocket(mcPort);
            socket.joinGroup(mcAddress);


            System.out.println("Joined group");

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println("Usage: ./peer mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
}
