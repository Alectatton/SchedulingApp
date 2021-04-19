/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class AddCustomerController implements Initializable {

    //Initialize variables and FXML
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    @FXML private TextField idText;
    @FXML private TextField nameText;
    @FXML private TextField addressText;
    @FXML private TextField postalText;
    @FXML private TextField phoneText;
    
    //Handle Save button action
    public void handleSaveButton () throws SQLException, IOException {
        //Initialize Variables
        String name = nameText.getText();
        String address = addressText.getText();
        String postal = postalText.getText();
        String phone = phoneText.getText();
        String createdBy = "User"; //Need to make this find current user
        String lastUpdatedBy = createdBy;
        int divisionId = 43; 
          
        //Execute the query
        Statement statement = DBConnection.conn.createStatement();
        String sqlInsert = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) ";        
        String sqlValues = "Values ('" + name + "', '" + address + "', '" + postal + "', '" + phone + "', CURRENT_TIMESTAMP, '" + createdBy + "', CURRENT_TIMESTAMP, '" + lastUpdatedBy + "', '" + divisionId + "');";
        String sqlStatement = sqlInsert + sqlValues;
        statement.execute(sqlStatement);
        
        //Return to the customers screen
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) saveButton.getScene().getWindow();
        window.setScene(scene);
        window.show(); 
    }
    
    //Handle Cancel button action
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) cancelButton.getScene().getWindow();
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
