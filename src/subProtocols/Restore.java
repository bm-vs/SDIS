package subProtocols;

import channel.Channel;
import file.FileInfo;
import file.FileRestore;
import header.Type;
import server.Peer;

public class Restore extends SubProtocol implements Runnable{

    public Restore(String filePath){
        super(filePath);
        fileId = Peer.getRestorations().get(filePath).fileId;
    }

    public void run() {
        FileInfo fileInfo;
        if ((fileInfo = Peer.getRestorations().get(filePath)) == null) {
            return;
        }
        int i = 1;
        while (i <= fileInfo.totalChunks) {
            String header = Channel.createHeader(Type.getchunk, fileInfo.fileId, i, -1);
            Peer.sendToChannel(header.getBytes(), Peer.mcChannel);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException err) {
                byte[] body = Peer.mdrChannel.getChunk(fileId + i);
                new Thread(new FileRestore(filePath, body)).start();
                System.out.println("Chunk " + i + " transfered.");
                i++;
            }
        }
        System.out.println("Restore completed");
    }
}
