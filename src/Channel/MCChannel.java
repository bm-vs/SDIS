package Channel;


import java.net.DatagramPacket;
import java.util.HashMap;

public class MCChannel extends Channel{

    //ChunkInfo has fileId and chunkNo to use as key
    //Reply has an array for saving the ones who sent the hashmap and comparing to replication degree
    //HashMap<ChunkInfo, Reply> replies;

    public MCChannel(int port, String address){
        super(port, address);
        //replies = new HashMap<>();
    }

    public void handle(DatagramPacket packet){
        super.handle(packet);

        String header = packetData[0];
        String[] headerFields = header.split(" +");
        switch(headerFields[0]){
            case "STORED":
                stored(headerFields);
                break;
            case "GETCHUNK":
                break;
            case "DELETE":
                break;
            case "REMOVED":
                break;
        }
    }

    public int getStoredMessages(int fileId, int chunkNo, int replDegree){
        //Use a hashmap with keys fileId chunkNo to save parallel files waiting for backup
        return 1;
    }

    public void stored(String[] args){
        int fileId = Integer.parseInt(args[3]);
        int chunkNo = Integer.parseInt(args[4]);
        int senderId = Integer.parseInt(args[2]);

        //if (replies.get(new ChunkInfo(fileId, chunkNo))== null){

        //}

    }
}
