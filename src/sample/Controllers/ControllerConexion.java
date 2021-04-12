package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.Main;
import sample.RMI.Client.Client;


public class ControllerConexion {
    private Main mainApp;

    @FXML
    private TextField txtfield_username;

    @FXML
    private PasswordField psfield_password;

    @FXML
    private TextField txtfield_hostname;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistro;

    @FXML
    private TextField txtfield_port;

    @FXML
    private Text txt_error;

    @FXML
    void Conectar(ActionEvent event) {
        if(this.txtfield_port.getText().isEmpty() || this.txtfield_hostname.getText().isEmpty() || this.txtfield_username.getText().isEmpty() || this.psfield_password.getText().isEmpty()){
            this.txt_error.setVisible(true);
        }else{
            this.txt_error.setVisible(false);
            try{
                this.mainApp.setClient(new Client(this.txtfield_port.getText(), this.txtfield_hostname.getText(), this.txtfield_username.getText(), this.psfield_password.getText()));
                this.mainApp.getClient().iniciarSesion();
                this.mainApp.iniciarChat();
            }catch(Exception e){
                System.out.println("Exception ControllerConexion Conexion Servidor: "+e);
                this.txt_error.setVisible(true);
            }
        }
    }

    @FXML
    void Registrar(ActionEvent event) {
        if(this.txtfield_port.getText().isEmpty() || this.txtfield_hostname.getText().isEmpty() || this.txtfield_username.getText().isEmpty() || this.psfield_password.getText().isEmpty()){
            this.txt_error.setVisible(true);
        }else{
            this.txt_error.setVisible(false);
            try{
                this.mainApp.setClient(new Client(this.txtfield_port.getText(), this.txtfield_hostname.getText(), this.txtfield_username.getText(), this.psfield_password.getText()));
                this.mainApp.getClient().registrarse();
                this.mainApp.iniciarChat();
            }catch(Exception e){
                System.out.println("Exception ControllerConexion Conexion Servidor: "+e);
                this.txt_error.setVisible(true);
            }
        }
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
