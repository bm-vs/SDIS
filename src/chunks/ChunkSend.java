package chunks;


import channel.Channel;
import header.Field;
import header.Type;
import server.Peer;
import utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ChunkSend implements Runnable {
	private String senderId;
    private String fileId;
    private int chunkNo;
    private byte[] body = new byte[Utils.MAX_BODY];

    public ChunkSend(String senderId, String fileId, int chunkNo){
        this.senderId = senderId;
    	this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void run() {
        String fileName = Utils.storage + "/" + fileId + "/" + chunkNo;
        File file = new File(fileName);
        if(!file.exists()){
            return;
        }
        
        Utils.waitTime();
        if(Peer.mdrChannel.getChunk(fileId, chunkNo) != null){
            Peer.mdrChannel.removeChunk(fileId, chunkNo);
            return;
        }
        
        if (Integer.parseInt(Peer.peerId.version) == 2) {
        	Socket privateSocket;
        	try {
        		privateSocket = new Socket("224.0." + Integer.parseInt(senderId) + ".0", 6789);
        	}
        	catch (IOException e){
        		byte[] buf = createPacket(fileName);
            	Peer.sendToChannel(buf, Peer.mdrChannel);
            	return;
        	}
        	
        	try {
	    		byte[] header = createEmptyPacket(fileName);
	        	byte[] body = readChunk(fileName);
	        	Peer.sendToChannel(header, Peer.mdrChannel);
	        	
	        	// send body through private channel
	        	DataOutputStream toServer = new DataOutputStream(privateSocket.getOutputStream());
	        	toServer.writeBytes(body.toString());
	        	
	        	// disconnect
	        	privateSocket.close();
        	}
        	catch (IOException e){
        		e.printStackTrace();
        	}
        }
        else {
        	byte[] buf = createPacket(fileName);
        	Peer.sendToChannel(buf, Peer.mdrChannel);
        }
    }

    private byte[] createPacket(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            byte[] body = readChunk(fileName);
            String header = Channel.createHeader(Type.chunk, fileId, chunkNo, -1);
            byte[] headerArray = header.getBytes();
            outputStream.write(headerArray);
            outputStream.write(body);
        } catch(IOException err){
            err.printStackTrace();
        }
        return outputStream.toByteArray();
    }
    
    private byte[] createEmptyPacket(String filename) {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	try{
    		String header = Channel.createHeader(Type.chunk, fileId, chunkNo, -1);
            byte[] headerArray = header.getBytes();
            outputStream.write(headerArray);
    	} catch(IOException err){
            err.printStackTrace();
        }
        return outputStream.toByteArray();
    }   
    
    private byte[] readChunk(String fileName){
        try {

            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            int i = r.read(body);
            r.close();
            if(i != Utils.MAX_BODY){
                body = Arrays.copyOfRange(body, 0, i);
            }
            return body;
        }catch(IOException err){
            err.printStackTrace();
        }
        return null;
    }
}
