package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {
	boolean backup(String file, int replDegree) throws RemoteException;
	boolean restore() throws RemoteException;
	boolean delete() throws RemoteException;
	boolean reclaim() throws RemoteException;
}
