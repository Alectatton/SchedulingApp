/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Country;
import Model.Division;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    
    //Text boxes
    @FXML private TextField idText;
    @FXML private TextField nameText;
    @FXML private TextField addressText;
    @FXML private TextField postalText;
    @FXML private TextField phoneText;
    
    //Combo Boxes
    @FXML private ComboBox countryBox;
    @FXML private ComboBox divisionBox;
    
    //Lists
    ObservableList<Country> countries = FXCollections.observableArrayList();
    ObservableList<Division> divisions = FXCollections.observableArrayList();
    
     /**
     * Method to save customer
     * Verify all input, execute SQL query and then return to the main screen
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    public void handleSaveButton () throws SQLException, IOException {  
        if (isValid()) {            
            //Initialize Variables
            String name = nameText.getText();
            String address = addressText.getText();
            String postal = postalText.getText();
            String phone = phoneText.getText();
            String createdBy = "Test";
            String lastUpdatedBy = createdBy;
            Division division = (Division) divisionBox.getSelectionModel().getSelectedItem();
            int divisionId = division.getDivisionId();
        
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
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input!");
            alert.setContentText("Double check EVERY field");
            alert.showAndWait();
        }
    }
    
    /**
    * Method to check if all fields are filled
    * @return
    */
    private boolean isValid() {
        try {
            if (nameText.getText().isEmpty() ||
                addressText.getText().isEmpty() ||
                postalText.getText().isEmpty() ||
                phoneText.getText().isEmpty() ||
                "".equals(divisionBox.getSelectionModel().getSelectedItem().toString())) {
                return false;
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            return false;
        }
    }
    
    /**
    * Method to update the country list after a country is selected
     * @throws java.sql.SQLException
    */
    public void countryList() throws SQLException {     
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Country, Country_ID FROM countries WHERE Country_ID = 231 OR Country_ID = 230 OR Country_ID = 38 GROUP BY Country_ID;";               
        ResultSet result = statement.executeQuery(sqlStatement);
            
        while (result.next()) {         
            Country country = new Country();  
            country.setCountryId(result.getInt("Country_ID"));
            country.setCountryName(result.getString("Country"));
            countries.addAll(country);
        }               
        countryBox.setItems(countries);
    }
    
    /**
    * Method to update division list
     * @throws java.sql.SQLException
    */
    public void updateDivision() throws SQLException {
        Country country = (Country) countryBox.getSelectionModel().getSelectedItem();
        int id = country.getCountryId();
        divisionBox.getItems().clear();       
        
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Division, Division_ID FROM first_level_divisions WHERE COUNTRY_ID = '" + id + "';";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while (result.next()) {
            Division division = new Division();
            division.setDivision(result.getString("Division"));
            division.setDivisionId(result.getInt("Division_ID"));
            divisions.addAll(division);
        }
        divisionBox.setItems(divisions);
    }
    
     /**
     * Method to cancel and return to the main screen
     * @throws java.io.IOException
     */
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);
        window.show(); 
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            countryList();
        } catch (SQLException ex) {
            Logger.getLogger(AddCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    
    
}
