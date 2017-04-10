package subProtocols;

import java.io.DataInputStream;
import java.io.IOException;
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
    		privateChannel = new ServerSocket(222+Peer.peerId.id);
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
                if (new String(body).length() == 0) {                	
                	// get body from private channel
                	try {
                		Socket privateSocket = privateChannel.accept();
                		
                		DataInputStream dIn = new DataInputStream(privateSocket.getInputStream());
                		int length = dIn.readInt();
                		if(length > 0) {
                		    body = new byte[length];
                		    dIn.readFully(body, 0, body.length);
                		}
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
