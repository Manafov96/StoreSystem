/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sale;

import Tools.DropDown;
import java.io.IOException; // dont remove for INI file
import client.Client;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static Tools.getConnection.getConnection;
import static Tools.setValuesComboBox.setValuesComboBox;
import java.io.InputStream;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import login.Login;
import net.sf.jasperreports.engine.JasperExportManager;

/**
 *
 * @author Viko
 */
public class SALE extends javax.swing.JFrame {

    public int saleID = 0;
    public int creditID = 0;
    public double VatPCT = 0;
    public int isSave = 0;
    int insertMaster = 0;
    public int isActive = 1;
    public int checkInvoiceNumber = 1;
    public int Exist = 0;
    public static int savedClient = 0;
    public int checkQuantity = 1; // This is flag for check for availability quantity and sale quantity!!!!!

    // 04.11.2019 Added Offer and Credit Note
    public int isCreditNote = 0;
    public int isOffer = 0;

    /**
     * Creates new form SALE
     */
    public SALE() {
        initComponents();
        // set comboBox DealType
        setValuesComboBox("select DT.ID, DT.NAME DEAL_TYPE from N_DEAL_TYPES DT", jcbDealType, false, -1, false);
        // set comboBox ChannelType
        setValuesComboBox("select NC.ID, NC.NAME CHANNELS from N_CHANNELS NC", jcbChannel, false, -1, false);
        setValuesClients();
        setValuesLangs();
        // set comboBox Statuses
        setValuesComboBox("select NS.ID, NS.NAME STATUS from N_STATUSES NS", jcbStatuses, false, 0, false);
        setDate();
        jtxtSaleNumber.setEditable(false);
        jlbCreditNote.setVisible(false);
        jcbPayment.setEnabled(false);

        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        TableColumn colPrice = jTable1.getColumnModel().getColumn(4);
        TableColumn colPriceWoVat = jTable1.getColumnModel().getColumn(5);
        TableColumn total = jTable1.getColumnModel().getColumn(8);
        colPrice.setCellRenderer(new DecimalFormatRenderer());
        colPriceWoVat.setCellRenderer(new DecimalFormatRenderer());
        total.setCellRenderer(new DecimalFormatRenderer());
        jcbDealType.setSelectedIndex(0);
        jcbChannel.setSelectedIndex(0);
        if (jtxtSaleNumber.getText().isEmpty()) {
            jtxtSaleNumber.setBackground(Color.pink);
        }

        // 04.11.2019
        jxtCredit.setVisible(false);
        jdtchCredit.setVisible(false);
        jlbCredit.setVisible(false);
        jlbCreditDate.setVisible(false);
        jchbReversal.setVisible(false);
    }

    private boolean checkInputs() {
        if ((saleID == 0
                || jtxtSaleNumber.getText().isEmpty()
                || jcbDealType.getSelectedIndex() == -1
                || jcbClient.getSelectedIndex() == - 1
                || jcbChannel.getSelectedIndex() == -1)
                && isActive == 1) {
            return true;
        }
        return false;
    }

    private void setDate() {
        Date date = new Date();
        jDateSale.setDate(date);

    }

    private void setValuesLots() {
        String sql = "select LC.ID, LC.NUMBER from LOTS_COMBO("
                + ((DropDown) jcbArticle.getSelectedItem()).getId() + ")LC";
        if (isCreditNote == 1) {
            sql = "select distinct DD.LOT_ID, L.NUMBER\n"
                    + "from DEAL_DETAILS DD join LOTS L on L.ID = DD.LOT_ID\n"
                    + "where DD.ARTICLE_ID = " + ((DropDown) jcbArticle.getSelectedItem()).getId();
        }
        setValuesComboBox(sql, jcbLots, false, -1, false);
    }

    private void setValuesClients() {
        setValuesComboBox("select C.ID, C.NAME CLIENTS from CLIENTS C where C.TYPE_ID = 1", jcbClient, true, -1, false);
    }

    private void setValuesLangs() {
        setValuesComboBox("select NL.ID, NL.SHORT_NAME LANG from N_LANGS NL", jcbLang, false, 2, false);
        jcbArticleGroups.setSelectedIndex(-1);
    }

    private void setValuesArticles() {
        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
            case 1: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_DE) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                if (isCreditNote == 1) {
                    sql = "select A.ID, (A.CODE || ' | ' || A.NAME_DE) NAME_DE from ARTICLES A "
                            + "where A.ARTICLE_GROUPS_ID = "
                            + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                }
                setValuesComboBox(sql, jcbArticle, false, -1, false);
                break;
            }
            case 2: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_EN) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                if (isCreditNote == 1) {
                    sql = "select A.ID, (A.CODE || ' | ' || A.NAME_EN) NAME_DE from ARTICLES A "
                            + "where A.ARTICLE_GROUPS_ID = "
                            + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                }
                setValuesComboBox(sql, jcbArticle, false, -1, false);
                break;
            }
            case 3: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_BG) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                if (isCreditNote == 1) {
                    sql = "select A.ID, (A.CODE || ' | ' || A.NAME_BG) NAME_DE from ARTICLES A "
                            + "where A.ARTICLE_GROUPS_ID = "
                            + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                }
                setValuesComboBox(sql, jcbArticle, false, -1, false);
                break;
            }
            default:
                break;
        }
    }

    private void setValuesPayment() {
        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
            case 1: {
                setValuesComboBox("select NPM.ID, NPM.NAME_GERM from  N_PAYMENT_METHODS NPM", jcbPayment, false, -1, false);
                break;
            }
            case 2: {
                setValuesComboBox("select NPM.ID, NPM.NAME_ENG from  N_PAYMENT_METHODS NPM", jcbPayment, false, -1, false);
                break;
            }
            case 3: {
                setValuesComboBox("select NPM.ID, NPM.NAME_BUL from  N_PAYMENT_METHODS NPM", jcbPayment, false, -1, false);
                break;
            }
            default:
                break;
        }
    }

    private void GenerateSaleNumber() {
        String saleNumber = null;
        String sql = "SELECT NEXT VALUE FOR DEAL_NUMBER_GEN FROM RDB$DATABASE;";
        if (isOffer == 1) {
            sql = "SELECT NEXT VALUE FOR OFFER_NUMBER_GEN FROM RDB$DATABASE;";
        }
        if (jtxtSaleNumber.getText().isEmpty()) {
            try {
                jtxtSaleNumber.setBackground(Color.white);
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    saleNumber = rs.getString(1);
                    jtxtSaleNumber.setText(saleNumber);
                }
                jtxtSaleNumber.setBackground(Color.white);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void getSum() {
        double sum = 0.00;
        double price = 0.00;
        double qty = 0.00;
        double ValueVat = 0.00;
        double finalPrice = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            sum = sum + (double) (jTable1.getValueAt(i, 8));
            price = (double) (jTable1.getValueAt(i, 4));
            qty = Double.parseDouble((String) (jTable1.getValueAt(i, 6)));
            finalPrice += price * qty;
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        jtxtTotalWoVat.setText(nf.format(sum));
        if (VatPCT == 0) {
            jtxtVatValue.setText("0.00".replace(".", ","));
            jtxtTotalWithVat.setText(jtxtTotalWoVat.getText());
        } else {
            ValueVat = finalPrice - sum;
            jtxtVatValue.setText(nf.format(ValueVat));
            jtxtTotalWithVat.setText(nf.format(finalPrice));

        }
    }

    private void generateSaleID() {

        if (jtxtSaleNumber.getText().isEmpty()) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT NEXT VALUE FOR DEALS_GEN FROM RDB$DATABASE;");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    saleID = rs.getInt(1);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void InsertMaster() {
        double TransportCosts = 0;
        double BankCosts = 0;
        double OtherCosts = 0;
        double ChannelCosts = 0;

        if (!jtxtTransportCosts.getText().isEmpty()) {
            TransportCosts = Double.parseDouble(jtxtTransportCosts.getText().toString());
        }
        if (!jtxtBankCosts.getText().isEmpty()) {
            BankCosts = Double.parseDouble(jtxtBankCosts.getText().toString());
        }
        if (!jtxtOtherCosts.getText().isEmpty()) {
            OtherCosts = Double.parseDouble(jtxtOtherCosts.getText().toString());
        }
        if (!jtxtChannelCosts.getText().isEmpty()) {
            ChannelCosts = Double.parseDouble(jtxtChannelCosts.getText().toString());
        }
        try {
            Connection con = getConnection();

            PreparedStatement ps = con.prepareStatement("update or insert into DEALS (ID, DEAL_NUMBER, DEAL_DATE, OPERATION_ID, DEAL_TYPE_ID, CHANNEL_ID, STATUS_ID, CLIENT_ID, LANG_ID,\n"
                    + " DEAL_VALUE, TRANSPORT_COSTS, CHANNEL_COSTS, BANK_COSTS, OTHER_COSTS, CURRENCY_ID, USER_ID, VALUE_VAT, CREDIT_DEAL_NUMBER, CREDIT_DEAL_DATE)\n"
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, ?,\n"
                    + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) matching (ID, DEAL_NUMBER)");

            ps.setInt(1, saleID);
            ps.setInt(2, Integer.parseInt(jtxtSaleNumber.getText()));
            java.sql.Date sqldate = new java.sql.Date(jDateSale.getDate().getTime());
            ps.setDate(3, sqldate);
            int operation = 1;
            if (isOffer == 1) {
                operation = 5;
            }
            if (isCreditNote == 1) {
                operation = 6;
            }
            ps.setInt(4, operation);  // TODO: This is operation type
            ps.setInt(5, ((DropDown) jcbDealType.getSelectedItem()).getId());
            ps.setInt(6, ((DropDown) jcbChannel.getSelectedItem()).getId());
            ps.setInt(7, ((DropDown) jcbStatuses.getSelectedItem()).getId());
            ps.setInt(8, ((DropDown) jcbClient.getSelectedItem()).getId());
            ps.setInt(9, ((DropDown) jcbLang.getSelectedItem()).getId());
            ps.setDouble(10, Double.parseDouble(jtxtTotalWithVat.getText().replace(",", ".").replace("\u00A0", "")));
            ps.setDouble(11, TransportCosts);
            ps.setDouble(12, ChannelCosts);
            ps.setDouble(13, BankCosts);
            ps.setDouble(14, OtherCosts);
            ps.setInt(15, 1); // TODO: This is for currency_ID
            ps.setInt(16, Login.userID);
            ps.setDouble(17, Double.parseDouble(jtxtVatValue.getText().replace(",", ".").replace("\u00A0", "")));
            String Credit = null;
            java.sql.Date sqlCreditDate = null;
            if (isCreditNote == 1) {
                sqlCreditDate = new java.sql.Date(jdtchCredit.getDate().getTime());
                Credit = jxtCredit.getText();
            }
            ps.setString(18, Credit);
            ps.setDate(19, sqlCreditDate);
            ps.executeUpdate();
            insertMaster = 1;
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());

        }
    }

    private void InsertDetail() {
        try {
            Connection con = getConnection();
            int rows = jTable1.getRowCount();
            PreparedStatement ps = con.prepareStatement("update or insert into DEAL_DETAILS (DEAL_ID, ARTICLE_ID, QUANTITY, PRICE, CURRENCY_ID, LOT_ID, DVY_PRICE_WITH_VAT)\n"
                    + "values (?, ?, ?, ?, ?, ?, ?) matching(DEAL_ID, ARTICLE_ID, LOT_ID)");

            for (int row = 0; row < rows; row++) {

                int Article_ID = (Integer) jTable1.getValueAt(row, 0);
                Double QTY = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 6)).replace("\u00A0", ""));
                Double Price = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 8)).replace("\u00A0", ""));
                Double PriceWithVat = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 4)).replace("\u00A0", ""));
                ps.setInt(1, saleID);
                ps.setInt(2, Article_ID);
                ps.setDouble(3, QTY);
                ps.setDouble(4, Price);
                ps.setInt(5, 1); // TODO : Currency
                ps.setInt(6, ((DropDown) jTable1.getValueAt(row, 3)).getId());
                ps.setDouble(7, PriceWithVat);
                ps.executeUpdate();
            }
            isSave = 1;

        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }

    private void GenerateInvoiceNumber() {
        String invoiceNumber = null;
        if (jcbPaymentType.getSelectedIndex() != -1) {

            try {
                Connection con = getConnection();
                String sql = "SELECT NEXT VALUE FOR INVOICE_NUMBER_GEN FROM RDB$DATABASE;";
                if (isCreditNote == 1)
                    sql = "SELECT NEXT VALUE FOR CREDIT_NUMBER_GEN FROM RDB$DATABASE;";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    invoiceNumber = rs.getString(1);
                    if (isCreditNote == 1)
                        invoiceNumber = "CN " + invoiceNumber;
                    jtxtInvoiceNumberDialog.setText(invoiceNumber);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
    }

    private void GenerateCreditNote() {
        String creditNumber = null;
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT NEXT VALUE FOR CREDIT_NOTES_GEN CREDIT_ID, NEXT VALUE FOR CREDIT_NOTES_NUMBER_GEN CREDIT_NUMBER FROM RDB$DATABASE");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                creditID = rs.getInt(1);
                creditNumber = rs.getString(2);
                jtxtCreditNoteNumber.setText(creditNumber);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void InsertUpdateInvoice() {
        String creditIdString = null;
        if (jchbReversal.isSelected()) {
            creditIdString = String.valueOf(creditID);
        }
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("update or insert into INVOICES (INVOICE_NUMBER, INVOICE_DATE, DEAL_ID, PAYMENT_ID, PAYMENT_TEXT, CREDIT_NOTE_ID, OPERATION_ID)\n"
                    + "values (?, ?, ?, ?, ?, ?, ?) matching(DEAL_ID)");

            String number = jtxtInvoiceNumber.getText();
            String invoiceNumber = number.substring(0, number.indexOf("/"));
            ps.setString(1, invoiceNumber);
            java.sql.Date sqldate = new java.sql.Date(jDateInvoice.getDate().getTime());
            ps.setDate(2, sqldate);
            ps.setInt(3, saleID);
            ps.setInt(4, ((DropDown) jcbPaymentType.getSelectedItem()).getId());
            ps.setString(5, jtaPaymentText.getText());
            ps.setString(6, creditIdString);
            int operation = 1;
            if (isCreditNote == 1) {
                operation = 6;
            }
            ps.setInt(7, operation); // This is for Operation_type (sale == 1);
            ps.executeUpdate();

            this.DialogInvoice.setVisible(false);

        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void setValuesPaymentDialog() {
        switch (((DropDown) SALE.jcbLang.getSelectedItem()).getId()) {
            case 1: {
                setValuesComboBox("select NPM.ID, NPM.NAME_GERM from  N_PAYMENT_METHODS NPM", jcbPaymentType, false, -1, false);
                break;
            }
            case 2: {
                setValuesComboBox("select NPM.ID, NPM.NAME_ENG from  N_PAYMENT_METHODS NPM", jcbPaymentType, false, -1, false);
                break;
            }
            case 3: {
                setValuesComboBox("select NPM.ID, NPM.NAME_BUL from  N_PAYMENT_METHODS NPM", jcbPaymentType, false, -1, false);
                break;
            }
            default:
                break;
        }
    }

    private void ResetGenerator() {
        try {
            Connection con = getConnection();
            int genNumber = 0;
            genNumber = Integer.parseInt(jtxtSaleNumber.getText());
            genNumber = genNumber - 1;
            PreparedStatement ps = con.prepareStatement("ALTER SEQUENCE DEAL_NUMBER_GEN RESTART WITH " + genNumber);

            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void checkForExist() {
        Exist = 0;
        int rows = jTable1.getRowCount();
        for (int row = 0; row < rows; row++) {
            String article = jTable1.getValueAt(row, 0).toString();
            int articleID = Integer.parseInt(article);
            int lotID = ((DropDown) jTable1.getValueAt(row, 3)).getId();
            if (articleID == ((DropDown) jcbArticle.getSelectedItem()).getId() && lotID == ((DropDown) jcbLots.getSelectedItem()).getId()) {
                JOptionPane.showMessageDialog(null, "Такъв артикул вече съществува!", "Продажба", JOptionPane.NO_OPTION);
                Exist = 1;
                jcbArticleGroups.setSelectedIndex(-1);
                jcbArticle.setSelectedIndex(-1);
                jcbLots.setSelectedIndex(-1);
                jtxtPrice.setText("");
                jtxtQty.setValue(1);
                jtxtAvailability.setText("");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopUpDelete = new javax.swing.JMenuItem();
        jbtnRefreshSale = new javax.swing.JButton();
        jcbCountry = new javax.swing.JComboBox<>();
        DialogInvoice = new javax.swing.JDialog();
        jlbInvoice = new javax.swing.JLabel();
        jlbInvoiceNumber = new javax.swing.JLabel();
        jtxtInvoiceNumberDialog = new javax.swing.JTextField();
        jDateInvoice = new com.toedter.calendar.JDateChooser();
        jlbInvoiceDate = new javax.swing.JLabel();
        jcbPaymentType = new javax.swing.JComboBox<>();
        jlbPaymentType = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtaPaymentText = new javax.swing.JTextArea();
        jlbPaymentText = new javax.swing.JLabel();
        jchbReversal = new javax.swing.JCheckBox();
        jpCreditNote = new javax.swing.JPanel();
        jlbCreditNoteText = new javax.swing.JLabel();
        jlbCreditNoteNumber = new javax.swing.JLabel();
        jtxtCreditNoteNumber = new javax.swing.JTextField();
        jlbDateCreditNote = new javax.swing.JLabel();
        jCreditNoteDate = new com.toedter.calendar.JDateChooser();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jbtnSaveDialog = new javax.swing.JButton();
        jbtnCloseDialog = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jtxtSaleNumber = new javax.swing.JTextField();
        jlbSale = new javax.swing.JLabel();
        jDateSale = new com.toedter.calendar.JDateChooser();
        jlbDate = new javax.swing.JLabel();
        jcbDealType = new javax.swing.JComboBox<>();
        jlbDealType = new javax.swing.JLabel();
        jcbChannel = new javax.swing.JComboBox<>();
        jlbChannel = new javax.swing.JLabel();
        jcbClient = new javax.swing.JComboBox<>();
        jlbClient = new javax.swing.JLabel();
        jbtnNewClient = new javax.swing.JButton();
        jcbLang = new javax.swing.JComboBox<>();
        jlbLang = new javax.swing.JLabel();
        jlbSaleNumber = new javax.swing.JLabel();
        jbtnEditClient = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jcbArticleGroups = new javax.swing.JComboBox<>();
        jlbArticleGroup = new javax.swing.JLabel();
        jcbArticle = new javax.swing.JComboBox<>();
        jlbArticle = new javax.swing.JLabel();
        jcbLots = new javax.swing.JComboBox<>();
        jlbLot = new javax.swing.JLabel();
        jtxtPrice = new javax.swing.JTextField();
        jlbPrice = new javax.swing.JLabel();
        jlbAvailability = new javax.swing.JLabel();
        jtxtAvailability = new javax.swing.JTextField();
        jbtnAdd = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jlbTotalWoVat = new javax.swing.JLabel();
        jlbVatValue = new javax.swing.JLabel();
        jlbTotalWithVat = new javax.swing.JLabel();
        jtxtTotalWoVat = new javax.swing.JTextField();
        jtxtVatValue = new javax.swing.JTextField();
        jtxtTotalWithVat = new javax.swing.JTextField();
        jbtnClear = new javax.swing.JButton();
        jtxtQty = new javax.swing.JSpinner();
        jlbQty = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jtxtTransportCosts = new javax.swing.JTextField();
        jtxtOtherCosts = new javax.swing.JTextField();
        jtxtBankCosts = new javax.swing.JTextField();
        jtxtChannelCosts = new javax.swing.JTextField();
        jlbTransportCosts = new javax.swing.JLabel();
        jlbBankCosts = new javax.swing.JLabel();
        jlbChannelCosts = new javax.swing.JLabel();
        jlbOtherCosts = new javax.swing.JLabel();
        jtxtInvoiceNumber = new javax.swing.JTextField();
        jlbInvoiceNumbers = new javax.swing.JLabel();
        jcbPayment = new javax.swing.JComboBox<>();
        jlbPayment = new javax.swing.JLabel();
        jcbStatuses = new javax.swing.JComboBox<>();
        jlbStatus = new javax.swing.JLabel();
        jbtnSave = new javax.swing.JButton();
        jtbnPrint = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jbntInvoice = new javax.swing.JButton();
        jlbCreditNote = new javax.swing.JLabel();
        jxtCredit = new javax.swing.JTextField();
        jlbCredit = new javax.swing.JLabel();
        jlbCreditDate = new javax.swing.JLabel();
        jdtchCredit = new com.toedter.calendar.JDateChooser();
        jSeparator1 = new javax.swing.JSeparator();

        jPopUpDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/delete16.png"))); // NOI18N
        jPopUpDelete.setText("Изтрий!");
        jPopUpDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPopUpDeleteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jPopUpDelete);

        jbtnRefreshSale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/refresh.png"))); // NOI18N
        jbtnRefreshSale.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnRefreshSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRefreshSaleActionPerformed(evt);
            }
        });

        DialogInvoice.setTitle("Фактура");
        DialogInvoice.setAutoRequestFocus(false);
        DialogInvoice.setMinimumSize(new java.awt.Dimension(450, 450));
        DialogInvoice.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        jlbInvoice.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbInvoice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbInvoice.setText("Фактура");
        jlbInvoice.setToolTipText("");
        jlbInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jlbInvoiceNumber.setText("Номер:");

        jtxtInvoiceNumberDialog.setEditable(false);
        jtxtInvoiceNumberDialog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtInvoiceNumberDialogFocusLost(evt);
            }
        });
        jtxtInvoiceNumberDialog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtxtInvoiceNumberDialogMouseClicked(evt);
            }
        });

        jlbInvoiceDate.setText("Дата:");

        jcbPaymentType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbPaymentTypeActionPerformed(evt);
            }
        });

        jlbPaymentType.setText("Тип на плащане:");

        jtaPaymentText.setColumns(20);
        jtaPaymentText.setRows(5);
        jScrollPane2.setViewportView(jtaPaymentText);

        jlbPaymentText.setText("Допълнително описание:");

        jchbReversal.setText("Сторно");
        jchbReversal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchbReversalActionPerformed(evt);
            }
        });

        jpCreditNote.setEnabled(false);

        jlbCreditNoteText.setText("Допълнително описание:");

        jlbCreditNoteNumber.setText("Номер на кредитно известие:");

        jtxtCreditNoteNumber.setEditable(false);
        jtxtCreditNoteNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtxtCreditNoteNumberFocusGained(evt);
            }
        });

        jlbDateCreditNote.setText("Дата на кредитно известие:");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        javax.swing.GroupLayout jpCreditNoteLayout = new javax.swing.GroupLayout(jpCreditNote);
        jpCreditNote.setLayout(jpCreditNoteLayout);
        jpCreditNoteLayout.setHorizontalGroup(
            jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpCreditNoteLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addGroup(jpCreditNoteLayout.createSequentialGroup()
                        .addGroup(jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbCreditNoteNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbDateCreditNote))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCreditNoteDate, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtCreditNoteNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jlbCreditNoteText))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jpCreditNoteLayout.setVerticalGroup(
            jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpCreditNoteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbCreditNoteNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCreditNoteNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jpCreditNoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCreditNoteDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbDateCreditNote))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlbCreditNoteText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jbtnSaveDialog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Save32.png"))); // NOI18N
        jbtnSaveDialog.setText("Запис");
        jbtnSaveDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveDialogActionPerformed(evt);
            }
        });

        jbtnCloseDialog.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnCloseDialog.setText("Изход");
        jbtnCloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseDialogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DialogInvoiceLayout = new javax.swing.GroupLayout(DialogInvoice.getContentPane());
        DialogInvoice.getContentPane().setLayout(DialogInvoiceLayout);
        DialogInvoiceLayout.setHorizontalGroup(
            DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                                .addComponent(jtxtInvoiceNumberDialog, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38)
                                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbInvoiceDate)
                                    .addComponent(jDateInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jlbPaymentType)
                            .addComponent(jlbPaymentText)
                            .addComponent(jlbInvoiceNumber)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jchbReversal))
                            .addComponent(jpCreditNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jbtnSaveDialog)
                        .addGap(26, 26, 26)
                        .addComponent(jbtnCloseDialog)))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        DialogInvoiceLayout.setVerticalGroup(
            DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbInvoice)
                .addGap(18, 18, 18)
                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addComponent(jlbInvoiceNumber)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtInvoiceNumberDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addComponent(jlbInvoiceDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addComponent(jlbPaymentType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jlbPaymentText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jchbReversal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpCreditNote, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22)
                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCloseDialog)
                    .addComponent(jbtnSaveDialog)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Продажба");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jtxtSaleNumber.setEditable(false);
        jtxtSaleNumber.setBackground(new java.awt.Color(204, 204, 204));
        jtxtSaleNumber.setToolTipText("Номер на продажбата!");
        jtxtSaleNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtSaleNumberFocusLost(evt);
            }
        });
        jtxtSaleNumber.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtxtSaleNumberMouseClicked(evt);
            }
        });

        jlbSale.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jlbSale.setText("Продажба");

        jDateSale.setToolTipText("Дата на сключване на сделката!");

        jlbDate.setText("Дата на сделката:");

        jcbDealType.setToolTipText("От типа зависи ДДС процента!");
        jcbDealType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbDealTypeActionPerformed(evt);
            }
        });

        jlbDealType.setText("Тип на сделката:");

        jcbChannel.setToolTipText("През кой канал продаваме!");

        jlbChannel.setText("Канал на сделката:");

        jcbClient.setToolTipText("Клиент, на когото продаваме!");

        jlbClient.setText("Клиент:");

        jbtnNewClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/NewClient.png"))); // NOI18N
        jbtnNewClient.setText("Нов клиент");
        jbtnNewClient.setToolTipText("Добавяне на нов клиент!");
        jbtnNewClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNewClientActionPerformed(evt);
            }
        });

        jcbLang.setToolTipText("Език на който е сключена сделката!");
        jcbLang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbLangActionPerformed(evt);
            }
        });

        jlbLang.setText("Език на сделката:");

        jlbSaleNumber.setText("Номер:");

        jbtnEditClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/EditClient32x32.png"))); // NOI18N
        jbtnEditClient.setText("Редактирай");
        jbtnEditClient.setToolTipText("");
        jbtnEditClient.setFocusPainted(false);
        jbtnEditClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditClientActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(564, 564, 564)
                .addComponent(jlbSale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbLang, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbLang))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbSaleNumber)
                    .addComponent(jtxtSaleNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlbDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDateSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbDealType)
                    .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbChannel)
                    .addComponent(jcbChannel, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbClient)
                    .addComponent(jcbClient, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jbtnNewClient)
                .addGap(18, 18, 18)
                .addComponent(jbtnEditClient)
                .addGap(147, 147, 147))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbLang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbSale)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jlbDate, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jlbDealType))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jDateSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jlbChannel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbChannel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jlbClient)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jbtnEditClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addComponent(jbtnNewClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbSaleNumber)
                                .addGap(10, 10, 10)
                                .addComponent(jtxtSaleNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(51, 51, 51))))
        );

        jcbArticleGroups.setMaximumRowCount(15);
        jcbArticleGroups.setToolTipText("Група на артикула!");
        jcbArticleGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbArticleGroupsActionPerformed(evt);
            }
        });

        jlbArticleGroup.setText("Група на артикула:");

        jcbArticle.setMaximumRowCount(15);
        jcbArticle.setToolTipText("Артикул!");
        jcbArticle.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jcbArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbArticleActionPerformed(evt);
            }
        });

        jlbArticle.setText("Артикул:");

        jcbLots.setToolTipText("Партида, от която продаваме.\nЗа да се генерира наличната партида задължително\nтрябва да сме избрали Артикул и Канал.");
        jcbLots.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbLotsActionPerformed(evt);
            }
        });

        jlbLot.setText("Партида:");

        jtxtPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtPrice.setToolTipText("Цена на артикула, пресметната на база Канал!\nАко е празно значи нямаме цена в нито един канал за този Артикул!");
        jtxtPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPriceFocusLost(evt);
            }
        });

        jlbPrice.setText("Цена (с ДДС)");

        jlbAvailability.setText("Налично:");

        jtxtAvailability.setEditable(false);
        jtxtAvailability.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtAvailability.setToolTipText("Наличността на артикула!");

        jbtnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/cart32.png"))); // NOI18N
        jbtnAdd.setText("Добави");
        jbtnAdd.setToolTipText("Добавяне на артикула в кошницата!");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Код (SKU)", "Артикул", "Партида", "Единична цена (€)", "Цена без ДДС (€)", "Количество", "МE", "Обща цена (€)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setComponentPopupMenu(jPopupMenu1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTable1PropertyChange(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(400);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(40);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(5);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(70);
        }

        jlbTotalWoVat.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlbTotalWoVat.setText("Общо:");
        jlbTotalWoVat.setToolTipText("");

        jlbVatValue.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlbVatValue.setText("ДДС (%):");

        jlbTotalWithVat.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlbTotalWithVat.setText("Всичко:");

        jtxtTotalWoVat.setEditable(false);
        jtxtTotalWoVat.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jtxtTotalWoVat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtTotalWoVat.setToolTipText("");

        jtxtVatValue.setEditable(false);
        jtxtVatValue.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jtxtVatValue.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jtxtTotalWithVat.setEditable(false);
        jtxtTotalWithVat.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jtxtTotalWithVat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jbtnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/clear.png"))); // NOI18N
        jbtnClear.setText("Изтрий");
        jbtnClear.setToolTipText("Премахване на артикула от кошницата!");
        jbtnClear.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearActionPerformed(evt);
            }
        });

        jtxtQty.setEditor(new javax.swing.JSpinner.NumberEditor(jtxtQty, "#"));
        jtxtQty.setValue(1);

        jlbQty.setText("Количество:");

        //try{
            ///MaskFormatter format = new MaskFormatter("");
            // DecimalFormat dc = new DecimalFormat("0.00");
            // NumberFormat f = NumberFormat.getNumberInstance();
            //format.setValidCharacters("1234567890");
            //format.setPlaceholderCharacter('0');
            //format.setOverwriteMode(true);  
            // jtxtTransportCosts.setText(dc.format(jtxtTransportCosts.getText()));
            //}catch(java.text.ParseException e){
            //  e.printStackTrace();
            //}
        jtxtTransportCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtTransportCosts.setToolTipText("Разходи за транспорт!");
        jtxtTransportCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTransportCostsFocusLost(evt);
            }
        });

        jtxtOtherCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtOtherCosts.setToolTipText("Допълнителни разходи!");
        jtxtOtherCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtOtherCostsFocusLost(evt);
            }
        });

        jtxtBankCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtBankCosts.setToolTipText("Банкови разходи!");
        jtxtBankCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBankCostsFocusLost(evt);
            }
        });

        jtxtChannelCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtChannelCosts.setToolTipText("Разходи за канала!");
        jtxtChannelCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtChannelCostsFocusLost(evt);
            }
        });

        jlbTransportCosts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlbTransportCosts.setText("Транспортни разходи:");

        jlbBankCosts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlbBankCosts.setText("Банкови разходи:");

        jlbChannelCosts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlbChannelCosts.setText("За посредника:");

        jlbOtherCosts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlbOtherCosts.setText("Други разходи:");

        jtxtInvoiceNumber.setEditable(false);
        jtxtInvoiceNumber.setToolTipText("Номер и дата на фактурата!\nГенерира се автоматично от бутон фактура!\nАко е празно значи нямаме издадена фактура по тази продажба!");

        jlbInvoiceNumbers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlbInvoiceNumbers.setText("Номер и дата на фактурата:");

        jcbPayment.setForeground(new java.awt.Color(255, 255, 255));
        jcbPayment.setToolTipText("Тип на плащане!");

        jlbPayment.setText("Тип плащане:");

        jcbStatuses.setToolTipText("Статус на сделката!\nПри продажба по подразбиране е продадено!");
        jcbStatuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbStatusesActionPerformed(evt);
            }
        });

        jlbStatus.setText("Статус на сделката:");

        jbtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Save32.png"))); // NOI18N
        jbtnSave.setText("Запис");
        jbtnSave.setToolTipText("Записване на продажбата в базата данни!");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });

        jtbnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Print32.png"))); // NOI18N
        jtbnPrint.setText("Печат");
        jtbnPrint.setToolTipText("Отпечатване на фактура!");
        jtbnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnPrintActionPerformed(evt);
            }
        });

        jbtnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnClose.setText("Изход");
        jbtnClose.setToolTipText("Затваряне на формата!");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jbntInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/invoice32.png"))); // NOI18N
        jbntInvoice.setText("Фактура");
        jbntInvoice.setToolTipText("Въвеждане на информация за фактура по продажбата!");
        jbntInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbntInvoiceActionPerformed(evt);
            }
        });

        jlbCreditNote.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlbCreditNote.setForeground(new java.awt.Color(255, 0, 0));
        jlbCreditNote.setText("Сторнирана продажба!");

        jlbCredit.setText("Към фактура No:");

        jlbCreditDate.setText("Дата на фактурата:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlbOtherCosts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtOtherCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlbChannelCosts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtChannelCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jlbBankCosts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jtxtBankCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jlbTransportCosts)
                        .addGap(32, 32, 32)
                        .addComponent(jtxtTransportCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlbCredit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlbCreditDate)
                    .addComponent(jxtCredit)
                    .addComponent(jdtchCredit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlbInvoiceNumbers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtInvoiceNumber))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jlbCreditNote)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtbnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jbntInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbStatus)
                    .addComponent(jcbStatuses, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbPayment))
                .addGap(116, 116, 116)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnSave, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jSeparator2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jbntInvoice)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jlbInvoiceNumbers)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jtxtInvoiceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jtbnPrint))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addGap(29, 29, 29)
                                            .addComponent(jlbCreditNote))))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jlbPayment)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jcbPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(10, 10, 10)
                                    .addComponent(jlbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jcbStatuses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jbtnSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtnClose))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jlbCredit)
                                .addGap(8, 8, 8)
                                .addComponent(jxtCredit, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlbCreditDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdtchCredit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(16, 16, 16))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtTransportCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbTransportCosts))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtBankCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbBankCosts))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbChannelCosts)
                            .addComponent(jtxtChannelCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtOtherCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbOtherCosts)))))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbArticleGroup)
                            .addComponent(jcbArticleGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbArticle)
                            .addComponent(jcbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbLot)
                            .addComponent(jcbLots, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlbAvailability)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jtxtAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlbQty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbPrice)
                            .addComponent(jtxtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1214, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbVatValue)
                    .addComponent(jlbTotalWithVat)
                    .addComponent(jlbTotalWoVat, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtTotalWoVat, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtVatValue, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotalWithVat, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbArticleGroup)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbArticleGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jtxtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jbtnAdd)
                                .addComponent(jbtnClear))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jlbPrice)
                                .addGap(27, 27, 27)))
                        .addComponent(jlbQty)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jlbAvailability)
                                .addComponent(jlbLot))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jtxtAvailability, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jcbLots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jlbArticle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbTotalWoVat)
                    .addComponent(jtxtTotalWoVat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbVatValue)
                    .addComponent(jtxtVatValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlbTotalWithVat)
                    .addComponent(jtxtTotalWithVat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jcbLangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbLangActionPerformed
        jcbArticle.setEditable(false);
        jcbArticleGroups.setEditable(false);
        jcbLots.removeAllItems();
        jcbArticle.removeAllItems();
        jcbArticleGroups.removeAllItems();

        jcbLots.setSelectedIndex(-1);
        jcbArticle.setSelectedIndex(-1);
        jcbArticleGroups.setSelectedIndex(-1);
        jtxtAvailability.setText("");
        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
            case 1: {
                setValuesComboBox("select AG.ID, AG.NAME_DE from ARTICLE_GROUPS AG ", jcbArticleGroups, true, -1, false);
                break;
            }
            case 2: {
                setValuesComboBox("select AG.ID, AG.NAME_EN from ARTICLE_GROUPS AG ", jcbArticleGroups, true, -1, false);
                break;
            }
            case 3: {
                setValuesComboBox("select AG.ID, AG.NAME_BG from ARTICLE_GROUPS AG ", jcbArticleGroups, true, -1, false);
                break;
            }
            default:
                break;
        }
        setValuesPayment();
        jcbLang.setBackground(Color.red);
    }//GEN-LAST:event_jcbLangActionPerformed

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        int availQty = 0;
        if (!jtxtAvailability.getText().isEmpty()) {
            availQty = Integer.parseInt(jtxtAvailability.getText());
        }
        int qty = (Integer) jtxtQty.getValue();
        if (isCreditNote == 1) {
            qty *= -1;
        }
        if (jcbArticle.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Моля изберете артикул!", "Продажба", JOptionPane.NO_OPTION);
        } else if (jcbLots.getSelectedIndex() == -1 && isActive == 1) {
            JOptionPane.showMessageDialog(null, "Моля изберете партида!", "Продажба", JOptionPane.NO_OPTION);
        } else if (jtxtPrice.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Моля въведете цена на артикул!", "Продажба", JOptionPane.NO_OPTION);
        } else if (checkQuantity == 1 && (availQty < qty) && (((DropDown) jcbArticle.getSelectedItem()).getId()) != 2) {
            JOptionPane.showMessageDialog(null, "Недостатъчно количество в партидата!", "Продажба", JOptionPane.NO_OPTION);
        } else {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            double priceWoVat = 0.00;
            String valueWoVat = null;
            double priceWithVat = 0.00;
            String valueWithVat = null;
            String code = null;
            int ID = 0;

            String MeasureEN = null;
            String MeasureDE = null;
            String MeasureBG = null;
            double Total = 0;
            String TotalSTR = null;
            if (VatPCT == 0) {
                Total = Double.valueOf(jtxtPrice.getText()) * Double.valueOf(qty);
                TotalSTR = String.format("%.2f", Total);
                Total = Double.valueOf(TotalSTR.replace(",", "."));
                priceWoVat = Double.valueOf(jtxtPrice.getText()); // priceWithVat
                valueWoVat = String.format("%.2f", priceWoVat);
                priceWoVat = Double.valueOf(valueWoVat.replace(",", "."));
            } else {
                // Used formula : Z – (Z * A /(100+A));
                priceWithVat = Double.valueOf(jtxtPrice.getText());
                valueWithVat = String.format("%.2f", priceWithVat);
                priceWithVat = Double.valueOf(valueWithVat.replace(",", "."));

                priceWoVat = priceWithVat - (priceWithVat * VatPCT / (100 + VatPCT));
                valueWoVat = String.format("%.2f", priceWoVat);
                priceWoVat = Double.valueOf(valueWoVat.replace(",", "."));

                Total = priceWoVat * Double.valueOf(qty);
                TotalSTR = String.format("%.2f", Total);
                Total = Double.valueOf(TotalSTR.replace(",", "."));

            }
            try {

                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select A.ID, A.CODE, M.SHORT_NAME_EN, M.SHORT_NAME_DE, M.SHORT_NAME_BG from ARTICLES A join N_MEASURES M on A.MEASURE_ID = M.ID\n"
                        + "where A.ID = " + (((DropDown) jcbArticle.getSelectedItem()).getId()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ID = rs.getInt(1);
                    code = rs.getString(2);
                    MeasureEN = rs.getString(3);
                    MeasureDE = rs.getString(4);
                    MeasureBG = rs.getString(5);
                    String article = jcbArticle.getSelectedItem().toString();
                    article = article.substring(article.indexOf("|") + 2, article.length());
                    if (jTable1.getRowCount() > 0) {
                        checkForExist();
                    }
                    if (Exist == 0) {
                        String Qty = String.valueOf(jtxtQty.getValue());
                        if (isCreditNote == 1) {
                            Qty = "-" + Qty;
                        }
                        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
                            case 1:
                                model.addRow(new Object[]{ID, code, article, jcbLots.getSelectedItem(), Double.parseDouble(jtxtPrice.getText()), priceWoVat, Qty, MeasureDE, Total});
                                break;
                            case 2:
                                model.addRow(new Object[]{ID, code, article, jcbLots.getSelectedItem(), Double.parseDouble(jtxtPrice.getText()), priceWoVat, Qty, MeasureEN, Total});
                                break;
                            case 3:
                                model.addRow(new Object[]{ID, code, article, jcbLots.getSelectedItem(), Double.parseDouble(jtxtPrice.getText()), priceWoVat, Qty, MeasureBG, Total});
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            getSum();
            jcbArticleGroups.setSelectedIndex(-1);
            jcbArticle.setSelectedIndex(-1);
            jcbLots.setSelectedIndex(-1);
            jtxtPrice.setText("");
            jtxtQty.setValue(1);
            jtxtAvailability.setText("");
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jcbArticleGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbArticleGroupsActionPerformed
        if (jcbArticleGroups.getSelectedIndex() != -1) {
            setValuesArticles();
            AutoCompleteDecorator.decorate(this.jcbArticle);
        }
    }//GEN-LAST:event_jcbArticleGroupsActionPerformed

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (jTable1.getSelectedRow() == -1) {
            if (jTable1.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Няма данни!", "Продажба", JOptionPane.NO_OPTION);
            } else {
                JOptionPane.showMessageDialog(null, "Моля изберете реда, който искате да изтриете!", "Продажба", JOptionPane.NO_OPTION);
            }
        } else {
            model.removeRow(jTable1.getSelectedRow());
        }
        getSum();
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jcbArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbArticleActionPerformed
        if ((jcbArticle.getSelectedIndex() != -1 && (jcbChannel.getSelectedIndex() == -1 || jcbDealType.getSelectedIndex() == -1)) && isActive == 1) {
            JOptionPane.showMessageDialog(null, "Моля изберете канал и тип на сделката!", "Продажба", JOptionPane.NO_OPTION);
            jcbArticleGroups.setSelectedIndex(-1);
            jcbArticle.setSelectedIndex(-1);
            jcbArticle.setEditable(false);
        } else if (jcbArticle.getSelectedIndex() != -1) {
            setValuesLots();
            if (jcbLots.getItemCount() == 1) {
                jcbLots.setSelectedIndex(0);
            }
            int priceListType = 0;
            double Price = 0;
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select NC.PRICE_LIST from N_CHANNELS NC where NC.ID = " + ((DropDown) jcbChannel.getSelectedItem()).getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    priceListType = rs.getInt(1);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            switch (priceListType) {
                case 1:
                    try {
                        Connection con = getConnection();
                        PreparedStatement ps = con.prepareStatement("select PL.SALE_PRICE1 from PRICE_LISTS PL where PL.ARTICLE_ID =  " + ((DropDown) jcbArticle.getSelectedItem()).getId());
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            Price = rs.getDouble(1);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                    jtxtPrice.setText(String.format("%.2f", Price).replace(",", "."));
                    break;
                case 2:
                    try {
                        Connection con = getConnection();
                        PreparedStatement ps = con.prepareStatement("select PL.SALE_PRICE2 from PRICE_LISTS PL where PL.ARTICLE_ID =  " + ((DropDown) jcbArticle.getSelectedItem()).getId());
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            Price = rs.getDouble(1);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                    jtxtPrice.setText(String.format("%.2f", Price).replace(",", "."));
                    break;
                case 3:
                    try {
                        Connection con = getConnection();
                        PreparedStatement ps = con.prepareStatement("select PL.SALE_PRICE3 from PRICE_LISTS PL where PL.ARTICLE_ID =  " + ((DropDown) jcbArticle.getSelectedItem()).getId());
                        ResultSet rs = ps.executeQuery();
                        while (rs.next()) {
                            Price = rs.getDouble(1);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                    jtxtPrice.setText(String.format("%.2f", Price).replace(",", "."));
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_jcbArticleActionPerformed

    private void jbntInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbntInvoiceActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (isCreditNote == 1) {
            jlbInvoice.setText("Кредитно известие");
            DialogInvoice.setTitle("Кредитно известие");
        }
        else {
           jlbInvoice.setText("Фактура");
           DialogInvoice.setTitle("Фактура"); 
        }
        if (isSave == 0) {
            String msg = "Искате ли да издадете фактура?";
            String msgTypе = "Продажба";
            if (isCreditNote == 1) {
                msg = "Искате ли да издадете КИ?";
                msgTypе = "Сторнирана продажба";
            }
            int dialogButton = JOptionPane.showConfirmDialog(null, msg, msgTypе, JOptionPane.YES_NO_OPTION);
            if (dialogButton == JOptionPane.YES_OPTION) {
                if (checkInputs()) {
                    JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
                } else if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Моля Добавете поне един артикул в таблицата!");
                } else {
                    InsertMaster();
                    InsertDetail();
                    jDateInvoice.setDate(jDateSale.getDate());
                    jpCreditNote.setVisible(false);
                    setValuesPaymentDialog();
                    if (jtxtInvoiceNumber.getText().isEmpty()) {
                        jcbPaymentType.setSelectedIndex(-1);
                    }
                    DialogInvoice.pack();
                    DialogInvoice.setLocationRelativeTo(null);
                    DialogInvoice.setVisible(true);
                }
                if (dialogButton == JOptionPane.NO_OPTION) {
                    remove(dialogButton);
                }
            }
        } else if (isSave == 1) {
            jDateInvoice.setDate(jDateSale.getDate());
            if (jchbReversal.isSelected()) {
                jpCreditNote.setVisible(true);
            } else {
                jpCreditNote.setVisible(false);
            }
            setValuesPaymentDialog();
            if (jtxtInvoiceNumber.getText().isEmpty()) {
                jcbPaymentType.setSelectedIndex(-1);
            } else {
                String number = jtxtInvoiceNumber.getText();
                String invoiceNumber = number.substring(number.indexOf("-") + 1, number.indexOf("/"));
                jtxtInvoiceNumberDialog.setText(invoiceNumber);
                System.out.println(invoiceNumber);
                jcbPaymentType.getModel().setSelectedItem(jcbPayment.getModel().getSelectedItem());
            }
            DialogInvoice.pack();
            DialogInvoice.setLocationRelativeTo(null);
            DialogInvoice.setVisible(true);
        }
    }//GEN-LAST:event_jbntInvoiceActionPerformed

    private void jbtnNewClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNewClientActionPerformed
        Client client = new Client();
        client.setVisible(true);
        Client.jcbClientType.setSelectedIndex(0);
        client.isFromForm = 1;
    }//GEN-LAST:event_jbtnNewClientActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        int dialogButton = JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да излезнете!", "Продажба", JOptionPane.YES_NO_OPTION);
        if (dialogButton == JOptionPane.YES_OPTION) {
            if (!jtxtSaleNumber.getText().isEmpty() && isSave == 0) {
                ResetGenerator();
                setVisible(false);
            } else {
                setVisible(false);
            }
            if (dialogButton == JOptionPane.NO_OPTION) {
                remove(dialogButton);
            }
        }
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (checkInputs()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Моля Добавете поне един артикул в таблицата!");
        } else if (isCreditNote == 1 && (jxtCredit.getText().isEmpty() || jdtchCredit.getDate() == null)) {
            JOptionPane.showMessageDialog(null, "Моля въведете към Фактура No и дата на фактура!");
        } else {
            InsertMaster();
            InsertDetail();
        }
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jcbDealTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbDealTypeActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        jtxtVatValue.setText("");
        jtxtTotalWoVat.setText("");
        jtxtTotalWithVat.setText("");
        if (jcbDealType.getSelectedIndex() != -1) {
            int Accuring = 0;
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select DT.ACCRUING_VAT, iif(DT.ACCRUING_VAT = 1,(select C.VAT_PERCENTAGE from MY_COMPANY C),0) VAT_PCT from N_DEAL_TYPES DT where DT.ID = " + (((DropDown) jcbDealType.getSelectedItem()).getId()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Accuring = rs.getInt(1);
                    VatPCT = rs.getDouble(2);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            if (Accuring == 1) {
                jlbVatValue.setText("ДДС (" + VatPCT + "%): ");
            } else {
                jlbVatValue.setText("ДДС (" + 0.00 + "%): ");
            }
        }

    }//GEN-LAST:event_jcbDealTypeActionPerformed

    private void jtbnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnPrintActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (isSave == 0) {
            int dialogButton = JOptionPane.showConfirmDialog(null, "Преди да печатате трябва да запишете продажбата в базата данни! \n Искате ли да го направите!", "Продажба", JOptionPane.YES_NO_OPTION);
            if (dialogButton == JOptionPane.YES_OPTION) {
                if (checkInputs()) {
                    JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
                } else if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Моля Добавете поне един артикул в таблицата!");
                } else if (jtxtInvoiceNumber.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Моля попълнете данните за фактура!");
                } else {
                    InsertMaster();
                    InsertDetail();
                    isSave = 1;
                }
                if (dialogButton == JOptionPane.NO_OPTION) {
                    remove(dialogButton);
                }
            }
        } else if (isSave == 1) {
            try {
                if (jtxtInvoiceNumber.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Моля попълнете данните за фактура!");
                } else {
                    String number = jtxtInvoiceNumber.getText();
                    String invoiceNumber = number.substring(0, number.indexOf("/"));
                    switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
                        case 1: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 3 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 4 order by R.ID) SUBREPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 13 order by R.ID) OFFER,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 16 order by R.ID) CREDIT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                if (isOffer == 1) {
                                    fileMain = rs.getBinaryStream("OFFER");
                                }
                                if (isCreditNote == 1) {
                                    fileMain = rs.getBinaryStream("CREDIT");
                                }
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(saleID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Invoice " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        case 2: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 1 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 2 order by R.ID) SUBREPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 14 order by R.ID) OFFER,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 17 order by R.ID) CREDIT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                if (isOffer == 1) {
                                    fileMain = rs.getBinaryStream("OFFER");
                                }
                                if (isCreditNote == 1) {
                                    fileMain = rs.getBinaryStream("CREDIT");
                                }
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(saleID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Invoice " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        case 3: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 5 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 6 order by R.ID) SUBREPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 15 order by R.ID) OFFER,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 18 order by R.ID) CREDIT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                if (isOffer == 1) {
                                    fileMain = rs.getBinaryStream("OFFER");
                                }
                                if (isCreditNote == 1) {
                                    fileMain = rs.getBinaryStream("CREDIT");
                                }
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(saleID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Invoice " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            } catch (JRException e) {
                Logger.getLogger(SALE.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(this, "Възникна грешка при печат на фактура, моля проверете данните и опитайте отново!");
            } catch (SQLException ex) {
                Logger.getLogger(SALE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jtbnPrintActionPerformed

    private void jcbLotsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbLotsActionPerformed
        if (jcbArticleGroups.getSelectedIndex() != -1 && jcbArticle.getSelectedIndex() != -1 && jcbLots.getSelectedIndex() != -1) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select cast(AV.QTY as DM_INTEGER) QTY from AVAILABILITY(?,?) AV");
                ps.setInt(1, (((DropDown) jcbArticle.getSelectedItem()).getId()));
                ps.setInt(2, (((DropDown) jcbLots.getSelectedItem()).getId()));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    jtxtAvailability.setText(rs.getString("QTY"));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }//GEN-LAST:event_jcbLotsActionPerformed

    private void jtxtTransportCostsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtTransportCostsFocusLost
        if (!jtxtTransportCosts.getText().isEmpty()) {
            try {
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedText = dc.format(Double.parseDouble(jtxtTransportCosts.getText()));
                jtxtTransportCosts.setText(formattedText.replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtTransportCosts.setText("");
            }
        }
    }//GEN-LAST:event_jtxtTransportCostsFocusLost

    private void jtxtPriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtPriceFocusLost
        if (!jtxtPrice.getText().isEmpty()) {
            try {
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedText = dc.format(Double.parseDouble(jtxtPrice.getText()));
                jtxtPrice.setText(formattedText.replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtPrice.setText("");
            }
        }
    }//GEN-LAST:event_jtxtPriceFocusLost

    private void jtxtBankCostsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtBankCostsFocusLost
        if (!jtxtBankCosts.getText().isEmpty()) {
            try {
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedText = dc.format(Double.parseDouble(jtxtBankCosts.getText()));
                jtxtBankCosts.setText(formattedText.replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtBankCosts.setText("");
            }
        }
    }//GEN-LAST:event_jtxtBankCostsFocusLost

    private void jtxtChannelCostsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtChannelCostsFocusLost
        if (!jtxtChannelCosts.getText().isEmpty()) {
            try {
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedText = dc.format(Double.parseDouble(jtxtChannelCosts.getText()));
                jtxtChannelCosts.setText(formattedText.replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtChannelCosts.setText("");
            }
        }
    }//GEN-LAST:event_jtxtChannelCostsFocusLost

    private void jtxtOtherCostsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtOtherCostsFocusLost
        if (!jtxtOtherCosts.getText().isEmpty()) {
            try {
                DecimalFormat dc = new DecimalFormat("0.00");
                String formattedText = dc.format(Double.parseDouble(jtxtOtherCosts.getText()));
                jtxtOtherCosts.setText(formattedText.replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtOtherCosts.setText("");
            }
        }
    }//GEN-LAST:event_jtxtOtherCostsFocusLost

    private void jtxtSaleNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtSaleNumberFocusLost
        if (!jtxtSaleNumber.getText().isEmpty()) {
            jtxtSaleNumber.setBackground(Color.WHITE);
            try {
                NumberFormat nfm = NumberFormat.getInstance(Locale.getDefault());
                String formattedText = nfm.format(Integer.parseInt(jtxtSaleNumber.getText()));
                formattedText = formattedText.replace("\u00A0", "");
                jtxtSaleNumber.setText(formattedText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtSaleNumber.setText("");
            }
        }
    }//GEN-LAST:event_jtxtSaleNumberFocusLost

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        jbtnClose.doClick();
    }//GEN-LAST:event_onExit

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        if (jTable1.getRowCount() != 0 && jTable1.getValueAt(jTable1.getSelectedRow(), 6).equals("")) {
            JOptionPane.showMessageDialog(null, "Моля попълнете количество");
        } else if (jTable1.getRowCount() != 0 && !jTable1.getValueAt(jTable1.getSelectedRow(), 5).equals("")) {
            String unitPrice = jTable1.getValueAt(jTable1.getSelectedRow(), 4).toString();
            double unitPriceD = Double.parseDouble(unitPrice);
            double priceWoVat = 0;
            priceWoVat = unitPriceD - (unitPriceD * VatPCT / (100 + VatPCT));
            jTable1.setValueAt(priceWoVat, jTable1.getSelectedRow(), 5);
            String qty = jTable1.getValueAt(jTable1.getSelectedRow(), 6).toString();
            String price = jTable1.getValueAt(jTable1.getSelectedRow(), 5).toString();
            double qtyD = Double.parseDouble(qty);
            double priceD = Double.parseDouble(price);
            jTable1.setValueAt(qtyD * priceD, jTable1.getSelectedRow(), 8);
            getSum();
        }
    }//GEN-LAST:event_jTable1PropertyChange

    private void jcbStatusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbStatusesActionPerformed
        if (((DropDown) jcbStatuses.getSelectedItem()).getId() == 5) {
            jbtnSave.setEnabled(true);
            jPopUpDelete.setEnabled(true);
        } else if (isSave == 1) {
            jbtnSave.setEnabled(false);
            jPopUpDelete.setEnabled(false);
        }
    }//GEN-LAST:event_jcbStatusesActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenu1.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTable1MouseReleased

    private void jPopUpDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPopUpDeleteActionPerformed
        int dialogButton = JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете артикула!", "Продажба", JOptionPane.YES_NO_OPTION);
        if (dialogButton == JOptionPane.YES_OPTION) {
            try {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                if (jTable1.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "Моля изберете артикула, който искате да изтриете!", "Продажба", JOptionPane.NO_OPTION);
                } else {
                    Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement("delete from DEAL_DETAILS DD\n"
                            + "where (DD.DEAL_ID = ? and DD.ARTICLE_ID = ? and DD.LOT_ID = ?)");
                    ps.setInt(1, saleID);
                    ps.setString(2, jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0).toString());
                    ps.setInt(3, ((DropDown) jTable1.getValueAt(jTable1.getSelectedRow(), 3)).getId());
                    ps.executeUpdate();
                    model.removeRow(jTable1.getSelectedRow());
                    getSum();
                    InsertMaster();
                    InsertDetail();
                    JOptionPane.showMessageDialog(null, "Успешно изтрихте Артикула!");

                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Не може да изтриете този Артикул!");
            }
            if (dialogButton == JOptionPane.NO_OPTION) {
                remove(dialogButton);
            }
        }
    }//GEN-LAST:event_jPopUpDeleteActionPerformed

    private void jbtnRefreshSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRefreshSaleActionPerformed
        setValuesClients();
        if (savedClient == 1) {
            int itemCount = 0;
            itemCount = jcbClient.getItemCount();
            jcbClient.setSelectedIndex(itemCount - 1);
        }
    }//GEN-LAST:event_jbtnRefreshSaleActionPerformed

    private void jbtnEditClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditClientActionPerformed
        if (jcbClient.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Не сте избрали клиент!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select first 1\n"
                        + "C.ID, C.PERSON, C.NAME, C.COUNTRY_ID, CC.NAME COUNTRY, C.CITY, C.ADDRESS, C.VAT_NUMBER, C.MOL\n"
                        + "from\n"
                        + "  CLIENTS C\n"
                        + "  join N_COUNTRIES CC on CC.ID = C.COUNTRY_ID\n"
                        + "where\n"
                        + "  C.ID = " + (((DropDown) jcbClient.getSelectedItem()).getId()));
                ResultSet rs = ps.executeQuery();
                Vector<DropDown> countries = new Vector<>();
                while (rs.next()) {
                    int person = rs.getInt("PERSON");
                    countries.addElement(new DropDown(rs.getInt("COUNTRY_ID"), rs.getString("COUNTRY")));
                    jcbCountry.setModel(new DefaultComboBoxModel(countries));

                    Client client = new Client();
                    client.clientID = rs.getInt("ID");
                    client.jcbClientType.setSelectedIndex(0);
                    if (person == 1) {
                        client.jchbPerson.setSelected(true);
                        client.jchbFirm.setSelected(false);
                    } else if (person == 0) {
                        client.jchbFirm.setSelected(true);
                        client.jchbPerson.setSelected(false);
                    }
                    client.jcbCountries.getModel().setSelectedItem(jcbCountry.getModel().getSelectedItem());
                    client.jtxtClientName.setText(rs.getString("NAME"));
                    client.jtxtCity.setText(rs.getString("CITY"));
                    client.jtxtAddress.setText(rs.getString("ADDRESS"));
                    client.jtxtVatNumber.setText(rs.getString("VAT_NUMBER"));
                    client.jtxtMOL.setText(rs.getString("MOL"));

                    countries.clear();
                    client.isFromForm = 0;
                    client.setVisible(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SALE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jbtnEditClientActionPerformed

    private void jtxtSaleNumberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtxtSaleNumberMouseClicked
        generateSaleID();
        GenerateSaleNumber();
    }//GEN-LAST:event_jtxtSaleNumberMouseClicked

    private void jtxtInvoiceNumberDialogFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtInvoiceNumberDialogFocusLost
        Date date = jDateInvoice.getDate();
        String year = String.format("%1$tY", date);
        String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
        SALE.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);
        String number = jtxtInvoiceNumber.getText();
        String invoiceNumber = number.substring(0, number.indexOf("/"));
        String myInvoiceNumber = null;
        if (!jtxtInvoiceNumberDialog.getText().isEmpty() && checkInvoiceNumber == 1) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select I.INVOICE_NUMBER from INVOICES I where I.INVOICE_NUMBER = ? and I.OPERATION_ID = 1");
                ps.setString(1, invoiceNumber);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    myInvoiceNumber = rs.getString(1);
                    if (invoiceNumber.equals(myInvoiceNumber)) {
                        JOptionPane.showMessageDialog(this, "Фактура с такъв номер вече съществува!");
                        jtxtInvoiceNumberDialog.setText("");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        }
    }//GEN-LAST:event_jtxtInvoiceNumberDialogFocusLost

    private void jtxtInvoiceNumberDialogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtxtInvoiceNumberDialogMouseClicked
        if (evt.getClickCount() == 2) {
            jtxtInvoiceNumberDialog.setEditable(true);
            checkInvoiceNumber = 1;
        }
    }//GEN-LAST:event_jtxtInvoiceNumberDialogMouseClicked

    private void jcbPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbPaymentTypeActionPerformed
        if (jtxtInvoiceNumberDialog.getText().isEmpty()) {
            GenerateInvoiceNumber();
            Date date = jDateInvoice.getDate();
            String year = String.format("%1$tY", date);
            String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
            SALE.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);
            String number = jtxtInvoiceNumber.getText();
            String invoiceNumber = number.substring(0, number.indexOf("/"));
            String myInvoiceNumber = null;
            if (!jtxtInvoiceNumberDialog.getText().isEmpty() && checkInvoiceNumber == 1) {
                try {
                    Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement("select I.INVOICE_NUMBER from INVOICES I where I.INVOICE_NUMBER = ? and I.OPERATION_ID = 1");
                    ps.setString(1, invoiceNumber);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        myInvoiceNumber = rs.getString(1);
                        if (invoiceNumber.equals(myInvoiceNumber)) {
                            JOptionPane.showMessageDialog(this, "Фактура с такъв номер вече съществува!");
                            jtxtInvoiceNumberDialog.setText("");
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                }
            }
        }
    }//GEN-LAST:event_jcbPaymentTypeActionPerformed

    private void jchbReversalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchbReversalActionPerformed
        /*        // jpCreditNote.setVisible(false);
        if (jchbReversal.isSelected()) {
            jpCreditNote.setVisible(true);
            Date creditDate = new Date();
            jCreditNoteDate.setDate(creditDate);
        } else {
            jpCreditNote.setVisible(false);
        }*/
    }//GEN-LAST:event_jchbReversalActionPerformed

    private void jtxtCreditNoteNumberFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtCreditNoteNumberFocusGained
        if (jchbReversal.isSelected() && jtxtCreditNoteNumber.getText().isEmpty()) {
            if (isSave == 0) {
                JOptionPane.showMessageDialog(null, "Не може да сторнирате продажба, която не е записана!");
            } else {
                GenerateCreditNote();
            }
        }
    }//GEN-LAST:event_jtxtCreditNoteNumberFocusGained

    private void jbtnSaveDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveDialogActionPerformed
        if (jtxtInvoiceNumberDialog.getText().isEmpty() && jcbPaymentType.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else if (!jchbReversal.isSelected()) {
            InsertUpdateInvoice();
            checkInvoiceNumber = 0;
            jtxtInvoiceNumberDialog.setEditable(false);
            Date date = jDateInvoice.getDate();
            String year = String.format("%1$tY", date);
            String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
            if (SALE.jtxtInvoiceNumber.getText().isEmpty()) {
                SALE.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);
            }
            SALE.jcbPayment.getModel().setSelectedItem(SALE.jcbPaymentType.getModel().getSelectedItem());
        } else if (jchbReversal.isSelected()) {
            if (jtxtCreditNoteNumber.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Моля попълнете номер на кредитно известие!");
            } else {
                try {
                    Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement("update or insert into CREDIT_NOTES (ID, CR_NOTE_NUMBER, CR_NOTE_DATE, CR_NOTE_TEXT)\n"
                            + "values (?, ?, ?, ?) matching (ID, CR_NOTE_NUMBER)");
                    ps.setInt(1, creditID);
                    ps.setString(2, jtxtCreditNoteNumber.getText());
                    java.sql.Date sqldate = new java.sql.Date(jCreditNoteDate.getDate().getTime());
                    ps.setDate(3, sqldate);
                    ps.setString(4, jTextArea1.getText());
                    ps.executeUpdate();
                    InsertUpdateInvoice();
                    this.DialogInvoice.setVisible(false);

                } catch (HeadlessException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_jbtnSaveDialogActionPerformed

    private void jbtnCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseDialogActionPerformed
        DialogInvoice.setVisible(false);
    }//GEN-LAST:event_jbtnCloseDialogActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SALE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SALE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SALE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SALE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SALE().setVisible(true);

            }

        });

    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {

        public DecimalFormatRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }
        //   private static final DecimalFormat formatter = new DecimalFormat("#.00");
        private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            // First format the cell value as required
            value = formatter.format((Number) value);

            // And pass it on to parent class
            return super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog DialogInvoice;
    public com.toedter.calendar.JDateChooser jCreditNoteDate;
    private com.toedter.calendar.JDateChooser jDateInvoice;
    public static com.toedter.calendar.JDateChooser jDateSale;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JMenuItem jPopUpDelete;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private static javax.swing.JTable jTable1;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JButton jbntInvoice;
    public javax.swing.JButton jbtnAdd;
    public javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnCloseDialog;
    private javax.swing.JButton jbtnEditClient;
    public javax.swing.JButton jbtnNewClient;
    public static javax.swing.JButton jbtnRefreshSale;
    public javax.swing.JButton jbtnSave;
    public javax.swing.JButton jbtnSaveDialog;
    public javax.swing.JComboBox<String> jcbArticle;
    public javax.swing.JComboBox<String> jcbArticleGroups;
    public javax.swing.JComboBox<String> jcbChannel;
    public javax.swing.JComboBox<String> jcbClient;
    private javax.swing.JComboBox<String> jcbCountry;
    public javax.swing.JComboBox<String> jcbDealType;
    public static javax.swing.JComboBox<String> jcbLang;
    public javax.swing.JComboBox<String> jcbLots;
    public static javax.swing.JComboBox<String> jcbPayment;
    private static javax.swing.JComboBox<String> jcbPaymentType;
    public javax.swing.JComboBox<String> jcbStatuses;
    public javax.swing.JCheckBox jchbReversal;
    public com.toedter.calendar.JDateChooser jdtchCredit;
    private javax.swing.JLabel jlbArticle;
    private javax.swing.JLabel jlbArticleGroup;
    private javax.swing.JLabel jlbAvailability;
    private javax.swing.JLabel jlbBankCosts;
    private javax.swing.JLabel jlbChannel;
    private javax.swing.JLabel jlbChannelCosts;
    private javax.swing.JLabel jlbClient;
    public javax.swing.JLabel jlbCredit;
    public javax.swing.JLabel jlbCreditDate;
    public javax.swing.JLabel jlbCreditNote;
    public javax.swing.JLabel jlbCreditNoteNumber;
    public javax.swing.JLabel jlbCreditNoteText;
    private javax.swing.JLabel jlbDate;
    public javax.swing.JLabel jlbDateCreditNote;
    private javax.swing.JLabel jlbDealType;
    public javax.swing.JLabel jlbInvoice;
    private javax.swing.JLabel jlbInvoiceDate;
    private javax.swing.JLabel jlbInvoiceNumber;
    public javax.swing.JLabel jlbInvoiceNumbers;
    private javax.swing.JLabel jlbLang;
    private javax.swing.JLabel jlbLot;
    private javax.swing.JLabel jlbOtherCosts;
    private javax.swing.JLabel jlbPayment;
    private javax.swing.JLabel jlbPaymentText;
    private javax.swing.JLabel jlbPaymentType;
    private javax.swing.JLabel jlbPrice;
    private javax.swing.JLabel jlbQty;
    public javax.swing.JLabel jlbSale;
    private javax.swing.JLabel jlbSaleNumber;
    private javax.swing.JLabel jlbStatus;
    private javax.swing.JLabel jlbTotalWithVat;
    private javax.swing.JLabel jlbTotalWoVat;
    private javax.swing.JLabel jlbTransportCosts;
    public javax.swing.JLabel jlbVatValue;
    public javax.swing.JPanel jpCreditNote;
    public javax.swing.JTextArea jtaPaymentText;
    private javax.swing.JButton jtbnPrint;
    private javax.swing.JTextField jtxtAvailability;
    public javax.swing.JTextField jtxtBankCosts;
    public javax.swing.JTextField jtxtChannelCosts;
    public javax.swing.JTextField jtxtCreditNoteNumber;
    public static javax.swing.JTextField jtxtInvoiceNumber;
    public javax.swing.JTextField jtxtInvoiceNumberDialog;
    public javax.swing.JTextField jtxtOtherCosts;
    public javax.swing.JTextField jtxtPrice;
    public javax.swing.JSpinner jtxtQty;
    public javax.swing.JTextField jtxtSaleNumber;
    private javax.swing.JTextField jtxtTotalWithVat;
    private javax.swing.JTextField jtxtTotalWoVat;
    public javax.swing.JTextField jtxtTransportCosts;
    private javax.swing.JTextField jtxtVatValue;
    public javax.swing.JTextField jxtCredit;
    // End of variables declaration//GEN-END:variables
}
