package sample.RMI.Client;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Model.Mensaje;
import sample.Model.User;
import sample.RMI.Server.ServerInterface;

import java.io.Serializable;
import java.rmi.*;
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

public class Client implements Serializable {

    private String username;
    private String password;
    private ServerInterface server;
    private ClientInterface clientInterface;
    private StringProperty conectado;
    private ObservableList<Client> usuarios_sesion = FXCollections.observableArrayList(); //Observable porque los cambios repercuten en la gráfica
    private HashMap<String, ObservableList<Mensaje>> mensajes;
    private StringProperty mensajesNoLeidos;
    private ObservableList<String> peticiones_amistad;

    //Constructores
    public Client(String alias){
        this.username = alias;
        this.conectado = new SimpleStringProperty("Desconectado");
        this.mensajesNoLeidos = new SimpleStringProperty("0");
    }

    public Client(String alias, ClientInterface clientInterface){
        this.username = alias;
        this.clientInterface = clientInterface;
        this.conectado = new SimpleStringProperty("En Linea");
        this.mensajesNoLeidos = new SimpleStringProperty("0");
    }

    //Constructor con conexión a servidor y registro en el callback
    public Client(String PortNum, String hostName, String username, String password) throws Exception{
        try {
            this.username = username;
            this.password = password;
            this.mensajes = new HashMap<>();
            this.conectado = new SimpleStringProperty("En Linea");
            this.peticiones_amistad = FXCollections.observableArrayList();
            String registryURL = "rmi://"+hostName+":" + PortNum + "/callback";

            // find the remote object and cast it to an
            //   interface object
            this.server = (ServerInterface) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            System.out.println("Server said " + this.server.sayHello());
            this.clientInterface =
                    new ClientImpl(this.username.toString(), this);
        } catch (Exception e) {
            System.out.println(
                    "Exception register Client: " + e);
            throw new Exception("Problema de conexión con el servidor.");
        } // end catch
    }

    public void registrarse() throws Exception{
        try {
            this.server.newUser(this.clientInterface, this.username, this.password);
        }catch (Exception e){
            System.out.println(
                    "Exception register Client: " + e);
            throw new Exception("Problema de conexión con el servidor.");
        }

    }

    public void iniciarSesion() throws Exception{
        // register for callback
        try{
            this.server.registerForCallback(this.clientInterface, this.username, this.password);
            System.out.println("Registered for callback.");
        }catch (Exception e){
            System.out.println(
                    "Exception login Client: " + e);
            throw new Exception("Problema de inicio sesión con el servidor.");
        }
    }

    //Desconexión del Servidor y unregister del callback
    public void desconectar(){
        try{
            this.server.unregisterForCallback(this.clientInterface, this.username, this.password);
            this.conectado.setValue("Desconectado");
            System.out.println("Unregistered for callback.");
        }catch(Exception e){
            System.out.println("Exception unregister Cliente: "+e);
        }
    }


    //Control de usuarios sesión
    //Iniciar el vector en el momento que nos conectamos al servidor
    public void setAmigos(Vector<String> users_linea) {
        for(String u: users_linea){
            Client client = new Client(u);
            if(!client.getUsername().equals(this.username)) //A el mismo no se añade
                this.usuarios_sesion.add(client);
            this.mensajes.put(u,FXCollections.observableArrayList());
        }

    }

    public void setUsers_linea(Vector<User> users_linea) {
        for(User u: users_linea){
            Client c = new Client(u.getUsername(),u.getClientInterface());
            this.usuarios_sesion.remove(c);
            this.usuarios_sesion.add(c);
        }

    }

    //Añadir usuarios nuevos en línea
    public void addUsers_linea(User new_user) {
        if(new_user.getClientInterface()!=null){
            Client new_client = new Client(new_user.getUsername(), new_user.getClientInterface());
            if (this.usuarios_sesion.contains(new_client)) {
                for (Client c : this.usuarios_sesion) {
                    if (c.username.equals(new_client.getUsername())) {
                        c.clientInterface = new_client.getClientInterface();
                        c.conectado.setValue("En Linea");
                    }
                }
            } else {
                this.usuarios_sesion.add(new_client);
                this.mensajes.put(new_user.getUsername(), FXCollections.observableArrayList());
            }
        }else {
            Client friend = new Client(new_user.getUsername());
            this.usuarios_sesion.add(friend);
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


    //Recepcion
    public void addMensaje(String mensaje, String alias_emisor, ClientInterface emisor_interface){
        Client c = new Client(alias_emisor, emisor_interface);
        Mensaje m = new Mensaje(c,mensaje,true);
        this.mensajes.get(alias_emisor).add(m);
        for(Client u: usuarios_sesion){
            if(u.getUsername().equals(alias_emisor)){
                int aux = Integer.parseInt(u.mensajesNoLeidos.get())+1;
                u.mensajesNoLeidos.setValue(aux+"");
                break;
            }
        }
        System.out.println("Mensaje añadido a la cola de mensajes.");
    }

    //ENVIO
    public Boolean enviar(Client destinatario, Mensaje m){
        try {
            if(destinatario.clientInterface.recibirMensaje(m.getContenido(), m.getAutor().getUsername(), this.clientInterface)) {
                this.mensajes.get(destinatario.getUsername()).add(m);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception enviar Client: "+e);
            return false;
        }
    }

    public void leer(Client destinatario){
        for(Client u: usuarios_sesion){
            if(u.equals(u)){
                int aux = 0;
                u.mensajesNoLeidos.setValue(aux+"");
                break;
            }
        }
    }

    //GETTERS Y SETTERS
    public ClientInterface getClientInterface() {
        return clientInterface;
    }

    public ObservableList<Client> getUsuarios_sesion() {
        return usuarios_sesion;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String alias) {
        this.username = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public StringProperty getConectado() {
        return conectado;
    }

    public HashMap<String, ObservableList<Mensaje>> getMensajes() {
        return mensajes;
    }

    public ObservableList<String> getPeticiones_amistad() {
        return peticiones_amistad;
    }

    public String getMensajesNoLeidos() {
        return mensajesNoLeidos.get();
    }

    public StringProperty mensajesNoLeidosProperty() {
        return mensajesNoLeidos;
    }

    public void setMensajesNoLeidos(String mensajesNoLeidos) {
        this.mensajesNoLeidos.set(mensajesNoLeidos);
    }

    public void setPeticiones_amistad(Vector<String> peticiones){
        for(String p: peticiones){
            this.peticiones_amistad.add(p);
        }
    }

    //AÑADIR AMIGO
    public Boolean addFriend(String friend){
        try{
            if(this.server.newFriendship(this.username, this.password, friend))
                return true;
            else
                return false;
        }catch (Exception e){
            System.out.println("Exception addFriend Client: "+e);
            return false;
        }
    }

    //Añadir Petición de Amistad
    public void addPeticion(String emisor){
        this.peticiones_amistad.add(emisor);
    }

    public void deletePeticion(String emisor){
        this.peticiones_amistad.remove(emisor);
    }

    public Boolean aceptarPeticion(String friend) {
        try {
            return this.server.acceptFriend(this.username, this.password, friend);
        } catch (Exception e) {
            System.out.println("Exception acceptarPeticion Client: " + e);
        }
        return false;
    }

    //Credenciales
    public Boolean changePassword(String new_password){
        try {
            return this.server.changePassword(this.username,this.password,new_password);
        }catch (Exception e){
            System.out.println("Exception in changePassword Client: "+e);
            return false;
        }

    }

    //EQUALS, dos usuarios son el mismo si coincide al alias
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return Objects.equals(getUsername(), client.getUsername()); //MISMO ALIAS
    }
}//end class
