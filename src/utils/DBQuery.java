/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author alect
 */
public class DBQuery {
    
    private static Statement statement;
    
    //Setter for statement object
    public static void setStatement(Connection conn) throws SQLException {
        statement = conn.createStatement();
    }
    
    //Getter for statement object
    public static Statement getStatement() {
        return statement;
    }
}
