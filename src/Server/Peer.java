package Server;

import Channel.*;

import Header.Type;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

public class Peer {

    public static MulticastSocket socket;
    static DatagramPacket packet;
    HashMap<Integer, Map<Integer, byte[]>> storage = new HashMap<>();

    public static InetAddress mcAddress;
    public static InetAddress mdbAddress;
    public static InetAddress mdrAddress;

    public static int mcPort;
    public static int mdbPort;
    public static int mdrPort;

    public static String protVersion;
    public static String id;
    public static String remObj;

    public static Channel mcChannel;
    public static Channel mdbChannel;
    public static Channel mdrChannel;


    static byte[] buf = new byte[64000];

    public static void main(String[] args) {

//        if(args.length != 6){
//            printUsage(args);
//            return;
//        } else{
            init(args);
//        }
        boolean finish = false;

        //new Thread(mcChannel.receive());

//        do {
//            try {
//                packet = new DatagramPacket(buf, buf.length);
//                socket.receive(packet);
//                int recPort = packet.getPort();
//                String receive = new String(packet.getData(), 0, packet.getLength());
//                System.out.println("message from port " + recPort);
//                System.out.println(receive.length());
//                System.out.println(receive);
//
//                //analyze
//                    //get header
//
//                    //put chunk in proper place of storage hashmap
//
//            } catch (IOException err) {
//                err.printStackTrace();
//            }
//        } while(!finish);

        try{
            socket.leaveGroup(mcAddress);

        }catch (IOException err) {
            err.printStackTrace();
        }


    }

    public static void init(String[] args){
            mcChannel = new MCChannel(Integer.parseInt(args[1]), args[0]);
            mdbChannel = new MDBChannel(Integer.parseInt(args[3]), args[2]);
            mdrChannel = new MDRChannel(Integer.parseInt(args[5]), args[6]);
    }

    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println("Usage: ./peer mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
}
