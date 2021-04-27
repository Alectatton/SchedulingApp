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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import utils.DBConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
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
    @FXML private RadioButton byCustomer;
    @FXML private RadioButton reportThree;
    @FXML private ComboBox<Customer> customerBox;
    @FXML private TextArea display;
    ObservableList<Customer> customers = FXCollections.observableArrayList();
    
    
    public void handleHomeButton() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) homeButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    public void handleByMonth() throws SQLException {
        String msgType = "Type \t\t\t\t\t\t Count \n";
        String typeReturn = "";
        String msgSplit = "\n\n";
        String msgMonth = "Month \t\t\t\t\t\t Count \n";
        String monthReturn = "";
        
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Type, Count(Type) FROM appointments GROUP BY Type";
        ResultSet result = statement.executeQuery(sqlStatement);
        
        while(result.next()) {
            String type = result.getString("Type");
            String count = result.getString("Count(Type)");
            typeReturn = typeReturn + type + "\t\t\t\t\t\t" + count + "\n";
        }

        sqlStatement = "SELECT monthname(Start) as Month, Count(monthname(Start)) as Count FROM appointments GROUP BY monthname(Start);";
        result = statement.executeQuery(sqlStatement);
        
        while(result.next()) {
            String month = result.getString("Month");
            String count = result.getString("Count");
            monthReturn = monthReturn + month + "\t\t\t\t\t\t\t" + count + "\n";
        }
        
        String msg = msgType + typeReturn + msgSplit + msgMonth + monthReturn;
        display.setText(msg);
    }
    
    public void handleByCustomer() throws SQLException {
        if (customerBox.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Select a customer");
            alert.setContentText("Please select a customer!");
            alert.showAndWait();
            byMonth.setSelected(true);
        }
        else {
            Customer customer = (Customer) customerBox.getSelectionModel().getSelectedItem();;
            String ID = valueOf(customer.getCustomerId());
            String name = valueOf(customer.getCustomerName());
            String title = "Schedule for customer: " + name + "\n\n\n";
            String header = "Appointment ID \t\t Title \t\t Type \t\t Description \t\t Start \t\t End \t\t Customer ID \n\n";
            String sql = "";
        
            Statement statement = DBConnection.conn.createStatement();
            String sqlStatement = "SELECT Appointment_ID, Title, Type, Description, Start, End FROM appointments WHERE Customer_ID = '" + ID + "';";
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
    
    //Number of appointments created by each user
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
    
    public void handleCustomerChange() throws SQLException {
        byCustomer.setSelected(true);
        handleByCustomer();
    }
    
    public void updateCustomerList() throws SQLException {
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
        
        //Set drop boxes
        customerBox.setItems(customers);
    }
        

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup reportType = new ToggleGroup();
        byMonth.setToggleGroup(reportType);
        byCustomer.setToggleGroup(reportType);
        reportThree.setToggleGroup(reportType);
        byMonth.setSelected(true);
        try {
            handleByMonth();
        } catch (SQLException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            updateCustomerList();
        } catch (SQLException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    
}
