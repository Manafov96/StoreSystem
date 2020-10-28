/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import static Tools.getConnection.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject; 

/**
 *
 * @author Viko
 */
public class JsonService {
    
    // Get resultSet like List of JSONObjects
    public static List<JSONObject> getFormattedResultSet(ResultSet rs){
    
        List<JSONObject> resList = new ArrayList<>();
        
        try{
            
            //getColumn names
            ResultSetMetaData  rsMeta = rs.getMetaData();
            int columnCount = rsMeta.getColumnCount();
            List<String> columnNames =  new ArrayList<>();
            //Loop to get all column names
            for(int i = 1; i <= columnCount; i++){
                //Adding all retrieved column name to List Object
                columnNames.add(rsMeta.getColumnName(i).toUpperCase());
            }
            
            while(rs.next()){
                // Convert all object to JSON object  
                JSONObject obj = new JSONObject();
                for(int i = 1; i <= columnCount; i++){
                    String key = columnNames.get(i - 1);
                    String value = rs.getString(i);
                    obj.put(key, value);
                }
                resList.add(obj);
            }
            
        } catch(SQLException | JSONException ex){
            
          System.out.println(ex);
          
        } finally{
            
          try{
              
              rs.close();
              
          }catch(SQLException e){
              
             System.out.println(e.getMessage());
             
          }
        }
        return resList;
    }
    
    // Get Json Object from SQL Query
    public static List<JSONObject> getJSONObject(String SQL){

    //final String SQL = "select * from articles";
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
 
    try{

        con = getConnection();
        pst = con.prepareStatement(SQL);
        rs = pst.executeQuery();

    }catch(SQLException ex){

      System.out.println("Error:" + ex.getMessage());

    }

    List<JSONObject> resList = JsonService.getFormattedResultSet(rs);
    return resList;

    }
    
    // print Json Objcet
    public static List<JSONObject> printJson(List<JSONObject> obj){
       
        for(int i = 0; i < obj.size(); i++){
            obj.get(i);
        }
        return obj;
    }

}
