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
            printUsage();
            return;
        }

        // Get the address that we are going to connect to.
        System.out.println("Started client");

        
        // Start RMI
        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
        	RMIService peer = (RMIService) registry.lookup(access);

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
        if(args.length >= 2 && args.length <= 4) {
            access = args[0];
            operation = args[1];
            if (args.length >= 3) {
                op1 = args[2];
                if (args.length == 4) {
                    op2 = Integer.parseInt(args[3]);
                    return operation.equals(Service.backup);
                } else{
                    return operation.equals(Service.delete) || operation.equals(Service.reclaim) || operation.equals(Service.restore) || operation.equals(Service.space);
                }
            } else return operation.equals(Service.state);
        } else return false;
    }

    private static void printUsage(){
        System.out.println("Wrong number of arguments for specified operation. Usage:");
        System.out.println("java client.Client <RMIName> " + Service.backup + " <filePath> <replication degree>");
        System.out.println("java client.Client <RMIName> " + Service.restore + " <filePath>");
        System.out.println("java client.Client <RMIName> " + Service.delete + " <filePath>");
        System.out.println("java client.Client <RMIName> " + Service.reclaim + " <space>");
        System.out.println("java client.Client <RMIName> " + Service.space + " <space>");
        System.out.println("java client.Client <RMIName> " + Service.state);


    }
}
