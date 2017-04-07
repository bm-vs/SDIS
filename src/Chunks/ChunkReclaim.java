package Chunks;

import java.io.RandomAccessFile;
import java.util.Random;

public class ChunkReclaim implements Runnable {
	String fileId;
    int chunkNo;
    RandomAccessFile in;
	
	public ChunkReclaim(String fileId, int chunkNo) {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }
	
	public void run() {
		//wait 0 to 400ms and check if someone already sent
        Random rnd = new Random();
        int time = rnd.nextInt(400);
        try {
            Thread.sleep(time);
        } catch(InterruptedException err){
            System.err.println(err);
        }
	
	     // if putchunk received
			// end thread
	     // else
	     	// new PutChunk
	}
}
