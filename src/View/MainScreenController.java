/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import static View.Log_InController.appointments;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;

import javafx.stage.Stage;
import utils.DBConnection;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class MainScreenController implements Initializable {

    //Initialize variables and FXML
    @FXML private Button addAppointment;
    @FXML private Button updateAppointment;
    @FXML private Button removeAppointment;
    @FXML private Button customers;
    @FXML private Button reportsButton;
    
    //Table Columns
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML private TableColumn<Appointment, String> appointmentDescCol;
    @FXML private TableColumn<Appointment, String> appointmentLocationCol;
    @FXML private TableColumn<Appointment, String> appointmentContactCol;
    @FXML private TableColumn<Appointment, String> appointmentTypeCol;
    @FXML private TableColumn<Appointment, String> appointmentStartCol;
    @FXML private TableColumn<Appointment, String> appointmentEndCol;
    @FXML private TableColumn<Appointment, Integer> appointmentCustIdCol;    
        
    //Static variables
    static Appointment selectedAppointment;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    //Toggle Group
    @FXML private RadioButton weekView;
    @FXML private RadioButton monthView;
    private ToggleGroup viewToggle;
    private boolean isWeekView = true;
    
    //Local time zone
    public ZoneId zone = ZoneId.systemDefault();
    
    /**
     * Method to go to the add appointment screen 
     * @throws java.io.IOException
     */
    public void handleAddAppointment () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("addAppointment.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) addAppointment.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    /**
     * Update the selected appointment
     * @throws java.io.IOException
     */
    public void handleUpdateAppointment() throws IOException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            Alert nullalert = new Alert(Alert.AlertType.ERROR);
            nullalert.setTitle("Alert!");
            nullalert.setContentText("Must select an appointment before this action!");
            nullalert.showAndWait();
        }
        else {
            Parent loader = FXMLLoader.load(getClass().getResource("modAppointment.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) updateAppointment.getScene().getWindow();
            window.setScene(scene);
            window.centerOnScreen();
            window.show();
        }
    }
    
    /**
     * Delete the selected appointment
     * @throws java.sql.SQLException
     */
    public void handleDeleteAppointment() throws SQLException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();     
        
        if (selectedAppointment == null) {
            Alert nullalert = new Alert(Alert.AlertType.ERROR);
            nullalert.setTitle("Alert!");
            nullalert.setContentText("Must select an appointment before this action!");
            nullalert.showAndWait();
        }
        else {
            int selectedId = selectedAppointment.getAppointmentID(); 
            String selectedType = selectedAppointment.getType();
            Statement statement = DBConnection.conn.createStatement();
            String sqlStatement = "DELETE FROM appointments WHERE Appointment_ID = " + selectedId + ";";        
            statement.execute(sqlStatement);  
            
            
            
            appointments.removeAll(appointments);
            updateAppointmentList();
        
            Alert nullalert = new Alert(Alert.AlertType.CONFIRMATION);
            nullalert.setTitle("Success!");
            nullalert.setContentText("Appointment with ID: " + selectedId + "\nand of type: " + selectedType + "\nhas been removed from the database.");
            nullalert.showAndWait();
        }
    }
    
    /**
     * Go to the reports screen
     * @throws java.io.IOException
     */
    public void handleReports() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("reports.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) reportsButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    /**
     * Go to the customers screen
     * @throws java.io.IOException
     */
    public void handleCustomersButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) customers.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    /**
     * Update the list of appointments
     * @throws java.sql.SQLException
     */
    public void updateAppointmentList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID, User_ID FROM appointments ORDER BY Appointment_ID";               
        ResultSet result = statement.executeQuery(sqlStatement);
            
        while (result.next()) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(result.getInt("Appointment_ID"));
            appointment.setTitle(result.getString("Title"));
            appointment.setDescription(result.getString("Description"));
            appointment.setLocation(result.getString("Location"));
            appointment.setContact(result.getString("Contact_ID"));
            appointment.setType(result.getString("Type"));
            appointment.setUserId(result.getInt("User_ID"));
            
            //Perform time zone conversions and set the start time
            LocalDateTime startParse = LocalDateTime.parse(result.getString("Start"), formatter);
            ZonedDateTime startZoned = startParse.atZone(ZoneId.of("Etc/GMT"));
            Instant startInstant = startZoned.toInstant();
            LocalDateTime startLocal = startInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();           
            appointment.setStartTime(startLocal);
            
            //Perform time zone conversions and set the end time
            LocalDateTime endParse = LocalDateTime.parse(result.getString("End"), formatter);
            ZonedDateTime endZoned = endParse.atZone(ZoneId.of("Etc/GMT"));
            Instant endInstant = endZoned.toInstant();
            LocalDateTime endLocal = endInstant.atZone(ZoneId.systemDefault()).toLocalDateTime();           
            appointment.setEndTime(endLocal);
            
            appointment.setCustId(result.getInt("Customer_ID"));           
            appointments.addAll(appointment);
        }        
        updateAppointmentTable();        
    }
    
    /**
     * Update the appointments table
     */
    public void updateAppointmentTable() {
        //appointmentTable.getItems().clear();
        
        if (isWeekView) {
            nextWeekAppointments(appointments);
        }
        else {
            nextMonthAppointments(appointments);
        }     
    }
    
    /**
     * Method to get the selected appointment
     * @return 
     */
    public static Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

    /**
     * Find appointments within the next week
     */
    private void nextWeekAppointments(ObservableList<Appointment> appointments) {
        
        ObservableList<Appointment> newAppointments = FXCollections.observableArrayList();
        newAppointments.clear();
        
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneWeek = LocalDateTime.now().plusWeeks(1);
        LocalDateTime start;
        
        for(Appointment appointment : appointments) {
            start = appointment.getStartTime();
            if (start.isBefore(oneWeek) && start.isAfter(today)) {
                newAppointments.add(appointment);
            }
        }  
        appointmentTable.getItems().clear();
        appointmentTable.setItems(newAppointments); 
    }

    /**
     * Find appointments within the next month
     */
    private void nextMonthAppointments(ObservableList<Appointment> appointments) {
        ObservableList<Appointment> newAppointments = FXCollections.observableArrayList();
        newAppointments.clear();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneMonth = LocalDateTime.now().plusMonths(1);
        LocalDateTime start;
        
        for(Appointment appointment : appointments) {
            start = appointment.getStartTime();
            if (start.isBefore(oneMonth) && start.isAfter(today)) {
                newAppointments.add(appointment);
            }
        }        
        appointmentTable.getItems().clear();
        appointmentTable.setItems(newAppointments);
    }
    
    /**
     * Handle the next week radio button
     * @throws java.sql.SQLException
     */
    public void handleWeek () throws SQLException {
        isWeekView = true;
        appointmentTable.getItems().clear();    
        updateAppointmentTable();
    }
    
    /**
     * Handle the next month radio button
     * @throws java.sql.SQLException
     */
    public void handleMonth () throws SQLException {
        isWeekView = false;
        appointmentTable.getItems().clear();
        updateAppointmentTable();
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
 
        ToggleGroup viewType = new ToggleGroup();
        weekView.setToggleGroup(viewType);
        monthView.setToggleGroup(viewType);
        weekView.setSelected(true);
        appointments.clear();
        
        appointmentIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAppointmentID()).asObject());
        appointmentTitleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        appointmentDescCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        appointmentLocationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        appointmentContactCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContact()));
        appointmentTypeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        appointmentStartCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().atZone(zone).format(formatter)));
        appointmentEndCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().atZone(zone).format(formatter)));
        appointmentCustIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustId()).asObject());
        
        try {
            updateAppointmentList();
        } catch (SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
