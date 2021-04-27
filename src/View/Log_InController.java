
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Model.Appointment;
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
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * FXML Controller class
 *
 * @author alect
 */
public class Log_InController implements Initializable {

    @FXML private TextField zoneId;
    @FXML private TextField usernameText;
    @FXML private TextField passwordText;
    @FXML private Button loginButton;    
    
    static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
     
    //Handle login button action
    public void handleLoginButton () throws IOException, SQLException, InterruptedException {
        String username = usernameText.getText().trim();
        String password = passwordText.getText().trim();
        boolean isValid = isValidLogin(username, password);
        logLogin(isValid);
        if (!isValid) {              
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access denied!");
            alert.setContentText("Wrong username or password!");
            alert.showAndWait();
        }
        else {
            
            appointmentAlert();
            loadMainScreen();         
        }
    }
    
    //Determine if the login information is valid
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
    
    private void loadMainScreen() throws IOException {
        Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(loader);
        Stage window = (Stage) loginButton.getScene().getWindow();
        window.setScene(scene);
        window.centerOnScreen();
        window.show();
    }
    
    public void appointmentAlert() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteen = now.plusMinutes(15);
        System.out.println(now);
        for (Appointment appointment : appointments) {
            if (appointment.getStartTime().isAfter(now) && appointment.getStartTime().isBefore(fifteen)) {
                int ID = appointment.getAppointmentID();
                String dateTime = appointment.getStartTime().toString();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming appointment!");
                alert.setContentText("Appointment with ID: " + ID + " at: " + dateTime);
                alert.showAndWait();
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome!");
        alert.setContentText("No upcoming appointments");
        alert.showAndWait();
    }
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usernameText.setText("test");
        passwordText.setText("test");
    }    

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
        //Files.write(Paths.get("login_activity.txt"), msg.getBytes(), StandardOpenOption.APPEND);
    }


}
