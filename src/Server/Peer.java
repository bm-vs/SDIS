package Server;

import Channel.*;

import Header.Type;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Peer implements RMIService {

    public static MulticastSocket socket;
    HashMap<Integer, Map<Integer, byte[]>> storage = new HashMap<>();

    public static String remObj;

    public static Channel mcChannel;
    public static Channel mdbChannel;
    public static Channel mdrChannel;

    public static PeerId peer;

    public static Thread mcThread;
    public static Thread mdbThread;
    public static Thread mdrThread;

    public static void main(String[] args) {
    	
    	// Initiate RMI
    	try {
    		String name = "Peer";
    		Peer peer = new Peer();
    		RMIService stub = (RMIService) UnicastRemoteObject.exportObject(peer, 0);
    		Registry registry = LocateRegistry.getRegistry();
    		registry.rebind(name, stub);
    		System.out.println("Peer bound");
    	}
    	catch(Exception e) {
    		System.err.println("RMIService exception");
    		e.printStackTrace();
    	}
    	

//        if(args.length != 6){
//            printUsage(args);
//            return;
//        } else{
        init(args);
//        }
        try {
            socket = new MulticastSocket();
        }catch(IOException err){
            System.err.println(err);

        }
        mcThread = new Thread(mcChannel);
        mdbThread = new Thread(mdbChannel);
        mdrThread = new Thread(mdrChannel);

        System.out.println("Peer" + args[7]);
        
        mcThread.start();
        mdbThread.start();
        mdrThread.start();

    }


    public static void init(String[] args){
        mcChannel = new MCChannel(Integer.parseInt(args[1]), args[0]);
        mdbChannel = new MDBChannel(Integer.parseInt(args[3]), args[2]);
        mdrChannel = new MDRChannel(Integer.parseInt(args[5]), args[4]);

        peer = new PeerId(Integer.parseInt(args[7]), args[6]);
    }

    public static void sendToMC(String message, Channel channel){
        byte[]buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, channel.address, channel.port);
        try {
            socket.send(packet);
        }catch(IOException err){
            System.err.println(err);
        }
    }

    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println("Usage: ./peer mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
    
    public boolean backup() {
    	System.out.println("Backup");
    	return true;
    }
    
    public boolean restore() {
    	System.out.println("Restore");
    	return true;
    }
    
    public boolean delete() {
    	System.out.println("Delete");
    	return true;
    }
    
    public boolean reclaim() {
    	System.out.println("Reclaim");
    	return true;
    }
}
