package sample.RMI.Client;

import sample.Model.User;

import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;

/**
 * This class implements the remote interface 
 * ClientInterface.
 * @author M. L. Liu
 */

public class ClientImpl extends UnicastRemoteObject
     implements ClientInterface, Serializable{

   private String alias;
   private Client cliente;

   public ClientImpl( String alias, Client client ) throws RemoteException {
      super( );
      this.alias = alias;
      this.cliente = client;
   }


   //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
   public synchronized String notifyListaAmigos(Vector usuarios){
      String returnMessage = "Call back notifyListaAmigos received";
      this.cliente.setAmigos(usuarios);
      System.out.println("Call back notifyListaAmigos received.");
      return returnMessage;
   }

   //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
   public synchronized String notifyInicio(Vector usuarios){
      String returnMessage = "Call back notifyInicio received";
      this.cliente.setUsers_linea(usuarios);
      System.out.println("Call back notifyInicio received.");
      return returnMessage;
   }

   //recibe la notificación de un nuevo usuario en línea
   public synchronized String notifyConnection(String username, ClientInterface new_client){
      String returnMessage = "Call back notifyConnection received";
      User u = new User(username,new_client);
      this.cliente.addUsers_linea(u);
      System.out.println("Call back notifyConnection received.");
      return returnMessage;
   }

   //recibe la notificación de la desconexión de un usuario.
   public synchronized String notifyDesconnection(User off_client){
      String returnMessage = "Call back notifyDesconnection received";
      this.cliente.deleteUsers_linea(off_client.getClientInterface());
      System.out.println("Call back Desconnection received.");
      return returnMessage;
   }

   //Recepción de Mensajes

   @Override
   public synchronized Boolean recibirMensaje(String mensaje, String alias_emisor, ClientInterface emisor_interface) throws RemoteException{
      this.cliente.addMensaje(mensaje, alias_emisor, emisor_interface);
      System.out.println("Mensaje recibido de "+alias_emisor+" con interfaz "+emisor_interface+": "+mensaje);
      return true;
   }

   //PETICIONES PENDIENTES

   public synchronized String notifyPeticionesPendientes(Vector peticiones) throws RemoteException{
      String mensaje = "Call back Peticiones Pendientes received";
      this.cliente.setPeticiones_amistad(peticiones);
      return mensaje;
   }

   public synchronized void notifyNewFriendship(String emisor) throws RemoteException{
      System.out.println("Amigo "+emisor);
      this.cliente.addPeticion(emisor);
      System.out.println("Call back newFriendship received.");
   }

   public synchronized void notifyNewFriend(User friend) throws RemoteException{
      this.cliente.addUsers_linea(friend);
      System.out.println("Callback newFriend received");
   }

   //Obtiene el alias que utiliza el cliente
   public String getUsername() throws RemoteException {
      return alias;
   }

   //permite fijar el alias del cliente
   public void setUsername(String alias) throws RemoteException {
      this.alias = alias;
      this.cliente.setUsername(alias);
   }


}// end ClientImpl class
