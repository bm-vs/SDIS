package Client;

import SubProtocols.Backup;
import Server.RMIService;

import java.io.IOException;
import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

        
        // Start RMI
        try {
            String name = "Peer";
            Registry registry = LocateRegistry.getRegistry("localhost");
        	RMIService peer = (RMIService) registry.lookup(name);

        	boolean status = false;
        	
        	switch (operation) {
	            case "BACKUP":
	            	status = peer.backup();
	                break;
	            case "RESTORE":
	            	status = peer.restore();
	                break;
	            case "DELETE":
	            	status = peer.delete();
	                break;
	            case "RECLAIM":
	            	status = peer.reclaim();
	                break;
        	}
        	
        	if(status) {
        		System.out.println("Answer received");
        	}

        } catch (Exception ex) {
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
