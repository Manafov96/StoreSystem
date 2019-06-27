/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package article;
/**
 *
 * @author Viko
 */
public class Articles {
   private int GroupID;
   private String GroupNameDE;
   private String GroupNameEN;
   private String GroupNameBG;
   
   private int ArticleID;
   private String ArticleCode;
   private String ArticleNameDE;
   private String ArticleNameEN;
   private String ArticleNameBG;
   private Object Status;
   private int minQTY;
   private Object Measure;
   private String Notes;

    public Articles(int pGroupID, String pGroupNameDE, String pGroupNameEN, String pGroupNameBG, int pArticleID, 
                    String pArticleCode, String pArticleNameDE, String pArticleNameEN, String pArticleNameBG, 
                    Object pStatus, int pminQTY, Object pMeasure, String pNotes) {
        this.GroupID = pGroupID;
        this.GroupNameDE = pGroupNameDE;
        this.GroupNameEN = pGroupNameEN;
        this.GroupNameBG = pGroupNameBG;
        this.ArticleID = pArticleID;
        this.ArticleCode = pArticleCode;
        this.ArticleNameDE = pArticleNameDE;
        this.ArticleNameEN = pArticleNameEN;
        this.ArticleNameBG = pArticleNameBG;
        this.Status = pStatus;
        this.minQTY = pminQTY;
        this.Measure = pMeasure;
        this.Notes = pNotes;
    }
   
    public Articles(int pGroupID, String pGroupNameDE, String pGroupNameEN, String pGroupNameBG){
        this.GroupID = pGroupID;
        this.GroupNameDE = pGroupNameDE;
        this.GroupNameEN = pGroupNameEN;
        this.GroupNameBG = pGroupNameBG;
    }
    
    public Articles(int pArticleID, String pArticleCode, String pArticleNameDE, String pArticleNameEN, 
                    String pArticleNameBG, Object pStatus, int pminQTY, Object pMeasure, String pNotes){
        this.ArticleID = pArticleID;
        this.ArticleCode = pArticleCode;
        this.ArticleNameDE = pArticleNameDE;
        this.ArticleNameEN = pArticleNameEN;
        this.ArticleNameBG = pArticleNameBG;
        this.Status = pStatus;
        this.minQTY = pminQTY;
        this.Measure = pMeasure;
        this.Notes = pNotes;
    }
    
    public int getGroupID() {
        return GroupID;
    }

    public String getGroupNameDE() {
        return GroupNameDE;
    }

    public String getGroupNameEN() {
        return GroupNameEN;
    }

    public String getGroupNameBG() {
        return GroupNameBG;
    }

    public int getArticleID() {
        return ArticleID;
    }

    public String getArticleCode() {
        return ArticleCode;
    }

    public String getArticleNameBG() {
        return ArticleNameBG;
    }

    public String getArticleNameDE() {
        return ArticleNameDE;
    }

    public String getArticleNameEN() {
        return ArticleNameEN;
    }

    public Object getMeasure() {
        return Measure;
    }

    public int getMinQTY() {
        return minQTY;
    }

    public String getNotes() {
        return Notes;
    }

    public Object getStatus() {
        return Status;
    }
 
}
