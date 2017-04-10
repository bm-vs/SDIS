package channel;


import chunks.ChunkDelete;
import chunks.ChunkId;
import chunks.ChunkInfo;
import chunks.ChunkReclaim;
import chunks.ChunkSend;
import file.FileInfo;
import header.Field;
import header.Type;
import server.Peer;
import utils.Utils;

import java.net.DatagramPacket;
import java.util.Set;

public class MCChannel extends Channel{

    //ChunkId has fileId and chunkNo to use as key
    //Reply has an array for saving the ones who sent the hashmap and comparing to replication degree

    public MCChannel(int port, String address){
        super(port, address);
    }

    public boolean handle(DatagramPacket packet){
        if(!super.handle(packet))
            return false;
        switch(packetHeader[Field.type]){
            case Type.stored:
                storedMessage(packetHeader);
                break;
            case Type.getchunk:
                new Thread(new ChunkSend(packetHeader[Field.fileId], Integer.parseInt(packetHeader[Field.chunkNo]))).start();
                break;
            case Type.delete:
                new Thread(new ChunkDelete(packetHeader[Field.fileId])).start();
                break;
            case Type.removed:
				removedChunk(packetHeader);
                break;
        }
        return true;
    }

    public synchronized void  startStoredCount(String fileId, int chunkNo, int replDegree){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo value = new ChunkInfo(replDegree);
		
        if(Peer.getChunkInfo(key) == null){
            Peer.addReply(key, value);
        }
    }

    public synchronized int getStoredMessages(String fileId, int chunkNo){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo c = Peer.getChunkInfo(key);
        Peer.deleteReply(key);
        return c.confirmations;
    }

    public synchronized void storedMessage(String[] args){
        String fileId = args[3];
        int chunkNo = Integer.parseInt(args[4]);
        ChunkId id = new ChunkId(fileId, chunkNo);
        ChunkInfo info;
        if ((info = Peer.getChunkInfo(id)) != null){
            info.confirmations++;
            Peer.addReply(id, info);
        }
    }

    public void resetStored(ChunkId chunkId){

    }
	
    public void removedChunk(String[] args) {
    	String fileId = args[Field.fileId];
    	int chunkNo = Integer.parseInt(args[Field.chunkNo]);


    	ChunkId id = new ChunkId(fileId, chunkNo);
    	ChunkInfo info;
    	Set<String> key;
        FileInfo fileInfo = new FileInfo(fileId, 0);
    	if ((info = Peer.getChunkInfo(id)) != null) {
    		info.confirmations--;
    		Peer.addReply(id, info);
    		
        	if (info.confirmations < info.replDegree) {
        		Thread t = new Thread(new ChunkReclaim(id, info));
        		Peer.addProtocol("CHUNKBACKUP", fileId + " " + chunkNo, t);
        		t.run();
        	}   		
    	}else if((key = Utils.getKeysByValue(Peer.getRestorations(), fileInfo)) != null){
            for (String filePath : key){
                fileInfo = Peer.getRestorations().get(filePath);
                fileInfo.removedReplication(chunkNo-1);
                if(!fileInfo.acceptableReplications(chunkNo-1)){
                    Thread t = new Thread(new ChunkReclaim(id, fileInfo.getDesiredReplication(), filePath));
                    //Peer.addProtocol("CHUNKBACKUP", fileId + " " + chunkNo, t);
                    t.start();
                }
            }
        }
    }
}
