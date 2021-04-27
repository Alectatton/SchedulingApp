/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.Customer;
import static View.Log_InController.appointments;
import java.io.IOException;
import static java.lang.String.valueOf;
import static java.lang.System.in;
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
public class AddAppointmentController implements Initializable {

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
    ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();   
    
    //Handle submit button
    public void handleSubmitButton () throws IOException, SQLException {
        if (validInfo()) {
            saveAppointment();
            Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) submitButton.getScene().getWindow();
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input!");
            alert.setContentText("Check that all fields are filled with the correct Data. Check that there are no overlapping appointments");
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
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    //Update the customer list and time boxes
    private void updateCustomerList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Customer_ID, Customer_Name FROM customers";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        //Add customers from database into customer list
        while (result.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(result.getInt("Customer_ID"));
            customer.setCustomerName(result.getString("Customer_Name"));           
            customers.addAll(customer);
        }
        
        //Allow scheduling on the hour from 8AM to 10PM
        for (int i = 0; i < 14; i++) {            
            startTimes.add(LocalTime.of(i + 8, 0, 0));
            endTimes.add(LocalTime.of(i + 9, 0, 0));
        }
        
        //Set drop boxes
        customerBox.setItems(customers);
        startTimeBox.setItems(startTimes);
        endTimeBox.setItems(endTimes);
    }

    //Verify all input is valid
    public boolean validInfo() {
        if (customerIdBox.getText().isEmpty() ||
                titleBox.getText().isEmpty() ||
                descriptionBox.getText().isEmpty() ||
                locationBox.getText().isEmpty() ||
                contactBox.getText().isEmpty() ||
                typeBox.getText().isEmpty() ||
                startTimeBox.getItems().isEmpty() ||
                endTimeBox.getItems().isEmpty() ||
                dateBox.getConverter().toString().isEmpty()) {     
            return false;            
        }
        else if (isOverlapping()) {
            return false;
        }
        else {
            return true;
        }
    }
    
    //Update customer info
    public void updateCustomer() {
        Customer customer = (Customer) customerBox.getSelectionModel().getSelectedItem();;
        customerIdBox.setText(valueOf(customer.getCustomerId()));
        customerNameBox.setText(customer.getCustomerName());
    }

    //Save appointment and add to the database
    private void saveAppointment() throws IOException, SQLException { 
        
        //Create variables
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
        String createdBy = "User";
        String lastUpdatedBy = "User";
        String custId = customerIdBox.getText();
        int userId = 1;   
        
        //Execute the query
        Statement statement = DBConnection.conn.createStatement();
        String sqlInsert = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, " + 
                "Created_By, Last_Updated_By, Customer_ID, User_ID, Contact_ID) ";        
        String split = "', '";
        String sqlValues = "VALUES('" + title + split + description + split + location + split + type + split + 
                startString + split + endString + split + createdBy + split + lastUpdatedBy + split + custId + split + 
                userId + split + contactId + "');";
        String sqlStatement = sqlInsert + sqlValues;
        System.out.println(sqlStatement);
        statement.execute(sqlStatement);
    }   

    private boolean isOverlapping() {
        LocalDate startDate = dateBox.getValue();
        LocalTime startTime = (LocalTime) startTimeBox.getValue();
        LocalTime endTime = (LocalTime) endTimeBox.getValue();
        LocalDateTime startDateTime = startDate.atTime(startTime);
        LocalDateTime endDateTime = startDate.atTime(endTime);
        //ObservableList<Appointment> appointmentsToCompare = FXCollections.observableArrayList();
       
        for (Appointment appointment : appointments) {
            LocalDateTime compStart = appointment.getStartTime();
            LocalDateTime compEnd = appointment.getEndTime();
            if (startDateTime.isBefore(compEnd) && compStart.isBefore(endDateTime)) {
                return true;
            }
        }       
        return false;
    }
}
