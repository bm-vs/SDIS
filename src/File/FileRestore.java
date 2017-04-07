package File;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * For restore, writes the byte array received to file in filePath
 */
public class FileRestore implements Runnable {

    private byte[] body;
    private String filePath;

    public FileRestore(String file, byte[] body){
        this.body = body;
        this.filePath = file;
    }

    public void run(){
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            file.seek(file.length());
            file.write(body);
            file.close();
        }catch(IOException err){
            System.err.println(err);
        }
    }
}
