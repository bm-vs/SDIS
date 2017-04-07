package Chunks;

import java.io.File;

public class ChunkDelete implements Runnable {
    String fileId;

    public ChunkDelete(String fileId){
        this.fileId = fileId;
    }

    public void run(){
        File folder = new File("storage/" + fileId);
        if(folder.isDirectory()){
            String[] chunks = folder.list();
            for (int i = 0; i < chunks.length; i++) {
                new File(chunks[1]).delete();
            }
            folder.delete();
        }
    }
}
