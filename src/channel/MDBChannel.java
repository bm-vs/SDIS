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
        ChunkSave store = new ChunkSave(fileId, chunkNo, body);
        new Thread(store).start();
        int replDegree = Integer.parseInt(packetHeader[Field.replication]);

        //create entry in hashmap of replies
        Peer.addReply(new ChunkId(fileId, chunkNo), new ChunkInfo(replDegree, 1));

        try {
            Random rnd = new Random();
            Thread.sleep(rnd.nextInt(400));
        }catch(InterruptedException err){
            err.printStackTrace();
        }
        String header = Channel.createHeader(Type.stored, fileId, chunkNo, -1);
        Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
    }
}
