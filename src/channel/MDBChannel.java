package channel;


import java.net.DatagramPacket;

import chunks.ChunkSave;
import header.Field;
import header.Type;
import server.Peer;

public class MDBChannel extends Channel{

    public MDBChannel(int port, String address){
        super(port, address);
    }

    public boolean handle(DatagramPacket packet){
        if(!super.handle(packet))
            return false;
        String type = packetHeader[Field.type];
        switch (type){
            case Type.putchunk:
                handlePUTCHUNK(packetHeader, packetBody);
        }
        return true;
    }

    private void handlePUTCHUNK(String[] packetHeader, byte[] body){
        //create backup using info in header
        String fileId = packetHeader[Field.fileId];
        int chunkNo = Integer.parseInt(packetHeader[Field.chunkNo]);
        int replDegree = Integer.parseInt(packetHeader[Field.replication]);
        
        // If chunk backup pending from reclaim
        String thread_id = "CHUNKBACKUP" + " " + fileId + " " + chunkNo;
        if (Peer.threadExists(thread_id)) {
        	Peer.wakeThread(thread_id);
        }
        
        ChunkSave store = new ChunkSave(fileId, chunkNo, replDegree, body);
        new Thread(store).start();
    }
}
