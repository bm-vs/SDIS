package Channel;


import Header.Field;
import Header.Type;
import Server.Peer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.util.HashMap;

public class MDRChannel extends Channel {

    private HashMap<String, byte[]> chunks = new HashMap<>();

    public MDRChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        if(Integer.parseInt(packetHeader[Field.senderId]) == Peer.peerId.id)
            return;
        String type = packetHeader[Field.type];
        switch (type){
            case Type.chunk:
                handleCHUNK(packetHeader, packetBody);
        }
    }

    private void handleCHUNK(String[] packetHeader, byte[]body){
        //TODO
        //verify header for fileID and chunkNo
        String key = "RESTORE " + packetHeader[Field.fileId];
        if(Peer.threadExists(key)) {
            //put byte array to hashmap and wake up restore thread
            chunks.put(packetHeader[Field.fileId] + packetHeader[Field.chunkNo], body);
            Peer.wakeThread(key);
        } else{
            chunks.put(packetHeader[Field.fileId] + packetHeader[Field.chunkNo], new byte[0]);
        }
    }

    public byte[] getChunk(String fileId, int chunkNo) {
        return chunks.get(fileId + chunkNo);
    }
}
