package sample.RMI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Controllers.ControllerChat;
import sample.Model.Mensaje;
import sample.Model.User;
import sample.RMI.ClientImpl;
import sample.RMI.ClientInterface;
import sample.RMI.ServerInterface;

import java.io.*;
import java.rmi.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

/**
 **ESTA CLASE REPRESENTA A UN OBJETO CLIENTE
 * LO QUE VIENE SIENDO UN USUARIO DEL CHAT
 * CONTIENE EL ALIAS
 * SERVIDOR AL QUE ESTA CONECTADO
 * SU PROPIA INTERFAZ
 * UN ESTADO DE CONEXION { En Linea, Desconectado}
 * UN VECTOR CON LOS USUARIOS CON LOS QUE SE HA ENCONTRADO EN LA SESION ACTUAL (CONECTADOS Y DESCONECTADOS)
 *
 * Autor: Jonathan González López
 */

public class Client {

    private String alias;
    private ServerInterface server;
    private ClientInterface clientInterface;
    private StringProperty conectado;
    private ObservableList<Client> usuarios_sesion = FXCollections.observableArrayList(); //Observable porque los cambios repercuten en la gráfica
    private HashMap<ClientInterface, ObservableList<Mensaje>> mensajes;

    //Constructores
    public Client(String alias){
        this.alias = alias;
    }

    public Client(String alias, ClientInterface clientInterface){
        this.alias = alias;
        this.clientInterface = clientInterface;
        this.conectado = new SimpleStringProperty("En Linea");
    }

    //Constructor con conexión a servidor y registro en el callback
    public Client(String PortNum, String hostName, String alias) {
        try {
            this.alias = alias;
            this.mensajes = new HashMap<>();
            this.conectado = new SimpleStringProperty("En Linea");
            String registryURL = "rmi://"+hostName+":" + PortNum + "/callback";

            // find the remote object and cast it to an
            //   interface object
            this.server = (ServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            System.out.println("Server said " + this.server.sayHello());
            this.clientInterface =
                    new ClientImpl(this.alias.toString(), this);
            // register for callback
            this.server.registerForCallback(this.clientInterface);
            System.out.println("Registered for callback.");
        } catch (Exception e) {
            System.out.println(
                    "Exception register Client: " + e);
        } // end catch
    }

    //Desconexión del Servidor y unregister del callback
    public void desconectar(){
        try{
            this.server.unregisterForCallback(this.clientInterface);
            this.conectado.setValue("Desconectado");
            System.out.println("Unregistered for callback.");
        }catch(Exception e){
            System.out.println("Exception unregister Cliente: "+e);
        }
    }


    //Control de usuarios sesión
    //Iniciar el vector en el momento que nos conectamos al servidor
    public void setUsers_linea(Vector<ClientInterface> users_linea) {
        try{
            for(ClientInterface ci: users_linea){
                Client client = new Client(ci.getAlias(), ci);
                if(!client.getAlias().equals(this.alias)) //A el mismo no se añade
                    this.usuarios_sesion.add(client);
                    this.mensajes.put(ci,FXCollections.observableArrayList());
                }
        }catch (Exception ex){
            System.out.println("Exception setUsers_linea Cliente: "+ex);
        }

    }

    //Añadir usuarios nuevos en línea
    public void addUsers_linea(ClientInterface new_user){
        try {
            Client new_client = new Client(new_user.getAlias(), new_user);
            if (this.usuarios_sesion.contains(new_client)) { //Posible reconexion
                for (Client c : this.usuarios_sesion) {
                    if (c.equals(new_client)){
                        c.conectado.setValue("En Linea");
                    }
                }
            }else {
                this.usuarios_sesion.add(new_client);
                this.mensajes.put(new_user,FXCollections.observableArrayList());
            }
        }catch(Exception ex){
                System.out.println("Exception GetAlias in addUsers_linea ClientImpl: "+ex);
        }
    }

    //Pasar usuarios de estado en linea a DESCONECTADO
    public void deleteUsers_linea(ClientInterface off_user){
        for (Client c : this.usuarios_sesion) {
            if (c.clientInterface.equals(off_user)){
                c.conectado.setValue("Desconectado");
            }
        }
    }

    public void addMensaje(String mensaje, String alias_emisor, ClientInterface emisor_interface){
        Client c = new Client(alias_emisor, emisor_interface);
        Mensaje m = new Mensaje(c,mensaje,true);
        this.mensajes.get(emisor_interface).add(m);
        System.out.println("Mensaje añadido a la cola de mensajes.");
    }

    //ENVIO Y RECEPCION
    public Boolean enviar(Client destinatario, Mensaje m){
        try {
            if(destinatario.clientInterface.recibirMensaje(m.getContenido(), m.getAutor().getAlias(), this.clientInterface)) {
                this.mensajes.get(destinatario.clientInterface).add(m);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception enviar Client: "+e);
            return false;
        }
    }

    //GETTERS Y SETTERS


    public ClientInterface getClientInterface() {
        return clientInterface;
    }

    public ObservableList<Client> getUsuarios_sesion() {
        return usuarios_sesion;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public StringProperty getConectado() {
        return conectado;
    }

    public HashMap<ClientInterface, ObservableList<Mensaje>> getMensajes() {
        return mensajes;
    }

    //EQUALS, dos usuarios son el mismo si coincide al alias
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return Objects.equals(getAlias(), client.getAlias()); //MISMO ALIAS
    }
}//end class
