package chunks;

import server.Peer;
import utils.Utils;

import java.io.File;

public class ChunkDelete implements Runnable {
    private String fileId;

    public ChunkDelete(String fileId){
        this.fileId = fileId;
    }

    public void run(){
        String folderName = Utils.storage + "/" + fileId;
        File folder = new File(folderName);
        if(folder.isDirectory()){
            String[] chunks = folder.list();
            for (int i = 0; i < chunks.length; i++) {
                new File(folderName + "/" + chunks[i]).delete();
                Peer.deleteReply(new ChunkId(fileId, i));
            }
            folder.delete();
        }
        System.out.println("Deleted " + fileId + " and all its chunks");
    }
}
