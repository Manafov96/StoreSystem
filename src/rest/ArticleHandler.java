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
public class ArticleHandler implements HttpHandler {
    
    
     String getArticlesQuery = 
            "select\n" +
            "  A.ID, A.ARTICLE_GROUPS_ID, A.NAME_BG, A.NOTES, PL.SALE_PRICE1\n" +
            "from\n" +
            "  ARTICLES A\n" +
            "  join PRICE_LISTS PL on A.ID = PL.ARTICLE_ID";
    
    List<JSONObject> articlejObj = JsonService.getJSONObject(getArticlesQuery);
    @Override
    public void handle(HttpExchange exchange) throws IOException {

     String encoding = "UTF-8";
     String response = JsonService.printJson(articlejObj).toString();
     exchange.getResponseHeaders().set("Content-Type", "application/json; charset=" + encoding);
     exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);//response code and length
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        
    }
    
}
