package subProtocols;

import channel.Channel;
import file.FileInfo;
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
        int i = 0, repeats, confirmations;
        int timeout = 500; //in miliseconds
        byte[] buf, body = new byte[Utils.MAX_BODY];
        try {
            in = new RandomAccessFile(filePath, "r");
        } catch (IOException err){
            System.err.println("File not found");
            return;
        }

        System.out.println("Backup initiated");
        if(numChunks != 0){
            System.out.println("Continuing backup from system failure. Proceeding from chunk no: " + numChunks);
            try {
                in.seek(Utils.MAX_BODY * numChunks);
            }catch(IOException err){
                err.printStackTrace();
            }
        }

        if(Peer.getRestorations().get(filePath) == null)
            Peer.savePath(filePath, fileId, replDegree);
        do {
            //read bytes from file
            try {
                i = in.read(body);
                numChunks++;
            } catch (IOException err) {
                err.printStackTrace();
            }
            if(i != Utils.MAX_BODY && i != -1){
                body = Arrays.copyOfRange(body, 0, i);
            }

            buf = createPacket(body, numChunks);
            repeats = 1;
            do{
                System.out.println("Sending chunk no: " + numChunks + ". Try no: " + repeats);
                if(++repeats > Utils.MAX_REPEAT){
                    System.out.println("Maximum number of tries exceeded. Failed to receive acceptable stored messages at chunk no: " + numChunks);
                    Peer.deletePath(filePath);
                    return;
                }
                timeout *= 2;
                Peer.mcChannel.startStoredCount(fileId, numChunks, replDegree);
                Peer.sendToChannel(buf, Peer.mdbChannel);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException err){
                    err.printStackTrace();
                }

                confirmations = Peer.mcChannel.getStoredMessages(fileId, numChunks);

            } while(confirmations < replDegree);
            if(confirmations >= replDegree){
                timeout = 500;
                System.out.println("Stored chunk " + numChunks + " with acceptable replication degree: " + confirmations);
                Peer.saveStores(filePath, confirmations);
            } else{
                System.out.println("No answer. Repeating chunk:" + numChunks);
            }
        } while(i == Utils.MAX_BODY);

        System.out.println("Backup completed");
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
