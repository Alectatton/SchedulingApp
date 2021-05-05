/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Country;
import Model.Customer;
import Model.Division;
import static View.modCustomerController.countries;
import static View.modCustomerController.divisions;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class CustomersController implements Initializable {
    
    //Initialize variables and FXML
    @FXML private Button mainScreenButton;
    @FXML private Button addCustomerButton;
    @FXML private Button removeCustomerButton;
    @FXML private Button updateCustomerButton;
    
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> customerIdCol;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> customerAddressCol;
    @FXML private TableColumn<Customer, String> customerPostalCol;
    @FXML private TableColumn<Customer, String> customerPhoneCol;
    
    static Customer selectedCustomer;
    
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    static ObservableList<Country> countries = FXCollections.observableArrayList();
    static ObservableList<Division> divisions = FXCollections.observableArrayList();
    
    
    /**
     * Method to go back to the main screen
     * @throws java.io.IOException
     */
    public void handleGoBack () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) mainScreenButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    /**
     * Method to go to the add customer screen
     * @throws java.io.IOException
     */
    public void handleAddCustomer () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("addCustomer.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) addCustomerButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }    
   
    /**
     * Method to handle updating the selected customer
     * @throws java.io.IOException
     */
    public void handleUpdateCustomer () throws IOException {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            noSelectionAlert();
        }        
        else {
            Parent loader = FXMLLoader.load(getClass().getResource("modCustomer.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) updateCustomerButton.getScene().getWindow();
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
    }
     
    /**
     *  Method to remove the selected customer
     * @throws java.sql.SQLException
     */
    public void handleRemoveCustomer () throws SQLException {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();        
        if (selectedCustomer == null) {
            noSelectionAlert();
        }
        else {
            try {
                int selectedId = selectedCustomer.getCustomerId();  
                Statement statement = DBConnection.conn.createStatement();
                String sqlStatement = "DELETE FROM customers WHERE Customer_ID = " + selectedId + ";";        
                statement.execute(sqlStatement);      
                customers.removeAll(customers);
                updateCustomersTable();
                Alert nullalert = new Alert(Alert.AlertType.CONFIRMATION);
                nullalert.setTitle("Success!");
                nullalert.setContentText("Selected appointment removed");
                nullalert.showAndWait();
            }
            catch (SQLException e) {
                Alert nullalert = new Alert(Alert.AlertType.ERROR);
                nullalert.setTitle("Customer has an appointment");
                nullalert.setContentText("You must delete all associated appointments before removing");
                nullalert.showAndWait();
            }
        }
    }
    
     /**
     * Method to update the customers table and List with all of the customers in the database
     * @throws java.sql.SQLException
     */
    public void updateCustomersTable () throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers ORDER BY customers.Customer_ID";        
        ResultSet result = statement.executeQuery(sqlStatement);
        while (result.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(result.getInt("Customer_ID"));
            customer.setCustomerName(result.getString("Customer_Name"));
            customer.setCustomerAddress(result.getString("Address"));
            customer.setCustomerPostal(result.getString("Postal_Code"));
            customer.setCustomerPhone(result.getString("Phone"));
            customer.setCustomerDivisionId(result.getInt("Division_ID"));
            customer.setCustomerDivision(findDivision(result.getInt("Division_ID")));
            customer.setCustomerCountry(findCountry(result.getInt("Division_ID")));
            customers.addAll(customer);
        }        
        customerTable.setItems(customers);
    }
    
    /**
     * Find the division for each given customer based on the division ID
     * @param id
     * @return
     * @throws SQLException 
     */
    public String findDivision(int id) throws SQLException {
        Statement divStatement = DBConnection.conn.createStatement();
        String divSQL = "SELECT Division FROM first_level_divisions WHERE Division_ID = '" + id + "';";
        ResultSet divisionResult = divStatement.executeQuery(divSQL);
        divisionResult.next();
        return divisionResult.getString("Division");
    }
    
    /**
     * Find the country for each given customer based on the division ID
     * @param id
     * @return
     * @throws SQLException 
     */
    public String findCountry (int id) throws SQLException {
        Statement countryStatement = DBConnection.conn.createStatement();
        String countrySQL = "SELECT Country FROM countries WHERE countries.Country_ID = (SELECT Country_ID FROM first_level_divisions WHERE Division_ID = '" + id + "');";
        ResultSet countryResult = countryStatement.executeQuery(countrySQL);
        countryResult.next();
        return countryResult.getString("Country");
    }
    
    /**
     * Method to alert if no customer is selected
     */
    private void noSelectionAlert() {
        Alert nullalert = new Alert(Alert.AlertType.ERROR);
        nullalert.setTitle("No Selection");
        nullalert.setContentText("You must make a selection before you can do this action");
        nullalert.showAndWait();
    }
    
    /**
     *
     * Method to get the selected customer
     * @return
     */
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }
    
    public void updateCountriesList() throws SQLException {     
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Country, Country_ID FROM countries GROUP BY Country_ID;";               
        ResultSet result = statement.executeQuery(sqlStatement);
            
        while (result.next()) {         
            Country country = new Country();  
            country.setCountryId(result.getInt("Country_ID"));
            country.setCountryName(result.getString("Country"));
            countries.addAll(country);
        }               
    }
    
    public void updateDivisionList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Division, Division_ID FROM first_level_divisions;";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while (result.next()) {
            Division division = new Division();
            division.setDivision(result.getString("Division"));
            division.setDivisionId(result.getInt("Division_ID"));
            divisions.addAll(division);
        }
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        customerIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        customerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        customerAddressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerAddress()));
        customerPostalCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPostal()));
        customerPhoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerPhone()));
        
        try {
            updateCustomersTable();
            updateCountriesList();
            updateDivisionList();
        } catch (SQLException ex) {
            Logger.getLogger(CustomersController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }  
    
}
