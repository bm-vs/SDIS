package SubProtocols;

import File.FileInfo;
import File.FileRestore;
import Server.Peer;

public class Restore extends SubProtocol implements Runnable{

    public Restore(String filePath){
        super(filePath);
    }

    public void run(){
        FileInfo fileInfo;
        if((fileInfo = Peer.getRestorations().get(filePath)) == null){
            return;
        }

        for (int i = 0; i < fileInfo.totalChunks; i++) {
            //send to mcchannel the getchunk request
            String header = createHeader(i);

            Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
            //append to file named in filepath
            try{
                Thread.sleep(1000);
            }catch(InterruptedException err){
                byte[] body = Peer.mdrChannel.getChunk(fileId, i);
                new Thread(new FileRestore(filePath, body)).run();
            }
        }
    }

    public String createHeader(int chunkNo){
        String common = super.getCommonHeader();
        String header = "GETCHUNK " + common + " " + chunkNo + " " + "\r\n\r\n";
        return header;
    }
}
