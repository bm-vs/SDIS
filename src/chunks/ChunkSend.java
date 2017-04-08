package chunks;


import channel.Channel;
import header.Type;
import server.Peer;
import utils.Utils;

import java.io.*;
import java.util.Arrays;

public class ChunkSend implements Runnable {
    private String fileId;
    private int chunkNo;
    private byte[] body = new byte[Utils.MAX_BODY];

    public ChunkSend(String fileId, int chunkNo){
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public void run() {
        String fileName = Utils.storage + "/" + fileId + "/" + chunkNo;
        File file = new File(fileName);
        if(!file.exists()){
            return;
        }
        byte[] buf = createPacket(fileName);

        Utils.waitTime();
        if(Peer.mdrChannel.getChunk(fileId, chunkNo) != null){
            Peer.mdrChannel.removeChunk(fileId, chunkNo);
            return;
        }
        Peer.sendToChannel(buf, Peer.mdrChannel);
    }

    private byte[] createPacket(String fileName){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            byte[] body = readChunk(fileName);
            String header = Channel.createHeader(Type.chunk, fileId, chunkNo, -1);
            byte[] headerArray = header.getBytes();
            outputStream.write(headerArray);
            outputStream.write(body);
        } catch(IOException err){
            err.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private byte[] readChunk(String fileName){
        try {

            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            int i = r.read(body);
            r.close();
            if(i != Utils.MAX_BODY){
                body = Arrays.copyOfRange(body, 0, i);
            }
            return body;
        }catch(IOException err){
            err.printStackTrace();
        }
        return null;
    }
}
