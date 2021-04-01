package sample.RMI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Model.Mensaje;
import sample.Model.User;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;

/**
 * This class implements the remote interface 
 * ClientInterface.
 * @author M. L. Liu
 */

public class ClientImpl extends UnicastRemoteObject
     implements ClientInterface {

   private String alias;
   private Client cliente;

   public ClientImpl( String alias, Client client ) throws RemoteException {
      super( );
      this.alias = alias;
      this.cliente = client;
   }


   //Permite obtener la lista inicial (momento de la conexion) de usuarios en linea
   public synchronized String notifyInicio(Vector usuarios){
      String returnMessage = "Call back notifyInicio received";
      this.cliente.setUsers_linea(usuarios);
      System.out.println("Call back notifyInicio received.");
      return returnMessage;
   }

   //recibe la notificación de un nuevo usuario en línea
   public synchronized String notifyConnection(ClientInterface new_client){
      String returnMessage = "Call back notifyConnection received";
      this.cliente.addUsers_linea(new_client);
      System.out.println("Call back notiifyConnection received.");
      return returnMessage;
   }

   //recibe la notificación de la desconexión de un usuario.
   public synchronized String notifyDesconnection(ClientInterface off_client){
      String returnMessage = "Call back notifyDesconnection received";
      this.cliente.deleteUsers_linea(off_client);
      System.out.println("Call back Desconnection received.");
      return returnMessage;
   }

   //Envio y recepción de Mensajes


   /*@Override
   public Boolean recibirMensaje(Mensaje mensaje) throws RemoteException {
      this.cliente.addMensaje(mensaje);
      return true;
   }*/
   @Override
   public Boolean recibirMensaje(String mensaje) throws RemoteException {
      //this.cliente.addMensaje(mensaje);
      System.out.println("Mensaje recibido: "+mensaje);
      return true;
   }

   //Obtiene el alias que utiliza el cliente
   public String getAlias() {
      return alias;
   }

   //permite fijar el alias del cliente
   @Override
   public void setAlias(String alias) {
      this.alias = alias;
      this.cliente.setAlias(alias);
   }
}// end ClientImpl class
