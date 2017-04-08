package channel;


import java.net.DatagramPacket;
import java.util.Random;

import chunks.ChunkId;
import chunks.ChunkInfo;
import chunks.ChunkSave;
import header.Field;
import header.Type;
import server.Peer;

public class MDBChannel extends Channel{

    public MDBChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        if(Integer.parseInt(packetHeader[Field.senderId]) == Peer.peerId.id)
            return;
        String type = packetHeader[Field.type];
        switch (type){
            case Type.putchunk:
                handlePUTCHUNK(packetHeader, packetBody);
        }
    }

    private void handlePUTCHUNK(String[] packetHeader, byte[] body){
        //create backup using info in header
        String fileId = packetHeader[Field.fileId];
        int chunkNo = Integer.parseInt(packetHeader[Field.chunkNo]);
        int replDegree = Integer.parseInt(packetHeader[Field.replication]);
        ChunkSave store = new ChunkSave(fileId, chunkNo, replDegree, body);
        new Thread(store).start();
    }
}
