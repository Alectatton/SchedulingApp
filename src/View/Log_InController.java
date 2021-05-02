
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
import static View.MainScreenController.formatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DBConnection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import static java.time.temporal.TemporalQueries.offset;
import static java.util.Date.UTC;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class Log_InController implements Initializable {

    @FXML private TextField zoneId;
    @FXML private TextField usernameText;
    @FXML private TextField passwordText;    
    @FXML private Text zoneText;
    @FXML private Text userText;
    @FXML private Text passText;    
    @FXML private Button loginButton;    
    public ZoneId zone = ZoneId.systemDefault();
    
    static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
     
     /**
     * Method to handle the login button 
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws java.lang.InterruptedException
     */   
    public void handleLoginButton () throws IOException, SQLException, InterruptedException {
        String username = usernameText.getText().trim();
        String password = passwordText.getText().trim();
        boolean isValid = isValidLogin(username, password);
        logLogin(isValid);
        if (!isValid) {            
            var rb = ResourceBundle.getBundle("resources/locale", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("denied"));
            alert.setContentText(rb.getString("wrong"));
            alert.showAndWait();
        }
        else {          
            appointmentAlert();
            loadMainScreen();         
        }
    }
    

    
    /**
     * Determine if the login information is valid
     * @param username
     * @param password
     * @return 
     * @throws java.sql.SQLException
     */
    public boolean isValidLogin (String username, String password) throws SQLException {        
        Statement statement = DBConnection.conn.createStatement();
        String sqlStatement = "SELECT Password FROM users WHERE User_Name = '" + username + "'";        
        ResultSet result = statement.executeQuery(sqlStatement);
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        else {
            try {
                result.next();
                String pass = result.getString("Password");
                return password.equals(pass);
            }
            catch (SQLException e){
                return false;
            }
        }
    }
    
    /**
     * Load the main screen
     */
    private void loadMainScreen() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) loginButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    
    /**
     * Check for appointments within the next fifteen minutes and alert if there is one
     * @throws java.io.IOException
     */
    public void appointmentAlert() throws IOException, SQLException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteen = now.plusMinutes(15);
        System.out.println(now);
        var rb = ResourceBundle.getBundle("resources/locale", Locale.getDefault());
        System.out.println("Times");
        
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
        
        for (Appointment appointment : appointments) {
            if (appointment.getEndTime().isAfter(now) && appointment.getStartTime().isBefore(fifteen)) {
                int ID = appointment.getAppointmentID();
                String dateTime = appointment.getStartTime().toString();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("upcoming"));
                alert.setContentText(rb.getString("withid") + " " + ID + "\n" + rb.getString("at") + " " + dateTime);
                alert.showAndWait();
                return;
            }
        }        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(rb.getString("welcome"));
        alert.setContentText(rb.getString("noupcoming"));
        alert.showAndWait();
    }
    
    /**
     * Method to write any login attempts in the login_activity file
     */
    
    private void logLogin(boolean success) throws IOException {
        String user = usernameText.getText();
        String dateTime = LocalDateTime.now().toString();
        String msg = "";
        
        if (success) {
            msg = "User: " + user + "\t logged in at: " + dateTime + "\n";
        }
        else {
            msg = "Unsuccessful login attempt by: " + user + "\t at " + dateTime + "\n";
        }
        
        File file = new File("login_activity.txt");
        FileWriter fileWrite = new FileWriter(file, true);
        fileWrite.write(msg);
        fileWrite.close();
    }
    
    /**
     * Method to find the zone ID
     */
    private void findZoneID() {
        ZoneId zone = ZoneId.systemDefault();
        zoneId.setText(zone.toString());
    }
    
    /**
     * Load the resource bundles
     */
    private void loadResources() {
        var rb = ResourceBundle.getBundle("resources/locale", Locale.getDefault());
        zoneText.setText(rb.getString("zone"));
        userText.setText(rb.getString("user"));
        passText.setText(rb.getString("pass"));
        usernameText.setPromptText(rb.getString("user"));
        passwordText.setPromptText(rb.getString("pass"));
        loginButton.setText(rb.getString("login"));
    }
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {     
        findZoneID();
        loadResources();   
    }    


}
