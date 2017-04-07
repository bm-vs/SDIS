package SubProtocols;

import File.FileInfo;
import File.FileRestore;
import Server.Peer;

public class Restore extends SubProtocol implements Runnable{

    public Restore(String filePath){
        super(filePath);
        fileId = Peer.getRestorations().get(filePath).fileId;
    }

    public void run(){
        FileInfo fileInfo;
        if((fileInfo = Peer.getRestorations().get(filePath)) == null){
            return;
        }
        int i = 1;
        while(i <= fileInfo.totalChunks) {
            //send to mcchannel the getchunk request
            String header = createHeader(fileInfo.fileId, i);

            Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
            //append to file named in filepath
            try{
                Thread.sleep(10000);
            }catch(InterruptedException err){
                byte[] body = Peer.chunks.get(fileId + i);
                new Thread(new FileRestore(filePath, body)).start();
                System.out.println("Chunk " + i + " transfered.");
                i++;
            }

        }
        System.out.println("Restore completed");
    }

    public String createHeader(String fileId, int chunkNo){
        String common = super.getCommonHeader();
        String header = "GETCHUNK " + common + " " + fileId + " " + chunkNo + " " + "\r\n\r\n";
        return header;
    }
}
