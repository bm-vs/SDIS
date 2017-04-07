package SubProtocols;

import File.FileInfo;
import File.File;
import Server.Peer;
import Server.PeerId;

import java.net.InetAddress;
import java.net.MulticastSocket;

public class Restore extends SubProtocol implements Runnable{

    String filePath;

    public Restore(PeerId peerId, String filePath, MulticastSocket socket, InetAddress address, int port){
        super(peerId, filePath);
        this.socket = socket;
        this.address = address;
        this.port = port;
        this.filePath = filePath;
    }

    public void run(){
        FileInfo fileInfo;
        if((fileInfo = Peer.getRestorations().get(filePath)) == null){
            return;
        }

        for (int i = 0; i < fileInfo.totalChunks; i++) {
            //send to mcchannel the getchunk request
            String header = createHeader(i);

            Peer.sendToChannel(header, Peer.mcChannel);
            //append to file named in filepath
            try{
                Thread.sleep(1000);
            }catch(InterruptedException err){
                byte[] body = Peer.mdrChannel.getChunk(fileId, i);
                new Thread(new File(filePath, body)).run();
            }
        }
    }

    public String createHeader(int chunkNo){
        String common = super.getCommonHeader();
        String header = "GETCHUNK " + common + " " + chunkNo + " " + "\r\n\r\n";
        return header;
    }
}
