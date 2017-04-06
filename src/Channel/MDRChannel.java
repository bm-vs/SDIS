package Channel;


import Header.Field;
import Header.Type;
import Server.Peer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;

public class MDRChannel extends Channel {



    public MDRChannel(int port, String address){
        super(port, address);
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);
        if(Integer.parseInt(packetHeader[Field.senderId]) == Peer.peerId.id)
            return;
        String type = packetHeader[0];
        switch (type){
            case Type.chunk:
                handleCHUNK(packetHeader, packetBody);
        }
    }

    private void handleCHUNK(String[] packetHeader, byte[]body){
        //TODO
        //verify header for fileID and chunkNo
        try {
            RandomAccessFile file= new RandomAccessFile("ola", "rw");
            long length = file.length();
            file.seek(length);
            file.write(body);
            file.close();
        }catch(IOException err){
        }
    }

}
