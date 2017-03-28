package Channel;

import java.net.DatagramPacket;

import Header.Field;
import Header.Type;
import Server.PeerId;

public class MDBChannel extends Channel{

    public MDBChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        String type = packetHeader[0];
        switch (type){
            case Type.putchunk:
                handlePUTCHUNK(packetHeader, packetBody);
        }
    }

    private void handlePUTCHUNK(String[] packetHeader, String body){
        //create backup using info in header


        //send response
        String fileId = packetHeader[Field.fileId];
        String chunkNo = packetHeader[Field.chunkNo];
        answer(Type.stored, fileId, chunkNo);
    }

    private void answer(String type, String fileId, String chunkNo){
        PeerId peerId= peer.peer;
        String message = Type.stored + " " +
                peerId.version + " " +
                peerId.id + " " +
                fileId + " " +
                chunkNo + " " +
                Field.cr + Field.lf +
                Field.cr + Field.lf;
        peer.sendToMC(message, peer.mcChannel);
    }
}
