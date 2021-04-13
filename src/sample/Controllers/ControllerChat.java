package sample.Controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sample.Main;
import sample.Model.Mensaje;
import sample.RMI.Client.Client;

import java.util.ArrayList;
import java.util.HashMap;

public class ControllerChat {

    //ELEMENTOS JAVA FX
    @FXML
    private TableView<Client> userTable;
    @FXML
    private TableColumn<Client, String> idUserColumn;
    @FXML
    private TableColumn<Client, String> idStateColumn;
    @FXML
    private TableColumn<Client, String> idNLColumn;

    @FXML
    private Button btnCargar;

    @FXML
    void addUser(ActionEvent event) {
        System.out.println("HOLA");
    }

    @FXML
    private TextField txtfield_mensaje;

    @FXML
    private Text txt_receptor;

    @FXML
    private Text txt_usuario;

    @FXML
    private HBox cabeceraChat;

    @FXML
    private ScrollPane pane_cconversacion;

    @FXML
    private VBox panelConversacion;

    @FXML
    private Button btn_enviar;

    @FXML
    private TextField txtfield_friend;

    @FXML
    private Button btn_AddFriend;

    @FXML
    private Button btnAccept;

    @FXML
    private Button btnReject;

    @FXML
    private TableView<String> friendsTable;

    @FXML
    private TableColumn<String, String> idFriendColumn;

    @FXML
    private Text txt_mnsjGood;

    @FXML
    private Text txt_mnsjError;

    //ATRIBUTOS NECESARIOS
    private Client client;
    HashMap<String,ArrayList<String>> mensajesPorUsuario = new HashMap<>();
    private ObservableList<Client> userData; //Info Tabla
    // Reference to the main application.
    private Main mainApp;
    //ATRIBUTOS ZONA CONVERSACION
    Client destinatario;
    ObservableList<Mensaje> mensajes_destinatario;
    //Color de mis mensajes
    private String colorE;
    private String colorR;


   //CONSTRUCTOR
    public ControllerChat() {
        this.colorE = "#F2B591";
        this.colorR = "#D9A796";
    }



    @FXML
    void enviar(ActionEvent event) {
        if(!this.txtfield_mensaje.getText().equals("")){
            if(this.destinatario.getConectado().get().equals("En Linea")){
                Mensaje m = new Mensaje(this.client, this.txtfield_mensaje.getText(), false);
                if(this.client.enviar(this.destinatario,m)){
                    //this.mostrarMensaje(m);
                    this.txtfield_mensaje.setText("");
                }
            }
        }
    }

    @FXML
    void cargarConversacion(ActionEvent event) {
        destinatario = userTable.getSelectionModel().getSelectedItem();
        if(destinatario != null)
            cargarConversacion(destinatario);
    }

    @FXML
    void addFriend(ActionEvent event) {
        if(!this.txtfield_friend.getText().isEmpty()){
            if(this.client.addFriend(this.txtfield_friend.getText())){
                this.txt_mnsjGood.setVisible(true);
                this.txt_mnsjError.setVisible(false);
            }else{
                this.txt_mnsjError.setVisible(true);
                this.txt_mnsjGood.setVisible(false);
            }
        }
    }

    @FXML
    void ocultarRespuesta(MouseEvent event) {
        this.txt_mnsjError.setVisible(false);
        this.txt_mnsjGood.setVisible(false);
    }

    @FXML
    void acceptFriend(ActionEvent event) {
        System.out.println("AAAAA");
        String friend = this.friendsTable.getSelectionModel().getSelectedItem();
        if(friend != null){
            if(this.client.aceptarPeticion(friend)){
                this.client.deletePeticion(friend);
            }else
                System.out.println("ZZZZZ");
        }
    }

    @FXML
    void rejectFriend(ActionEvent event) {
        System.out.println("EEEEEE");
    }

    /**
     Se ejecuta después de que el loader haya sido invocado
     */
    @FXML
    private void initialize() {
        idUserColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getUsername()); });

        idStateColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().getConectado();
        });

        idNLColumn.setCellValueFactory(cellData -> {
            return cellData.getValue().mensajesNoLeidosProperty();
        });

        idFriendColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue());
        });

    }

    /**
     LLamado desde el main para asignar una referencia a el mismo
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
    LLamado desde el main para indicarle el cliente al que corresponde el chat y fijar los usuarios en línea inicialmente
     */
    public void setCliente(Client client) {
        this.client = client;
        this.userData = client.getUsuarios_sesion();
        this.txt_usuario.setText(this.client.getUsername());
        // Add observable list data to the table
        userTable.setItems(this.userData);
        this.friendsTable.setItems(this.client.getPeticiones_amistad());
    }

    //Métodos control gráfica

    void cargarConversacion(Client destinatario){
        this.panelConversacion.getChildren().clear();//limpiamos una posible conversacion anterior
        this.txt_receptor.setText(destinatario.getUsername());
        this.mensajes_destinatario = this.client.getMensajes().get(destinatario.getUsername());
        this.client.leer(destinatario);
        for(Mensaje m : this.mensajes_destinatario)
            this.mostrarMensaje(m);

        this.mensajes_destinatario.addListener(new ListChangeListener<Mensaje>() {
            @Override
            public void onChanged(Change<? extends Mensaje> c) {
                c.next();
                for(Mensaje m: c.getAddedSubList()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            destinatario.setMensajesNoLeidos(0+"");
                            mostrarMensaje( m );
                        }
                    });
                }
            }
        });
    }

    void mostrarMensaje( Mensaje mensaje){
        String colorM = "";
        if(mensaje.getRecibido())
            colorM = this.colorR;
        else
            colorM = this.colorE;
        Text texto = null;
        if(mensaje.getAutor().getUsername().equals(this.client.getUsername()))
            texto = new Text("Tu: \n"+mensaje.getContenido());
        else
            texto = new Text(mensaje.getAutor().getUsername()+"\n"+mensaje.getContenido());
        texto.wrappingWidthProperty();
        texto.setWrappingWidth(282);
        VBox contenedor = new VBox();
        contenedor.setBackground(new Background(new BackgroundFill(Color.web(colorM), CornerRadii.EMPTY, Insets.EMPTY)));
        contenedor.setPadding(new Insets(5));
        contenedor.getChildren().add(texto);
        this.panelConversacion.getChildren().add(contenedor);
    }


}
