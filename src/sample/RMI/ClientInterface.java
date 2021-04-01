package sample.RMI;

import javafx.beans.property.StringProperty;
import sample.Model.Mensaje;

import java.rmi.*;
import java.util.Vector;

/**
 * This is a remote interface for illustrating RMI 
 * client callback.
 * @author M. L. Liu
 */

public interface ClientInterface
  extends Remote{

    //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
    public String notifyInicio(Vector usuarios)
      throws RemoteException;

    //recibe la notificación de un nuevo usuario en línea
    public String notifyConnection(ClientInterface new_client) throws RemoteException;

    //recibe la notificación de la desconexión de un usuario.
    public String notifyDesconnection(ClientInterface off_client) throws RemoteException;

    //Obtiene el alias que utiliza el cliente
    public String getAlias() throws RemoteException;

    //permite fijar el alias del cliente
    public void setAlias(String alias) throws RemoteException;

    //Permite recibir un mensaje
    public Boolean recibirMensaje(String mensaje, ClientInterface emisor, String alias)
            throws RemoteException;
} // end interface
