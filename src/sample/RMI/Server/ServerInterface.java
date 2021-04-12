package sample.RMI.Server;

import sample.RMI.Client.ClientInterface;

import java.rmi.*;

/**
 * This is a remote interface for illustrating RMI 
 * client callback.
 * @author M. L. Liu
 */

public interface ServerInterface extends Remote {

  public String sayHello( )   
    throws RemoteException;

  //Permite el registro de un nuevo usuario y su registro en la recepcion de callback si el registro tiene exito
  public void newUser(ClientInterface callbackClientObject, String username, String password) throws RemoteException;

  //Permite el registro de un cliente para la recepción de callbacks
  public void registerForCallback(
    ClientInterface callbackClientObject, String username, String password
    ) throws RemoteException;


  //Permite el unregister de un cliente para la recepción de callbacks
  public void unregisterForCallback(
    ClientInterface callbackClientObject, String username, String password)
    throws RemoteException;
}
