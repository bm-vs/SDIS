package chunks;


import server.Peer;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class ChunkSend implements Runnable {
    private String fileId;
    private int chunkNo;
    private byte[] body = new byte[64000];
    private final String storageFolder = "storage";

    public ChunkSend(String fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void run() {
        String fileName = storageFolder + "/" + fileId + "/" + chunkNo;
        File file = new File(fileName);
        if(!file.exists()){
            return;
        }
        byte[] buf = createPacket(fileName);

        //wait 0 to 400ms and check if someone already sent
        try {
            Random rnd = new Random();
            Thread.sleep(rnd.nextInt(400));
        }catch(InterruptedException err){
            err.printStackTrace();
        }
        if(Peer.mdrChannel.getChunk(fileId + chunkNo) != null){
            Peer.mdrChannel.removeChunk(fileId, chunkNo);
            return;
        }
        Peer.sendToChannel(buf, Peer.mdrChannel);
    }

    byte[] createPacket(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            byte[] body = readChunk(fileName);
            String header = "CHUNK " + Peer.peerId.toString() + " " + fileId + " " + chunkNo + " \r\n\r\n";
            byte[] headerArray = header.getBytes();
            outputStream.write(headerArray);
            outputStream.write(body);
        } catch(IOException err){
            err.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    byte[] readChunk(String fileName){
        try {

            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            int i = r.read(body);
            r.close();
            if(i != 64000){
                body = Arrays.copyOfRange(body, 0, i);
            }
            return body;
        }catch(IOException err){
            err.printStackTrace();
        }
        return null;
    }
}
