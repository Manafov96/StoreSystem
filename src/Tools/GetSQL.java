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

/**
 *
 * @author Developer
 */
public class GetSQL {

    public static String getSQL(int ID) throws SQLException {
        Connection con = getConnection();
        String sql = null;
        PreparedStatement ps = con.prepareStatement("select RS.SQL from REPORTS_SQL RS where RS.ID = " + ID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            sql = rs.getString(1);
        }
        return sql;
    }

}
