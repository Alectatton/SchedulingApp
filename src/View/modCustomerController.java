/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Customer;
import static View.CustomersController.getSelectedCustomer;
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
public class modCustomerController implements Initializable {

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
        Customer selectedCustomer = CustomersController.getSelectedCustomer();
        int customerId = selectedCustomer.getCustomerId();
        
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
        String sqlInsert = "UPDATE customers";        
        String sqlValues = " SET Customer_Name = '"+ name + "' , Address = '" + address + "' , Postal_Code = '" + postal + "' , Phone = '" + phone + "' , Last_Update = CURRENT_TIMESTAMP, Last_Updated_By = '" + lastUpdatedBy + "' , Division_ID = " + divisionId;
        String sqlWhere = " WHERE Customer_ID = " + customerId + ";";
        String sqlStatement = sqlInsert + sqlValues + sqlWhere;
        statement.execute(sqlStatement);
        
        //Return to the customers screen
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) saveButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    //Handle Cancel button action
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Customer selectedCustomer = getSelectedCustomer();
        nameText.setText(selectedCustomer.getCustomerName());
        addressText.setText(selectedCustomer.getCustomerAddress());
        postalText.setText(selectedCustomer.getCustomerPostal());
        phoneText.setText(selectedCustomer.getCustomerPhone());
    }    


    
}
