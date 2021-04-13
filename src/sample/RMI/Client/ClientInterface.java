package sample.RMI.Client;


import sample.Model.User;

import java.io.Serializable;
import java.rmi.*;
import java.util.Vector;

/**
 * This is a remote interface for illustrating RMI 
 * client callback.
 * @author M. L. Liu
 */

public interface ClientInterface
  extends Remote, Serializable {

    //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
    public String notifyListaAmigos(Vector usuarios)
            throws RemoteException;

    //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
    public String notifyInicio(Vector usuarios)
      throws RemoteException;

    //recibe la notificación de un nuevo usuario en línea
    public String notifyConnection(String username, ClientInterface new_client) throws RemoteException;

    //recibe la notificación de la desconexión de un usuario.
    public String notifyDesconnection(User off_client) throws RemoteException;

    //Permite recibir un mensaje
    public Boolean recibirMensaje(String mensaje, String alias_emisor, ClientInterface emisor_interface)
            throws RemoteException;

    public String notifyPeticionesPendientes(Vector peticiones) throws RemoteException;

    public void notifyNewFriendship(String emisor) throws RemoteException;

    public void notifyNewFriend(User friend) throws RemoteException;
} // end interface
