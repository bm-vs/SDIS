package chunks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Random;

import channel.Channel;
import client.Service;
import header.Type;
import server.Peer;
import utils.Utils;

public class ChunkReclaim implements Runnable {
    private String filePath;
    private int replication;
    ChunkId id;
    private boolean original;
    private int time = 1000;
    private int repeats = 0;

    public ChunkReclaim(ChunkId id, ChunkInfo info) {
        this.id = id;
        this.replication = info.replDegree;
        this.original = false;
    }

    public ChunkReclaim(ChunkId id, int replDegree, String filePath) {
        this.id = id;
        this.replication = replDegree;
        this.filePath = filePath;
        this.original = true;
    }

    public void run() {
        //wait 0 to 400ms and check if someone already sent
        Random rnd = new Random();
        int time = rnd.nextInt(Utils.MAX_WAIT_TIME);
        try {
            Thread.sleep(time);
        } catch (InterruptedException err) {
            // if putchunk received end thread unless enhancement is activated
            if(Peer.peerId.version.equals(Utils.SPACE_ENHANCE) || Peer.peerId.version.equals(Utils.ALL_ENHANCE)){
                if(enhancement())
                    return;
                else run();

            }else return;
        }

        System.out.println("Started backup following removal of chunks by a connected peer");

        String fileName;
        if(original){
            fileName = filePath;
        }else {
            fileName = Utils.storage + "/" + id.getFileId() + "/" + id.getChunkNo();
        }
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        byte[] body = readFile(fileName);
        byte[] buf = createHeader(body);

        int repeats = 0, timeout = Utils.TIMER_START, confirmations;
        do{
            if(Utils.maxedRequests(++repeats)) return;
            timeout *= 2;
            Peer.mcChannel.startStoredCount(id.getFileId(), id.getChunkNo(), replication);
            Peer.sendToChannel(buf, Peer.mdbChannel);
            Utils.sleep(timeout);
            confirmations = Peer.mcChannel.getStoredMessages(id.getFileId(), id.getChunkNo());
        } while(confirmations < replication);

        System.out.println("Stored chunk " + id.getChunkNo() + " with acceptable replication degree: " + confirmations);
        Peer.saveStores(filePath, confirmations);
        Peer.deleteProtocol(Service.backup, id.getFileId());
    }

    private boolean enhancement(){
        Peer.addReply(id, new ChunkInfo(replication));
        try{
            Thread.sleep(this.time);
        }catch(InterruptedException err){}
        this.time *= 2;
        this.repeats++;
        int confirmations = Peer.getReplies().get(id).confirmations;
        return confirmations >= replication && this.repeats >= Utils.MAX_REPEAT;

    }

    private byte[] createHeader(byte[]body){
        String header = Channel.createHeader(Type.putchunk, id.getFileId(), id.getChunkNo(), replication);

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

    private byte[] readFile(String fileName){
        byte[] body = new byte[Utils.MAX_BODY];
        try {
            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            if (original)
                r.seek((id.getChunkNo() - 1) * Utils.MAX_BODY);
            int i = r.read(body);
            r.close();
            if (i != Utils.MAX_BODY && i != -1)
                body = Arrays.copyOfRange(body, 0, i);

        }catch(IOException err){
            err.printStackTrace();
        }
        return body;
    }
}