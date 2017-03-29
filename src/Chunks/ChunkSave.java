package Chunks;


import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkSave implements Runnable {
    String fileId;
    int chunkNo;
    byte[] body;

    public ChunkSave(String fileId, int chunkNo, byte[] body){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.body = body;
    }

    public void run() {
        try {
            RandomAccessFile r = new RandomAccessFile(fileId + " " + chunkNo, "rw");
            r.write(body);
            r.close();
        }catch(IOException err){

            return;
        }

    }
}
