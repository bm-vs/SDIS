package SubProtocols;

import Server.Peer;
import Server.PeerId;

import java.io.IOException;
import java.io.RandomAccessFile;
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
        String fileId;
        if((fileId = Peer.getRestorations().get(filePath)) == null){
            return;
        }
        
    }
}
