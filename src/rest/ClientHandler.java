/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Viko
 */
public class ClientHandler implements HttpHandler {
    
    
     String getClientsQuery = 
                            "select\n" +
                            "  C.ID, C.NAME, C.CITY, C.ADDRESS\n" +
                            "from\n" +
                            "  CLIENTS C";
    
    List<JSONObject> clientsjObj = JsonService.getJSONObject(getClientsQuery);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
    
     String encoding = "UTF-8";
     String response = JsonService.printJson(clientsjObj).toString();
     exchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
     exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);//response code and lengthx
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        
    }
    
}
