/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 *
 * @author alect
 */
public class DBConnection {
    
    // JDBC URL
    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ0838Y";

    // Full URL
    private static final String jdbcURL = protocol + vendorName + ipAddress + dbName;   

    //Driver reference
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    public static  Connection conn = null;
    
    //Username and password
    private static final String username = "U0838Y";
    private static final String password = "53689201210";
    
    public static Connection startConnection() {
        try{  
            Class.forName(driver);
            conn = (Connection)DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection succesful");
        }
        catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
        return conn;
        
    }
    
   public static Connection getConnection() {
       return conn;
   }
    
    public static void closeConnection() throws IOException, SQLException  {
        conn.close();
        System.out.println("Connection closed!");
    }
}
