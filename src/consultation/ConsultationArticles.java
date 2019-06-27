/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultation;

import java.awt.Font;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.swing.JLabel;
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
public class ConsultationArticles extends javax.swing.JFrame {

    public int Type = 0;

    /**
     * Creates new form ConsultationArticles
     */
    public ConsultationArticles() {
        initComponents();
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DATE));

        Date firstDayOfMonth = cal.getTime();

        jdToDate.setDate(date);
        jdFromDate.setDate(firstDayOfMonth);
        // set comboBox Article Groups
        setValuesComboBox("select AG.ID, AG.NAME_BG from ARTICLE_GROUPS AG", jcbGroups, false, 0, true);
        TableColumn dealValue = jTable1.getColumnModel().getColumn(4);
        dealValue.setCellRenderer(new ConsultationSales.DecimalFormatRenderer());
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        jTable1.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        jTable1.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void Show_ArticleSales() {
        try {
            String code = null;
            String group = null;
            String article = null;
            int qty = 0;
            Double total = 0.0;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select\n"
                    + "  A.CODE, AG.NAME_BG GROUPS, A.NAME_BG ARTICLE, cast(T.QUANTITY as DM_INTEGER) QTY, T.TOTAL\n"
                    + "from\n"
                    + "  (\n"
                    + "    select\n"
                    + "      DD.ARTICLE_ID, sum(DD.QUANTITY) QUANTITY, cast(sum(DD.DVY_PRICE_WITH_VAT * DD.QUANTITY) as DM_DOUBLE) TOTAL\n"
                    + "    from\n"
                    + "      DEALS D\n"
                    + "      join DEAL_DETAILS DD on D.ID = DD.DEAL_ID\n"
                    + "    where\n"
                    + "      D.DEAL_DATE between ? and ? and\n"
                    + "      DD.ARTICLE_ID <> 2 and D.OPERATION_ID = ?\n"
                    + "    group by\n"
                    + "      DD.ARTICLE_ID\n"
                    + "   )T\n"
                    + "   join ARTICLES A on A.ID = T.ARTICLE_ID\n"
                    + "   join ARTICLE_GROUPS AG on AG.ID = A.ARTICLE_GROUPS_ID\n"
                    + "where\n"
                    + "    A.ID = iif(cast(? as DM_REF) = -1 , A.ID , cast(? as DM_REF)) and\n"
                    + "    A.ARTICLE_GROUPS_ID = iif(cast(? as DM_REF) = -1, A.ARTICLE_GROUPS_ID, cast(? as DM_REF))");
            java.sql.Date sqldateFrom = new java.sql.Date(jdFromDate.getDate().getTime());
            java.sql.Date sqldateTo = new java.sql.Date(jdToDate.getDate().getTime());
            ps.setDate(1, sqldateFrom);
            ps.setDate(2, sqldateTo);
            ps.setInt(3, Type);
            ps.setInt(4, ((DropDown) jcbArticles.getSelectedItem()).getId());
            ps.setInt(5, ((DropDown) jcbArticles.getSelectedItem()).getId());
            ps.setInt(6, ((DropDown) jcbGroups.getSelectedItem()).getId());
            ps.setInt(7, ((DropDown) jcbGroups.getSelectedItem()).getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                code = rs.getString("CODE");
                group = rs.getString("GROUPS");
                article = rs.getString("ARTICLE");
                qty = rs.getInt("QTY");
                total = rs.getDouble("TOTAL");
                model.addRow(new Object[]{code, group, article, qty, total});
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationArticles.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showSum() {
        int rows = jTable1.getRowCount();
        int sumQTY = 0;
        String QTY = null;
        double sumTotal = 0;
        String Total = null;
        for (int row = 0; row < rows; row++) {
            sumQTY += (Integer) jTable1.getValueAt(row, 3);
            sumTotal += (Double) jTable1.getValueAt(row, 4);
        }
        NumberFormat nf = new DecimalFormat("#,##0.00");
        Total = nf.format(sumTotal);
        QTY = String.valueOf(sumQTY);
        jtxtTotalSum.setText(Total);
        jtxtTotalCount.setText(QTY);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jlbAva = new javax.swing.JLabel();
        jtxtTotalCount = new javax.swing.JTextField();
        jtxtTotalSum = new javax.swing.JTextField();
        jdFromDate = new com.toedter.calendar.JDateChooser();
        jdToDate = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jlbFromDate = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jbtnExport = new javax.swing.JButton();
        jcbGroups = new javax.swing.JComboBox<>();
        jcbArticles = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        jbtnClose = new javax.swing.JButton();

        setTitle("По артикули");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Код", "Група", "Артикул", "Брой продадени", "Обща сума"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        jlbAva.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jlbAva.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlbAva.setText("По артикули");

        jtxtTotalCount.setEditable(false);
        jtxtTotalCount.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotalCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jtxtTotalSum.setEditable(false);
        jtxtTotalSum.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jtxtTotalSum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setText("До дата:");

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

        jcbGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbGroupsActionPerformed(evt);
            }
        });

        jLabel1.setText("Група:");

        jLabel3.setText("Артикул:");

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
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(642, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(38, 38, 38)
                .addComponent(jtxtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81)
                .addComponent(jtxtTotalSum, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlbFromDate)
                            .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 137, Short.MAX_VALUE))
                            .addComponent(jcbGroups, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jcbArticles, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnExport)))
                .addGap(27, 27, 27))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGap(408, 408, 408)
                .addComponent(jlbAva)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbAva)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1)
                        .addComponent(jbtnExport))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jlbFromDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdFromDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbGroups, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jdToDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbArticles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtTotalCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTotalSum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(31, 31, 31)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Show_ArticleSales();
        showSum();
        if (jTable1.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Няма данни по зададените критерии!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExportActionPerformed
        String Date = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(Calendar.getInstance().getTime());
        File directory = new File(".");
        String absolutePath = directory.getAbsolutePath().substring(0, directory.getAbsolutePath().length() - 1);
        String fileName = absolutePath + "Reports\\Articles " + Date;
        if (ExcelExport(jTable1, fileName)) {
            JOptionPane.showMessageDialog(this, "Успешно експортиране!");
        } else {
            JOptionPane.showMessageDialog(this, "Възникна грешка!");
        }
    }//GEN-LAST:event_jbtnExportActionPerformed

    private void jcbGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbGroupsActionPerformed
        if (jcbGroups.getSelectedIndex() != -1) {
            String sql = "select A.ID, A.NAME_BG from ARTICLES A where A.ARTICLE_GROUPS_ID = " + ((DropDown) jcbGroups.getSelectedItem()).getId();
            setValuesComboBox(sql, jcbArticles, false, 0, true);
        }
    }//GEN-LAST:event_jcbGroupsActionPerformed

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
            java.util.logging.Logger.getLogger(ConsultationArticles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultationArticles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultationArticles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultationArticles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsultationArticles().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnExport;
    private javax.swing.JComboBox<String> jcbArticles;
    private javax.swing.JComboBox<String> jcbGroups;
    public com.toedter.calendar.JDateChooser jdFromDate;
    public com.toedter.calendar.JDateChooser jdToDate;
    private javax.swing.JLabel jlbAva;
    private javax.swing.JLabel jlbFromDate;
    private javax.swing.JTextField jtxtTotalCount;
    private javax.swing.JTextField jtxtTotalSum;
    // End of variables declaration//GEN-END:variables
}
