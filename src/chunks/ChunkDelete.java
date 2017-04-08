package chunks;

import java.io.File;

public class ChunkDelete implements Runnable {
    String fileId;

    public ChunkDelete(String fileId){
        this.fileId = fileId;
    }

    public void run(){
        String folderName = "storage/" + fileId;
        File folder = new File(folderName);
        if(folder.isDirectory()){
            String[] chunks = folder.list();
            for (int i = 0; i < chunks.length; i++) {
                new File(folderName + "/" + chunks[i]).delete();
            }
            folder.delete();
        }
        System.out.println("Deleted " + fileId + " and all its chunks");
    }
}
