/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.DB;

import sample.Model.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

/**
 *
 * @author jonyglez
 */
public class FachadaBD {
    private java.sql.Connection conexion;
    public FachadaBD(){
        Properties configuracion = new Properties();
        FileInputStream arqConfiguracion;
        try{
            arqConfiguracion = new FileInputStream("baseDatos.properties");
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();
            
            Properties usuario = new Properties();
            
            String gestor = configuracion.getProperty("gestor");
            usuario.setProperty("user", configuracion.getProperty("usuario"));
            usuario.setProperty("password",configuracion.getProperty("clave"));

            this.conexion=java.sql.DriverManager.getConnection("jdbc:"+gestor+"://"+
                    configuracion.getProperty("servidor")+":"+
                    configuracion.getProperty("puerto")+"/"+
                    configuracion.getProperty("baseDatos"),
                    usuario);
        }catch(FileNotFoundException f){
            System.out.println(f.getMessage());
            
        } catch(IOException i){
            System.out.println(i.getMessage());
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean registrar(String username, String password) {
        if (!existeUser(username)) {
            Connection con;
            PreparedStatement stmUser = null;

            con = this.conexion;

            try {
                stmUser = con.prepareStatement("insert into usuarios (nombre, password) " +
                        "values (?,?)");
                stmUser.setString(1, username);
                stmUser.setString(2, password);
                stmUser.executeUpdate();
                return true;

            } catch (SQLException e) {
                return false;
            } finally {
                try {
                    stmUser.close();
                } catch (SQLException e) {
                    System.out.println("Imposible cerrar cursores");
                }
            }
        }else{
            return false;
        }
    }

    public boolean existeUser(String username){
        Connection con;
        PreparedStatement stmUsers=null;
        ResultSet rsUsers;

        con=this.conexion;
        try{
            stmUsers=con.prepareStatement("select * "
                    +"from usuarios "
                    +"where nombre = ?");
            stmUsers.setString(1,username);
            rsUsers=stmUsers.executeQuery();
            if(rsUsers.next()){
                return true;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmUsers.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return false;
    }

    public boolean validar(String username, String password){
        Connection con;
        PreparedStatement stmUsers=null;
        ResultSet rsUsers;

        con=this.conexion;
        try{
            stmUsers=con.prepareStatement("select * "
                    +"from usuarios "
                    +"where nombre = ? and password = ? ");
            stmUsers.setString(1,username);
            stmUsers.setString(2,password);
            rsUsers=stmUsers.executeQuery();
            if(rsUsers.next()){
                return true;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmUsers.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return false;
    }

    //AMIGOS
    public boolean rejectFriendship(String user1,String user2){
        Connection con;
        PreparedStatement stmFriends=null;
        con=this.conexion;

        try {
            stmFriends=con.prepareStatement("Delete from friends "+
                    "where user1 = ? and user2 = ? and estado = ?");
            stmFriends.setString(1, user2);
            stmFriends.setString(2, user1);
            stmFriends.setString(3, "Pendiente");
            stmFriends.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try {stmFriends.close();} catch (SQLException e){System.out.println("Imposible cerrar cursores");}
        }
        return false;
    }

    public boolean confirmFriendship(String user1,String user2){
        Connection con;
        PreparedStatement stmFriends=null;
        con=this.conexion;

        try {
            stmFriends=con.prepareStatement("update friends "+
                    "set estado = ?"+
                    "where user1 = ? and user2 = ?");
            stmFriends.setString(1, "aceptado");
            stmFriends.setString(2, user2);
            stmFriends.setString(3, user1);
            stmFriends.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try {stmFriends.close();} catch (SQLException e){System.out.println("Imposible cerrar cursores");}
        }
        return false;
    }

    public Vector<String> obtenerPeticionesPendientes(String username){
        Connection con;
        PreparedStatement stmPeticiones=null;
        ResultSet rsPeticiones;
        Vector resultado = new Vector();

        con=this.conexion;
        try{
            stmPeticiones=con.prepareStatement("select * "
                    +"from friends "
                    +"where (user2 = ? and estado = ?)");
            stmPeticiones.setString(1,username);
            stmPeticiones.setString(2, "Pendiente");

            rsPeticiones=stmPeticiones.executeQuery();
            while(rsPeticiones.next()){
                resultado.addElement(rsPeticiones.getString("user1"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmPeticiones.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return resultado;
    }

    public boolean newFriendship(String username1, String username2) {
        Connection con;
        PreparedStatement stmUser = null;

        con = this.conexion;

        try {
            stmUser = con.prepareStatement("insert into friends (user1, user2) " +
                    "values (?,?)");
            stmUser.setString(1, username1);
            stmUser.setString(2, username2);
            stmUser.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false;
        } finally {
            try {
                stmUser.close();
            } catch (SQLException e) {
                System.out.println("Imposible cerrar cursores");
            }
        }
    }

    public Vector<String> obtenerAmigos(String username){
        Connection con;
        PreparedStatement stmUsers=null;
        ResultSet rsUsers;
        Vector resultado = new Vector();

        con=this.conexion;
        try{
            stmUsers=con.prepareStatement("select * "
                    +"from friends "
                    +"where (user1 = ? and estado = ?) or (user2 = ? and estado = ?)");
            stmUsers.setString(1,username);
            stmUsers.setString(2, "aceptado");
            stmUsers.setString(3,username);
            stmUsers.setString(4,"aceptado");

            rsUsers=stmUsers.executeQuery();
            while(rsUsers.next()){
                if(rsUsers.getString("user1").equals(username))
                    resultado.addElement(rsUsers.getString("user2"));
                else
                    resultado.addElement(rsUsers.getString("user1"));
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmUsers.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return resultado;
    }

    public boolean sonAmigos(String username1, String username2){
        Connection con;
        PreparedStatement stmUsers=null;
        ResultSet rsUsers;

        con=this.conexion;
        try{
            stmUsers=con.prepareStatement("select * "
                    +"from friends "
                    +"where (user1 = ? and user2 = ? and estado = ?) or (user1 = ? and user2 = ? and estado = ?)");
            stmUsers.setString(1,username1);
            stmUsers.setString(2,username2);
            stmUsers.setString(3, "aceptado");
            stmUsers.setString(4,username2);
            stmUsers.setString(5,username1);
            stmUsers.setString(6,"aceptado");

            rsUsers=stmUsers.executeQuery();
            if(rsUsers.next()){
                return true;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmUsers.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return false;
    }

    public boolean existeFriendship(String username1, String username2){
        Connection con;
        PreparedStatement stmUsers=null;
        ResultSet rsUsers;

        con=this.conexion;
        try{
            stmUsers=con.prepareStatement("select * "
                    +"from friends "
                    +"where (user1 = ? and user2 = ?) or (user1 = ? and user2 = ?)");
            stmUsers.setString(1,username1);
            stmUsers.setString(2,username2);
            stmUsers.setString(3,username2);
            stmUsers.setString(4,username1);

            rsUsers=stmUsers.executeQuery();
            if(rsUsers.next()){
                return true;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try{
                stmUsers.close();
            }catch(SQLException e){
                System.out.println("Imposible cerrar cursores.");
            }
        }
        return false;
    }

    public Boolean changePassword(String user, String new_password){
        Connection con;
        PreparedStatement stmPassword=null;
        con=this.conexion;

        try {
            stmPassword=con.prepareStatement("update usuarios "+
                    "set password = ?"+
                    "where nombre = ?");
            stmPassword.setString(1, new_password);
            stmPassword.setString(2, user);
            stmPassword.executeUpdate();
            return true;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }finally{
            try {stmPassword.close();} catch (SQLException e){System.out.println("Imposible cerrar cursores");}
        }
        return false;
    }
}
