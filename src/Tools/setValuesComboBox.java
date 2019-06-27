/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import static Tools.getConnection.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Developer
 */
public class setValuesComboBox {
    public static void setValuesComboBox(String sql, JComboBox combo, boolean autoComplete, int index, boolean additional) {
        try {
            if(autoComplete){
              AutoCompleteDecorator.decorate(combo);
            }
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            combo.removeAllItems();
            Vector<DropDown> vector = new Vector<>();
            if(additional){
               vector.addElement(new DropDown(-1, "Всички"));
            }
            while (rs.next()) {
                vector.addElement(new DropDown(rs.getInt(1), rs.getString(2)));
            }
            combo.setModel(new DefaultComboBoxModel(vector));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        combo.setSelectedIndex(index);
    }
    
}
