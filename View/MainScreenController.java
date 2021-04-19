/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class MainScreenController implements Initializable {

    //Initialize variables and FXML
    @FXML private Button addAppointment;
    @FXML private Button updateAppointment;
    @FXML private Button removeAppointment;
    @FXML private Button customers;
    
    //Add appointment handler
    public void handleAddAppointment () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("addAppointment.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) addAppointment.getScene().getWindow();
        window.setScene(scene);
        window.show(); 
    }
    
    //update appointment handler
    public void handleUpdateAppointment() {
        
    }
    
    //remove appointment handler
    public void handleDeleteAppointment() {
        
    }
    
    //customers page handler
    public void handleCustomersButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) customers.getScene().getWindow();
        window.setScene(scene);
        window.show(); 
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
