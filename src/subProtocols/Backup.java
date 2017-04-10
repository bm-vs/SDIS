package subProtocols;

import channel.Channel;
import client.Service;
import header.Type;
import server.Peer;
import utils.Utils;

import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;

public class Backup extends SubProtocol implements Runnable {

    private int replDegree;
    private RandomAccessFile in;
    private int numChunks;

    public Backup(String filePath, int replDegree){
        super(filePath);
        fileId = getFileId(filePath);
        this.replDegree = replDegree;
        this.numChunks = 0;
        this.filePath = filePath;
        Peer.deletePath(filePath);
    }

    public Backup(String filePath, int replDegree, String fileId, int chunks){
        super(filePath);
        this.fileId = fileId;
        this.replDegree = replDegree;
        this.numChunks = chunks;
        this.filePath = filePath;
    }

    public void run(){
        int i, confirmations;
        byte[] buf, body;

        if(!openFile()){
            Peer.deleteProtocol(Service.backup, fileId);
            return;
        }
        System.out.println("Backup initiated");
        continuation();
        if(Peer.getRestorations().get(filePath) == null)
            Peer.savePath(filePath, fileId, replDegree);

        do {
            body = readFile();
            i = body.length;
            int timeout = Utils.TIMER_START, repeats = 1;
            numChunks++;
            buf = createPacket(body, numChunks);

            do{
                if(Utils.maxedRequests(++repeats)){
                    Peer.deletePath(filePath);
                    return;
                }
                timeout *= 2;
                Peer.mcChannel.startStoredCount(fileId, numChunks, replDegree);
                Peer.sendToChannel(buf, Peer.mdbChannel);
                Utils.sleep(timeout);
                confirmations = Peer.mcChannel.getStoredMessages(fileId, numChunks);
            } while(confirmations < replDegree);

            System.out.println("Stored chunk " + numChunks + " with acceptable replication degree: " + confirmations);
            Peer.saveStores(filePath, confirmations);
        } while(i == Utils.MAX_BODY);
        System.out.println("Backup completed");
        Peer.deleteProtocol(Service.backup, fileId);
    }



    private byte[] createPacket(byte[]body, int numChunks){
        String header = Channel.createHeader(Type.putchunk, fileId, numChunks, replDegree);
        byte[] headerArray = header.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(headerArray);
            outputStream.write(body);
        } catch(IOException err){
            err.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    private boolean openFile(){
        try {
            in = new RandomAccessFile(filePath, "r");
            return true;
        } catch (IOException err){
            System.err.println("File not found");
            Peer.deleteProtocol(Service.backup, fileId);
            return false;
        }
    }

    private byte[] readFile(){
        int i = 0;
        byte[] body = new byte[Utils.MAX_BODY];
        try {
            i = in.read(body);
        } catch (IOException err) {
            err.printStackTrace();
        }
        if(i != Utils.MAX_BODY && i != -1){
            body = Arrays.copyOfRange(body, 0, i);
        }
        return body;
    }

    private void continuation(){
        if(numChunks != 0){
            System.out.println("Continuing backup from system failure. Proceeding from chunk no: " + numChunks);
            try {
                in.seek(Utils.MAX_BODY * numChunks);
            }catch(IOException err){
                err.printStackTrace();
            }
        }
    }

    private static String getFileId(String path) {
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
}
