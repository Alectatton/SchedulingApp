
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.io.IOException;
import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author alect
 */
public class LogInController implements Initializable {

    @FXML private TextField zoneId;
    @FXML private TextField usernameText;
    @FXML private TextField passwordText;
    @FXML private Button loginButton;    
     
    //Handle login button action
    public void handleLoginButton () throws IOException, SQLException {
        String username = usernameText.getText().trim();
        String password = passwordText.getText().trim();
        boolean isValid = isValidLogin(username, password);
        if (isValid) {
            Parent loader = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
            Scene scene = new Scene(loader);
            Stage window = (Stage) loginButton.getScene().getWindow();
            window.setScene(scene);
            window.show();    
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access denied!");
            alert.setContentText("Wrong username or password!");
            alert.showAndWait();
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
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
}
