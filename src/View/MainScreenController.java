/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import Model.Customer;
import static View.CustomersController.selectedCustomer;
import static View.Log_InController.appointments;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    
    //Add appointment handler
    public void handleAddAppointment () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("addAppointment.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) addAppointment.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show(); 
    }
    
    //update appointment handler
    public void handleUpdateAppointment() throws IOException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        Parent loader = FXMLLoader.load(getClass().getResource("modAppointment.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) updateAppointment.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    //remove appointment handler
    public void handleDeleteAppointment() throws SQLException {
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();        
        int selectedId = selectedAppointment.getAppointmentID();  
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "DELETE FROM appointments WHERE Appointment_ID = " + selectedId + ";";        
        statement.execute(sqlStatement);      
        appointments.removeAll(appointments);
        updateAppointmentTable();
        System.out.println(selectedId);
    }
    
    public void handleReports() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("reports.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) reportsButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    //customers page handler
    public void handleCustomersButton () throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("customers.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) customers.getScene().getWindow();
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
        appointmentStartCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)));
        appointmentEndCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter)));
        appointmentCustIdCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCustId()).asObject());
        
        try {
            updateAppointmentList();
        } catch (SQLException ex) {
            Logger.getLogger(MainScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private void updateAppointmentList() throws SQLException {
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Appointment_ID, Title, Description, Location, Contact_ID, Type, Start, End, Customer_ID FROM appointments ORDER BY Appointment_ID";               
        ResultSet result = statement.executeQuery(sqlStatement);
        
        
        while (result.next()) {
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(result.getInt("Appointment_ID"));
            appointment.setTitle(result.getString("Title"));
            appointment.setDescription(result.getString("Description"));
            appointment.setLocation(result.getString("Location"));
            appointment.setContact(result.getString("Contact_ID"));
            appointment.setType(result.getString("Type"));
            appointment.setStartTime(LocalDateTime.parse(result.getString("Start"), formatter));
            appointment.setEndTime(LocalDateTime.parse(result.getString("End"), formatter));
            appointment.setCustId(result.getInt("Customer_ID"));           
            appointments.addAll(appointment);
        }        
        updateAppointmentTable();        
    }
    
    public void updateAppointmentTable() {
        //appointmentTable.getItems().clear();
        
        if (isWeekView) {
            nextWeekAppointments(appointments);
        }
        else {
            nextMonthAppointments(appointments);
        }     
    }
    
    public static Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

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
    
    public void handleWeek () throws SQLException {
        isWeekView = true;
        appointmentTable.getItems().clear();    
        updateAppointmentTable();
    }
    
    public void handleMonth () throws SQLException {
        isWeekView = false;
        appointmentTable.getItems().clear();
        updateAppointmentTable();
    }
    

}
