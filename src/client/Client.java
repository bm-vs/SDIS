package client;

import server.RMIService;

import java.net.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    private static String access;
    private static String operation;
    private static String op1;
    private static int op2;

    public static void main(String[] args) throws UnknownHostException {
        if (!checkArguments(args)) {
            System.out.println("Error: Invalid action");
            return;
        }

        // Get the address that we are going to connect to.
        System.out.println("Started client");

        
        // Start RMI
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
        	RMIService peer = (RMIService) registry.lookup(args[0]);

        	boolean status = false;
        	
        	switch (operation) {
	            case Service.backup:
	            	status = peer.backup(op1, op2);
	                break;
	            case Service.restore:
	            	status = peer.restore(op1);
	                break;
	            case Service.delete:
	            	status = peer.delete(op1);
	                break;
	            case Service.reclaim:
	            	status = peer.reclaim(Integer.parseInt(op1));
	                break;
                case Service.space:
                    status = peer.space(Integer.parseInt(op1));
                    break;
                case Service.state:
                    System.out.println(peer.state());
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
                access = args[0];
                operation = args[1];
                if (operation.equals("BACKUP")) {
                    op1 = args[2];
                    op2 = Integer.parseInt(args[3]);
                    return true;
                }
                return true;
            case 3:
                access = args[0];
                operation = args[1];
                if (operation.equals("RESTORE") || operation.equals("DELETE") || operation.equals("RECLAIM")) {
                    op1 = args[2];
                    return true;
                }
                return false;
            case 2:
                access = args[0];
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
