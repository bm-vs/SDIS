package Client;

import Server.PeerId;
import SubProtocols.Backup;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

//for testing purposes creates a putchunk server
public class TestClient {
    static int port;


    private Scanner scanner;

    public static void main(String[] args) throws UnknownHostException {
        port = Integer.parseInt(args[1]);
        int id = Integer.parseInt(args[2]);
        PeerId peer = new PeerId(id, args[3]);
        // Get the address that we are going to connect to.
        System.out.println("Started client");
        Scanner scanner = new Scanner(System.in);

        String file = "a.txt";  //needs to be in project root
        int repDeg = 1;

        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
        try {
            InetAddress address = InetAddress.getByName(args[0]);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(address);
            Thread t;
            Backup backup = new Backup(peer, 1, file, repDeg, socket, address, port, t);
            backup.transfer();
            socket.leaveGroup(address);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
