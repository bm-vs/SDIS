package Channel;


import Chunks.ChunkId;
import Chunks.ChunkInfo;
import Header.Field;

import java.net.DatagramPacket;
import java.util.HashMap;

public class MCChannel extends Channel{

    //ChunkId has fileId and chunkNo to use as key
    //Reply has an array for saving the ones who sent the hashmap and comparing to replication degree
    HashMap<ChunkId, ChunkInfo> replies;

    public MCChannel(int port, String address){
        super(port, address);

        replies = new HashMap<>();
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);

        switch(packetHeader[Field.type]){
            case "STORED":
                storedMessage(packetHeader);
                break;
            case "GETCHUNK":
                break;
            case "DELETE":
                break;
            case "REMOVED":
                break;
        }
    }

    public void startStoredCount(String fileId, int chunkNo, int replDegree){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo value = new ChunkInfo(replDegree);
        if(replies.get(key) == null){
            replies.put(key, value);
        }
    }

    public int getStoredMessages(String fileId, int chunkNo){
        ChunkId key = new ChunkId(fileId, chunkNo);
        ChunkInfo c = replies.get(key);
        replies.remove(key);
        return c.confirmations;
    }

    public void storedMessage(String[] args){
        String fileId = args[3];
        int chunkNo = Integer.parseInt(args[4]);
        String senderId = args[2];
        ChunkId id = new ChunkId(fileId, chunkNo);
        ChunkInfo info;
        if ((info = replies.get(id)) != null){
            info.confirmations++;
            replies.put(id, info);

            if(info.confirmations >= info.replDegree){
                peer.wakeThread(fileId + " " + chunkNo);
            }
        }

    }
}
