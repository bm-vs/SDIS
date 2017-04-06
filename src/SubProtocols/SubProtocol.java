package SubProtocols;

import Server.PeerId;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.MessageDigest;

public class SubProtocol {

    protected PeerId peer;
    protected String fileId;
    protected RandomAccessFile in;
    protected MulticastSocket socket;
    protected InetAddress address;
    protected int port;

    SubProtocol(PeerId peer, String filePath){
        this.peer = peer;
        fileId = getFileId(filePath);

    }

    protected static String getFileId(String path) {
        File file = new File(path);
        String base = path + file.lastModified() + file.length();

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public String getFileId(){
        return fileId;
    }

    protected String getCommonHeader(){
        return peer.version + " " + peer.id + " " + fileId;
    }
}
