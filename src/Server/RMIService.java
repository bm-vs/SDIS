package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {
	boolean backup() throws RemoteException;
	boolean restore() throws RemoteException;
	boolean delete() throws RemoteException;
	boolean reclaim() throws RemoteException;
}
