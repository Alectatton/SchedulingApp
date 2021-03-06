/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Contact;
import java.io.IOException;
import static java.lang.String.valueOf;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import utils.DBConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class ReportsController implements Initializable {

    @FXML private Button homeButton;
    @FXML private RadioButton byMonth;
    @FXML private RadioButton byContact;
    @FXML private RadioButton reportThree;
    @FXML private ComboBox<Contact> contactBox;
    @FXML private TextArea display;
    ObservableList<Contact> contacts = FXCollections.observableArrayList();
    
    /**
     * Handle returning to the main screen
     * @throws java.io.IOException
     */
    public void handleHomeButton() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) homeButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    /**
     * Generate report for appointments by month and type
     * @throws java.sql.SQLException
     */
    public void handleByMonth() throws SQLException {      
        String msg = "Number of appointments per month by type \n\n";
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Type FROM appointments GROUP BY Type";
        ResultSet result = statement.executeQuery(sqlStatement);
        List<String> typeList = new ArrayList<String>();
        display.setText(msg);    
        
        while(result.next()) {
            String typeString = result.getString("Type");
            typeList.add(typeString);
        }
        
        for (String type : typeList) {
            sqlStatement = "SELECT monthname(Start) as Month, Count(monthname(start)) as Count FROM appointments WHERE Type='" + type + "' GROUP BY monthname(Start)";
            result = statement.executeQuery(sqlStatement);
            String message = "";
            while(result.next()) {
                String month = result.getString("Month");
                String count = result.getString("Count");
                message = message + month + "\t\t\t\t\t\t" + count + "\n";
            }
            String header = "\n\nAppointments by type: " + type + "\nMonth \t\t\t\t\t Count \n";
            String output = header + message;
            display.appendText(output);
        }
    }
    
    /**
     * Generate the report with a schedule for the selected contact
     * @throws java.sql.SQLException
     */
    public void handleByContact() throws SQLException {
        if (contactBox.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a contact");
            alert.setContentText("Please select a contact!");
            alert.showAndWait();
            byMonth.setSelected(true);
        }
        else {
            Contact contact = (Contact) contactBox.getSelectionModel().getSelectedItem();
            String ID = valueOf(contact.getContactId());
            String name = valueOf(contact.getContactName());
            String title = "Schedule for contact: " + name + "\n\n\n";
            String header = "Appointment ID \t\t Title \t\t Type \t\t Description \t\t Start \t\t End \t\t Customer ID \n\n";
            String sql = "";
        
            Statement statement = DBConnection.conn.createStatement();
            String sqlStatement = "SELECT Appointment_ID, Title, Type, Description, Start, End FROM appointments WHERE Contact_ID = '" + ID + "';";
            ResultSet result = statement.executeQuery(sqlStatement);
        
            while(result.next()) {
                String appt = result.getString("Appointment_ID");
                String titleAppt = result.getString("Title");
                String type = result.getString("Type");
                String desc = result.getString("Description");
                String start = result.getString("Start");
                String end = result.getString("End");
                String split = "\t\t";
                       
                sql = sql + appt + split + titleAppt + split + type + split + desc + split + start + split + end + split + ID + "\n";
            }

            String msg = title + header + sql;
            display.setText(msg);
        }
    }
    
    /**
     * Report of how many appointments created by each user
     * @throws java.sql.SQLException
     */
    public void handleReportThree() throws SQLException {
        
        String header = "Number of appointments created by each user \n\n User \t\t\t # of appointments \n\n";
        String sql = "";
        
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Created_By, Count(Created_By) FROM appointments GROUP BY Created_By;";
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while(result.next()) {
            String user = result.getString("Created_By");
            String count = result.getString("Count(Created_By)");
            sql = sql + user + "\t\t\t\t" + count + "\n";
        }
        
        String msg = header + sql;
        display.setText(msg);
    }
    
    /**
     * Handle selecting a customer
     * @throws java.sql.SQLException
     */
    public void handleContactChange() throws SQLException {
        byContact.setSelected(true);
        handleByContact();
    }
    
    /**
     * Add all of the customers in the database into the customer list
     * @throws java.sql.SQLException
     */
    public void updateContactList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Contact_ID, Contact_Name FROM contacts";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        //Add customers from database into customer list
        while (result.next()) {
            Contact contact = new Contact();
            contact.setContactId(result.getInt("Contact_ID"));
            contact.setContactName(result.getString("Contact_Name"));           
            contacts.addAll(contact);
        }
        
        //Set drop boxes
        contactBox.setItems(contacts);
    }
        

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup reportType = new ToggleGroup();
        byMonth.setToggleGroup(reportType);
        byContact.setToggleGroup(reportType);
        reportThree.setToggleGroup(reportType);
        byMonth.setSelected(true);
        try {
            handleByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            updateContactList();
        } catch (SQLException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
