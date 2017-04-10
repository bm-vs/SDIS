package chunks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Random;

import channel.Channel;
import header.Type;
import server.Peer;
import utils.Utils;

public class ChunkReclaim implements Runnable {
    String filePath;
    int replication;
    private final int SEND_REPEAT = 5;
    ChunkId id;
    private boolean original;
    byte[] body = new byte[64000];
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
        int time = rnd.nextInt(400);
        try {
            Thread.sleep(time);
        } catch (InterruptedException err) {
            // if putchunk received end thread unless enhancement is activated
            if(Peer.peerId.version.equals("1.1")){
                if(enhancement())
                    return;
                else run();

            }else return;
        }

        System.out.println("Started backup following removal of chunks by a connected peer");

        try {
            String fileName;
            if(original){
                fileName = filePath;
            }else {
                fileName = Utils.storage + "/" + id.getFileId() + "/" + id.getChunkNo();
            }
            int i;
            File file = new File(fileName);
            if (!file.exists()) {
                return;
            }
            RandomAccessFile r = new RandomAccessFile(fileName, "r");
            if(original)
                r.seek((id.getChunkNo()-1)*Utils.MAX_BODY);

            i = r.read(body);
            r.close();

            if(i != Utils.MAX_BODY && i != -1){
                body = Arrays.copyOfRange(body, 0, i);
            }

            //send through mdbChannel
            String header = Channel.createHeader(Type.putchunk, id.getFileId(), id.getChunkNo(), replication);

            byte[] headerArray = header.getBytes();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                outputStream.write(headerArray);
                outputStream.write(body);
            } catch(IOException err){
                System.err.println(err);
            }

            byte[] buf = outputStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, Peer.mdbChannel.address, Peer.mdbChannel.port);

            int repeats = 0, timeout = 500, confirmations;
            do {
                repeats++;
                timeout *= 2;
                try {
                    Peer.mcChannel.startStoredCount(id.getFileId(), id.getChunkNo(), replication);
                    Peer.socket.send(packet);
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException err){
                        System.err.println(err);
                    }
                } catch (IOException err){
                    System.err.println(err);
                }
                confirmations = Peer.mcChannel.getStoredMessages(id.getFileId(), id.getChunkNo());

            } while(confirmations < replication && repeats < SEND_REPEAT);

            if(confirmations >= replication){
                System.out.println("Stored chunk with acceptable replication degree");
            } else{
                System.out.println("No answer");
            }

        }catch(IOException err){
            System.err.println(err);
        }
    }

    private boolean enhancement(){
        Peer.addReply(id, new ChunkInfo(replication));
        try{
            Thread.sleep(this.time);
        }catch(InterruptedException err){

        }
        this.time *= 2;
        this.repeats++;
        int confirmations = Peer.getReplies().get(id).confirmations;
        return confirmations >= replication && this.repeats >= SEND_REPEAT;

    }
}