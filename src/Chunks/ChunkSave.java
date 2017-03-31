package Chunks;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkSave implements Runnable {
    String fileId;
    int chunkNo;
    byte[] body;
    final String storageFolder = "storage";

    public ChunkSave(String fileId, int chunkNo, byte[] body){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.body = body;
    }

    public void run() {
        try {
            File file = new File(storageFolder);
            if(!file.exists()){
                file.mkdir();
            }
            RandomAccessFile r = new RandomAccessFile(storageFolder + "/" + fileId + " " + chunkNo, "rw");
            r.write(body);
            r.close();
        }catch(IOException err){
            return;
        }
    }
}
