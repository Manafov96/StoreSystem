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
public class DetailDealsHandler implements HttpHandler {
    
    
     String getDetailDealsQuery = 
                            "select\n" +
                            "  DD.DEAL_ID, DD.ARTICLE_ID, DD.QUANTITY, DD.DVY_PRICE_WITH_VAT\n" +
                            "from\n" +
                            "  DEAL_DETAILS DD\n" +
                            "  join DEALS D on D.ID = DD.DEAL_ID\n" +
                            "where\n" +
                            "  D.OPERATION_ID = 1";
    
    List<JSONObject> detailDealsjObj = JsonService.getJSONObject(getDetailDealsQuery);
    @Override
    public void handle(HttpExchange exchange) throws IOException {
    
     String encoding = "UTF-8";
     String response = JsonService.printJson(detailDealsjObj).toString();
     exchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
     exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);//response code and lengthx
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        
    }
}
    
