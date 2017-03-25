package Client;

import SubProtocols.Backup;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    static int port;
    static String operation;
    static String op1;
    static int op2;

    private Scanner scanner;

    public static void main(String[] args) throws UnknownHostException {
        if (!checkArguments(args)) {
            System.out.println("Error: Invalid action");
            return;
        }

        // Get the address that we are going to connect to.
        System.out.println("Started client");

        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
        try {
            InetAddress address = InetAddress.getByName(args[0]);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(address);

            switch (operation) {
                case "BACKUP":
                    //Backup backup = new Backup(op1, op2, socket, address, port);
                    //backup.transfer();
                    break;
            }

            socket.leaveGroup(address);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean checkArguments(String[] args) {
        switch(args.length) {
            case 4:
                port = Integer.parseInt(args[0]);
                operation = args[1];
                if (operation.equals("BACKUP")) {
                    op1 = args[2];
                    op2 = Integer.parseInt(args[3]);
                    return true;
                }
                return true;
            case 3:
                port = Integer.parseInt(args[0]);
                operation = args[1];
                if (operation.equals("RESTORE") || operation.equals("DELETE") || operation.equals("RECLAIM")) {
                    op1 = args[2];
                    return true;
                }
                return false;
            case 2:
                port = Integer.parseInt(args[0]);
                operation = args[1];
                if (operation.equals("STATE")) {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }


}
