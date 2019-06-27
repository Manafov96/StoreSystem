/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import login.Login;
import org.ini4j.Wini;

/**
 *
 * @author Developer
 */
public class getConnection {
     public static Connection getConnection() {
        Connection con = null;
        String server = null;
        String port = null;
        String file = null;
        try {
            Wini ini = new Wini(new File("dbPath.ini"));
            server = ini.get("database", "server");
            port = ini.get("database", "port");
            file = ini.get("database", "file");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
           
            con = DriverManager.getConnection("jdbc:firebirdsql://" + server + ":" + port + "/" + file + "?encoding=UTF8;defaultResultSetHoldable=True", "SYSDBA", "masterkey");
            return con;
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Грешен път до Базата данни!");
            return null;
        }
    }
}
