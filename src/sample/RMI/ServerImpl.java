package sample.RMI;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;

/**
 * This class implements the remote interface 
 * ServerInterface.
 * @author M. L. Liu
 */

public class ServerImpl extends UnicastRemoteObject
     implements ServerInterface {

   private Vector clientList; //Lista de clientes conectados al servidor
   private Vector clientAlias; //Lista con los alias de dichos clientes, mejora de rendimiento


   public ServerImpl() throws RemoteException {
      super( );
     clientList = new Vector();
     clientAlias = new Vector();
   }

  public String sayHello( )   
    throws RemoteException {
      return("hello");
  }

  //Registramos para callbacks y asignamos alias unico
  public synchronized void registerForCallback(
    ClientInterface callbackClientObject)
    throws RemoteException{
      // store the callback object into the vector
      if (!(clientList.contains(callbackClientObject))) {
          //Comprobamos alias y sino añadimos numeros hasta hacerlo valido
          int aux = 0;
          String alias = callbackClientObject.getAlias();
          String aliax_aux = alias;
          while(clientAlias.contains(aliax_aux)){
              aux++;
              aliax_aux = alias+aux;
          }
          //Alias valido lo asignamos y lo registramos
          clientList.addElement(callbackClientObject);
          callbackClientObject.setAlias(aliax_aux);
          clientAlias.addElement(aliax_aux);
          System.out.println("Registered new client ");
          doCallbacksUsuarioLinea(callbackClientObject);
      } // end if
  }  

  //Quitamos el registro del cliente indicado ya no recibirá callbacks
  public synchronized void unregisterForCallback(
    ClientInterface callbackClientObject)
    throws RemoteException{
    if (clientList.removeElement(callbackClientObject)) {
      clientAlias.removeElement(callbackClientObject.getAlias());
      System.out.println("Unregistered client ");
      doCallbacksUsuarioDesconectado(callbackClientObject);
    } else {
       System.out.println(
         "unregister: client wasn't registered.");
    }
  } 

  //Callback que Notifica a todos los usuarios la llegada de uno nuevo (CONEXION NUEVA), al nuevo le pasa la lista de usuarios en línea
  private synchronized void doCallbacksUsuarioLinea(ClientInterface new_client) throws RemoteException{
    // make callback to each registered client
    System.out.println(
       "**************************************\n"
        + "Callbacks initiated ---");
    for (int i = 0; i < clientList.size(); i++){
      if(!clientList.get(i).equals(new_client)){
          System.out.println("doing "+ i +"-th callback\n");
          // convert the vector object to a callback object
          ClientInterface nextClient =
                  (ClientInterface)clientList.elementAt(i);
          // invoke the callback method
          nextClient.notifyConnection(new_client);
      }else{
          // convert the vector object to a callback object
          ClientInterface nextClient =
                  (ClientInterface)clientList.elementAt(i);
          // invoke the callback method
          nextClient.notifyInicio(this.clientList);
      }
    }// end for
    System.out.println("********************************\n" +
                       "Server completed callbacks ---");
  }

    //Callback que Notifica a todos los usuarios la baja de uno de ellos (BAJA DESCONEXION)
    private synchronized void doCallbacksUsuarioDesconectado(ClientInterface off_client) throws RemoteException{
        // make callback to each registered client
        System.out.println(
                "**************************************\n"
                        + "Callbacks initiated ---");
        for (int i = 0; i < clientList.size(); i++){
                System.out.println("doing "+ i +"-th callback\n");
                // convert the vector object to a callback object
                ClientInterface nextClient =
                        (ClientInterface)clientList.elementAt(i);
                // invoke the callback method
                nextClient.notifyDesconnection(off_client);

        }// end for
        System.out.println("********************************\n" +
                "Server completed callbacks ---");
    }

}// end ServerImpl class
