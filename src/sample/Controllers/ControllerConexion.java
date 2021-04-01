package sample.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sample.Main;
import sample.RMI.Client;

public class ControllerConexion {
    private Main mainApp;

    @FXML
    private TextField txtfield_alias;

    @FXML
    private TextField txtfield_hostname;

    @FXML
    private Button btnConectar;

    @FXML
    private TextField txtfield_port;

    @FXML
    void Conectar(ActionEvent event) {
        this.mainApp.setClient(new Client(this.txtfield_port.getText(), this.txtfield_hostname.getText(), this.txtfield_alias.getText()));
        this.mainApp.iniciarChat();
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
