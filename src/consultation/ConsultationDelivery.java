/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;

import delivery.Delivery;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import Tools.DropDown;
import static Tools.ExcelExporter.ExcelExport;
import static Tools.setValuesComboBox.setValuesComboBox;

/**
 *
 * @author Viko
 */
public class ConsultationDelivery extends javax.swing.JFrame {

    /**
     * Creates new form ConsultationSales
     */
    public ConsultationDelivery() {
        initComponents();
        Date date = new Date();
        jdFromDate.setDate(date);
        jdToDate.setDate(date);
        TableColumn dealValue = jTable1.getColumnModel().getColumn(7);
        TableColumn ValueVat = jTable1.getColumnModel().getColumn(8);
        dealValue.setCellRenderer(new DecimalFormatRenderer());
        ValueVat.setCellRenderer(new DecimalFormatRenderer());
        // set comboBox Payments
        setValuesComboBox("select NPM.ID, NPM.NAME_ENG from  N_PAYMENT_METHODS NPM", jcbPayment, false, 0, true);
        // set comboBox DealType
        setValuesComboBox("select DT.ID, DT.NAME DEAL_TYPE from N_DEAL_TYPES DT", jcbDealType, false, 0, true);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    Delivery delivery = new Delivery();

    public ArrayList<ConsultationDeals> getSales() throws SQLException {
        ArrayList<ConsultationDeals> salesList = new ArrayList<>();
        Connection con = getConnection();
        java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
        java.sql.Date sqldateTo = new java.sql.Date(jdToDate.getDate().getTime());
        PreparedStatement ps = con.prepareStatement("select\n"
                + "  D.DEAL_NUMBER, I.INVOICE_NUMBER, D.DEAL_DATE, C.NAME CLIENT, CC.NAME COUNTRY, CH.NAME CHANNEL,\n"
                + "  PM.NAME_ENG PAYMENTS, DT.NAME DEAL_TYPE, D.DEAL_VALUE, D.VALUE_VAT,\n"
                + "                                    (D.TRANSPORT_COSTS + D.CHANNEL_COSTS + D.BANK_COSTS + D.OTHER_COSTS) OTHER\n"
                + "from\n"
                + "   DEALS D\n"
                + "   left join INVOICES I on I.DEAL_ID = D.ID\n"
                + "   join CLIENTS C on C.ID = D.CLIENT_ID\n"
                + "   join N_COUNTRIES CC on CC.ID = C.COUNTRY_ID\n"
                + "   left join N_CHANNELS CH on CH.ID = D.CHANNEL_ID\n"
                + "   left join N_PAYMENT_METHODS PM on PM.ID = I.PAYMENT_ID\n"
                + "   join N_DEAL_TYPES DT on DT.ID = D.DEAL_TYPE_ID\n"
                + "where\n"
                + "D.ID > 0 and D.OPERATION_ID = 2 and D.DEAL_DATE between ? and ? and\n"
                + "I.PAYMENT_ID is not distinct from iif(cast(? as DM_REF) = -1 , I.PAYMENT_ID , cast(? as DM_REF)) and\n"
                + "D.DEAL_TYPE_ID = iif(cast(? as DM_REF) = -1 , D.DEAL_TYPE_ID , cast(? as DM_REF))");

        ps.setDate(1, sqldateFrom);
        ps.setDate(2, sqldateTo);
        ps.setInt(5, ((DropDown) jcbDealType.getSelectedItem()).getId());
        ps.setInt(6, ((DropDown) jcbDealType.getSelectedItem()).getId());
        ps.setInt(3, ((DropDown) jcbPayment.getSelectedItem()).getId());
        ps.setInt(4, ((DropDown) jcbPayment.getSelectedItem()).getId());
        ResultSet rs = ps.executeQuery();
        try {

            ConsultationDeals sales;

            while (rs.next()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                sales = new ConsultationDeals(rs.getInt("DEAL_NUMBER"), rs.getString("INVOICE_NUMBER"), saleDate,
                        rs.getString("CLIENT"), rs.getDouble("DEAL_VALUE"), rs.getString("CHANNEL"),
                        rs.getString("COUNTRY"), rs.getString("PAYMENTS"), rs.getString("DEAL_TYPE"), rs.getDouble("VALUE_VAT"), rs.getDouble("OTHER"));
                salesList.add(sales);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }

        return salesList;

    }

    public void Show_Sales_In_JTable() throws SQLException {
        ArrayList<ConsultationDeals> list = getSales();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        // clear jtable content
        model.setRowCount(0);
        Object[] row = new Object[9];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getDealNumber();
            row[1] = list.get(i).getInvoiceNumber();
            row[2] = list.get(i).getDealDate();
            row[3] = list.get(i).getClientName();
            row[4] = list.get(i).getCountry();
            row[5] = list.get(i).getPayment();
            row[6] = list.get(i).getType();
            row[7] = list.get(i).getDealValue();
            row[8] = list.get(i).getOther();

            model.addRow(row);
        }
    }

    private void showMaster() {
        delivery.jtxtDeliveryNumber.setBackground(Color.white);
        try {
            Connection con = getConnection();
            int row = jTable1.getSelectedRow();
            int correctModel = jTable1.convertRowIndexToModel(row);
            
            PreparedStatement ps = con.prepareStatement("select\n"
                    + "  D.ID, D.DEAL_NUMBER, D.DEAL_DATE, D.OPERATION_ID, O.NAME OPERATION,\n"
                    + "  D.DEAL_TYPE_ID, DT.NAME DEAL_TYPE,\n"
                    + "  D.CHANNEL_ID, C.NAME CHANNEL, D.STATUS_ID, S.NAME STATUS, D.CLIENT_ID, CC.NAME CLIENT,\n"
                    + "  D.LANG_ID LANG_ID, L.SHORT_NAME LANG, D.DEAL_VALUE,\n"
                    + "  D.TRANSPORT_COSTS, D.CHANNEL_COSTS, D.BANK_COSTS, D.OTHER_COSTS, I.INVOICE_NUMBER, I.PAYMENT_ID, I.PAYMENT_TEXT,\n"
                    + "  case\n"
                    + "    when L.ID = 1 then P.NAME_GERM\n"
                    + "    when L.ID = 2 then P.NAME_ENG\n"
                    + "    when L.ID = 3 then P.NAME_BUL\n"
                    + "  end PAYMENT,\n"
                    + " iif(DT.ACCRUING_VAT = 1, (select MC.VAT_PERCENTAGE from MY_COMPANY MC), 0) VAT_PCT\n"
                    + "from\n"
                    + "  DEALS D\n"
                    + "  join N_OPERATIONS O on O.ID = D.OPERATION_ID\n"
                    + "  join N_DEAL_TYPES DT on DT.ID = D.DEAL_TYPE_ID\n"
                    + "  left join N_CHANNELS C on C.ID = D.CHANNEL_ID\n"
                    + "  join N_STATUSES S on S.ID = D.STATUS_ID\n"
                    + "  join CLIENTS CC on CC.ID = D.CLIENT_ID\n"
                    + "  join N_LANGS L on L.ID = D.LANG_ID\n"
                    + "  left join INVOICES I on I.DEAL_ID = D.ID\n"
                    + "  left join N_PAYMENT_METHODS P on P.ID = I.PAYMENT_ID\n"
                    + "where\n"
                    + "  D.OPERATION_ID = 2 and D.DEAL_NUMBER = " + jTable1.getModel().getValueAt(correctModel, 0)); // Make Deal_number parameter
            ResultSet rs = ps.executeQuery();
            Vector<DropDown> dealType = new Vector<>();
            Vector<DropDown> status = new Vector<>();
            Vector<DropDown> client = new Vector<>();
            Vector<DropDown> lang = new Vector<>();
            Vector<DropDown> payment = new Vector<>();

            delivery.isActive = 0;
            delivery.checkInvoiceNumber = 0;
            delivery.isSave = 1;

            while (rs.next()) {
                delivery.deliveryID = rs.getInt("ID");
                delivery.VatPCT = rs.getDouble("VAT_PCT");
                dealType.addElement(new DropDown(rs.getInt("DEAL_TYPE_ID"), rs.getString("DEAL_TYPE")));
                client.addElement(new DropDown(rs.getInt("CLIENT_ID"), rs.getString("CLIENT")));
                status.addElement(new DropDown(rs.getInt("STATUS_ID"), rs.getString("STATUS")));
                lang.addElement(new DropDown(rs.getInt("LANG_ID"), rs.getString("LANG")));
                payment.addElement(new DropDown(rs.getInt("PAYMENT_ID"), rs.getString("PAYMENT")));

                jDealType.setModel(new DefaultComboBoxModel(dealType));
                jClient.setModel(new DefaultComboBoxModel(client));
                jStatus.setModel(new DefaultComboBoxModel(status));
                jLang.setModel(new DefaultComboBoxModel(lang));
                jPayment.setModel(new DefaultComboBoxModel(payment));

                delivery.jtxtDeliveryNumber.setText(rs.getString("DEAL_NUMBER"));
                delivery.jDateDelivery.setDate(rs.getDate("DEAL_DATE"));
                delivery.jcbDealType.getModel().setSelectedItem(jDealType.getModel().getSelectedItem());
                delivery.jcbSupplier.getModel().setSelectedItem(jClient.getModel().getSelectedItem());
                delivery.jtxtDeliveryNumber.setText(rs.getString("DEAL_NUMBER"));
                delivery.jcbStatuses.getModel().setSelectedItem(jStatus.getModel().getSelectedItem());
                delivery.jcbLang.getModel().setSelectedItem(jLang.getModel().getSelectedItem());
                delivery.jtxtTransportCosts.setText(rs.getString("TRANSPORT_COSTS"));
                delivery.jtxtOtherCosts.setText(rs.getString("OTHER_COSTS"));
                delivery.jtxtChannelCosts.setText(rs.getString("CHANNEL_COSTS"));
                delivery.jtxtBankCosts.setText(rs.getString("BANK_COSTS"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                delivery.jtxtInvoiceNumber.setText(rs.getString("INVOICE_NUMBER") + "/" + saleDate);
                delivery.jtxtInvoiceNumberDialog.setText(rs.getString("INVOICE_NUMBER"));
                delivery.jcbPayment.getModel().setSelectedItem(jPayment.getModel().getSelectedItem());
                delivery.jtaPaymentText.setText(rs.getString("PAYMENT_TEXT"));
                delivery.jlbVatValue.setText("ДДС (" + delivery.VatPCT + "%): ");

                dealType.clear();
                client.clear();
                status.clear();
                lang.clear();
                payment.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showDetails() {
        //delivery.isActive = 0;
        try {
            Connection con = getConnection();
            int row = jTable1.getSelectedRow();
            int correctModel = jTable1.convertRowIndexToModel(row);
            
            PreparedStatement ps = con.prepareStatement("select\n"
                    + "  AG.ID ARTICLE_GROUP_ID,\n"
                    + "  case\n"
                    + "    when D.LANG_ID = 1 then AG.NAME_DE\n"
                    + "    when D.LANG_ID = 2 then AG.NAME_EN\n"
                    + "    when D.LANG_ID = 3 then AG.NAME_BG\n"
                    + "  end ARTICLE_GROUP,\n"
                    + "  DD.ARTICLE_ID,\n"
                    + "  case\n"
                    + "    when D.LANG_ID = 1 then A.CODE || ' | ' || A.NAME_DE\n"
                    + "    when D.LANG_ID = 2 then A.CODE || ' | ' || A.NAME_EN\n"
                    + "    when D.LANG_ID = 3 then A.CODE || ' | ' || A.NAME_BG\n"
                    + "  end ARTICLE,\n"
                    + "  DD.QUANTITY, DD.PRICE, DD.LOT_ID, L.NUMBER LOT_NUMBER, DD.DVY_PRICE_WITH_VAT\n"
                    + "from\n"
                    + "  DEAL_DETAILS DD\n"
                    + "  join DEALS D on D.ID = DD.DEAL_ID\n"
                    + "  join ARTICLES A on A.ID = DD.ARTICLE_ID\n"
                    + "  join ARTICLE_GROUPS AG on AG.ID = A.ARTICLE_GROUPS_ID\n"
                    + "  join LOTS L on L.ID = DD.LOT_ID\n"
                    + "where\n"
                    + " D.OPERATION_ID = 2 and D.DEAL_NUMBER = " + jTable1.getModel().getValueAt(correctModel, 0) + " order by A.ID desc");
            ResultSet rs = ps.executeQuery();
            Vector<DropDown> article = new Vector<>();
            Vector<DropDown> articleGroup = new Vector<>();
            while (rs.next()) {
                delivery.lotID = rs.getInt("LOT_ID");
                article.addElement(new DropDown(rs.getInt("ARTICLE_ID"), rs.getString("ARTICLE")));
                articleGroup.addElement(new DropDown(rs.getInt("ARTICLE_GROUP_ID"), rs.getString("ARTICLE_GROUP")));

                jArticle.setModel(new DefaultComboBoxModel(article));
                jGroup.setModel(new DefaultComboBoxModel(articleGroup));

                delivery.jcbArticleGroups.getModel().setSelectedItem(jGroup.getModel().getSelectedItem());
                delivery.jcbArticle.getModel().setSelectedItem(jArticle.getModel().getSelectedItem());
                delivery.jtxtPrice.setText(rs.getString("DVY_PRICE_WITH_VAT"));

                delivery.jtxtQty.setValue(rs.getInt("QUANTITY"));
                delivery.jtxtLots.setText(rs.getString("LOT_NUMBER"));
                delivery.jbtnAdd.doClick();

                article.clear();
                articleGroup.clear();

            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showSum() {
        int rows = jTable1.getRowCount();
        double sumTotal = 0;
        String Total = null;
        double sumOther = 0;
        String Other = null;
        for (int row = 0; row < rows; row++) {
            sumTotal += (Double) jTable1.getValueAt(row, 7);
            sumOther += (Double) jTable1.getValueAt(row, 8);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        Total = nf.format(sumTotal);
        Other = nf.format(sumOther);
        jtxtTotal.setText(Total);
        jtxtOther.setText(Other);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDealType = new javax.swing.JComboBox<>();
        jClient = new javax.swing.JComboBox<>();
        jChannel = new javax.swing.JComboBox<>();
        jStatus = new javax.swing.JComboBox<>();
        jLang = new javax.swing.JComboBox<>();
        jPayment = new javax.swing.JComboBox<>();
        jArticle = new javax.swing.JComboBox<>();
        jGroup = new javax.swing.JComboBox<>();
        jLot = new javax.swing.JComboBox<>();
        jlbConsultation = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jdFromDate = new com.toedter.calendar.JDateChooser();
        jdToDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jlbFromDate = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jbtnExport = new javax.swing.JButton();
        jcbPayment = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jcbDealType = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtOther = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jtxtTotal = new javax.swing.JTextField();
        jbtnClose = new javax.swing.JButton();

        setTitle("Доставки");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jlbConsultation.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbConsultation.setText("Доставки");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/run24 (2).png"))); // NOI18N
        jButton1.setText("Изпълни");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jlbFromDate.setText("От дата:");

        jLabel2.setText("До дата:");

        jbtnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/excel24.png"))); // NOI18N
        jbtnExport.setText("Експорт");
        jbtnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportActionPerformed(evt);
            }
        });

        jLabel3.setText("Плащане:");

        jLabel4.setText("ДДС категория:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFromDate)
                    .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jcbPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jbtnExport)
                .addGap(47, 47, 47))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbFromDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jbtnExport))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jcbPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(19, 19, 19))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Сделка №", "Фактура №", "Дата на фактура", "Доставчик", "Държава", "Плащане", "ДДС категория", "Стойност  (€)", "Доп.разходи  (€)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(50);
        }

        jtxtOther.setEditable(false);
        jtxtOther.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtOther.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Общо:");

        jtxtTotal.setEditable(false);
        jtxtTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jbtnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnClose.setText("Изход");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1066, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(495, 495, 495)
                        .addComponent(jlbConsultation, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 823, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtOther, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jlbConsultation, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jtxtOther, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            Show_Sales_In_JTable();
            showSum();
            if (jTable1.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Няма данни по зададените критерии!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            showMaster();
            showDetails();
            delivery.jcbArticleGroups.setSelectedIndex(-1);
            delivery.jcbArticle.setSelectedIndex(-1);
            delivery.setVisible(true);
            delivery.jbtnClear.setEnabled(false);
            delivery.jtxtInvoiceNumberDialog.setEditable(false);
            delivery.jbtnNewSupplier.setEnabled(false);
            if (((DropDown) delivery.jcbStatuses.getSelectedItem()).getId() == 5) {
                delivery.jbtnSave.setEnabled(true);
            } else {
                delivery.jbtnSave.setEnabled(false);
            }
            delivery.isSave = 1;
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
        String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        File directory = new File(".");
        String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
        String fileName = absolutePath + "Reports\\Delivery " + Date;
        if (ExcelExport(jTable1, fileName)) {
            JOptionPane.showMessageDialog(this, "Успешно експортиране!");
        } else {
            JOptionPane.showMessageDialog(this, "Възникна грешка!");
        }
    }//GEN-LAST:event_jbtnExportActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbtnCloseActionPerformed

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
            java.util.logging.Logger.getLogger(ConsultationDelivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultationDelivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultationDelivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultationDelivery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultationDelivery().setVisible(true);
            }
        });
    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {

        private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

        public DecimalFormatRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }

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
    private javax.swing.JComboBox<String> jArticle;
    public javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jChannel;
    private javax.swing.JComboBox<String> jClient;
    private javax.swing.JComboBox<String> jDealType;
    private javax.swing.JComboBox<String> jGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox<String> jLang;
    private javax.swing.JComboBox<String> jLot;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> jPayment;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox<String> jStatus;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExport;
    private javax.swing.JComboBox<String> jcbDealType;
    private javax.swing.JComboBox<String> jcbPayment;
    public com.toedter.calendar.JDateChooser jdFromDate;
    public com.toedter.calendar.JDateChooser jdToDate;
    private javax.swing.JLabel jlbConsultation;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JTextField jtxtOther;
    private javax.swing.JTextField jtxtTotal;
    // End of variables declaration//GEN-END:variables
}
