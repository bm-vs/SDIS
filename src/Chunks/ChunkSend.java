package Chunks;


import Server.Peer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class ChunkSend implements Runnable {
    String fileId;
    int chunkNo;
    byte[] body = new byte[64000];
    final String storageFolder = "storage";

    public ChunkSend(String fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void run() {
        try {
            String fileName = storageFolder + "/" + fileId + " " + chunkNo;
            File file = new File(fileName);
            if(!file.exists()){
                return;
            }
            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            r.read(body);
            r.close();

            //send through mdrChannel
            String header = "CHUNK " + Peer.peerId.toString() + " " + fileId + " " + chunkNo + " \r\n\r\n";

            byte[] headerArray = header.getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(headerArray);
                outputStream.write(body);
            } catch(IOException err){
                System.err.println(err);
            }

            //wait 0 to 400ms and check if someone already sent
            Random rnd = new Random();
            int time = rnd.nextInt(400);
            try {
                Thread.sleep(time);
            }catch(InterruptedException err){
                System.err.println(err);
            }
            if(Peer.mdrChannel.getChunk(fileId, chunkNo) == null){
                return;
            }

            Peer.sendToChannel(outputStream.toByteArray(), Peer.mdrChannel);
        }catch(IOException err){
            System.err.println(err);
        }
    }
}
