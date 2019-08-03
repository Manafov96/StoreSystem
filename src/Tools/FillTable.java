/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import static Tools.getConnection.getConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Developer
 */
//FillTable(MyTable, "select * Customers;");
public class FillTable {
    public static void FillTable(JTable table, String Query) throws SQLException
    {
        try
        {
            Connection con = getConnection();
            Statement stat = con.createStatement();
            ResultSet rs = stat.executeQuery(Query);
            java.sql.ResultSetMetaData rsm=rs.getMetaData();
            //To remove previously added rows
            while(table.getRowCount() > 0) 
            {
                ((DefaultTableModel) table.getModel()).removeRow(0);
            }
            int columns = rs.getMetaData().getColumnCount();
            Vector<String> columnsName = new Vector<String>(columns);
           // to set Column labels
            for(int i=1; i<=columns; i++)
            {
                columnsName.add(rsm.getColumnLabel(i));

            }
            ((DefaultTableModel) table.getModel()).setColumnIdentifiers(columnsName);
            while(rs.next())
            {  
                Object[] row = new Object[columns];
                for (int i = 1; i <= columns; i++)
                {  
                    row[i - 1] = rs.getObject(i);
                }
                ((DefaultTableModel) table.getModel()).insertRow(rs.getRow() - 1, row);
            }
            rs.close();
            stat.close();
            con.close();
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e);
        }
    }
}
