package SubProtocols;

import Server.Peer;

import java.io.RandomAccessFile;

public class SubProtocol {

    String filePath;

    protected String fileId;
    protected RandomAccessFile in;

    final static public String BACKUP = "BACKUP";
    final static public String RESTORE = "RESTORE";
    final static public String DELETE = "DELETE";
    final static public String REMOVED = "REMOVED";


    SubProtocol(String filePath){
        this.filePath = filePath;
    }

    public String getFileId(){
        return fileId;
    }

    String getCommonHeader(){
        return Peer.peerId.toString() + " " + fileId;
    }
}
