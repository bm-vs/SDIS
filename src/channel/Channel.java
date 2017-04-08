package channel;

import header.Field;
import server.Peer;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Channel implements Runnable {
    public int port;
    public InetAddress address;
    private MulticastSocket socket;
    String[] packetHeader;
    byte[] packetBody;

    Channel(int port, String address) {
        try {
            this.port = port;
            this.address = InetAddress.getByName(address);

        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void run() {
        try {
            socket = new MulticastSocket(port);
            socket.joinGroup(this.address);
        } catch (IOException err) {
            err.printStackTrace();
        }
        byte[] buf = new byte[70000];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                socket.receive(packet);
                handle(packet);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    public void handle(DatagramPacket packet) {
        byte[] raw = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        String pac = new String(raw);
        String[] packetData = pac.split(Field.crlf+Field.crlf, 2);
        packetHeader = packetData[0].split("\\s+");
        int headerLength = packetData[0].length() + Field.crlf.length() + Field.crlf.length();
        packetBody = Arrays.copyOfRange(raw, headerLength, raw.length);
    }

    public void startStoredCount(String fileId, int chunkNo, int replDegree) {
    }

    public static String createHeader(String type, String fileId, int chunkNo, int replication){
        String header = type + " " + Peer.peerId.toString();
        if(fileId != null)
            header += " " + fileId;
        if(chunkNo != -1)
            header += " " + chunkNo;
        if(replication != -1)
            header += " " + replication;
        header += " " + Field.crlf + Field.crlf;
        return header;
    }

    public int getStoredMessages(String fileId, int chunkNo) {
        return 5;
    }

    public byte[] getChunk(String fileId, int chunkNo) {
        return new byte[0];
    }

    public void removeChunk(String fileId, int chunkNo) {}
}
