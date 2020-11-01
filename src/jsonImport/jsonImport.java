/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsonImport;

// To work this functionality must add jar file in library "C:\Users\Вико\Downloads\json-simple-1.1.jar"

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static Tools.getConnection.getConnection;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Viko
 */
public class jsonImport {
    
    public void importJson() throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
      
      String filename = null; 
      
      JFileChooser file = new JFileChooser();
      file.setCurrentDirectory(new File(System.getProperty("user.home")));

      FileNameExtensionFilter filter = new FileNameExtensionFilter("*.json", "json");
      file.addChoosableFileFilter(filter);
      int result = file.showOpenDialog(null);
      if(result == JFileChooser.APPROVE_OPTION){
        File selectedFile = file.getSelectedFile();
        String path = selectedFile.getAbsolutePath();
        filename = path;
        }else{
          System.out.println("No File selected");
        }
        
      String order_status = "";
      
      String Date;
      
      String full_name = "";
      
      String billing_country = "";    
      
      String billing_company = "";
      
      String billing_city = "";
      
      String billing_address = "";
      
      String billing_email = "";
      
      String payment_method = "";
      
      String order_shipping = "";
      
      String order_shipping_plus_tax = "";
      
      String cart_discount = "";
      
      String order_total_tax = "";
      
      String order_total = "";
      
      String sku = "";
      
      String item_qty = "";
      
      String item_price = "";
      
      String item_price_inc_tax = "";
      
      
      JSONParser jsonParser = new JSONParser();
      
      JSONArray deals = (JSONArray) jsonParser.parse(new FileReader(filename));
      
      for(int i = 0; i < deals.size(); i++){
          
        // Get all master data and execute insert into db for them.  
      
        JSONObject dealsObject = (JSONObject) deals.get(i);
        
        order_status = dealsObject.get("order_status").toString();
        
        Date = dealsObject.get("order_date").toString();
         
        java.util.Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(Date);
        
        java.sql.Date date = new java.sql.Date(d.getTime());
               
        full_name = dealsObject.get("billing_first_name").toString() + " "  + dealsObject.get("billing_last_name").toString();
         
        billing_country = dealsObject.get("billing_country").toString();
        
        billing_company = dealsObject.get("billing_company").toString();
        
        payment_method = dealsObject.get("payment_method_title").toString();
        
        order_shipping = dealsObject.get("order_shipping").toString();
        
        order_shipping_plus_tax = dealsObject.get("order_shipping_plus_tax").toString();
        
        cart_discount = dealsObject.get("cart_discount").toString();
        
        order_total_tax = dealsObject.get("order_total_tax").toString();
        
        order_total = dealsObject.get("order_total").toString();
        
        billing_city = dealsObject.get("billing_postcode").toString() + " " + dealsObject.get("billing_city").toString();
        
        billing_address = dealsObject.get("billing_address").toString();
        
        billing_email = dealsObject.get("billing_email").toString();
        
        JSONArray details = (JSONArray) dealsObject.get("products");
        
        Connection connection;
        PreparedStatement ps;
        try {
            connection = getConnection();
            ps = connection.prepareStatement("execute procedure IMPORT_MASTER(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, order_status);
            ps.setDate(2, date);
            ps.setString(3, full_name);
            ps.setString(4, billing_country);
            ps.setString(5, billing_company);
            ps.setString(6, payment_method);
            ps.setString(7, order_total_tax);
            ps.setString(8, order_total);
            ps.setString(9, billing_city);
            ps.setString(10, billing_address);
            ps.setString(11, billing_email);
            ps.executeUpdate();
            //connection.close();
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, ex);
        }
        
        // Get all details data and execute insert into db for them.
        for(int j = 0; j < details.size(); j++){
            JSONObject detailsObject = (JSONObject) details.get(j);
            
            sku = detailsObject.get("sku").toString();
            
            item_qty = detailsObject.get("qty").toString();
            
            item_price = detailsObject.get("item_price").toString();
            
            item_price_inc_tax = detailsObject.get("item_price_inc_tax").toString();
            
        try {
            connection = getConnection();
            ps = connection.prepareStatement("execute procedure IMPORT_DETAILS(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, billing_country);
            ps.setString(2, billing_company);
            ps.setString(3, sku);
            ps.setString(4, item_qty);
            ps.setString(5, item_price);
            ps.setString(6, item_price_inc_tax);
            ps.setString(7, order_shipping);
            ps.setString(8, order_shipping_plus_tax);
            ps.setString(9, full_name);
            ps.setString(10, billing_city);
            ps.setDate(11, date);
            ps.executeUpdate();
            //connection.close();
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, ex);
        }
        }
      }   
    }  
}
    
    
