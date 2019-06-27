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
public class wrhAvailability extends javax.swing.JFrame {

    /**
     * Creates new form wrhAvailability
     */
    public wrhAvailability() {
        initComponents();
        // set comboBox Article Groups
        setValuesComboBox("select AG.ID, AG.NAME_EN from ARTICLE_GROUPS AG", jcbGroups, false, 0, true);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);
        TableColumn dvy = jTable1.getColumnModel().getColumn(6);
        TableColumn real = jTable1.getColumnModel().getColumn(7);
        TableColumn total = jTable1.getColumnModel().getColumn(8);
        dvy.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        real.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        total.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

    }

    private void Show_Availability() {
        try {
            String code = null;
            String lot = null;
            String article = null;
            int qty = 0;
            double dvyPrice = 0;
            double realPrice = 0;
            double Total = 0;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select WAV.CODE, WAV.NAME_EN, WAV.QTY, WAV.NUMBER, WAV.DEAL_DATE,\n"
                    + "WAV.DVY_PRICE_WITH_VAT, WAV.REAL_PRICE, WAV.TOTAL from WRH_AVAILABILITY(?,?)WAV");
            java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
            ps.setDate(2, sqldateFrom);
            ps.setInt(1, ((DropDown) jcbGroups.getSelectedItem()).getId());
            ResultSet rs = ps.executeQuery();
            int number = 1;
            while (rs.next()) {
                code = rs.getString("CODE");
                lot = rs.getString("NUMBER");
                article = rs.getString("NAME_EN");
                qty = rs.getInt("QTY");
                dvyPrice = rs.getDouble("DVY_PRICE_WITH_VAT");
                realPrice = rs.getDouble("REAL_PRICE");
                Total = rs.getDouble("TOTAL");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dealDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                model.addRow(new Object[]{number, code, article, lot, dealDate, qty, dvyPrice, realPrice, Total});
                number += 1;
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showSum() {
        int rows = jTable1.getRowCount();
        double sumTotal = 0;
        String Total = null;
        double sumDeliveryPrice = 0;
        String deliveryPrice = null;
        double sumRealPrice = 0;
        String realPrice = null;
        for (int row = 0; row < rows; row++) {
            sumTotal += (Double) jTable1.getValueAt(row, 8);
            sumRealPrice += (Double) jTable1.getValueAt(row, 7);
            sumDeliveryPrice += (Double) jTable1.getValueAt(row, 6);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        Total = nf.format(sumTotal);
        realPrice = nf.format(sumRealPrice);
        deliveryPrice = nf.format(sumDeliveryPrice);
        jtxtTotal.setText(Total);
        jtxtRealPrice.setText(realPrice);
        jtxtDeliveryPrice.setText(deliveryPrice);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jlbAva = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jdFromDate = new com.toedter.calendar.JDateChooser();
        jlbFromDate = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jbtnExport = new javax.swing.JButton();
        jcbGroups = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtTotal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();
        jtxtDeliveryPrice = new javax.swing.JTextField();
        jtxtRealPrice = new javax.swing.JTextField();

        setTitle("Счетоводна наличност");
        setResizable(false);

        jlbAva.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbAva.setText("Счетоводна наличност");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlbAva)
                .addGap(502, 502, 502))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jlbAva)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jlbFromDate.setText("Към дата:");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/run24 (2).png"))); // NOI18N
        jButton1.setText("Изпълни");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jbtnExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/consultation/excel24.png"))); // NOI18N
        jbtnExport.setText("Експорт");
        jbtnExport.setFocusPainted(false);
        jbtnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExportActionPerformed(evt);
            }
        });

        jLabel1.setText("Група:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFromDate)
                    .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jcbGroups, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79)
                .addComponent(jButton1)
                .addGap(44, 44, 44)
                .addComponent(jbtnExport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jlbFromDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jbtnExport)))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Номер", "Код(SKU)", "Артикул", "Партида", "Дата на доставка", "Брой налични", "Доставна цена", "Реална дост. цена", "Обща дост.стойност"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(210);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(30);
        }

        jtxtTotal.setEditable(false);
        jtxtTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtTotal.setToolTipText("");

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

        jtxtDeliveryPrice.setEditable(false);
        jtxtDeliveryPrice.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtDeliveryPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtDeliveryPrice.setToolTipText("");

        jtxtRealPrice.setEditable(false);
        jtxtRealPrice.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtRealPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jtxtRealPrice.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(797, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(33, 33, 33)
                        .addComponent(jtxtDeliveryPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(jtxtRealPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtDeliveryPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtRealPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jbtnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Show_Availability();
        showSum();
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Няма данни по зададените критерии!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
            String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
            File directory = new File(".");
            String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
            String fileName = absolutePath + "Reports\\WrhAvailability " + Date; 
            if(ExcelExport(jTable1, fileName)){
              JOptionPane.showMessageDialog(this, "Успешно експортиране!");
            }
            else{
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
            java.util.logging.Logger.getLogger(wrhAvailability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(wrhAvailability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(wrhAvailability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(wrhAvailability.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new wrhAvailability().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExport;
    private javax.swing.JComboBox<String> jcbGroups;
    public com.toedter.calendar.JDateChooser jdFromDate;
    private javax.swing.JLabel jlbAva;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JTextField jtxtDeliveryPrice;
    private javax.swing.JTextField jtxtRealPrice;
    private javax.swing.JTextField jtxtTotal;
    // End of variables declaration//GEN-END:variables
}
