/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import consultation.ConsultationDelivery;
import java.awt.Font;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static Tools.getConnection.getConnection;

/**
 *
 * @author Viko
 */
public class DealTypes extends javax.swing.JFrame {

    /**
     * Creates new form DealType
     */
    public DealTypes() {
        initComponents();
        Show_DealTypes();
        jTable1.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
    }

    private void Show_DealTypes() {
        try {
            String ID = null;
            String name = null;
            String accuring = null;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("select DT.ID, DT.NAME, iif(DT.ACCRUING_VAT = 1, 'ДА', 'НЕ') ACCURING from N_DEAL_TYPES DT");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ID = rs.getString("ID");
                name = rs.getString("NAME");
                accuring = rs.getString("ACCURING");
                model.addRow(new Object[]{ID, name, accuring});
            }
        } catch (SQLException e) {
            Logger.getLogger(ConsultationDelivery.class.getName()).log(Level.SEVERE, null, e);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jtxtID = new javax.swing.JTextField();
        jtxtName = new javax.swing.JTextField();
        jcbAccuring = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jbtnAdd = new javax.swing.JButton();
        jtbnUpdate = new javax.swing.JButton();
        jtbnExit = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Типове сделки");
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
                "Номер", "Име", "Начисляване ДДС"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(30);
        }

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Типове сделки");

        jtxtID.setEditable(false);

        jcbAccuring.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ДА", "НЕ" }));
        jcbAccuring.setSelectedIndex(-1);

        jLabel2.setText("Начисляване ДДС:");

        jLabel3.setText("Име:");

        jLabel4.setText("Номер:");

        jbtnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Add32.png"))); // NOI18N
        jbtnAdd.setText("Добави");
        jbtnAdd.setFocusPainted(false);
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });

        jtbnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Update32.png"))); // NOI18N
        jtbnUpdate.setText("Обнови");
        jtbnUpdate.setFocusPainted(false);
        jtbnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnUpdateActionPerformed(evt);
            }
        });

        jtbnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jtbnExit.setText("Изход");
        jtbnExit.setFocusPainted(false);
        jtbnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtbnExitActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/clear24.png"))); // NOI18N
        jButton1.setText("Изчисти");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(320, 320, 320)
                        .addComponent(jtbnUpdate)
                        .addGap(83, 83, 83)
                        .addComponent(jtbnExit))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(361, 361, 361)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbAccuring, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(47, 47, 47)
                        .addComponent(jbtnAdd)
                        .addGap(10, 10, 10)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 850, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnAdd)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jcbAccuring, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtbnUpdate)
                    .addComponent(jtbnExit))
                .addGap(24, 24, 24))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

    private void jtbnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnExitActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jtbnExitActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jtxtID.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 0).toString());
        jtxtName.setText(jTable1.getValueAt(jTable1.getSelectedRow(), 1).toString());
        jcbAccuring.setSelectedItem(jTable1.getValueAt(jTable1.getSelectedRow(), 2).toString());
        jbtnAdd.setEnabled(false);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jtxtID.setText("");
        jtxtName.setText("");
        jcbAccuring.setSelectedIndex(-1);
        jbtnAdd.setEnabled(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        if (jtxtName.getText().isEmpty() || jcbAccuring.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else {
            int accuring = 0;
            if (jcbAccuring.getSelectedIndex() == 0) {
                accuring = 1;
            } else if (jcbAccuring.getSelectedIndex() == 1) {
                accuring = 0;
            }
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("insert into N_DEAL_TYPES (NAME, ACCRUING_VAT)\n"
                        + "values (?, ?)");
                ps.setString(1, jtxtName.getText());
                ps.setInt(2, accuring);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Записано в базата данни!");
                jtxtID.setText("");
                jtxtName.setText("");
                jcbAccuring.setSelectedIndex(-1);

                Show_DealTypes();

            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jtbnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtbnUpdateActionPerformed
        if (jTable1.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете тип на сделката, която искате да редактирате!");
        } else {
            int accuring = 0;
            if (jcbAccuring.getSelectedIndex() == 0) {
                accuring = 1;
            } else if (jcbAccuring.getSelectedIndex() == 1) {
                accuring = 0;
            }
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update N_DEAL_TYPES\n"
                        + "set NAME = ?,\n"
                        + "    ACCRUING_VAT = ?\n"
                        + "where (ID = ?)");
                ps.setString(1, jtxtName.getText());
                ps.setInt(2, accuring);
                ps.setString(3, jtxtID.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно направихте промени!");
                jtxtID.setText("");
                jtxtName.setText("");
                jcbAccuring.setSelectedIndex(-1);
                DefaultTableModel dm = (DefaultTableModel) jTable1.getModel();
                dm.setRowCount(0);
                Show_DealTypes();
                jbtnAdd.setEnabled(true);
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jtbnUpdateActionPerformed

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
            java.util.logging.Logger.getLogger(DealTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DealTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DealTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DealTypes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DealTypes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JComboBox<String> jcbAccuring;
    private javax.swing.JButton jtbnExit;
    private javax.swing.JButton jtbnUpdate;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtName;
    // End of variables declaration//GEN-END:variables
}
