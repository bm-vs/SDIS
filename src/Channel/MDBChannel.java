package Channel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.util.Random;

import Chunks.ChunkId;
import Chunks.ChunkInfo;
import Chunks.ChunkSave;
import Header.Field;
import Header.Type;
import Server.Peer;
import Server.PeerId;

public class MDBChannel extends Channel{

    public MDBChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        if(Integer.parseInt(packetHeader[Field.senderId]) == Peer.peerId.id)
            return;
        String type = packetHeader[0];
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
        new Thread(store).run();

        int replDegree = Integer.parseInt(packetHeader[Field.replication]);


        //create entry in hashmap of replies
        Peer.addReply(new ChunkId(fileId, chunkNo), new ChunkInfo(replDegree, 1));

        //send response
        answer(Type.stored, fileId, packetHeader[Field.chunkNo]);
    }

    private void answer(String type, String fileId, String chunkNo){
        PeerId peerId= Peer.peerId;
        String message = Type.stored + " " +
                peerId.version + " " +
                peerId.id + " " +
                fileId + " " +
                chunkNo + " " +
                Field.cr + Field.lf +
                Field.cr + Field.lf;

        Peer.sendToChannel(message.getBytes(), Peer.mcChannel);
    }
}
