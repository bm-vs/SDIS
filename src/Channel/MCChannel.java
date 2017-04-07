package Channel;


import Chunks.ChunkDelete;
import Chunks.ChunkId;
import Chunks.ChunkInfo;
import Chunks.ChunkSend;
import Header.Field;
import Server.Peer;

import java.net.DatagramPacket;
import java.util.HashMap;

public class MCChannel extends Channel{

    //ChunkId has fileId and chunkNo to use as key
    //Reply has an array for saving the ones who sent the hashmap and comparing to replication degree

    public MCChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        if(Integer.parseInt(packetHeader[Field.senderId]) == Peer.peerId.id){
            return;
        }
        switch(packetHeader[Field.type]){
            case "STORED":
                storedMessage(packetHeader);
                break;
            case "GETCHUNK":
                new Thread(new ChunkSend(packetHeader[Field.fileId], Integer.parseInt(packetHeader[Field.chunkNo]))).start();
                break;
            case "DELETE":
                new Thread(new ChunkDelete(packetHeader[Field.fileId]));
                break;
            case "REMOVED":
                break;
        }
    }

    public void startStoredCount(String fileId, int chunkNo, int replDegree){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo value = new ChunkInfo(replDegree);
		
        if(Peer.getChunkInfo(key) == null){
            Peer.addReply(key, value);
        }
    }

    public int getStoredMessages(String fileId, int chunkNo){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo c = Peer.getChunkInfo(key);
        return c.confirmations;
    }

    public void storedMessage(String[] args){
        String fileId = args[3];
        int chunkNo = Integer.parseInt(args[4]);
        ChunkId id = new ChunkId(fileId, chunkNo);
        ChunkInfo info;
        if ((info = Peer.getChunkInfo(id)) != null){
            info.confirmations++;
            Peer.addReply(id, info);
        }

    }
}
