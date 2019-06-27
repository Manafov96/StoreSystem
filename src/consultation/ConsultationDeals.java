/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;
/**
 *
 * @author Viko
 */
public class ConsultationDeals {
    private int DealNumber;
    private String InvoiceNumber;
    private String DealDate;
    private String ClientName;
    private Double DealValue;
    private String channel;
    private String Country;
    private String payment;
    private String type;
    private Double ValueVat;
    private Double Other;

    public ConsultationDeals(int pDealNumber, String pInvoiceNumber, String pDealDate, String pClientName, Double pDealValue, String pchannel,
                             String pCountry, String ppayment, String ptype, Double pValueVat) {
        this.DealNumber = pDealNumber;
        this.InvoiceNumber = pInvoiceNumber;
        this.DealDate = pDealDate;
        this.ClientName = pClientName;
        this.DealValue = pDealValue;
        this.channel = pchannel;
        this.Country = pCountry;
        this.payment = ppayment;
        this.type = ptype;
        this.ValueVat = pValueVat;
        
    }  
    
        public ConsultationDeals(int pDealNumber, String pInvoiceNumber, String pDealDate, String pClientName, Double pDealValue, String pchannel,
                             String pCountry, String ppayment, String ptype, Double pValueVat, Double pOther) {
        this.DealNumber = pDealNumber;
        this.InvoiceNumber = pInvoiceNumber;
        this.DealDate = pDealDate;
        this.ClientName = pClientName;
        this.DealValue = pDealValue;
        this.channel = pchannel;
        this.Country = pCountry;
        this.payment = ppayment;
        this.type = ptype;
        this.ValueVat = pValueVat;
        this.Other = pOther;
    } 
    
     public int getDealNumber(){
      return this.DealNumber;
     }
     
     public String getDealDate(){
       return this.DealDate;
     }
     
     public String getInvoiceNumber(){
       return this.InvoiceNumber;
     }
     
     public String getClientName(){
       return this.ClientName;
     }
     
     public Double getDealValue(){
        return this.DealValue;
     }
     
     public String getChannel(){
       return this.channel;
     }
     
     public String getCountry(){
       return this.Country;
     }
     
     public String getPayment(){
       return this.payment;
     }
     
     public String getType(){
       return this.type;
     }
     
     public Double getValueVat(){
       return this.ValueVat;
     }
     
     public Double getOther(){
       return this.Other;
     }
}
