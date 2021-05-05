/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Country;
import Model.Customer;
import Model.Division;
import static View.CustomersController.getSelectedCustomer;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class modCustomerController implements Initializable {

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
    static ObservableList<Country> countries = FXCollections.observableArrayList();
    static ObservableList<Division> divisions = FXCollections.observableArrayList();
    
  
    /**
     * Method to handle the save button action
     * Verify all input, execute SQL query and then return to the main screen
     * @throws SQLException
     * @throws IOException 
     */
    public void handleSaveButton () throws SQLException, IOException {
        Customer selectedCustomer = CustomersController.getSelectedCustomer();
        int customerId = selectedCustomer.getCustomerId();
        
        if (isValid()) {
            //Initialize Variables
            String name = nameText.getText();
            String address = addressText.getText();
            String postal = postalText.getText();
            String phone = phoneText.getText();
            String lastUpdatedBy = "Test";
            Division division = (Division) divisionBox.getSelectionModel().getSelectedItem();
            int divisionId = division.getDivisionId();  
          
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
    * Method to update the country list
     * @throws java.sql.SQLException
    */
    public void countryList() throws SQLException {     
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Country, Country_ID FROM countries GROUP BY Country_ID;";               
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
    * Method to update division list after a country is selected
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
    }
    
    /**
     * Method to handle the cancel button
     * @throws IOException 
     */
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    /**
     * Get the country object from the string country name
     * @param name
     * @return 
     */
    public Country getCountryFromName(String name) {
       for (Country country : countries) {
            if (name.equals(country.getCountryName())) {
                return country;
            }
        }
        return null;  
    }
    
    /**
     * Get the division object from the string division name
     * @param name
     * @return 
     */
    public Division getDivisionFromName(String name) {
        for (Division division : divisions) {
            if (name.equals(division.getDivision())) {
                return division;
            }
        }
        return null;
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Customer selectedCustomer = getSelectedCustomer();
        
        try {
            countryList();
            countryBox.setItems(countries);
            countryBox.getSelectionModel().select(getCountryFromName(selectedCustomer.getCustomerCountry()));
        } catch (SQLException ex) {
            Logger.getLogger(modCustomerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            updateDivision();
            divisionBox.setItems(divisions);
            divisionBox.getSelectionModel().select(getDivisionFromName(selectedCustomer.getCustomerDivision()));
        } catch (SQLException ex) {
            Logger.getLogger(modCustomerController.class.getName()).log(Level.SEVERE, null, ex);        
        }

        nameText.setText(selectedCustomer.getCustomerName());
        addressText.setText(selectedCustomer.getCustomerAddress());
        postalText.setText(selectedCustomer.getCustomerPostal());
        phoneText.setText(selectedCustomer.getCustomerPhone());  
    }
    
    
}
