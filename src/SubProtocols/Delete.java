package SubProtocols;


import File.FileInfo;
import Server.Peer;

public class Delete extends SubProtocol implements Runnable {

    public Delete(String filePath){
        super(filePath);
    }

    public void run(){
        FileInfo fileInfo = Peer.getRestorations().get(filePath);
        if(fileInfo == null){
            System.out.println("This file is not backed up in this domain ");
        }

        String header = DELETE + " " + Peer.peerId.toString() + " " + fileInfo.fileId + " \r\n\r\n";
        Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
    }
}
