package file;

import chunks.ChunkId;
import utils.Utils;

import java.io.File;

public class Disk {

    public static int maxSpace = 64000000;
    public static int usedSpace;

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

    public static void setMaxSpace(int space){
        maxSpace = space;
    }

    public static long getChunkSize(ChunkId chunkId){
        File file = new File(Utils.storage + "/" + chunkId.getFileId() + "/" + chunkId.getChunkNo());
        return file.length();
    }

    public static void occupy(int size){
        usedSpace -= size;
    }

    public static void free(long size){
        usedSpace += (int)size;
    }
}
