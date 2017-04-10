package file;

import chunks.ChunkId;
import chunks.ChunkInfo;
import server.Peer;
import utils.Utils;

import java.io.File;
import java.util.HashMap;

public class Disk {

    private static int maxSpace = 64000000;
    private static int usedSpace;

    public static int getAvailableSpace(){
        return maxSpace - usedSpace;
    }

    public static void analyzeUsedSpace(){
        int space = 0;
        File storage = new File(Utils.storage);
        if(!storage.exists()){
            usedSpace = 0;
            return;
        } else{
            File[] files = storage.listFiles();
            for (File fileId : files) {
                File[] chunks = fileId.listFiles();
                for (File chunk : chunks) {
                    space += chunk.length();
                }
            }
        }
        usedSpace = space;
    }

    public static boolean canDeleteChunks(int size){
        HashMap<ChunkId, ChunkInfo> replies = Peer.getReplies();
        int removable = getAvailableSpace();
        for (ChunkId id : replies.keySet()) {
            ChunkInfo info = replies.get(id);
            if(info.confirmations > info.replDegree){
                File file = new File(Utils.storage + "/" + id.getFileId() + "/" + id.getChunkNo());
                removable += file.length();
                if(removable >= size)
                    return true;
            }
        }
        return removable >= size;
    }

    public static void setMaxSpace(int space){
        maxSpace = space;
    }

    public static long getChunkSize(ChunkId chunkId){
        File file = new File(Utils.storage + "/" + chunkId.getFileId() + "/" + chunkId.getChunkNo());
        return file.length();
    }

    public static int getMaxSpace(){
        return maxSpace;
    }

    public static int getUsedSpace(){
        return usedSpace;
    }

    public static void occupy(int size){
        usedSpace -= size;
    }

    public static void free(long size){
        usedSpace += (int)size;
    }
}
