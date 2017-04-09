package subProtocols;


import channel.Channel;
import file.FileInfo;
import header.Type;
import server.Peer;

public class Delete extends SubProtocol implements Runnable {

    public Delete(String filePath){
        super(filePath);
        fileId = Peer.getRestorations().get(filePath).fileId;
    }

    public void run(){
        FileInfo fileInfo = Peer.getRestorations().get(filePath);
        if(fileInfo == null){
            System.out.println("This file is not backed up in this domain ");
            return;
        }
        Peer.deletePath(filePath);
        String header = Channel.createHeader(Type.delete, fileId, -1, -1);
        Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
    }
}
