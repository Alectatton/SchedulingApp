/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.Customer;
import static View.CustomersController.getSelectedCustomer;
import static View.MainScreenController.getSelectedAppointment;
import java.io.IOException;
import static java.lang.String.valueOf;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static java.time.temporal.TemporalQueries.localTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class modAppointmentController implements Initializable {

    //Initialize variables and FXML
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    
    //Disabled Text Boxes
    @FXML private TextField appointmentIdBox;
    @FXML private TextField customerIdBox;
    @FXML private TextField customerNameBox;
    
    //Text Boxes
    @FXML private TextField titleBox;
    @FXML private TextField descriptionBox;
    @FXML private TextField locationBox;
    @FXML private TextField contactBox;
    @FXML private TextField typeBox;
    
    //Date and time
    @FXML private ComboBox startTimeBox;
    @FXML private ComboBox endTimeBox;
    @FXML private DatePicker dateBox;
    
    //Combo box
    @FXML private ComboBox<Customer> customerBox;

    //Lists
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<String> customerNames = FXCollections.observableArrayList();
    ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();   
    
    //Handle submit button
    public void handleSubmitButton () throws IOException, SQLException {
        if (validInfo()) {
            saveAppointment();
            Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) cancelButton.getScene().getWindow();
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
       
    //Handle cancel button
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
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
        try {
            updateCustomerList();
            updateInfo();
        } catch (SQLException ex) {
            Logger.getLogger(modAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private void updateCustomerList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Customer_ID, Customer_Name FROM customers";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while (result.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(result.getInt("Customer_ID"));
            customer.setCustomerName(result.getString("Customer_Name"));           
            customers.addAll(customer);
            customerNames.add(customer.customerName);
        }
        
        for (int i = 0; i < 8; i++) {
            startTimes.add(LocalTime.of(i + 8, 0, 0));
            endTimes.add(LocalTime.of(i + 9, 0, 0));
        }
        
        customerBox.setItems(customers);
        startTimeBox.setItems(startTimes);
        endTimeBox.setItems(endTimes);
    }

    //Verify all input is valid
    public boolean validInfo() {
        return true;
    }
    
    //Update customer info
    public void updateCustomer() {
        Customer customer = (Customer) customerBox.getSelectionModel().getSelectedItem();;
        customerIdBox.setText(valueOf(customer.getCustomerId()));
        customerNameBox.setText(customer.getCustomerName());
    }

    //Save appointment
    private void saveAppointment() throws IOException, SQLException { 
        
        //Create variables
        String appointmentId = appointmentIdBox.getText();
        String title = titleBox.getText();
        String description = descriptionBox.getText();
        String location = locationBox.getText();
        String type = typeBox.getText();
        String contactId = contactBox.getText();
        LocalDate date = dateBox.getValue();
        LocalTime startTime = (LocalTime) startTimeBox.getValue();
        LocalTime endTime = (LocalTime) endTimeBox.getValue();
        String startString = date.toString() + " " + startTime.toString() + ":00";
        String endString = date.toString() + " " + endTime.toString() + ":00";
        String lastUpdatedBy = "User";
        String custId = customerIdBox.getText();
        int userId = 1;   
        
        //Create the query
        Statement statement = DBConnection.conn.createStatement();                
        String sqlInsert = "UPDATE appointments ";        
        String sqlValues = 
                "SET Title = '" + title + "', Description = '" + description + "', Location = '" + location + "', Type = '" + type
                + "', Start = '" + startString + "', End = '" + endString + "', Last_Updated_By = '" + lastUpdatedBy
                + "', Customer_ID = '" + custId + "', User_ID = '" +userId + "', Contact_ID = '" + contactId + "' ";
        String sqlWhere = "WHERE Appointment_ID = " + appointmentId + ";";
        String sqlStatement = sqlInsert + sqlValues + sqlWhere;
        
        //Execute the query
        System.out.println(sqlStatement);
        statement.execute(sqlStatement);

    }

    //Set text boxes
    private void updateInfo() {
        Appointment selectedAppointment = getSelectedAppointment();
        
        appointmentIdBox.setText(String.valueOf(selectedAppointment.getAppointmentID()));
        customerIdBox.setText(String.valueOf(selectedAppointment.getCustId()));
        titleBox.setText(selectedAppointment.getTitle());
        descriptionBox.setText(selectedAppointment.getDescription());
        locationBox.setText(selectedAppointment.getLocation());
        typeBox.setText(selectedAppointment.getType());
        contactBox.setText(selectedAppointment.getContact());
        
        //dateBox.set
        //startTimeBox.set(selectedAppointment.getStartTime());
        //endTimeBox.set();
    }

}
