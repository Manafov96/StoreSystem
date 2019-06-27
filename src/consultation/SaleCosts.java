/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;

import static Tools.ExcelExporter.ExcelExport;
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

/**
 *
 * @author Viko
 */
public class SaleCosts extends javax.swing.JFrame {

    /**
     * Creates new form wrhAvailability
     */
    public SaleCosts() {
        initComponents();
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        jTable1.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        TableColumn transport = jTable1.getColumnModel().getColumn(3);
        TableColumn bank = jTable1.getColumnModel().getColumn(4);
        TableColumn channel = jTable1.getColumnModel().getColumn(5);
        TableColumn other = jTable1.getColumnModel().getColumn(6);
        TableColumn total = jTable1.getColumnModel().getColumn(7);
        transport.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        bank.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        channel.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        other.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        total.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void Show_Costs() {
        try {
            String dealNumber = null;
            String invoiceNumber = null;
            double transportCosts = 0;
            double bankCosts = 0;
            double channelCosts = 0;
            double otherCosts = 0;
            double Total = 0;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select\n"
                    + "  D.DEAL_NUMBER, I.INVOICE_NUMBER, D.DEAL_DATE,\n"
                    + "  D.TRANSPORT_COSTS, D.CHANNEL_COSTS, D.BANK_COSTS, D.OTHER_COSTS,\n"
                    + "  (D.TRANSPORT_COSTS + D.CHANNEL_COSTS + D.BANK_COSTS + D.OTHER_COSTS) TOTAL\n"
                    + "from\n"
                    + "  DEALS D\n"
                    + "  left join INVOICES I on D.ID = I.DEAL_ID\n"
                    + "where\n"
                    + "  D.OPERATION_ID = ? and D.DEAL_DATE between ? and ?");
            java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
            java.sql.Date sqldateTo = new java.sql.Date(jdToDate.getDate().getTime());
            int costsType = 1;
            if(jcbCostsType.getSelectedIndex() == 1){
              costsType = 2;
            }
            ps.setInt(1, costsType);
            ps.setDate(2, sqldateFrom);
            ps.setDate(3, sqldateTo);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                dealNumber = rs.getString("DEAL_NUMBER");
                invoiceNumber = rs.getString("INVOICE_NUMBER");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String saleDate = dateFormat.format(rs.getDate("DEAL_DATE"));
                transportCosts = rs.getDouble("TRANSPORT_COSTS");
                channelCosts = rs.getDouble("CHANNEL_COSTS");
                bankCosts = rs.getDouble("BANK_COSTS");
                otherCosts = rs.getDouble("OTHER_COSTS");
                Total = rs.getDouble("TOTAL");
                model.addRow(new Object[]{dealNumber, invoiceNumber, saleDate, transportCosts, bankCosts, channelCosts, otherCosts, Total});
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showSum() {
        int rows = jTable1.getRowCount();
        double sumTotal = 0;
        String Total = null;
        for (int row = 0; row < rows; row++) {
            sumTotal += (Double) jTable1.getValueAt(row, 7);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        Total = nf.format(sumTotal);
        jtxtTotal.setText(Total);
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
        jdToDate = new com.toedter.calendar.JDateChooser();
        jlbToDate = new javax.swing.JLabel();
        jcbCostsType = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jtxtTotal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();

        setTitle("Разходи");
        setResizable(false);

        jlbAva.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbAva.setText("Разходи");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlbAva)
                .addGap(406, 406, 406))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jlbAva)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jlbFromDate.setText("От дата:");

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

        jlbToDate.setText("До дата:");

        jcbCostsType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "При продажби", "При доставки" }));

        jLabel1.setText("Тип:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlbFromDate)
                    .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlbToDate))
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jcbCostsType, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(44, 44, 44)
                .addComponent(jbtnExport)
                .addGap(94, 94, 94))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jlbToDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jlbFromDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jbtnExport))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbCostsType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Сделка №", "Фактура №", "Дата", "Транспортни", "Банкови", "За посредника", "Други", "Общо"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(60);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(31, 31, 31)
                .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addComponent(jbtnClose)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Show_Costs();
        showSum();
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Няма данни по зададените критерии!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
        String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        File directory = new File(".");
        String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
        String fileName = absolutePath + "Reports\\Costs " + Date;
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
    private javax.swing.JComboBox<String> jcbCostsType;
    public com.toedter.calendar.JDateChooser jdFromDate;
    public com.toedter.calendar.JDateChooser jdToDate;
    private javax.swing.JLabel jlbAva;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JLabel jlbToDate;
    private javax.swing.JTextField jtxtTotal;
    // End of variables declaration//GEN-END:variables
}
