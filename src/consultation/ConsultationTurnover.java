/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;

import java.awt.Font;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class ConsultationTurnover extends javax.swing.JFrame {

    /**
     * Creates new form ConsultationTurnover
     */
    public ConsultationTurnover() {
        initComponents();
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        TableColumn price = jTable.getColumnModel().getColumn(6);
        TableColumn total = jTable.getColumnModel().getColumn(7);
        TableColumn vat = jTable.getColumnModel().getColumn(8);
        TableColumn dvyPrice = jTable.getColumnModel().getColumn(9);
        TableColumn totalDvyPrice = jTable.getColumnModel().getColumn(10);
        TableColumn profit = jTable.getColumnModel().getColumn(11);
        price.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        total.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        vat.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        dvyPrice.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        totalDvyPrice.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        profit.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        jTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        // set comboBox Article Groups
        setValuesComboBox("select AG.ID, AG.NAME_BG from ARTICLE_GROUPS AG", jcbGroups, false, 0, true);
        // set comboBox Articles
        String sql = "select A.ID, A.NAME_BG from ARTICLES A where A.ARTICLE_GROUPS_ID = " + ((DropDown) jcbGroups.getSelectedItem()).getId();
        setValuesComboBox(sql, jcbArticles, false, 0, true);
        
    }

    private void Show_Turnover() {
        try {
            String invoiceNumber = null;
            //String invoiceDate = null;
            String code = null;
            String article = null;
            Double salePrice = null;
            int qty = 0;
            Double Total = 0.00;
            Double Vat = 0.00;
            Double dvyPrice = 0.00;
            Double totalDvyPrice = 0.00;
            Double Profit = 0.00;
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select\n"
                    + "  T.INVOICE_NUMBER, T.INVOICE_DATE, T.CODE, T.NAME_DE, T.DVY_PRICE_WITH_VAT,\n"
                    + "  T.QTY, T.TOTAL, T.DELIVERY_PRICE, (T.QTY * T.DELIVERY_PRICE) TOTAL_DELIVERY_PRICE,\n"
                    + "  T.VAT,\n"
                    + "  (T.DVY_PRICE_WITH_VAT * T.QTY) - T.VAT - (T.QTY * T.DELIVERY_PRICE) PROFIT\n"
                    + "from\n"
                    + "  (\n"
                    + "  select\n"
                    + "    I.INVOICE_NUMBER, I.INVOICE_DATE, A.CODE, A.NAME_BG NAME_DE, DD.DVY_PRICE_WITH_VAT,\n"
                    + "    cast(DD.QUANTITY as DM_INTEGER) QTY,\n"
                    + "    cast((DD.DVY_PRICE_WITH_VAT * DD.QUANTITY) as DM_DOUBLE) TOTAL,\n"
                    + "    (select first 1 DDP.REAL_PRICE from DEAL_DETAILS DDP where\n"
                    + "    DDP.LOT_ID = DD.LOT_ID and DDP.ARTICLE_ID = DD.ARTICLE_ID and DDP.REAL_PRICE is not null) DELIVERY_PRICE,\n"
                    + "    iif(D.DEAL_TYPE_ID in (1,2),\n"
                    + "    cast(((DD.DVY_PRICE_WITH_VAT * DD.QUANTITY) * MC.VAT_PERCENTAGE / (100 + MC.VAT_PERCENTAGE)) as DM_DOUBLE), 0) VAT\n"
                    + "  from\n"
                    + "    DEALS D\n"
                    + "    join DEAL_DETAILS DD on D.ID = DD.DEAL_ID\n"
                    + "    join INVOICES I on I.DEAL_ID = D.ID\n"
                    + "    join ARTICLES A on A.ID = DD.ARTICLE_ID\n"
                    + "    join MY_COMPANY MC on (0 = 0)\n"
                    + "  where\n"
                    + "    D.OPERATION_ID = 1 and I.INVOICE_DATE between ? and ? and\n"
                    + "    A.ID = iif(cast(? as DM_REF) = -1 , A.ID , cast(? as DM_REF)) and\n "
                    + "    A.ARTICLE_GROUPS_ID = iif(cast(? as DM_REF) = -1 , ARTICLE_GROUPS_ID , cast(? as DM_REF))"
                    + "  order by\n"
                    + "    I.ID\n"
                    + "  )T ");
            java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
            java.sql.Date sqldateTo = new java.sql.Date(jdToDate.getDate().getTime());
            ps.setDate(1, sqldateFrom);
            ps.setDate(2, sqldateTo);
            ps.setInt(3, ((DropDown) jcbArticles.getSelectedItem()).getId());
            ps.setInt(4,((DropDown) jcbArticles.getSelectedItem()).getId());
            ps.setInt(5, ((DropDown) jcbGroups.getSelectedItem()).getId());
            ps.setInt(6,((DropDown) jcbGroups.getSelectedItem()).getId());
            ResultSet rs = ps.executeQuery();
            int number = 1;
            while (rs.next()) {

                invoiceNumber = rs.getString("INVOICE_NUMBER");
                // invoiceDate = rs.getString("INVOICE_DATE");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("INVOICE_DATE"));
                code = rs.getString("CODE");
                article = rs.getString("NAME_DE");
                salePrice = rs.getDouble("DVY_PRICE_WITH_VAT");
                qty = rs.getInt("QTY");
                Total = rs.getDouble("TOTAL");
                Vat = rs.getDouble("VAT");
                dvyPrice = rs.getDouble("DELIVERY_PRICE");
                totalDvyPrice = rs.getDouble("TOTAL_DELIVERY_PRICE");
                Profit = rs.getDouble("PROFIT");
                model.addRow(new Object[]{number, invoiceNumber, saleDate, code, article, qty, salePrice, Total, Vat,
                    dvyPrice, totalDvyPrice, Profit});
                number += 1;
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationTurnover.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showSum() {
        int rows = jTable.getRowCount();
        double sumVAT = 0;
        String VAT = null;
        double sumProfit = 0;
        String Profit = null;
        double sumTotalSale = 0;
        String TotalSale = null;
        double sumTotalDvy = 0;
        String TotalDvy = null;
        for (int row = 0; row < rows; row++) {
            sumVAT += (Double) jTable.getValueAt(row, 8);
            sumTotalSale += (Double) jTable.getValueAt(row, 7);
            sumProfit += (Double) jTable.getValueAt(row, 11);
            sumTotalDvy += (Double) jTable.getValueAt(row, 10);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        VAT = nf.format(sumVAT);
        Profit = nf.format(sumProfit);
        TotalSale = nf.format(sumTotalSale);
        TotalDvy = nf.format(sumTotalDvy);

        jtxtVatSum.setText(VAT);
        jtxtProfitSum.setText(Profit);
        jtxtTotalSale.setText(TotalSale);
        jtxtTotalDvy.setText(TotalDvy);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jdFromDate = new com.toedter.calendar.JDateChooser();
        jdToDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jlbFromDate = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jbtnExport = new javax.swing.JButton();
        jcbArticles = new javax.swing.JComboBox<>();
        jcbGroups = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jtxtVatSum = new javax.swing.JTextField();
        jtxtProfitSum = new javax.swing.JTextField();
        jtxtTotalDvy = new javax.swing.JTextField();
        jtxtTotalSale = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();

        setTitle("Приходи");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Приходи");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/run24 (2).png"))); // NOI18N
        jButton1.setText("Изпълни");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jlbFromDate.setText("От дата:");

        jLabel2.setText("До дата:");

        jbtnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/excel24.png"))); // NOI18N
        jbtnExport.setText("Експорт");
        jbtnExport.setFocusPainted(false);
        jbtnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportActionPerformed(evt);
            }
        });

        jcbGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbGroupsActionPerformed(evt);
            }
        });

        jLabel3.setText("Група:");

        jLabel5.setText("Артикул:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFromDate)
                    .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jcbGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jcbArticles, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jbtnExport)
                .addGap(121, 121, 121))
            .addComponent(jSeparator1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jcbArticles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jlbFromDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jbtnExport))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(5, 5, 5)
                        .addComponent(jcbGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jTable.setAutoCreateRowSorter(true);
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "№", "Фактура ", "Дата", "Код (SKU)", "Артикул", "Брой", "Ед.цена с ДДС", "Обща прод.ст-ст", "Начислено ДДС", "Доставна цена", "Обща дост.ст-ст", "Приход"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setPreferredWidth(1);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(30);
            jTable.getColumnModel().getColumn(3).setPreferredWidth(20);
            jTable.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTable.getColumnModel().getColumn(5).setPreferredWidth(30);
            jTable.getColumnModel().getColumn(6).setPreferredWidth(70);
            jTable.getColumnModel().getColumn(7).setPreferredWidth(80);
            jTable.getColumnModel().getColumn(8).setPreferredWidth(70);
            jTable.getColumnModel().getColumn(9).setPreferredWidth(60);
            jTable.getColumnModel().getColumn(10).setPreferredWidth(65);
            jTable.getColumnModel().getColumn(11).setPreferredWidth(20);
        }

        jtxtVatSum.setEditable(false);
        jtxtVatSum.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtVatSum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jtxtProfitSum.setEditable(false);
        jtxtProfitSum.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtProfitSum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jtxtTotalDvy.setEditable(false);
        jtxtTotalDvy.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotalDvy.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jtxtTotalSale.setEditable(false);
        jtxtTotalSale.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotalSale.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Общо:");

        jbtnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnClose.setText("Изход");
        jbtnClose.setFocusPainted(false);
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
                .addGap(582, 582, 582)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1289, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(36, 36, 36)
                        .addComponent(jtxtTotalSale, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(48, 48, 48)
                        .addComponent(jtxtVatSum, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(150, 150, 150)
                        .addComponent(jtxtTotalDvy, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtProfitSum, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtVatSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtProfitSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotalDvy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotalSale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(27, 27, 27)
                .addComponent(jbtnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Show_Turnover();
        showSum();
        if (jTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Няма данни по зададените критерии!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
        String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        File directory = new File(".");
        String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
        String fileName = absolutePath + "Reports\\Turnover " + Date;
        if (ExcelExport(jTable, fileName)) {
            JOptionPane.showMessageDialog(this, "Успешно експортиране!");
        } else {
            JOptionPane.showMessageDialog(this, "Възникна грешка!");
        }
    }//GEN-LAST:event_jbtnExportActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jcbGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbGroupsActionPerformed
        String sql = "select A.ID, A.NAME_BG from ARTICLES A where A.ARTICLE_GROUPS_ID = " + ((DropDown) jcbGroups.getSelectedItem()).getId();
        setValuesComboBox(sql, jcbArticles, false, 0, true);
    }//GEN-LAST:event_jcbGroupsActionPerformed

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
            java.util.logging.Logger.getLogger(ConsultationTurnover.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultationTurnover.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultationTurnover.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultationTurnover.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultationTurnover().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExport;
    private javax.swing.JComboBox<String> jcbArticles;
    private javax.swing.JComboBox<String> jcbGroups;
    public com.toedter.calendar.JDateChooser jdFromDate;
    public com.toedter.calendar.JDateChooser jdToDate;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JTextField jtxtProfitSum;
    private javax.swing.JTextField jtxtTotalDvy;
    private javax.swing.JTextField jtxtTotalSale;
    private javax.swing.JTextField jtxtVatSum;
    // End of variables declaration//GEN-END:variables
}
