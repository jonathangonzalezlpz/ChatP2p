package sample.RMI;

import java.rmi.*;

/**
 * This is a remote interface for illustrating RMI 
 * client callback.
 * @author M. L. Liu
 */

public interface ServerInterface extends Remote {

  public String sayHello( )   
    throws RemoteException;


  //Permite el registro de un cliente para la recepción de callbacks
  public void registerForCallback(
    ClientInterface callbackClientObject
    ) throws RemoteException;


  //Permite el unregister de un cliente para la recepción de callbacks
  public void unregisterForCallback(
    ClientInterface callbackClientObject)
    throws RemoteException;
}
