package Client;

import SubProtocols.Backup;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    static int port;


    private Scanner scanner;

    public static void main(String[] args) throws UnknownHostException {
        port = Integer.parseInt(args[1]);
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

            Backup backup = new Backup(file, repDeg, socket, address, port);
            backup.transfer();
            socket.leaveGroup(address);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}