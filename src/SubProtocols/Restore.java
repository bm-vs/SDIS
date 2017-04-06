package SubProtocols;

import Server.PeerId;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Restore extends SubProtocol implements Runnable{
    public Restore(PeerId peerId, String filePath, MulticastSocket socket, InetAddress address, int port){
        super(peerId, filePath);
        this.socket = socket;
        this.address = address;
        this.port = port;

    }

    public void run(){
        //get file info from txt file
        //txt file has path last modified and length
    }
}
