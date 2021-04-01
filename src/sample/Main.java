package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sample.Controllers.ControllerChat;
import sample.Controllers.ControllerConexion;
import sample.Model.User;
import sample.RMI.Client;
import sample.RMI.ClientImpl;
import sample.RMI.ClientInterface;
import sample.RMI.ServerInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.Naming;

import static java.lang.System.exit;

public class Main extends Application {

    private Client client;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("Conexion.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("InicioChatP2P");
        primaryStage.setScene(new Scene(root, 550, 600));
        this.primaryStage = primaryStage;
        // Give the controllerConexion access to the main app.
        ControllerConexion controllerConexion = loader.getController();
        controllerConexion.setMainApp(this);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


    //metodos cambio de escena
    public void iniciarChat(){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Chat.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("InicioChatP2P");
            primaryStage.setScene(new Scene(root, 550, 600));
            // Give the controllerChat access to the main app.
            ControllerChat controllerChat = loader.getController();
            controllerChat.setMainApp(this);
            controllerChat.setCliente(this.client);
            //Evento cierre asegurando una salida controlada, comprobamos si nos hemos deregistrado
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                        if(client.getConectado().get().equals("En Linea"))
                            client.desconectar();
                        exit(1);
                }
            });
            this.primaryStage.show();
        }catch (Exception e){
            System.out.println("Exception IniciarChat Main: "+e);
        }
    }

    //Getters y Setters
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
