package sample.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private StringProperty id;
    private String estado;

    public User(String id){
        this.id = new SimpleStringProperty(id);
    }

    public StringProperty getId() {
        return id;
    }
}
