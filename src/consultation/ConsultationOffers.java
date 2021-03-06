/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;

import Tools.DropDown;
import static Tools.ExcelExporter.ExcelExport;
import static Tools.GetSQL.getSQL;
import static Tools.getConnection.getConnection;
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
import sale.SALE;
/**
 *
 * @author Viko
 */
public class ConsultationOffers extends javax.swing.JFrame {

    public int Type = 1; // for type of report

    /**
     * Creates new form ConsultationSales
     */
    public ConsultationOffers() {
        initComponents();
        Date date = new Date();
        jdFromDate.setDate(date);
        jdToDate.setDate(date);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        TableColumn dealValue = jTable1.getColumnModel().getColumn(8);
        TableColumn ValueVat = jTable1.getColumnModel().getColumn(9);
        dealValue.setCellRenderer(new DecimalFormatRenderer());
        ValueVat.setCellRenderer(new DecimalFormatRenderer());
        setValuesChannelType();
        setValuesDealType();
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

    }

    private void setValuesChannelType() {
        try {

            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select NC.ID, NC.NAME CHANNELS from N_CHANNELS NC");
            ResultSet rs = ps.executeQuery();
            jcbChannels.removeAllItems();
            Vector<DropDown> vector = new Vector<>();
            vector.addElement(new DropDown(-1, "Всички"));
            while (rs.next()) {
                vector.addElement(new DropDown(rs.getInt(1), rs.getString(2)));
            }
            jcbChannels.setModel(new DefaultComboBoxModel(vector));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        jcbChannels.setSelectedIndex(0);
    }

    private void setValuesDealType() {
        try {
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select DT.ID, DT.NAME DEAL_TYPE from N_DEAL_TYPES DT");
            ResultSet rs = ps.executeQuery();
            jcbDealType.removeAllItems();
            Vector<DropDown> vector = new Vector<>();
            vector.addElement(new DropDown(-1, "Всички"));

            while (rs.next()) {
                vector.addElement(new DropDown(rs.getInt(1), rs.getString(2)));
            }
            jcbDealType.setModel(new DefaultComboBoxModel(vector));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
        jcbDealType.setSelectedIndex(0);
    }

    SALE sale = new SALE();

    public ArrayList<ConsultationDeals> getSales() throws SQLException {
        ArrayList<ConsultationDeals> salesList = new ArrayList<>();
        Connection con = getConnection();
        java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
        java.sql.Date sqldateTo = new java.sql.Date(jdToDate.getDate().getTime());
        String sql = getSQL(10);
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, Type);
        ps.setDate(2, sqldateFrom);
        ps.setDate(3, sqldateTo);
        ps.setInt(4, ((DropDown) jcbChannels.getSelectedItem()).getId());
        ps.setInt(5, ((DropDown) jcbChannels.getSelectedItem()).getId());

        ps.setInt(6, ((DropDown) jcbDealType.getSelectedItem()).getId());
        ps.setInt(7, ((DropDown) jcbDealType.getSelectedItem()).getId());
        ResultSet rs = ps.executeQuery();
        try {

            ConsultationDeals sales;
            while (rs.next()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                sales = new ConsultationDeals(rs.getInt("DEAL_NUMBER"), "------", saleDate,
                        rs.getString("CLIENT"), rs.getDouble("DEAL_VALUE"), rs.getString("CHANNEL"),
                        rs.getString("COUNTRY"), "------", rs.getString("DEAL_TYPE"), rs.getDouble("VALUE_VAT"));
                salesList.add(sales);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ConsultationOffers.class.getName()).log(Level.SEVERE, null, ex);
        }

        return salesList;

    }

    public void Show_Sales_In_JTable() throws SQLException {
        ArrayList<ConsultationDeals> list = getSales();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        // clear jtable content
        model.setRowCount(0);
        Object[] row = new Object[10];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getDealNumber();
            row[1] = list.get(i).getInvoiceNumber();
            row[2] = list.get(i).getDealDate();
            row[3] = list.get(i).getClientName();
            row[4] = list.get(i).getCountry();
            row[5] = list.get(i).getChannel();
            row[6] = list.get(i).getPayment();
            row[7] = list.get(i).getType();
            row[8] = list.get(i).getDealValue();
            row[9] = list.get(i).getValueVat();

            model.addRow(row);
        }
    }

    private void showMaster() {
        sale.jtxtSaleNumber.setBackground(Color.white);
        sale.jlbSale.setText("Оферта");
        sale.setTitle("Оферта");
        sale.jbntInvoice.setEnabled(false);
        sale.isOffer = 1;
        sale.isSave = 1; // only for test check this!!!!
        try {
            Connection con = getConnection();
            int row = jTable1.getSelectedRow();
            int correctModel = jTable1.convertRowIndexToModel(row);

            PreparedStatement ps = con.prepareStatement("select\n"
                    + " D.ID, D.DEAL_NUMBER, D.DEAL_DATE, D.OPERATION_ID, O.NAME OPERATION,\n"
                    + " D.DEAL_TYPE_ID, DT.NAME DEAL_TYPE,\n"
                    + " D.CHANNEL_ID, C.NAME CHANNEL, D.STATUS_ID, S.NAME STATUS, D.CLIENT_ID, CC.NAME CLIENT,\n"
                    + " D.LANG_ID LANG_ID, L.SHORT_NAME LANG, D.DEAL_VALUE,\n"
                    + " D.TRANSPORT_COSTS, D.CHANNEL_COSTS, D.BANK_COSTS, D.OTHER_COSTS, I.INVOICE_NUMBER, I.PAYMENT_ID, I.PAYMENT_TEXT,\n"
                    + " CN.ID CREDIT_ID, CN.CR_NOTE_NUMBER, CN.CR_NOTE_DATE, CN.CR_NOTE_TEXT,\n"
                    + " case\n"
                    + "    when L.ID = 1 then P.NAME_GERM\n"
                    + "    when L.ID = 2 then P.NAME_ENG\n"
                    + "    when L.ID = 3 then P.NAME_BUL\n"
                    + " end PAYMENT,\n"
                    + " iif(DT.ACCRUING_VAT = 1, (select MC.VAT_PERCENTAGE from MY_COMPANY MC), 0) VAT_PCT\n"
                    + " from\n"
                    + "  DEALS D\n"
                    + "  join N_OPERATIONS O on O.ID = D.OPERATION_ID\n"
                    + "  join N_DEAL_TYPES DT on DT.ID = D.DEAL_TYPE_ID\n"
                    + "  join N_CHANNELS C on C.ID = D.CHANNEL_ID\n"
                    + "  join N_STATUSES S on S.ID = D.STATUS_ID\n"
                    + "  join CLIENTS CC on CC.ID = D.CLIENT_ID\n"
                    + "  join N_LANGS L on L.ID = D.LANG_ID\n"
                    + "  left join INVOICES I on I.DEAL_ID = D.ID\n"
                    + "  left join N_PAYMENT_METHODS P on P.ID = I.PAYMENT_ID\n"
                    + "  left join CREDIT_NOTES CN on CN.ID = I.CREDIT_NOTE_ID\n"
                    + " where\n"
                    + "  D.OPERATION_ID = ? and D.DEAL_NUMBER = " + jTable1.getModel().getValueAt(correctModel, 0)); // Make Deal_number parameter
            // TODO: Must make INVOICE_NUMBER, because when reset generator........ or Add OPERATION_TYPE
            ps.setInt(1, Type);
            ResultSet rs = ps.executeQuery();
            Vector<DropDown> dealType = new Vector<>();
            Vector<DropDown> channel = new Vector<>();
            Vector<DropDown> status = new Vector<>();
            Vector<DropDown> client = new Vector<>();
            Vector<DropDown> lang = new Vector<>();
            Vector<DropDown> payment = new Vector<>();

            sale.isActive = 0;
            sale.checkInvoiceNumber = 0;
            sale.isSave = 1;

            while (rs.next()) {
                sale.VatPCT = rs.getDouble("VAT_PCT");
                sale.saleID = rs.getInt("ID");
                dealType.addElement(new DropDown(rs.getInt("DEAL_TYPE_ID"), rs.getString("DEAL_TYPE")));
                client.addElement(new DropDown(rs.getInt("CLIENT_ID"), rs.getString("CLIENT")));
                channel.addElement(new DropDown(rs.getInt("CHANNEL_ID"), rs.getString("CHANNEL")));
                status.addElement(new DropDown(rs.getInt("STATUS_ID"), rs.getString("STATUS")));
                lang.addElement(new DropDown(rs.getInt("LANG_ID"), rs.getString("LANG")));
                payment.addElement(new DropDown(rs.getInt("PAYMENT_ID"), rs.getString("PAYMENT")));

                jDealType.setModel(new DefaultComboBoxModel(dealType));
                jClient.setModel(new DefaultComboBoxModel(client));
                jChannel.setModel(new DefaultComboBoxModel(channel));
                jStatus.setModel(new DefaultComboBoxModel(status));
                jLang.setModel(new DefaultComboBoxModel(lang));
                jPayment.setModel(new DefaultComboBoxModel(payment));
                sale.jcbChannel.setSelectedIndex(0);
                sale.jtxtSaleNumber.setText(rs.getString("DEAL_NUMBER"));
                sale.jDateSale.setDate(rs.getDate("DEAL_DATE"));
                sale.jcbDealType.getModel().setSelectedItem(jDealType.getModel().getSelectedItem());
                sale.jcbClient.setSelectedIndex(0);
                sale.jcbClient.getModel().setSelectedItem(jClient.getModel().getSelectedItem());
                sale.jtxtSaleNumber.setText(rs.getString("DEAL_NUMBER"));
                sale.jcbChannel.getModel().setSelectedItem(jChannel.getModel().getSelectedItem());
                sale.jcbStatuses.getModel().setSelectedItem(jStatus.getModel().getSelectedItem());
                sale.jcbLang.getModel().setSelectedItem(jLang.getModel().getSelectedItem());
                sale.jtxtTransportCosts.setText(rs.getString("TRANSPORT_COSTS"));
                sale.jtxtOtherCosts.setText(rs.getString("OTHER_COSTS"));
                sale.jtxtChannelCosts.setText(rs.getString("CHANNEL_COSTS"));
                sale.jtxtBankCosts.setText(rs.getString("BANK_COSTS"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                sale.jtxtInvoiceNumber.setText(rs.getString("INVOICE_NUMBER") + "/" + saleDate);
                sale.jcbPayment.getModel().setSelectedItem(jPayment.getModel().getSelectedItem());
                sale.jtaPaymentText.setText(rs.getString("PAYMENT_TEXT"));

                sale.jlbVatValue.setText("ДДС (" + sale.VatPCT + "%): ");
                if (((DropDown) sale.jcbStatuses.getSelectedItem()).getId() == 4) {
                    sale.jchbReversal.setSelected(true);
                    sale.jpCreditNote.setVisible(true);
                    sale.jCreditNoteDate.setDate(rs.getDate("CR_NOTE_DATE"));
                    sale.jtxtCreditNoteNumber.setText(rs.getString("CR_NOTE_NUMBER"));
                    //sale.jtxtCreditNoteNumber.setBackground(Color.white);
                    sale.jTextArea1.setText(rs.getString("CR_NOTE_TEXT"));
                    sale.creditID = rs.getInt("CREDIT_ID");
                } else {
                    sale.jchbReversal.setSelected(false);
                    Date date = new Date();
                    sale.jCreditNoteDate.setDate(date);
                    sale.jtxtCreditNoteNumber.setText("");
                    sale.jTextArea1.setText("");
                    sale.creditID = 0;
                }
                dealType.clear();
                client.clear();
                channel.clear();
                status.clear();
                lang.clear();
                payment.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationOffers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showDetails() {
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
                    + "    when D.LANG_ID = 1 then A.CODE || ' | '|| A.NAME_DE\n"
                    + "    when D.LANG_ID = 2 then A.CODE || ' | '|| A.NAME_EN\n"
                    + "    when D.LANG_ID = 3 then A.CODE || ' | '|| A.NAME_BG\n"
                    + "  end ARTICLE,\n"
                    + "  DD.QUANTITY, DD.PRICE, DD.LOT_ID, L.NUMBER LOT_NUMBER, DD.DVY_PRICE_WITH_VAT\n"
                    + "from\n"
                    + "  DEAL_DETAILS DD\n"
                    + "  join DEALS D on D.ID = DD.DEAL_ID\n"
                    + "  join ARTICLES A on A.ID = DD.ARTICLE_ID\n"
                    + "  join ARTICLE_GROUPS AG on AG.ID = A.ARTICLE_GROUPS_ID\n"
                    + "  join LOTS L on L.ID = DD.LOT_ID\n"
                    + "where\n"
                    + "  D.OPERATION_ID = ? and D.DEAL_NUMBER = " + jTable1.getModel().getValueAt(correctModel, 0) + " order by A.ID desc");
            ps.setInt(1, Type);
            ResultSet rs = ps.executeQuery();
            Vector<DropDown> article = new Vector<>();
            Vector<DropDown> articleGroup = new Vector<>();
            Vector<DropDown> lot = new Vector<>();
            while (rs.next()) {
                article.addElement(new DropDown(rs.getInt("ARTICLE_ID"), rs.getString("ARTICLE")));
                articleGroup.addElement(new DropDown(rs.getInt("ARTICLE_GROUP_ID"), rs.getString("ARTICLE_GROUP")));
                lot.addElement(new DropDown(rs.getInt("LOT_ID"), rs.getString("LOT_NUMBER")));

                jArticle.setModel(new DefaultComboBoxModel(article));
                jGroup.setModel(new DefaultComboBoxModel(articleGroup));
                jLot.setModel(new DefaultComboBoxModel(lot));

                sale.jcbArticleGroups.getModel().setSelectedItem(jGroup.getModel().getSelectedItem());
                sale.jcbArticle.getModel().setSelectedItem(jArticle.getModel().getSelectedItem());
                sale.jcbLots.getModel().setSelectedItem(jLot.getModel().getSelectedItem());
                sale.jtxtPrice.setText(rs.getString("DVY_PRICE_WITH_VAT"));

                sale.jtxtQty.setValue(rs.getInt("QUANTITY"));

                sale.jbtnAdd.doClick();

                article.clear();
                articleGroup.clear();
                lot.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultationOffers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void showSum() {
        int rows = jTable1.getRowCount();
        double sumTotal = 0;
        String Total = null;
        double sumVat = 0;
        String Vat = null;
        for (int row = 0; row < rows; row++) {
            sumTotal += (Double) jTable1.getValueAt(row, 8);
            sumVat += (Double) jTable1.getValueAt(row, 9);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        Total = nf.format(sumTotal);
        Vat = nf.format(sumVat);
        jtxtTotal.setText(Total);
        jtxtTotalVat.setText(Vat);

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
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jmiDelete = new javax.swing.JMenuItem();
        jlbConsultation = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jdFromDate = new com.toedter.calendar.JDateChooser();
        jdToDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jlbFromDate = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jbtnExport = new javax.swing.JButton();
        jcbChannels = new javax.swing.JComboBox<>();
        jcbDealType = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtTotal = new javax.swing.JTextField();
        jtxtTotalVat = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();

        jmiDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/delete16.png"))); // NOI18N
        jmiDelete.setText("Изтрий!");
        jmiDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiDeleteActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jmiDelete);

        setTitle("Оферти");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jlbConsultation.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbConsultation.setText("Оферти");

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

        jLabel1.setText("Канал:");

        jLabel4.setText("ДДС категория:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFromDate)
                    .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jcbChannels, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jbtnExport)
                .addGap(32, 32, 32))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 1, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton1)
                                    .addComponent(jbtnExport))
                                .addGap(23, 23, 23))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jlbFromDate)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbChannels, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(28, 28, 28))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbDealType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Сделка №", "Фактура №", "Дата на фактура", "Купувач", "Държава", "Канал", "Плащане", "ДДС категория", "Стойност (€)", "Начислено ДДС (€) "
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setComponentPopupMenu(jPopupMenu1);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(90);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(130);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(120);
        }

        jtxtTotal.setEditable(false);
        jtxtTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jtxtTotalVat.setEditable(false);
        jtxtTotalVat.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotalVat.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtTotalVat.setToolTipText("");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Общо:");

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
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1075, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 24, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jlbConsultation, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(344, 344, 344))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(jtxtTotalVat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jlbConsultation, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotalVat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(32, 32, 32)
                .addComponent(jbtnClose)
                .addGap(21, 21, 21))
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
            Logger.getLogger(ConsultationOffers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) {
            sale.checkQuantity = 0; // This is flag for check for availability quantity and sale quantity!!!!!   
            showMaster();
            showDetails();
            sale.jcbArticleGroups.setSelectedIndex(-1);
            sale.jcbArticle.setSelectedIndex(-1);
            sale.jcbLots.setSelectedIndex(-1);
            sale.setVisible(true);
            sale.jbtnClear.setEnabled(false);
            sale.jtxtInvoiceNumberDialog.setEditable(false);
            if (((DropDown) sale.jcbStatuses.getSelectedItem()).getId() == 4) {
                sale.jlbCreditNote.setVisible(true);
                sale.jpCreditNote.setVisible(true);
            } else {
                sale.jlbCreditNote.setVisible(false);
            }
            sale.jbtnNewClient.setEnabled(false);
            if (((DropDown) sale.jcbStatuses.getSelectedItem()).getId() == 5) {
                sale.jbtnSave.setEnabled(true);
            } else {
                sale.jbtnSave.setEnabled(false);
            }
            sale.isSave = 1;
            sale.checkQuantity = 1; // This is flag for check for availability quantity and sale quantity!!!!!
        }
    }//GEN-LAST:event_jTable1MouseClicked
    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
        String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        File directory = new File(".");
        String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
        String fileName = absolutePath + "Reports\\Offers " + Date;
        if (ExcelExport(jTable1, fileName)) {
            JOptionPane.showMessageDialog(this, "Успешно експортиране!");
        } else {
            JOptionPane.showMessageDialog(this, "Възникна грешка!");
        }

    }//GEN-LAST:event_jbtnExportActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        if (evt.isPopupTrigger()) {
            jPopupMenu1.show(this, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTable1MouseReleased

    private void jmiDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiDeleteActionPerformed
        int dialogButton = JOptionPane.showConfirmDialog(null, "Сигурни ли сте, че искате да изтриете офертата!", "Справки", JOptionPane.YES_NO_OPTION);
        if (dialogButton == JOptionPane.YES_OPTION) {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("delete from DEALS D\n"
                        + "where (D.DEAL_NUMBER = ? and D.OPERATION_ID = 5)");
                ps.setString(1, jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 0).toString());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно изтрихте офертата!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Не може да изтриете тази оферта!");
            }
            if (dialogButton == JOptionPane.NO_OPTION) {
                remove(dialogButton);
            }
        }
    }//GEN-LAST:event_jmiDeleteActionPerformed

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
            java.util.logging.Logger.getLogger(ConsultationOffers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultationOffers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultationOffers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultationOffers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultationOffers().setVisible(true);
            }
        });
    }

    static class DecimalFormatRenderer extends DefaultTableCellRenderer {

        public DecimalFormatRenderer() {
            super();
            setHorizontalAlignment(JLabel.RIGHT);
        }
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
    private javax.swing.JComboBox<String> jArticle;
    public javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jChannel;
    private javax.swing.JComboBox<String> jClient;
    private javax.swing.JComboBox<String> jDealType;
    private javax.swing.JComboBox<String> jGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox<String> jLang;
    private javax.swing.JComboBox<String> jLot;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox<String> jPayment;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox<String> jStatus;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExport;
    private javax.swing.JComboBox<String> jcbChannels;
    private javax.swing.JComboBox<String> jcbDealType;
    public com.toedter.calendar.JDateChooser jdFromDate;
    public com.toedter.calendar.JDateChooser jdToDate;
    public javax.swing.JLabel jlbConsultation;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JMenuItem jmiDelete;
    private javax.swing.JTextField jtxtTotal;
    private javax.swing.JTextField jtxtTotalVat;
    // End of variables declaration//GEN-END:variables
}
