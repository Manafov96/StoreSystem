/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package priceLists;

/**
 *
 * @author Viko
 */
public class Prices {

    private final int ID;
    private final Object Article;
    private final double Price1;
    private final double Price2;
    private final double Price3;

    public Prices(int pID, Object pArticle, double pPrice1, double pPrice2, double pPrice3) {
        this.ID = pID;
        this.Article = pArticle;
        this.Price1 = pPrice1;
        this.Price2 = pPrice2;
        this.Price3 = pPrice3;
    }

    public int getID() {
        return ID;
    }

    public Object getArticle() {
        return Article;
    }

    public String getPrice1() {
        return String.format("%.2f", Price1);
    }

    public String getPrice2() {
        return String.format("%.2f", Price2);
    }

    public String getPrice3() {
        return String.format("%.2f", Price3);
    }
}
