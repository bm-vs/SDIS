package chunks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.util.Random;

import server.Peer;

public class ChunkReclaim implements Runnable {
	String fileId;
    int chunkNo;
	private final int SEND_REPEAT = 5;
	ChunkId id;
	ChunkInfo info;
    RandomAccessFile in;
    byte[] body = new byte[64000];
    final String storageFolder = "storage";
    
	public ChunkReclaim(ChunkId id, ChunkInfo info) {
        this.id = id;
        this.info = info;
    }
	
	public void run() {
		//wait 0 to 400ms and check if someone already sent
        Random rnd = new Random();
        int time = rnd.nextInt(400);
        try {
            Thread.sleep(time);
        } catch(InterruptedException err){
        	// if putchunk received end thread
            return;
        }
        
        try {
            String fileName = storageFolder + "/" + id.getFileId() + " " + id.getChunkNo();
            File file = new File(fileName);
            if(!file.exists()){
                return;
            }
            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            r.read(body);
            r.close();

            //send through mdbChannel
            String header = "PUTCHUNK " + Peer.peerId.toString() + " " + id.getFileId() + " " + id.getChunkNo() + info.replDegree + " \r\n\r\n";

            byte[] headerArray = header.getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(headerArray);
                outputStream.write(body);
            } catch(IOException err){
                System.err.println(err);
            }
            
            byte[] buf = outputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, Peer.mdbChannel.address, Peer.mdbChannel.port);
            
            int repeats = 0, timeout = 500, confirmations;
            do {
                repeats++;
                timeout *= 2;
                try {
                    Peer.mcChannel.startStoredCount(id.getFileId(), id.getChunkNo(), info.replDegree);
                    Peer.socket.send(packet);
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException err){
                        System.err.println(err);
                    }
                } catch (IOException err){
                    System.err.println(err);
                }
                confirmations = Peer.mcChannel.getStoredMessages(id.getFileId(), id.getChunkNo());

            } while(confirmations < info.replDegree && repeats < SEND_REPEAT);
            
            if(confirmations >= info.replDegree){
                System.out.println("Stored chunk with acceptable replication degree");
            } else{
                System.out.println("No answer");
            }
            
        }catch(IOException err){
            System.err.println(err);
        }
	}
}