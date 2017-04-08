package header;

public class Header {

    public String type;
    public String version;
    public String senderId;
    public String fileId;
    public String chunkNo;
    public String replicationDeg;
    public final String crlf = "0xD0xA";

    public enum messageType {PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED};

    public Header(String[] headerFields){
        type = headerFields[0];
        version = headerFields[1];
        senderId = headerFields[2];
        chunkNo = headerFields[3];
        replicationDeg = headerFields[4];
    }

}
