package SubProtocols;

import Server.PeerId;

import java.io.File;
import java.security.MessageDigest;

public class SubProtocol {

    protected PeerId peer;
    protected String fileId;

    public SubProtocol(PeerId peer){
        this.peer = peer;
    }

    public static String getFileId(String path) {
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

    public String getCommonHeader(){
        return peer.version + " " + peer.id + " " + fileId;
    }
}
