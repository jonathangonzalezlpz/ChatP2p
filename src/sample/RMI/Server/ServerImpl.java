package sample.RMI.Server;

import sample.DB.FachadaBD;
import sample.Model.User;
import sample.RMI.Client.ClientInterface;

import javax.jws.soap.SOAPBinding;
import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;

/**
 * This class implements the remote interface 
 * ServerInterface.
 * @author M. L. Liu
 */

public class ServerImpl extends UnicastRemoteObject
     implements ServerInterface, Serializable {
    private FachadaBD database;
    private Vector<User> clientList; //Lista de clientes conectados al servidor

    public ServerImpl() throws RemoteException {
      super( );
       //CONECTAR A LA BASE DE DATOS
       this.database = new FachadaBD();
       clientList = new Vector();
    }

    public String sayHello( )
            throws RemoteException {
        return("hello");
    }

    public void newUser(ClientInterface callbackClientObject, String username, String password) throws RemoteException{
          if(database.registrar(username, password)){
              this.registerForCallback(callbackClientObject,username,password);
          }
    }

    //Registramos para callbacks y asignamos alias unico
    public synchronized void registerForCallback(
            ClientInterface callbackClientObject,
            String username,
            String password)
            throws RemoteException{
        // store the callback object into the vector
        if (!(clientList.contains(callbackClientObject))) {
            //Comprobamos inicio de sesion
            if(this.database.validar(username,password)) {
                User user = new User(username,callbackClientObject);
                //valido lo asignamos
                clientList.addElement(user);
                System.out.println("Registered new client ");
                Vector<String> amigos = new Vector<>(this.database.obtenerAmigos(user.getUsername()));
                callbackClientObject.notifyListaAmigos(amigos);
                System.out.println("Inicializada su lista de Amigos");
                Vector<String> peticiones = new Vector<String>(this.database.obtenerPeticionesPendientes(user.getUsername()));
                callbackClientObject.notifyPeticionesPendientes(peticiones);
                doCallbacksUsuarioLinea(user);
            }else{
                throw new RemoteException();
            }
        } // end if
    }

    //Quitamos el registro del cliente indicado ya no recibirá callbacks

    public synchronized void unregisterForCallback(
            ClientInterface callbackClientObject,
            String username,
            String password)
            throws RemoteException{
        User u = new User(username,callbackClientObject);
        if (clientList.removeElement(u)) {
            //clientAlias.removeElement(callbackClientObject.getAlias()); El alias lo dejamos, otros usuarios pueden tener archivados chats asociados a ese alias
            System.out.println("Unregistered client ");
            doCallbacksUsuarioDesconectado(u);
        } else {
            System.out.println("unregister: client wasn't registered.");
        }
    }

    public synchronized Boolean newFriendship(String user, String password, String friend) throws RemoteException{
        if(this.database.validar(user,password)){
            if(!this.database.existeFriendship(user,friend)){
                if(this.database.newFriendship(user,friend)){
                    if(this.clientList.contains(new User(friend,null))){
                        for(User u: this.clientList){
                            if(u.getUsername().equals(friend)){
                                u.getClientInterface().notifyNewFriendship(user);
                                break;
                            }
                        }
                    }
                    return true;
                }else
                    return false;
            }
        }
        return false;
    }

    public synchronized Boolean acceptFriend(String user, String password, String friend) throws RemoteException{
        if(this.database.validar(user,password)){
            if(this.database.confirmFriendship(user,friend)){
                    User user1 = null;
                    for(User f: this.clientList){
                        if(f.getUsername().equals(user)){
                            user1 = f;
                            break;
                        }
                    }
                    if(this.clientList.contains(new User(friend,null))){
                        for(User u: this.clientList){
                            if(u.getUsername().equals(friend)){
                                u.getClientInterface().notifyNewFriend(user1);
                                user1.getClientInterface().notifyNewFriend(u);
                                break;
                            }
                        }
                    }else{
                        User user2 = new User(friend,null);
                        user1.getClientInterface().notifyNewFriend(user2);
                    }
                    return true;
                }else
                    return false;
            }
        return false;
    }


    //Callback que Notifica a todos los usuarios la llegada de uno nuevo (CONEXION NUEVA), al nuevo le pasa la lista de usuarios en línea
    private synchronized void doCallbacksUsuarioLinea(User new_client) throws RemoteException{
        // make callback to each registered client
        System.out.println(
                "**************************************\n"
                        + "Callbacks initiated ---");
        Vector amigos = new Vector();
        for (int i = 0; i < clientList.size(); i++){
            if(!clientList.get(i).equals(new_client)){
                if(this.database.sonAmigos(new_client.getUsername(),((User)clientList.elementAt(i)).getUsername())){ //Si son amigos avisamos al amigo
                    // convert the vector object to a callback object
                    ClientInterface nextClient =
                            ((User)clientList.elementAt(i)).getClientInterface();
                    // invoke the callback method
                    System.out.println(nextClient.notifyConnection(new_client.getUsername(),new_client.getClientInterface()));
                    amigos.addElement(clientList.elementAt(i));
                }
            }
        }// end for
        //Amigos en linea
        new_client.getClientInterface().notifyInicio(amigos);
        System.out.println("********************************\n" +
                "Server completed callbacks ---");
    }

    //Callback que Notifica a todos los usuarios la baja de uno de ellos (BAJA DESCONEXION)
    private synchronized void doCallbacksUsuarioDesconectado(User off_client) throws RemoteException{
        // make callback to each registered client
        System.out.println(
                "**************************************\n"
                        + "Callbacks initiated ---");
        for (int i = 0; i < clientList.size(); i++){

            System.out.println("doing "+ i +"-th callback\n");
            // convert the vector object to a callback object
            User nextClient =
                    (User)clientList.elementAt(i);
            if(this.database.sonAmigos(off_client.getUsername(),nextClient.getUsername())){
                // invoke the callback method
                nextClient.getClientInterface().notifyDesconnection(off_client);
            }

        }// end for
        System.out.println("********************************\n" +
                "Server completed callbacks ---");
    }


}// end ServerImpl class
