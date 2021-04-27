/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulingapp;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static utils.DBConnection.closeConnection;
import static utils.DBConnection.startConnection;

/**
 *
 * @author alect
 */
public class main extends Application{

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {
        startConnection();
        launch(args);
        closeConnection();    
    }
    
    @Override
    public void start(Stage stage) throws IOException{ 
        Parent root = FXMLLoader.load(getClass().getResource("/View/logIn.fxml"));        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Scheduling System");
        stage.setResizable(false);
        stage.show();   
    }
    
}
