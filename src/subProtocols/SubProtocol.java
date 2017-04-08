package subProtocols;

public class SubProtocol {

    String filePath;

    protected String fileId;

    SubProtocol(String filePath){
        this.filePath = filePath;
    }

    public String getFileId(){
        return fileId;
    }

    static void waitBeforeSend(){

    }
}
