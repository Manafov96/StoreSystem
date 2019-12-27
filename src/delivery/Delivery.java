/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delivery;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import static Tools.getConnection.getConnection;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import Tools.DropDown;
import static Tools.getConnection.getConnection;
import static Tools.setValuesComboBox.setValuesComboBox;
import java.io.InputStream;
import login.Login;
import net.sf.jasperreports.engine.JasperExportManager;
import sale.SALE;

/**
 *
 * @author Viko
 */
public class Delivery extends javax.swing.JFrame {

    public int deliveryID = 0;
    public int lotID = 0;
    public double VatPCT = 0;
    public int isSave = 0;
    public int Exist = 0;
    public int isActive = 1;
    public static int savedSupplier = 0;
    public int checkInvoiceNumber = 1;

    /**
     * Creates new form Delivery
     */
    public Delivery() {
        initComponents();
        // set comboBox Deal Type
        setValuesComboBox("select DT.ID, DT.NAME DEAL_TYPE from N_DEAL_TYPES DT", jcbDealType, false, -1, false);
        setValuesSuppliers();
        setValuesLangs();
        setValueStatuses();
        setValuesPayment();
        setDate();
        jtxtDeliveryNumber.setBackground(Color.pink);
        jtxtDeliveryNumber.setEditable(false);
        ////// This is for add Reversal Delivery////////
        jchbReversal.setVisible(false);
        jpCreditNote.setVisible(false);
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        /////////////////////////////////////////////////
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        TableColumn colPrice = jTable1.getColumnModel().getColumn(3);
        TableColumn colPriceWoVat = jTable1.getColumnModel().getColumn(4);
        TableColumn total = jTable1.getColumnModel().getColumn(7);
        colPrice.setCellRenderer(new DecimalFormatRenderer());
        colPriceWoVat.setCellRenderer(new DecimalFormatRenderer());
        total.setCellRenderer(new DecimalFormatRenderer());
    }

    private boolean checkInputs() {
        if ((deliveryID == 0
                || jtxtDeliveryNumber.getText().isEmpty()
                || jcbDealType.getSelectedIndex() == -1
                || jcbSupplier.getSelectedIndex() == - 1)
                && isActive == 1) {
            return true;
        }
        return false;
    }

    private void setDate() {
        Date date = new Date();
        jDateDelivery.setDate(date);
    }

    private void setValuesSuppliers() {
        setValuesComboBox("select C.ID, C.NAME CLIENTS from CLIENTS C where C.TYPE_ID = 2 and C.ID > 0", jcbSupplier, true, -1, false);
    }

    private void setValuesLangs() {
        setValuesComboBox("select NL.ID, NL.SHORT_NAME LANG from N_LANGS NL", jcbLang, false, 2, false);
    }

    private void setValueStatuses() {
        setValuesComboBox("select NS.ID, NS.NAME STATUS from N_STATUSES NS", jcbStatuses, false, 2, false);

    }

    private void setValuesPaymentDialog() {
        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
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

    private void setValuesArticles() {
        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
            case 1: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_DE) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                setValuesComboBox(sql, jcbArticle, true, -1, false);
                break;
            }
            case 2: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_EN) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                setValuesComboBox(sql, jcbArticle, true, -1, false);
                break;
            }
            case 3: {
                String sql = "select A.ID, (A.CODE || ' | ' || A.NAME_BG) NAME_DE from ARTICLES A "
                        + "where A.ARTICLE_GROUPS_ID = "
                        + (((DropDown) jcbArticleGroups.getSelectedItem()).getId());
                setValuesComboBox(sql, jcbArticle, true, -1, false);
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

    private void getSum() {
        double sum = 0.00;
        double price = 0.00;
        double qty = 0.00;
        double ValueVat = 0.00;
        double finalPrice = 0.00;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            sum = sum + (double) (jTable1.getValueAt(i, 7));
            price = (double) (jTable1.getValueAt(i, 3));
            qty = Double.parseDouble((String) (jTable1.getValueAt(i, 5)));
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

    private void GenerateDeliveryNumber() {
        String deliveryNumber = null;
        if (jtxtDeliveryNumber.getText().isEmpty()) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT NEXT VALUE FOR DELIVERY_NUMBER_GEN, NEXT VALUE FOR LOTS_GEN FROM RDB$DATABASE");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    deliveryNumber = rs.getString(1);
                    lotID = rs.getInt(2);
                    jtxtDeliveryNumber.setText(deliveryNumber);
                }
                jtxtDeliveryNumber.setBackground(Color.white);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void generateDeliveryID() {

        if (jtxtDeliveryNumber.getText().isEmpty()) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT NEXT VALUE FOR DEALS_GEN FROM RDB$DATABASE;");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    deliveryID = rs.getInt(1);
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

            PreparedStatement ps = con.prepareStatement("update or insert into DEALS (ID, DEAL_NUMBER, DEAL_DATE, OPERATION_ID, DEAL_TYPE_ID, STATUS_ID, CLIENT_ID, LANG_ID,\n"
                    + " DEAL_VALUE, TRANSPORT_COSTS, CHANNEL_COSTS, BANK_COSTS, OTHER_COSTS, CURRENCY_ID, USER_ID, VALUE_VAT)\n"
                    + "values (?, ?, ?, ?, ?, ?, ?, ?,\n"
                    + " ?, ?, ?, ?, ?, ?, ?, ?)"
                    + "matching(ID, DEAL_NUMBER)");

            ps.setInt(1, deliveryID);
            ps.setInt(2, Integer.parseInt(jtxtDeliveryNumber.getText()));
            java.sql.Date sqldate = new java.sql.Date(jDateDelivery.getDate().getTime());
            ps.setDate(3, sqldate);
            ps.setInt(4, 2);  // TODO: This is operation type
            ps.setInt(5, ((DropDown) jcbDealType.getSelectedItem()).getId());
            ps.setInt(6, ((DropDown) jcbStatuses.getSelectedItem()).getId());
            ps.setInt(7, ((DropDown) jcbSupplier.getSelectedItem()).getId());
            ps.setInt(8, ((DropDown) jcbLang.getSelectedItem()).getId());
            ps.setDouble(9, Double.parseDouble(jtxtTotalWithVat.getText().replace(",", ".").replace("\u00A0", "")));  // TODO: make TOTAL
            ps.setDouble(10, TransportCosts);
            ps.setDouble(11, ChannelCosts);
            ps.setDouble(12, BankCosts);
            ps.setDouble(13, OtherCosts);
            ps.setInt(14, 1); // TODO: This is for currency_ID
            ps.setInt(15, Login.userID); // TODO : This is for user_ID
            ps.setDouble(16, Double.parseDouble(jtxtVatValue.getText().replace(",", ".").replace("\u00A0", "")));

            ps.executeUpdate();

        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void InsertLots() {
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
        double supplyPrice = 0;
        double realSupplyPrice = 0;

        supplyPrice = Double.parseDouble(jtxtTotalWithVat.getText().replace(",", ".").replace("\u00A0", ""));
        realSupplyPrice = supplyPrice + TransportCosts + BankCosts + OtherCosts + ChannelCosts;
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("update or insert into LOTS (ID, DEAL_ID, NUMBER, SOLD_YN, SUPPLY_PRICE, REAL_SUPPLY_PRICE)\n"
                    + "values (?, ?, ?, ?, ?, ?) matching(ID, DEAL_ID)");
            ps.setInt(1, lotID);
            ps.setInt(2, deliveryID);
            ps.setString(3, jtxtLots.getText());
            ps.setInt(4, 0);

            ps.setDouble(5, supplyPrice);
            ps.setDouble(6, realSupplyPrice);
            ps.executeUpdate();

        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private void InsertDetail() {
        try {
            Connection con = getConnection();
            int rows = jTable1.getRowCount();
            PreparedStatement ps = con.prepareStatement("update or insert into DEAL_DETAILS (DEAL_ID, ARTICLE_ID, QUANTITY, PRICE, CURRENCY_ID, LOT_ID, DVY_PRICE_WITH_VAT)\n"
                    + "values (?, ?, ?, ?, ?, ?, ?) matching(DEAL_ID, LOT_ID, ARTICLE_ID)");
            for (int row = 0; row < rows; row++) {

                int Article_ID = (Integer) jTable1.getValueAt(row, 0);
                Double QTY = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 5)).replace("\u00A0", ""));
                Double Price = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 7)).replace("\u00A0", ""));
                Double priceWithVat = Double.parseDouble(String.valueOf(jTable1.getValueAt(row, 3)).replace("\u00A0", ""));

                ps.setInt(1, deliveryID);
                ps.setInt(2, Article_ID);
                ps.setDouble(3, QTY);
                ///    ps.setInt(4, 1); // TODO : Measure ID
                ps.setDouble(4, Price);
                ps.setInt(5, 1); // TODO : Currency
                ps.setInt(6, lotID);
                ps.setDouble(7, priceWithVat); // for recreate form
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
                PreparedStatement ps = con.prepareStatement("SELECT NEXT VALUE FOR INVOICE_NUMBER_DELIVERY_GEN FROM RDB$DATABASE;");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    invoiceNumber = rs.getString(1);
                    jtxtInvoiceNumberDialog.setText(invoiceNumber);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void ResetGenerator() {
        try {
            Connection con = getConnection();
            int genNumber = 0;
            genNumber = Integer.parseInt(jtxtDeliveryNumber.getText());
            genNumber = genNumber - 1;
            PreparedStatement ps = con.prepareStatement("ALTER SEQUENCE DELIVERY_NUMBER_GEN RESTART WITH " + genNumber);

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
            if (articleID == ((DropDown) jcbArticle.getSelectedItem()).getId()) {
                JOptionPane.showMessageDialog(null, "Такъв артикул вече съществува!", "Продажба", JOptionPane.NO_OPTION);
                Exist = 1;
                jcbArticleGroups.setSelectedIndex(-1);
                jcbArticle.setSelectedIndex(-1);
                jtxtPrice.setText("");
                jtxtQty.setValue(1);
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
        jPopupMenu2 = new javax.swing.JPopupMenu();
        jPopUpDelDelete = new javax.swing.JMenuItem();
        jbtnRefreshDelivery = new javax.swing.JButton();
        jcbCountry = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jtxtDeliveryNumber = new javax.swing.JTextField();
        jlbDelivery = new javax.swing.JLabel();
        jDateDelivery = new com.toedter.calendar.JDateChooser();
        jlbDate = new javax.swing.JLabel();
        jcbDealType = new javax.swing.JComboBox<>();
        jlbDealType = new javax.swing.JLabel();
        jcbSupplier = new javax.swing.JComboBox<>();
        jlbSupplier = new javax.swing.JLabel();
        jbtnNewSupplier = new javax.swing.JButton();
        jcbLang = new javax.swing.JComboBox<>();
        jlbLang = new javax.swing.JLabel();
        jlbDeliveryNumber = new javax.swing.JLabel();
        jbtnEditClient = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jtxtQty = new javax.swing.JSpinner();
        jcbArticleGroups = new javax.swing.JComboBox<>();
        jlbArticleGroup = new javax.swing.JLabel();
        jcbArticle = new javax.swing.JComboBox<>();
        jlbArticle = new javax.swing.JLabel();
        jlbLot = new javax.swing.JLabel();
        jtxtPrice = new javax.swing.JTextField();
        jlbPrice = new javax.swing.JLabel();
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
        jlbQty = new javax.swing.JLabel();
        jtxtLots = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
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
        jbntInvoice = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();

        DialogInvoice.setTitle("Фактура");
        DialogInvoice.setMaximumSize(new java.awt.Dimension(450, 450));
        DialogInvoice.setMinimumSize(new java.awt.Dimension(450, 450));
        DialogInvoice.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        DialogInvoice.setPreferredSize(new java.awt.Dimension(450, 450));

        jlbInvoice.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbInvoice.setText("Фактура");
        jlbInvoice.setToolTipText("");

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
        jchbReversal.setToolTipText("За момента не се използва!!!");
        jchbReversal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchbReversalActionPerformed(evt);
            }
        });

        jlbCreditNoteText.setText("Допълнително описание:");

        jlbCreditNoteNumber.setText("Номер на кредитно известие:");

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DialogInvoiceLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jlbInvoice)
                .addGap(175, 175, 175))
            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DialogInvoiceLayout.createSequentialGroup()
                                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtInvoiceNumberDialog, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbInvoiceNumber))
                                .addGap(38, 38, 38)
                                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbInvoiceDate)
                                    .addComponent(jDateInvoice, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jlbPaymentType)
                            .addComponent(jlbPaymentText)
                            .addComponent(jchbReversal)))
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jpCreditNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DialogInvoiceLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jbtnSaveDialog)
                        .addGap(30, 30, 30)
                        .addComponent(jbtnCloseDialog)))
                .addContainerGap(53, Short.MAX_VALUE))
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
                .addGap(18, 18, 18)
                .addComponent(jpCreditNote, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(DialogInvoiceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnSaveDialog)
                    .addComponent(jbtnCloseDialog))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPopUpDelDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/delete16.png"))); // NOI18N
        jPopUpDelDelete.setText("Изтрий!");
        jPopupMenu2.add(jPopUpDelDelete);

        jbtnRefreshDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/refresh.png"))); // NOI18N
        jbtnRefreshDelivery.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnRefreshDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnRefreshDeliveryActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Доставка");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onClose(evt);
            }
        });

        jtxtDeliveryNumber.setEditable(false);
        jtxtDeliveryNumber.setToolTipText("Номер на доставка!");
        jtxtDeliveryNumber.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDeliveryNumberFocusLost(evt);
            }
        });
        jtxtDeliveryNumber.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtxtDeliveryNumberMouseClicked(evt);
            }
        });

        jlbDelivery.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jlbDelivery.setText("Доставка");

        jDateDelivery.setToolTipText("Дата на доставка.");

        jlbDate.setText("Дата на сделката:");

        jcbDealType.setToolTipText("Тип на доставка.");
        jcbDealType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbDealTypeActionPerformed(evt);
            }
        });

        jlbDealType.setText("Тип на сделката:");

        jcbSupplier.setToolTipText("Доставчик.");

        jlbSupplier.setText("Доставчик:");

        jbtnNewSupplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/NewClient.png"))); // NOI18N
        jbtnNewSupplier.setText("Нов Доставчик");
        jbtnNewSupplier.setToolTipText("Добавяне на нов доставчик.");
        jbtnNewSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNewSupplierActionPerformed(evt);
            }
        });

        jcbLang.setToolTipText("Език на доставката.");
        jcbLang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbLangActionPerformed(evt);
            }
        });

        jlbLang.setText("Език на сделката:");

        jlbDeliveryNumber.setText("Номер:");

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
                .addComponent(jlbDelivery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbLang, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbLang))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbDeliveryNumber)
                    .addComponent(jtxtDeliveryNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbDealType)
                    .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbSupplier)
                    .addComponent(jcbSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jbtnNewSupplier)
                .addGap(18, 18, 18)
                .addComponent(jbtnEditClient)
                .addGap(204, 204, 204))
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbDelivery)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jlbDealType)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jlbSupplier)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jbtnEditClient, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbtnNewSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jlbDeliveryNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jtxtDeliveryNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(51, 51, 51))))
        );

        jtxtQty.setToolTipText("Количество.");
        jtxtQty.setEditor(new javax.swing.JSpinner.NumberEditor(jtxtQty, "#"));
        jtxtQty.setValue(1);

        jcbArticleGroups.setMaximumRowCount(15);
        jcbArticleGroups.setToolTipText("Група на артикула.");
        jcbArticleGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbArticleGroupsActionPerformed(evt);
            }
        });

        jlbArticleGroup.setText("Група на артикула:");

        jcbArticle.setMaximumRowCount(15);
        jcbArticle.setToolTipText("Артикул.");
        jcbArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbArticleActionPerformed(evt);
            }
        });

        jlbArticle.setText("Артикул:");

        jlbLot.setText("Партида:");

        jtxtPrice.setToolTipText("Единична цена с ДДС.");
        jtxtPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtPriceFocusLost(evt);
            }
        });

        jlbPrice.setText("Цена (с ДДС)");

        jbtnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/cart32.png"))); // NOI18N
        jbtnAdd.setText("Добави");
        jbtnAdd.setToolTipText("Добави артикула към доставката.");
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Код (SKU)", "Артикул", "Единична цена (€)", "Цена без ДДС (€)", "Количество", "ME", "Обща цена (€)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("Артикули към тази доставка.");
        jTable1.setComponentPopupMenu(jPopupMenu2);
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
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(490);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(85);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(80);
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
        jbtnClear.setToolTipText("Премахни артикула от доставката.");
        jbtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearActionPerformed(evt);
            }
        });

        jlbQty.setText("Количество:");

        jtxtLots.setEditable(false);
        jtxtLots.setToolTipText("Партида на доставката (генерира се автоматично).");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbArticleGroup)
                            .addComponent(jcbArticleGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbArticle)
                            .addComponent(jcbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbLot)
                            .addComponent(jtxtLots, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jlbQty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbPrice)
                            .addComponent(jtxtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addComponent(jbtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jbtnClear)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbTotalWoVat, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbVatValue)
                            .addComponent(jlbTotalWithVat))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxtTotalWoVat, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtVatValue, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtTotalWithVat, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbArticle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbArticleGroup)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbArticleGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jbtnClear)
                        .addComponent(jbtnAdd))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbPrice)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jtxtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jlbLot)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jtxtLots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbQty)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jtxtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jtxtTransportCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtTransportCosts.setToolTipText("Разходи за транспорт.");
        jtxtTransportCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtTransportCostsFocusLost(evt);
            }
        });

        jtxtOtherCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtOtherCosts.setToolTipText("Други допълнителни разходи.");
        jtxtOtherCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtOtherCostsFocusLost(evt);
            }
        });

        jtxtBankCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtBankCosts.setToolTipText("Разходи за банката.");
        jtxtBankCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtBankCostsFocusLost(evt);
            }
        });

        jtxtChannelCosts.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtChannelCosts.setToolTipText("Разходи за посредника.");
        jtxtChannelCosts.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtChannelCostsFocusLost(evt);
            }
        });

        jlbTransportCosts.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbTransportCosts.setText("Транспортни разходи:");
        jlbTransportCosts.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jlbBankCosts.setText("Банкови разходи:");

        jlbChannelCosts.setText("За посредника:");

        jlbOtherCosts.setText("Други разходи:");

        jtxtInvoiceNumber.setEditable(false);
        jtxtInvoiceNumber.setToolTipText("Номер и дата на фактура (генерира се автоматично при въвеждане на фактура).");

        jlbInvoiceNumbers.setText("Номер и дата на фактурата:");

        jcbPayment.setToolTipText("Начин на плащане.");
        jcbPayment.setEnabled(false);

        jlbPayment.setText("Тип плащане:");

        jcbStatuses.setToolTipText("Статус на доставката.");
        jcbStatuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbStatusesActionPerformed(evt);
            }
        });

        jlbStatus.setText("Статус на сделката:");

        jbtnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Save32.png"))); // NOI18N
        jbtnSave.setText("Запис");
        jbtnSave.setToolTipText("Записва продажбата в базата данни!");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });

        jtbnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Print32.png"))); // NOI18N
        jtbnPrint.setText("Печат");
        jtbnPrint.setToolTipText("Отпечатване на фактура по приключена доставка!");
        jtbnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnPrintActionPerformed(evt);
            }
        });

        jbtnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnClose.setText("Изход");
        jbtnClose.setToolTipText("Изход!");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        jbntInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/invoice32.png"))); // NOI18N
        jbntInvoice.setText("Фактура");
        jbntInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbntInvoiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbTransportCosts)
                    .addComponent(jlbBankCosts)
                    .addComponent(jlbOtherCosts)
                    .addComponent(jlbChannelCosts))
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jtxtBankCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtTransportCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jtxtChannelCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jtxtOtherCosts, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtxtInvoiceNumber)
                    .addComponent(jlbInvoiceNumbers))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jbntInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jtbnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(94, 94, 94)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jcbPayment, 0, 117, Short.MAX_VALUE)
                    .addComponent(jlbStatus)
                    .addComponent(jlbPayment)
                    .addComponent(jcbStatuses, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtnSave)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jbtnClose))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(29, 29, 29)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jlbInvoiceNumbers)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jtxtInvoiceNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jbntInvoice))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jtbnPrint))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jlbPayment)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jcbPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(13, 13, 13)
                                    .addComponent(jlbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jcbStatuses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtTransportCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbTransportCosts))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtBankCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbBankCosts))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtChannelCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbChannelCosts))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtOtherCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlbOtherCosts))))
                .addGap(57, 57, 57))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (jTable1.getSelectedRow() == -1) {
            if (jTable1.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Няма данни!", "Доставка", JOptionPane.NO_OPTION);
            } else {
                JOptionPane.showMessageDialog(null, "Моля изберете реда, който искате да изтриете!", "Доставка", JOptionPane.NO_OPTION);
            }
        } else {
            model.removeRow(jTable1.getSelectedRow());
        }
        getSum();
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jbtnNewSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNewSupplierActionPerformed
        Client client = new Client();
        client.setVisible(true);
        Client.jcbClientType.setSelectedIndex(1);
        client.isFromForm = 1;
    }//GEN-LAST:event_jbtnNewSupplierActionPerformed

    private void jcbArticleGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbArticleGroupsActionPerformed
        if (jcbArticleGroups.getSelectedIndex() != -1) {
            setValuesArticles();
        }
    }//GEN-LAST:event_jcbArticleGroupsActionPerformed

    private void jcbLangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbLangActionPerformed
        jcbArticle.setEditable(false);
        jcbArticleGroups.setEditable(false);
        jcbArticle.removeAllItems();
        jcbArticleGroups.removeAllItems();
        jcbArticle.setSelectedIndex(-1);
        jcbArticleGroups.setSelectedIndex(-1);
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
        if (jcbArticle.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Моля изберете артикул!", "Доставка", JOptionPane.NO_OPTION);
        } else if (jtxtPrice.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Моля въведете цена на артикул!", "Доставка", JOptionPane.NO_OPTION);
        } else {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            TableColumn colPrice = jTable1.getColumnModel().getColumn(3);
            TableColumn colPriceWoVat = jTable1.getColumnModel().getColumn(4);
            TableColumn total = jTable1.getColumnModel().getColumn(7);
            colPrice.setCellRenderer(new DecimalFormatRenderer());
            colPriceWoVat.setCellRenderer(new DecimalFormatRenderer());
            total.setCellRenderer(new DecimalFormatRenderer());
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

            String article = jcbArticle.getSelectedItem().toString();
            article = article.substring(article.indexOf("|") + 2, article.length());
            if (VatPCT == 0) {
                Total = Double.valueOf(jtxtPrice.getText()) * Double.valueOf(jtxtQty.getValue().toString());
                TotalSTR = String.format("%.2f", Total);
                Total = Double.valueOf(TotalSTR.replace(",", "."));
                priceWoVat = Double.valueOf(jtxtPrice.getText());
                valueWoVat = String.format("%.2f", priceWoVat);
                priceWoVat = Double.valueOf(valueWoVat.replace(",", "."));
            } else {
                //Z – (Z * A /(100+A)); 
                priceWithVat = Double.valueOf(jtxtPrice.getText());
                valueWithVat = String.format("%.2f", priceWithVat);
                priceWithVat = Double.valueOf(valueWithVat.replace(",", "."));

                priceWoVat = priceWithVat - (priceWithVat * VatPCT / (100 + VatPCT));
                valueWoVat = String.format("%.2f", priceWoVat);
                priceWoVat = Double.valueOf(valueWoVat.replace(",", "."));

                Total = priceWoVat * Double.valueOf(jtxtQty.getValue().toString());
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
                    if (jTable1.getRowCount() > 0) {
                        checkForExist();
                    }
                    if (Exist == 0) {
                        switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
                            case 1:
                                model.addRow(new Object[]{ID, code, article, Double.parseDouble(jtxtPrice.getText()), priceWoVat, jtxtQty.getValue().toString(), MeasureDE, Total});
                                break;
                            case 2:
                                model.addRow(new Object[]{ID, code, article, Double.parseDouble(jtxtPrice.getText()), priceWoVat, jtxtQty.getValue().toString(), MeasureEN, Total});
                                break;
                            case 3:
                                model.addRow(new Object[]{ID, code, article, Double.parseDouble(jtxtPrice.getText()), priceWoVat, jtxtQty.getValue().toString(), MeasureBG, Total});
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
            jtxtPrice.setText("");
            jtxtQty.setValue(1);
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbntInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbntInvoiceActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (isSave == 0) {
            int dialogButton = JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да въведете фактура?", "Продажба", JOptionPane.YES_NO_OPTION);
            if (dialogButton == JOptionPane.YES_OPTION) {
                if (checkInputs()) {
                    JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
                } else if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "Моля Добавете поне един артикул в таблицата!");
                } else {
                    InsertMaster();
                    InsertLots();
                    InsertDetail();
                    jDateInvoice.setDate(jDateDelivery.getDate());
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
            jDateInvoice.setDate(jDateDelivery.getDate());
            jpCreditNote.setVisible(false);
            setValuesPaymentDialog();
            if (jtxtInvoiceNumber.getText().isEmpty()) {
                jcbPaymentType.setSelectedIndex(-1);
            } else {
                String number = jtxtInvoiceNumber.getText();
                String invoiceNumber = number.substring(number.indexOf("-") + 1, number.indexOf("/"));
                jtxtInvoiceNumberDialog.setText(invoiceNumber);
                jcbPaymentType.getModel().setSelectedItem(jcbPayment.getModel().getSelectedItem());
            }
            DialogInvoice.pack();
            DialogInvoice.setLocationRelativeTo(null);
            DialogInvoice.setVisible(true);
        }
    }//GEN-LAST:event_jbntInvoiceActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        int dialogButton = JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да излезнете!", "Доставка", JOptionPane.YES_NO_OPTION);
        if (dialogButton == JOptionPane.YES_OPTION) {
            if (!jtxtDeliveryNumber.getText().isEmpty() && isSave == 0) {
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

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (checkInputs()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Моля Добавете поне един артикул в таблицата!");
        } else {
            InsertMaster();
            InsertLots();
            InsertDetail();
        }
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jtbnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnPrintActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        if (isSave == 0) {
            int dialogButton = JOptionPane.showConfirmDialog(null, "Преди да печатате трябва да запишете Доставката в базата данни! \n Искате ли да го направите!", "Доставка", JOptionPane.YES_NO_OPTION);
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
            if (jtxtInvoiceNumber.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Моля попълнете данните за фактура!");
            } else {
                try {
                    String number = jtxtInvoiceNumber.getText();
                    String invoiceNumber = number.substring(0, number.indexOf("/"));
                    switch (((DropDown) jcbLang.getSelectedItem()).getId()) {
                        case 1: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 9 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 10 order by R.ID) SUBREPORT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(deliveryID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Delivery " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        case 2: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 7 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 8 order by R.ID) SUBREPORT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(deliveryID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Delivery " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        case 3: {
                            Connection con = getConnection();
                            PreparedStatement ps = con.prepareStatement("select\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 11 order by R.ID) MAIN_REPORT,\n"
                                    + "  (select R.\"FILE\" MAIN_EN from REPORTS R where R.ID = 12 order by R.ID) SUBREPORT\n"
                                    + "from\n"
                                    + "  RDB$DATABASE");
                            ResultSet rs = ps.executeQuery();
                            while (rs.next()) {
                                InputStream fileMain = rs.getBinaryStream("MAIN_REPORT");
                                InputStream fileSub = rs.getBinaryStream("SUBREPORT");
                                JasperReport jr = (JasperReport) JRLoader.loadObject(fileMain);
                                JasperReport sub = (JasperReport) JRLoader.loadObject(fileSub);
                                HashMap param = new HashMap();
                                param.put("DEAL_ID", Long.valueOf(deliveryID));
                                param.put("Sub1", sub);
                                JasperPrint jp = JasperFillManager.fillReport(jr, param, con);
                                JasperViewer.viewReport(jp, false);

                                File directory = new File(".");
                                String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
                                JasperExportManager.exportReportToPdfFile(jp, absolutePath + "Invoices\\Delivery " + invoiceNumber + ".pdf");
                            }
                            break;
                        }
                        default:
                            break;
                    }
                } catch (JRException e) {
                    Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, e);
                    JOptionPane.showMessageDialog(this, "Възникна грешка при печат на фактура, моля проверете данните и опитайте отново!");
                } catch (SQLException ex) {
                    Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jtbnPrintActionPerformed

    private void jcbPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbPaymentTypeActionPerformed
        if (jtxtInvoiceNumberDialog.getText().isEmpty()) {
            GenerateInvoiceNumber();
            Date date = jDateInvoice.getDate();
            String year = String.format("%1$tY", date);
            String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
            Delivery.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);
        }

        //Check in DB for this Invoice_number
        String number = jtxtInvoiceNumber.getText();
        String invoiceNumber = number.substring(0, number.indexOf("/"));
        String myInvoiceNumber = null;
        if (!jtxtInvoiceNumberDialog.getText().isEmpty() && checkInvoiceNumber == 1) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select I.INVOICE_NUMBER from INVOICES I where I.INVOICE_NUMBER = ? and I.OPERATION_ID = 2");
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
    }//GEN-LAST:event_jcbPaymentTypeActionPerformed

    private void jchbReversalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchbReversalActionPerformed
        jpCreditNote.setVisible(false);
        if (jchbReversal.isSelected()) {
            jpCreditNote.setVisible(true);
            Date creditDate = new Date();
            jCreditNoteDate.setDate(creditDate);
        }
    }//GEN-LAST:event_jchbReversalActionPerformed

    private void jbtnCloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseDialogActionPerformed
        DialogInvoice.setVisible(false);
    }//GEN-LAST:event_jbtnCloseDialogActionPerformed

    private void jtxtInvoiceNumberDialogFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtInvoiceNumberDialogFocusLost
        Date date = jDateInvoice.getDate();
        String year = String.format("%1$tY", date);
        String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
        Delivery.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);

        //Check in DB for this Invoice_number
        String number = jtxtInvoiceNumber.getText();
        String invoiceNumber = number.substring(0, number.indexOf("/"));
        String myInvoiceNumber = null;
        if (!jtxtInvoiceNumberDialog.getText().isEmpty() && checkInvoiceNumber == 1) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select I.INVOICE_NUMBER from INVOICES I where I.INVOICE_NUMBER = ? and I.OPERATION_ID = 2");
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

    private void jcbArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbArticleActionPerformed
        if (jcbArticle.getSelectedIndex() != -1 && jcbDealType.getSelectedIndex() == -1 && isActive == 1) {
            JOptionPane.showMessageDialog(null, "Моля изберете тип на сделката!", "Доставка", JOptionPane.NO_OPTION);
            jcbArticleGroups.setEditable(false);
            jcbArticle.setEditable(false);
            jcbArticle.setSelectedIndex(-1);
            jcbArticleGroups.setSelectedIndex(-1);
            AutoCompleteDecorator.decorate(this.jcbArticleGroups);
        }
    }//GEN-LAST:event_jcbArticleActionPerformed

    private void jbtnSaveDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveDialogActionPerformed
        if (jtxtInvoiceNumber.getText().isEmpty() && jcbPaymentType.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else if (!jchbReversal.isSelected()) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update or insert into INVOICES (INVOICE_NUMBER, INVOICE_DATE, DEAL_ID, PAYMENT_ID, PAYMENT_TEXT, OPERATION_ID)\n"
                        + "values (?, ?, ?, ?, ?, ?) matching (DEAL_ID)");
                String number = jtxtInvoiceNumber.getText();
                String invoiceNumber = number.substring(0, number.indexOf("/"));
                ps.setString(1, invoiceNumber);
                java.sql.Date sqldate = new java.sql.Date(jDateInvoice.getDate().getTime());
                ps.setDate(2, sqldate);
                ps.setInt(3, deliveryID);
                ps.setInt(4, ((DropDown) jcbPaymentType.getSelectedItem()).getId());
                ps.setString(5, jtaPaymentText.getText());
                ps.setInt(6, 2); // This is for operation type 
                ps.executeUpdate();
                Date date = jDateInvoice.getDate();
                String year = String.format("%1$tY", date);
                String dateInvoice = new SimpleDateFormat("dd.MM.yyyy").format(date);
                Delivery.jtxtInvoiceNumber.setText(year + "-" + jtxtInvoiceNumberDialog.getText() + "/" + dateInvoice);
                Delivery.jcbPayment.getModel().setSelectedItem(Delivery.jcbPaymentType.getModel().getSelectedItem());
                this.DialogInvoice.setVisible(false);
                checkInvoiceNumber = 0;
                jtxtInvoiceNumber.setEditable(false);

            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnSaveDialogActionPerformed

    private void onClose(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onClose
        jbtnClose.doClick();
    }//GEN-LAST:event_onClose

    private void jtxtDeliveryNumberFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDeliveryNumberFocusLost
        if (!jtxtDeliveryNumber.getText().isEmpty()) {
            try {
                NumberFormat nfm = NumberFormat.getInstance(Locale.getDefault());
                String formattedText = nfm.format(Integer.parseInt(jtxtDeliveryNumber.getText()));
                formattedText = formattedText.replace("\u00A0", "");
                jtxtDeliveryNumber.setText(formattedText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Грешен формат!");
                jtxtDeliveryNumber.setText("");
            }
        }
    }//GEN-LAST:event_jtxtDeliveryNumberFocusLost

    private void jTable1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTable1PropertyChange
        if (jTable1.getRowCount() != 0 && jTable1.getValueAt(jTable1.getSelectedRow(), 5).equals("")) {
            JOptionPane.showMessageDialog(null, "Моля попълнете количество");
        } else if (jTable1.getRowCount() != 0 && !jTable1.getValueAt(jTable1.getSelectedRow(), 4).equals("")) {

            String unitPrice = jTable1.getValueAt(jTable1.getSelectedRow(), 3).toString();
            double unitPriceD = Double.parseDouble(unitPrice);
            double priceWoVat = 0;
            priceWoVat = unitPriceD - (unitPriceD * VatPCT / (100 + VatPCT));
            jTable1.setValueAt(priceWoVat, jTable1.getSelectedRow(), 4);
            String qty = jTable1.getValueAt(jTable1.getSelectedRow(), 5).toString();
            String price = jTable1.getValueAt(jTable1.getSelectedRow(), 4).toString();
            double qtyD = Double.parseDouble(qty);
            double priceD = Double.parseDouble(price);
            jTable1.setValueAt(qtyD * priceD, jTable1.getSelectedRow(), 7);
            getSum();
        }
    }//GEN-LAST:event_jTable1PropertyChange

    private void jcbStatusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbStatusesActionPerformed
        if (((DropDown) jcbStatuses.getSelectedItem()).getId() == 5) {
            jbtnSave.setEnabled(true);
        } else if (isSave == 1) {
            jbtnSave.setEnabled(false);
        }
    }//GEN-LAST:event_jcbStatusesActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        jPopUpDelDelete.setEnabled(false);
        if (evt.isPopupTrigger()) {
            jPopupMenu2.show(this, evt.getX(), evt.getY());

        }
    }//GEN-LAST:event_jTable1MouseReleased

    private void jbtnRefreshDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnRefreshDeliveryActionPerformed
        setValuesSuppliers();
        if (savedSupplier == 1) {
            int itemCount = 0;
            itemCount = jcbSupplier.getItemCount();
            jcbSupplier.setSelectedIndex(itemCount - 1);
        }
    }//GEN-LAST:event_jbtnRefreshDeliveryActionPerformed

    private void jbtnEditClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditClientActionPerformed
        if (jcbSupplier.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Не сте избрали доставчик!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("select first 1\n"
                        + "C.ID, C.PERSON, C.NAME, C.COUNTRY_ID, CC.NAME COUNTRY, C.CITY, C.ADDRESS, C.VAT_NUMBER, C.MOL\n"
                        + "from\n"
                        + "  CLIENTS C\n"
                        + "  join N_COUNTRIES CC on CC.ID = C.COUNTRY_ID\n"
                        + "where\n"
                        + "  C.ID = " + (((DropDown) jcbSupplier.getSelectedItem()).getId()));
                ResultSet rs = ps.executeQuery();
                Vector<DropDown> countries = new Vector<>();
                while (rs.next()) {
                    int person = rs.getInt("PERSON");
                    countries.addElement(new DropDown(rs.getInt("COUNTRY_ID"), rs.getString("COUNTRY")));
                    jcbCountry.setModel(new DefaultComboBoxModel(countries));

                    Client client = new Client();
                    client.jcbClientType.setSelectedIndex(1);
                    client.clientID = rs.getInt("ID");
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

    private void jtxtInvoiceNumberDialogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtxtInvoiceNumberDialogMouseClicked
        if (evt.getClickCount() == 2) {
            jtxtInvoiceNumberDialog.setEditable(true);
            checkInvoiceNumber = 1;
        }
    }//GEN-LAST:event_jtxtInvoiceNumberDialogMouseClicked

    private void jtxtDeliveryNumberMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtxtDeliveryNumberMouseClicked
        generateDeliveryID();
        GenerateDeliveryNumber();
        Date date = jDateDelivery.getDate();
        String year = String.format("%1$tY", date);
        jtxtLots.setText(year + "-" + jtxtDeliveryNumber.getText());
    }//GEN-LAST:event_jtxtDeliveryNumberMouseClicked

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
            java.util.logging.Logger.getLogger(Delivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Delivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Delivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Delivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Delivery().setVisible(true);
        });

    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {

        public DecimalFormatRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }

        private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");
        //    private static final DecimalFormat formatter = new DecimalFormat("#.00");  

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
    private com.toedter.calendar.JDateChooser jCreditNoteDate;
    public com.toedter.calendar.JDateChooser jDateDelivery;
    private com.toedter.calendar.JDateChooser jDateInvoice;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JMenuItem jPopUpDelDelete;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbntInvoice;
    public javax.swing.JButton jbtnAdd;
    public javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnCloseDialog;
    private javax.swing.JButton jbtnEditClient;
    public javax.swing.JButton jbtnNewSupplier;
    public static javax.swing.JButton jbtnRefreshDelivery;
    public javax.swing.JButton jbtnSave;
    public javax.swing.JButton jbtnSaveDialog;
    public javax.swing.JComboBox<String> jcbArticle;
    public javax.swing.JComboBox<String> jcbArticleGroups;
    private javax.swing.JComboBox<String> jcbCountry;
    public javax.swing.JComboBox<String> jcbDealType;
    public javax.swing.JComboBox<String> jcbLang;
    public static javax.swing.JComboBox<String> jcbPayment;
    private static javax.swing.JComboBox<String> jcbPaymentType;
    public javax.swing.JComboBox<String> jcbStatuses;
    public javax.swing.JComboBox<String> jcbSupplier;
    private javax.swing.JCheckBox jchbReversal;
    private javax.swing.JLabel jlbArticle;
    private javax.swing.JLabel jlbArticleGroup;
    private javax.swing.JLabel jlbBankCosts;
    private javax.swing.JLabel jlbChannelCosts;
    private javax.swing.JLabel jlbCreditNoteNumber;
    private javax.swing.JLabel jlbCreditNoteText;
    private javax.swing.JLabel jlbDate;
    private javax.swing.JLabel jlbDateCreditNote;
    private javax.swing.JLabel jlbDealType;
    private javax.swing.JLabel jlbDelivery;
    private javax.swing.JLabel jlbDeliveryNumber;
    private javax.swing.JLabel jlbInvoice;
    private javax.swing.JLabel jlbInvoiceDate;
    private javax.swing.JLabel jlbInvoiceNumber;
    private javax.swing.JLabel jlbInvoiceNumbers;
    private javax.swing.JLabel jlbLang;
    private javax.swing.JLabel jlbLot;
    private javax.swing.JLabel jlbOtherCosts;
    private javax.swing.JLabel jlbPayment;
    private javax.swing.JLabel jlbPaymentText;
    private javax.swing.JLabel jlbPaymentType;
    private javax.swing.JLabel jlbPrice;
    private javax.swing.JLabel jlbQty;
    private javax.swing.JLabel jlbStatus;
    private javax.swing.JLabel jlbSupplier;
    private javax.swing.JLabel jlbTotalWithVat;
    private javax.swing.JLabel jlbTotalWoVat;
    private javax.swing.JLabel jlbTransportCosts;
    public javax.swing.JLabel jlbVatValue;
    private javax.swing.JPanel jpCreditNote;
    public javax.swing.JTextArea jtaPaymentText;
    private javax.swing.JButton jtbnPrint;
    public javax.swing.JTextField jtxtBankCosts;
    public javax.swing.JTextField jtxtChannelCosts;
    private javax.swing.JTextField jtxtCreditNoteNumber;
    public javax.swing.JTextField jtxtDeliveryNumber;
    public static javax.swing.JTextField jtxtInvoiceNumber;
    public static javax.swing.JTextField jtxtInvoiceNumberDialog;
    public javax.swing.JTextField jtxtLots;
    public javax.swing.JTextField jtxtOtherCosts;
    public javax.swing.JTextField jtxtPrice;
    public javax.swing.JSpinner jtxtQty;
    private javax.swing.JTextField jtxtTotalWithVat;
    private javax.swing.JTextField jtxtTotalWoVat;
    public javax.swing.JTextField jtxtTransportCosts;
    private javax.swing.JTextField jtxtVatValue;
    // End of variables declaration//GEN-END:variables
}
