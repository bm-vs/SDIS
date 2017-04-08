package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIService extends Remote {
	boolean backup(String file, int replDegree) throws RemoteException;
	boolean restore(String file) throws RemoteException;
	boolean delete(String file) throws RemoteException;
	boolean reclaim(int space) throws RemoteException;
	boolean space(int space) throws RemoteException;
	String state() throws RemoteException;


}
