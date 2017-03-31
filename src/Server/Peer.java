package Server;

import Channel.*;

import Chunks.ChunkId;
import Chunks.ChunkInfo;
import Header.Type;
import SubProtocols.Backup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Scanner;

public class Peer implements RMIService {

	private static final String INDEXFILE = "index.txt";

    public static MulticastSocket socket;

    static HashMap<ChunkId, ChunkInfo> replies;

    //TODO
    //saves fileId and thread of the subprotocol process
    //when creating thread puts it into this hashmap
    static HashMap<String, Thread> protocols = new HashMap<>();

    public static String remObj;

    public static Channel mcChannel;
    public static Channel mdbChannel;
    public static Channel mdrChannel;

    public static PeerId peerId;

    public static Thread mcThread;
    public static Thread mdbThread;
    public static Thread mdrThread;

    public static void main(String[] args) {

        if(args.length != 9){
            printUsage(args);
            return;
        } else{
            init(args);
        }
        try {
            socket = new MulticastSocket();
        }catch(IOException err){
            System.err.println(err);
        }

        // Initiate RMI
        try {
            Peer peer = new Peer();
            RMIService stub = (RMIService) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(remObj, stub);
            System.out.println("Peer bound to remote object name: " + remObj);
        }
        catch(Exception e) {
            System.err.println("RMIService exception");
            e.printStackTrace();
        }
		
        replies = new HashMap<>();

        mcThread = new Thread(mcChannel);
        mdbThread = new Thread(mdbChannel);
        mdrThread = new Thread(mdrChannel);

        System.out.println("Initialized Peer " + args[1]);
        
        mcThread.start();
        mdbThread.start();
        mdrThread.start();
    }


    public static void init(String[] args){
        mcChannel = new MCChannel(Integer.parseInt(args[4]), args[3]);
        mdbChannel = new MDBChannel(Integer.parseInt(args[6]), args[5]);
        mdrChannel = new MDRChannel(Integer.parseInt(args[8]), args[7]);

        peerId = new PeerId(Integer.parseInt(args[1]), args[0]);
        remObj = args[2];
    }

    public static void sendToChannel(String message, Channel channel){
        byte[]buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buf, buf.length, channel.address, channel.port);
        try {
            socket.send(packet);
        }catch(IOException err){
            System.err.println(err);
        }
    }

    public static void wakeThread(String key){
        Thread t = protocols.get(key);
//        t.interrupt();
    }

		public static HashMap<ChunkId, ChunkInfo> getReplies() {
		return replies;
	}

	public static ChunkInfo getChunkInfo(ChunkId id) {
		return replies.get(id);
	}

	public static void addReply(ChunkId id, ChunkInfo info) {
		replies.put(id, info);
		saveRepliesToFile();
	}

	public static void deleteReply(ChunkId id) {
		replies.remove(id);
		saveRepliesToFile();
	}
	
	public static void saveRepliesToFile() {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(INDEXFILE);
			out = new BufferedWriter(fstream);
			
			for (ChunkId key: replies.keySet()) {
				out.write(key.getFileId() + " " + ((Integer) key.getChunkNo()).toString() + "\n");
				out.write(replies.get(key).replDegree + " " + replies.get(key).confirmations + "\n");
			}

			out.close();
		}
		catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

    private static void loadFile(){
        BufferedReader buffer;
        try {
             buffer = new BufferedReader(new FileReader(INDEXFILE));
        }catch(FileNotFoundException err){
            return;
        }
        String info, fileName;
        String[] splitInfo;
        ChunkInfo chunkInfo;
        do{
            try {
                fileName = buffer.readLine();
                if (fileName == null) return;

                info = buffer.readLine();
                if(info == null) return;
                splitInfo = info.split(" ");
                chunkInfo = new ChunkInfo(Integer.parseInt(splitInfo[0]), Integer.parseInt(splitInfo[1]));

            }catch(IOException err){
            }
        }while(true);
    }



    public static void printUsage(String[] args) {
        System.out.println("Wrong number of arguments");
        System.out.println("Usage: java Server.Peer version id RmiName mcAddress mcPort mdbAddress mdbPort mdrAddress mdrPort");
    }
    
    public boolean backup(String file, int replDegree) {
        Backup backup = new Backup(peerId, file, replDegree, socket, mdbChannel.address, mdbChannel.port);
        Thread t = new Thread(backup);
        protocols.put(backup.getFileId(), t);
        t.start();
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
