/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.Contact;
import Model.Customer;
import Model.User;
import static View.Log_InController.appointments;
import java.io.IOException;
import static java.lang.String.valueOf;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
    @FXML private TextField userIdBox;
    
    //Combo boxes
    @FXML private ComboBox startTimeBox;
    @FXML private ComboBox endTimeBox;
    @FXML private ComboBox contactList;
    @FXML private ComboBox<User> userBox;
    @FXML private ComboBox<Customer> customerBox;
    @FXML private DatePicker dateBox;
    
    
    //Lists
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    ObservableList<Contact> contacts = FXCollections.observableArrayList();
    ObservableList<LocalTime> startTimes = FXCollections.observableArrayList();
    ObservableList<LocalTime> endTimes = FXCollections.observableArrayList();   
    ObservableList<User> users = FXCollections.observableArrayList();
    
    public LocalDateTime now = LocalDateTime.now();
    public ZoneId zone = ZoneId.of("UTC");
    public ZoneOffset zoneOffset = zone.getRules().getOffset(now);
    
    /**
     * Handle the submit button
     * @throws IOException
     * @throws SQLException 
     */
    public void handleSubmitButton () throws IOException, SQLException {
        if (validInfo() && !isClosed()) {
            saveAppointment();
            Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) submitButton.getScene().getWindow();
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
        else if (isClosed()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Outside hours!");
            alert.setContentText("The office is open from 8AM to 10PM EST 7 Days a week");
            alert.showAndWait();
        }
        else if (isOverlapping()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Overlapping appointment!");
            alert.setContentText("Appointment is scheduled at the same time as another");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input!");
            alert.setContentText("Check that all fields are filled with the correct Data.");
            alert.showAndWait();
        }
    }
    
    /**
     * Method to handle cancelling and returning to the main screen
     * @throws java.io.IOException
     */
    public void handleCancelButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) cancelButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }

    /**
     * Update the contact list
     * @throws SQLException 
     */
    private void updateContactList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Contact_ID, Contact_Name FROM contacts";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while (result.next()) {
            Contact contact = new Contact();
            contact.setContactId(result.getInt("Contact_ID"));
            contact.setContactName(result.getString("Contact_Name"));
            contacts.addAll(contact);
        }     
        contactList.setItems(contacts);
    }
    
    /**
     * Update the user list
     * @throws SQLException 
     */
    private void updateUserList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT User_Name, User_ID FROM users";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while (result.next()) {
            User user = new User();
            user.setUserId(result.getInt("User_ID"));
            user.setUsername(result.getString("User_Name"));
            users.addAll(user);
        }     
        userBox.setItems(users);
    }
    
    /**
     * Update the user ID box with selected users ID
     */
    public void updateUserId() {
        User user = (User) userBox.getSelectionModel().getSelectedItem();
        userIdBox.setText(valueOf(user.getUserId()));
    }
    
    /**
     * Update contact ID box with the selected contact
     */
    public void updateContact() {
        Contact contact = (Contact) contactList.getSelectionModel().getSelectedItem();
        contactBox.setText(valueOf(contact.getContactId()));
    }

   /**
    * Update the customer and time drop boxes
    * @throws SQLException 
    */
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
        
        //Allow scheduling on each hour
        for (int i = 0; i < 23; i++) {            
            startTimes.add(LocalTime.of(i + 0, 0, 0));
            endTimes.add(LocalTime.of(i + 1, 0, 0));
        }
                             
        //Set drop boxes
        customerBox.setItems(customers);
        startTimeBox.setItems(startTimes);
        endTimeBox.setItems(endTimes);
    }
    
        
    /**
    * Method to check if the scheduled appointment is outside of office hours (8AM - 10PM EST 7 days a week)
    */
    
    private boolean isClosed() {
        LocalTime appointmentStart = (LocalTime) startTimeBox.getValue();
        LocalTime appointmentEnd = (LocalTime) endTimeBox.getValue();
        
        LocalDateTime hoursStart = LocalDateTime.of(2020, 6, 1, 8, 0);
        LocalDateTime hoursEnd = LocalDateTime.of(2020, 6, 1, 22, 0);
        
        ZonedDateTime zonedStart = hoursStart.atZone(ZoneId.of("US/Eastern"));
        Instant instantStart = zonedStart.toInstant();
        LocalDateTime ldtStart = instantStart.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalTime startTime = ldtStart.toLocalTime();
        
        ZonedDateTime zonedEnd = hoursEnd.atZone(ZoneId.of("US/Eastern"));
        Instant instantEnd = zonedEnd.toInstant();
        LocalDateTime ldtEnd = instantEnd.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalTime endTime = ldtEnd.toLocalTime();
        
        if (appointmentStart.isBefore(endTime) && startTime.isBefore(appointmentEnd)) {
            return false;
        }
        else {                    
            return true;
        }
    }

    /*
    * Verify all fields are correct for the input
    */
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
    
    /**
     * Update the customer boxes with info from the selected customer
     */
    public void updateCustomer() {
        Customer customer = (Customer) customerBox.getSelectionModel().getSelectedItem();
        customerIdBox.setText(valueOf(customer.getCustomerId()));
        customerNameBox.setText(customer.getCustomerName());
    }

    /**
     * Collect information and add to the SQL database
     * @throws IOException
     * @throws SQLException 
     */
    private void saveAppointment() throws IOException, SQLException { 
        
        //Collect appointment information
        String title = titleBox.getText();
        String description = descriptionBox.getText();
        String location = locationBox.getText();
        String type = typeBox.getText();
        String contactId = contactBox.getText();
        String createdBy = userBox.getSelectionModel().getSelectedItem().getUsername();
        String lastUpdatedBy = createdBy;
        String custId = customerIdBox.getText();
        String userId = userIdBox.getText(); 
        
        //Get the date and times from the user interface
        LocalDate date = dateBox.getValue();
        LocalTime startTime = (LocalTime) startTimeBox.getValue();        
        LocalTime endTime = (LocalTime) endTimeBox.getValue();
        ZoneId localId = ZoneId.systemDefault();
        
        //Convert the times to UTC and to a string
        ZonedDateTime startZoned = ZonedDateTime.of(date, startTime, localId);
        ZonedDateTime endZoned = ZonedDateTime.of(date, endTime, localId);
        Instant startToGMT = startZoned.toInstant();
        Instant endToGMT = endZoned.toInstant();        
        LocalDateTime startConverted = startToGMT.atZone(ZoneId.of("Etc/UTC")).toLocalDateTime();
        LocalDateTime endConverted = endToGMT.atZone(ZoneId.of("Etc/UTC")).toLocalDateTime();
        String startString = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(startConverted);
        String endString = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(endConverted);    
            
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

    /**
     * Check if the current appointment is overlapping with other appointments
     * Contains Lambda expression
     * @return 
     */
    private boolean isOverlapping() {
        LocalDate startDate = dateBox.getValue();
        LocalTime startTime = (LocalTime) startTimeBox.getValue();
        LocalTime endTime = (LocalTime) endTimeBox.getValue();
        LocalDateTime startDateTime = startDate.atTime(startTime);
        LocalDateTime endDateTime = startDate.atTime(endTime);
        
        //Lambda expression to filter appointments and check for overlap
        FilteredList<Appointment> overlapping = new FilteredList<>(appointments);
        overlapping.setPredicate(appointmentToCheck -> {
            LocalDateTime compStart = appointmentToCheck.getStartTime();
            LocalDateTime compEnd = appointmentToCheck.getEndTime();
            if (startDateTime.isBefore(compEnd) && compStart.isBefore(endDateTime)){
                return true;
            }
            return false;
        }
        );
        
        if (overlapping.isEmpty()) {
            return false;
        }
        else {
            return true;
        }  
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
            updateContactList();
            updateUserList();
        } catch (SQLException ex) {
            Logger.getLogger(AddAppointmentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
