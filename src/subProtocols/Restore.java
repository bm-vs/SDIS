package subProtocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import channel.Channel;
import file.FileInfo;
import file.FileRestore;
import header.Type;
import server.Peer;

public class Restore extends SubProtocol implements Runnable{

    public Restore(String filePath){
        super(filePath);
        fileId = Peer.getRestorations().get(filePath).fileId;
    }

    public void run() {
        FileInfo fileInfo;
        if ((fileInfo = Peer.getRestorations().get(filePath)) == null) {
            return;
        }
        int i = 1;
        
        // start private tcp channel
        ServerSocket privateChannel;
    	try {
    		InetAddress addr = InetAddress.getByName("224.0." + Peer.peerId.id + ".0");
    		privateChannel = new ServerSocket(6789, 50, addr);
    	}
    	catch (IOException err){
    		err.printStackTrace();
    		return;
    	}
        
        while (i <= fileInfo.chunksReplicated.size()) {
            String header = Channel.createHeader(Type.getchunk, fileInfo.fileId, i, -1);
            Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException err) {
                byte[] body = Peer.mdrChannel.getChunk(fileId, i);
                if (body.length == 0) {
                	// get body from private channel
                	try {
                		Socket privateSocket = privateChannel.accept();
                		BufferedReader fromClient = new BufferedReader(new InputStreamReader(privateSocket.getInputStream()));
                		body = fromClient.readLine().getBytes();
                	}
                	catch (IOException err2){
                		err2.printStackTrace();
                	}
                }
                
                new Thread(new FileRestore(filePath, body)).start();
                System.out.println("Chunk " + i + " transfered.");
                i++;
            }
        }
        
        try {
        	privateChannel.close();
        }
        catch (IOException err3){
    		err3.printStackTrace();
    	}
        System.out.println("Restore completed");
    }
}
