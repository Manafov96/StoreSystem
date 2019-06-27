/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package article;

import java.awt.Font;
import static Tools.getConnection.getConnection;
import java.io.IOException; // dont remove for INI file
import javax.swing.DefaultCellEditor;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import Tools.DropDown;
import static Tools.setValuesComboBox.setValuesComboBox;

/**
 *
 * @author Viko
 */
public class Article extends javax.swing.JFrame {

    int isSaveGroup = 0;
    int isSaveArticle = 0;

    /**
     * Creates new form Article
     */
    public Article() {
        initComponents();
        Show_Articles_In_JTable();
        setValuesMeasures();
        setValuesStatus();
        jTableArticles.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        jTableGroups.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

    }

    public boolean checkInputs() {
        if (jtxtCode.getText().isEmpty()
                || jtxtArticleNameDE.getText().isEmpty()
                || jtxtArticleNameEN.getText().isEmpty()
                || jtxtArticleNameBG.getText().isEmpty()
                || jcbStatus.getSelectedIndex() == -1
                || jtxtMinQTY.getText().isEmpty()
                || jcbMeasure.getSelectedIndex() == -1) {
            return true;
        }
        return false;
    }

    private void setValuesMeasures() {
        setValuesComboBox("select M.ID, M.SHORT_NAME_BG from N_MEASURES M", jcbMeasure, false, -1, false);
    }

    private void setValuesStatus() {
        setValuesComboBox("select YN.ID, YN.EN_TEXT from N_YESNO YN", jcbStatus, false, -1, false);
    }

    private ArrayList<Articles> getArticleGroups() {
        ArrayList<Articles> GroupList = new ArrayList<>();
        Connection con = getConnection();
        String query = "select AG.ID, AG.NAME_DE, AG.NAME_EN, AG.NAME_BG from ARTICLE_GROUPS AG order by AG.ID";

        Statement st;
        ResultSet rs;

        try {

            st = con.createStatement();
            rs = st.executeQuery(query);
            Articles groups;

            while (rs.next()) {
                groups = new Articles(rs.getInt("ID"), rs.getString("NAME_DE"), rs.getString("NAME_EN"),
                        rs.getString("NAME_BG"));
                GroupList.add(groups);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Articles.class.getName()).log(Level.SEVERE, null, ex);
        }

        return GroupList;

    }

    private void Show_Articles_In_JTable() {
        ArrayList<Articles> list = getArticleGroups();
        DefaultTableModel model = (DefaultTableModel) jTableGroups.getModel();
        model.setRowCount(0);
        Object[] row = new Object[4];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getGroupID();
            row[1] = list.get(i).getGroupNameDE();
            row[2] = list.get(i).getGroupNameEN();
            row[3] = list.get(i).getGroupNameBG();
            model.addRow(row);

        }
    }

    private ArrayList<Articles> getArticles(String ID) {
        ArrayList<Articles> ArticleList = new ArrayList<>();
        Connection con = getConnection();
        String query = "select\n"
                + "  A.ID, A.CODE, A.NAME_DE, A.NAME_EN,\n"
                + "  A.NAME_BG, A.NOTES, M.ID MEASURE_ID, M.SHORT_NAME_BG, A.MIN_QUANTITY, YN.ID STATUS, YN.EN_TEXT\n"
                + "from\n"
                + "  ARTICLES A\n"
                + "  join N_MEASURES M on A.MEASURE_ID = M.ID\n"
                + "  join N_YESNO YN on A.ACTIVE_YN = YN.ID\n"
                + "where A.ARTICLE_GROUPS_ID =" + ID;

        Statement st;
        ResultSet rs;

        try {

            st = con.createStatement();
            rs = st.executeQuery(query);
            Articles article;
            Vector<DropDown> measure = new Vector<>();
            Vector<DropDown> status = new Vector<>();

            while (rs.next()) {
                measure.addElement(new DropDown(rs.getInt("MEASURE_ID"), rs.getString("SHORT_NAME_BG")));
                status.addElement(new DropDown(rs.getInt("STATUS"), rs.getString("EN_TEXT")));
                jcbStatusTable.setModel(new DefaultComboBoxModel(status));
                jcbMeasureTable.setModel(new DefaultComboBoxModel(measure));

                article = new Articles(rs.getInt("ID"), rs.getString("CODE"), rs.getString("NAME_DE"),
                        rs.getString("NAME_EN"), rs.getString("NAME_BG"), jcbStatusTable.getModel().getSelectedItem(),
                        rs.getInt("MIN_QUANTITY"), jcbMeasureTable.getModel().getSelectedItem(), rs.getString("NOTES"));
                ArticleList.add(article);
                measure.clear();
                status.clear();
            }

        } catch (SQLException ex) {
            Logger.getLogger(Article.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ArticleList;

    }

    private void Show_Articles_In_JTableDetail() {
        ArrayList<Articles> list = getArticles(jTableGroups.getValueAt(jTableGroups.getSelectedRow(), 0).toString());
        DefaultTableModel model = (DefaultTableModel) jTableArticles.getModel();
        // clear jtable content
        model.setRowCount(0);
        Object[] row = new Object[9];
        for (int i = 0; i < list.size(); i++) {
            row[0] = list.get(i).getArticleID();
            row[1] = list.get(i).getArticleCode();
            row[2] = list.get(i).getArticleNameDE();
            row[3] = list.get(i).getArticleNameEN();
            row[4] = list.get(i).getArticleNameBG();
            row[5] = list.get(i).getNotes();
            row[6] = list.get(i).getMeasure();
            row[7] = list.get(i).getMinQTY();
            row[8] = list.get(i).getStatus();
            model.addRow(row);
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

        jcbStatusTable = new javax.swing.JComboBox<>();
        jcbMeasureTable = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableGroups = new javax.swing.JTable();
        jbtnAdd = new javax.swing.JButton();
        jbtnEdit = new javax.swing.JButton();
        jtxtID = new javax.swing.JTextField();
        jlbID = new javax.swing.JLabel();
        jtxtGroupNameDE = new javax.swing.JTextField();
        jtxtGroupNameEN = new javax.swing.JTextField();
        jlbGroupNameDE = new javax.swing.JLabel();
        jlbGroupNameEN = new javax.swing.JLabel();
        jtxtGroupNameBG = new javax.swing.JTextField();
        jlbGroupNameBG = new javax.swing.JLabel();
        jlbGroups = new javax.swing.JLabel();
        jbtnClear = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableArticles = new javax.swing.JTable();
        jbtnAddArticle = new javax.swing.JButton();
        jbtnEditArticle = new javax.swing.JButton();
        jlbArticles = new javax.swing.JLabel();
        jtxtIDArticle = new javax.swing.JTextField();
        jlbArticleID = new javax.swing.JLabel();
        jtxtCode = new javax.swing.JTextField();
        jlbCode = new javax.swing.JLabel();
        jtxtArticleNameDE = new javax.swing.JTextField();
        jlbArticleNameDE = new javax.swing.JLabel();
        jtxtArticleNameEN = new javax.swing.JTextField();
        jlbArticleNameEN = new javax.swing.JLabel();
        jtxtArticleNameBG = new javax.swing.JTextField();
        jlbArticleNameBG = new javax.swing.JLabel();
        jcbStatus = new javax.swing.JComboBox<>();
        jlbActive = new javax.swing.JLabel();
        jtxtMinQTY = new javax.swing.JTextField();
        jlbMinQTY = new javax.swing.JLabel();
        jcbMeasure = new javax.swing.JComboBox<>();
        jlbMeasure = new javax.swing.JLabel();
        jtxtNotes = new javax.swing.JTextField();
        jlbNotes = new javax.swing.JLabel();
        jbtnClearArticles = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();

        jcbMeasureTable.setToolTipText("");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Артикули");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                onExit(evt);
            }
        });

        jTableGroups.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Име (DE)", "Име (EN)", "Име (BG)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableGroups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableGroupsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableGroups);
        if (jTableGroups.getColumnModel().getColumnCount() > 0) {
            jTableGroups.getColumnModel().getColumn(0).setPreferredWidth(5);
        }

        jbtnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Add32.png"))); // NOI18N
        jbtnAdd.setText("Добави");
        jbtnAdd.setFocusPainted(false);
        jbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddActionPerformed(evt);
            }
        });

        jbtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Update32.png"))); // NOI18N
        jbtnEdit.setText("Обнови");
        jbtnEdit.setFocusPainted(false);
        jbtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditActionPerformed(evt);
            }
        });

        jtxtID.setEditable(false);

        jlbID.setText("ID:");

        jlbGroupNameDE.setText("Наименование (DE):");

        jlbGroupNameEN.setText("Наименование (EN):");

        jlbGroupNameBG.setText("Наименование (BG):");

        jlbGroups.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlbGroups.setText("Групи");

        jbtnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/clear24.png"))); // NOI18N
        jbtnClear.setText("Изчисти");
        jbtnClear.setFocusPainted(false);
        jbtnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbID))
                                .addGap(30, 30, 30)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtGroupNameDE, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbGroupNameDE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(80, 80, 80)
                                        .addComponent(jlbGroups))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbGroupNameEN)
                                    .addComponent(jtxtGroupNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(23, 23, 23)
                                        .addComponent(jlbGroupNameBG))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(jtxtGroupNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbtnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jlbGroups)
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbID)
                            .addComponent(jlbGroupNameDE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtGroupNameDE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbGroupNameEN)
                            .addComponent(jlbGroupNameBG))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtGroupNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtGroupNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jbtnEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnAdd)))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jTableArticles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Код", "Име (DE)", "Име (EN)", "Име (BG)", "Описание", "ME", "Мин.к-во", "Активен"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableArticles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableArticlesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableArticles);
        if (jTableArticles.getColumnModel().getColumnCount() > 0) {
            jTableArticles.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(1).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(2).setPreferredWidth(220);
            jTableArticles.getColumnModel().getColumn(3).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(4).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(5).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(6).setPreferredWidth(20);
            jTableArticles.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(jcbMeasureTable));
            jTableArticles.getColumnModel().getColumn(7).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(8).setPreferredWidth(40);
            jTableArticles.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(jcbStatusTable));
        }

        jbtnAddArticle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Add32.png"))); // NOI18N
        jbtnAddArticle.setText("Добави ");
        jbtnAddArticle.setFocusPainted(false);
        jbtnAddArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddArticleActionPerformed(evt);
            }
        });

        jbtnEditArticle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/Update32.png"))); // NOI18N
        jbtnEditArticle.setText("Обнови");
        jbtnEditArticle.setFocusPainted(false);
        jbtnEditArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditArticleActionPerformed(evt);
            }
        });

        jlbArticles.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jlbArticles.setText("Артикули");

        jtxtIDArticle.setEditable(false);

        jlbArticleID.setText("ID:");

        jlbCode.setText("Код:");

        jlbArticleNameDE.setText("Наименование (DE):");

        jlbArticleNameEN.setText("Наименование (EN):");

        jlbArticleNameBG.setText("Наименование (BG):");

        jlbActive.setText("Активен:");

        jlbMinQTY.setText("Мин.кол-во:");

        jlbMeasure.setText("Мерна единица:");

        jlbNotes.setText("Описание:");

        jbtnClearArticles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/article/clear24.png"))); // NOI18N
        jbtnClearArticles.setText("Изчисти");
        jbtnClearArticles.setFocusPainted(false);
        jbtnClearArticles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnClearArticlesActionPerformed(evt);
            }
        });

        jbtnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sale/Delete32.png"))); // NOI18N
        jbtnClose.setText("Изход");
        jbtnClose.setFocusPainted(false);
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtIDArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbArticleID))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jlbCode))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jlbArticleNameDE)
                                        .addGap(382, 382, 382))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jlbMeasure, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jcbMeasure, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jtxtArticleNameDE))
                                        .addGap(30, 30, 30))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(319, 319, 319)
                                .addComponent(jlbArticles)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbArticleNameEN)
                                    .addComponent(jtxtArticleNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbArticleNameBG)
                                    .addComponent(jtxtArticleNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jlbMinQTY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jtxtMinQTY, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlbNotes)
                                    .addComponent(jtxtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jlbActive)
                                        .addGap(0, 65, Short.MAX_VALUE))
                                    .addComponent(jcbStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(137, 137, 137)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtnAddArticle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnClearArticles, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnEditArticle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlbArticles)
                .addGap(38, 38, 38)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnEditArticle)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jlbCode)
                            .addComponent(jlbArticleID))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtIDArticle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jlbArticleNameDE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtxtArticleNameDE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnClearArticles, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnAddArticle))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlbArticleNameEN)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jtxtArticleNameEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jlbArticleNameBG)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtArticleNameBG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jlbMinQTY)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jtxtMinQTY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jlbMeasure)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jcbMeasure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jlbNotes)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jlbActive)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jcbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jtxtNotes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnClose)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTableGroupsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableGroupsMouseClicked
        Show_Articles_In_JTableDetail();
        jtxtID.setText(jTableGroups.getValueAt(jTableGroups.getSelectedRow(), 0).toString());
        jtxtGroupNameDE.setText(jTableGroups.getValueAt(jTableGroups.getSelectedRow(), 1).toString());
        jtxtGroupNameEN.setText(jTableGroups.getValueAt(jTableGroups.getSelectedRow(), 2).toString());
        jtxtGroupNameBG.setText(jTableGroups.getValueAt(jTableGroups.getSelectedRow(), 3).toString());
        jbtnAdd.setEnabled(false);
    }//GEN-LAST:event_jTableGroupsMouseClicked

    private void jTableArticlesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableArticlesMouseClicked
        jtxtIDArticle.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 0).toString());
        jtxtCode.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 1).toString());
        jtxtArticleNameDE.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 2).toString());
        jtxtArticleNameEN.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 3).toString());
        jtxtArticleNameBG.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 4).toString());
        jcbStatus.getModel().setSelectedItem(((DropDown) jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 8)));
        jtxtMinQTY.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 7).toString());
        jcbMeasure.getModel().setSelectedItem(((DropDown) jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 6)));
        jtxtNotes.setText(jTableArticles.getValueAt(jTableArticles.getSelectedRow(), 5).toString());
        jbtnAddArticle.setEnabled(false);
    }//GEN-LAST:event_jTableArticlesMouseClicked

    private void jbtnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddActionPerformed
        if (jtxtGroupNameDE.getText().isEmpty() || jtxtGroupNameEN.getText().isEmpty()
                || jtxtGroupNameBG.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update or insert into ARTICLE_GROUPS (NAME_DE, NAME_EN, NAME_BG)\n"
                        + "values(?,?,?) matching (NAME_DE, NAME_EN, NAME_BG)");
                ps.setString(1, jtxtGroupNameDE.getText());
                ps.setString(2, jtxtGroupNameEN.getText());
                ps.setString(3, jtxtGroupNameBG.getText());

                ps.executeUpdate();
                jtxtID.setText("");
                jtxtGroupNameDE.setText("");
                jtxtGroupNameEN.setText("");
                jtxtGroupNameBG.setText("");

                DefaultTableModel dm = (DefaultTableModel) jTableGroups.getModel();
                dm.setRowCount(0);
                Show_Articles_In_JTable();
                isSaveGroup = 1;
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnAddActionPerformed

    private void jbtnAddArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddArticleActionPerformed
        if (jTableGroups.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете група, в която да добавите артикула!");
        } else if (checkInputs()) {
            JOptionPane.showMessageDialog(this, "Моля попълнете всички полета!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update or insert into ARTICLES (CODE, ARTICLE_GROUPS_ID, NAME_DE, NAME_EN, NAME_BG, NOTES,\n"
                        + "MEASURE_ID, MIN_QUANTITY, ACTIVE_YN)\n"
                        + "values (?,?,?,?,?,?,?,?,?) matching (CODE, ARTICLE_GROUPS_ID, NAME_DE, NAME_EN, NAME_BG, NOTES,\n"
                        + "MEASURE_ID, MIN_QUANTITY, ACTIVE_YN)");
                ps.setString(1, jtxtCode.getText());
                ps.setInt(2, Integer.parseInt(jtxtID.getText()));
                ps.setString(3, jtxtArticleNameDE.getText());
                ps.setString(4, jtxtArticleNameEN.getText());
                ps.setString(5, jtxtArticleNameBG.getText());
                ps.setInt(9, (((DropDown) jcbStatus.getSelectedItem()).getId()));
                ps.setInt(8, Integer.parseInt(jtxtMinQTY.getText()));
                ps.setInt(7, (((DropDown) jcbMeasure.getSelectedItem()).getId()));
                ps.setString(6, jtxtNotes.getText());

                ps.executeUpdate();
                jtxtCode.setText("");
                jtxtIDArticle.setText("");
                jtxtArticleNameDE.setText("");
                jtxtArticleNameEN.setText("");
                jtxtArticleNameBG.setText("");
                jcbStatus.setSelectedIndex(-1);
                jtxtMinQTY.setText("");
                jcbMeasure.setSelectedIndex(-1);
                jtxtNotes.setText("");

                DefaultTableModel model = (DefaultTableModel) jTableArticles.getModel();
                model.setRowCount(0);
                Show_Articles_In_JTableDetail();
                isSaveArticle = 1;
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnAddArticleActionPerformed

    private void jbtnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearActionPerformed
        jtxtID.setText("");
        jtxtGroupNameDE.setText("");
        jtxtGroupNameEN.setText("");
        jtxtGroupNameBG.setText("");
        DefaultTableModel model = (DefaultTableModel) jTableArticles.getModel();
        model.setRowCount(0);
        jtxtCode.setText("");
        jtxtIDArticle.setText("");
        jtxtArticleNameDE.setText("");
        jtxtArticleNameEN.setText("");
        jtxtArticleNameBG.setText("");
        jcbStatus.setSelectedIndex(-1);
        jtxtMinQTY.setText("");
        jcbMeasure.setSelectedIndex(-1);
        jtxtNotes.setText("");
        jbtnAdd.setEnabled(true);
    }//GEN-LAST:event_jbtnClearActionPerformed

    private void jbtnClearArticlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnClearArticlesActionPerformed
        jtxtCode.setText("");
        jtxtIDArticle.setText("");
        jtxtArticleNameDE.setText("");
        jtxtArticleNameEN.setText("");
        jtxtArticleNameBG.setText("");
        jcbStatus.setSelectedIndex(-1);
        jtxtMinQTY.setText("");
        jcbMeasure.setSelectedIndex(-1);
        jtxtNotes.setText("");
        jbtnAddArticle.setEnabled(true);
    }//GEN-LAST:event_jbtnClearArticlesActionPerformed

    private void jbtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditActionPerformed
        if (jTableGroups.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете групата, която искате да редактирате!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update ARTICLE_GROUPS\n"
                        + "SET NAME_DE = ?,\n"
                        + "NAME_EN = ?,\n"
                        + "NAME_BG = ?\n"
                        + "where ID = ?");
                ps.setString(1, jtxtGroupNameDE.getText());
                ps.setString(2, jtxtGroupNameEN.getText());
                ps.setString(3, jtxtGroupNameBG.getText());
                ps.setString(4, jtxtID.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно направихте промени!");
                jtxtID.setText("");
                jtxtGroupNameDE.setText("");
                jtxtGroupNameEN.setText("");
                jtxtGroupNameBG.setText("");

                DefaultTableModel dm = (DefaultTableModel) jTableGroups.getModel();
                dm.setRowCount(0);
                Show_Articles_In_JTable();
                DefaultTableModel model = (DefaultTableModel) jTableArticles.getModel();
                model.setRowCount(0);
                jbtnAdd.setEnabled(true);
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnEditActionPerformed

    private void jbtnEditArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditArticleActionPerformed
        if (jTableArticles.getSelectedRow() == -1 || jtxtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Моля изберете артикула, който искате да редактирате!");
        } else {
            try {
                Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement("update ARTICLES\n"
                        + "set CODE = ?,\n"
                        + "    ARTICLE_GROUPS_ID = ?,\n"
                        + "    NAME_DE = ?,\n"
                        + "    NAME_EN = ?,\n"
                        + "    NAME_BG = ?,\n"
                        + "    NOTES = ?,\n"
                        + "    MEASURE_ID = ?,\n"
                        + "    MIN_QUANTITY = ?,\n"
                        + "    ACTIVE_YN = ?\n"
                        + "where ID = ?");
                ps.setString(1, jtxtCode.getText());
                ps.setInt(2, Integer.parseInt(jtxtID.getText()));
                ps.setString(3, jtxtArticleNameDE.getText());
                ps.setString(4, jtxtArticleNameEN.getText());
                ps.setString(5, jtxtArticleNameBG.getText());
                ps.setInt(9, (((DropDown) jcbStatus.getSelectedItem()).getId()));
                ps.setInt(8, Integer.parseInt(jtxtMinQTY.getText()));
                ps.setInt(7, (((DropDown) jcbMeasure.getSelectedItem()).getId()));
                ps.setString(6, jtxtNotes.getText());
                ps.setString(10, jtxtIDArticle.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Успешно направихте промени!");
                jtxtCode.setText("");
                jtxtIDArticle.setText("");
                jtxtArticleNameDE.setText("");
                jtxtArticleNameEN.setText("");
                jtxtArticleNameBG.setText("");
                jcbStatus.setSelectedIndex(-1);
                jtxtMinQTY.setText("");
                jcbMeasure.setSelectedIndex(-1);
                jtxtNotes.setText("");

                DefaultTableModel model = (DefaultTableModel) jTableArticles.getModel();
                model.setRowCount(0);
                Show_Articles_In_JTableDetail();
                jbtnAddArticle.setEnabled(true);
            } catch (HeadlessException | SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }//GEN-LAST:event_jbtnEditArticleActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void onExit(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onExit
        setVisible(false);
    }//GEN-LAST:event_onExit

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
            java.util.logging.Logger.getLogger(Article.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Article.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Article.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Article.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Article().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableArticles;
    private javax.swing.JTable jTableGroups;
    private javax.swing.JButton jbtnAdd;
    private javax.swing.JButton jbtnAddArticle;
    private javax.swing.JButton jbtnClear;
    private javax.swing.JButton jbtnClearArticles;
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnEdit;
    private javax.swing.JButton jbtnEditArticle;
    private javax.swing.JComboBox<String> jcbMeasure;
    private javax.swing.JComboBox<String> jcbMeasureTable;
    private javax.swing.JComboBox<String> jcbStatus;
    private javax.swing.JComboBox<String> jcbStatusTable;
    private javax.swing.JLabel jlbActive;
    private javax.swing.JLabel jlbArticleID;
    private javax.swing.JLabel jlbArticleNameBG;
    private javax.swing.JLabel jlbArticleNameDE;
    private javax.swing.JLabel jlbArticleNameEN;
    private javax.swing.JLabel jlbArticles;
    private javax.swing.JLabel jlbCode;
    private javax.swing.JLabel jlbGroupNameBG;
    private javax.swing.JLabel jlbGroupNameDE;
    private javax.swing.JLabel jlbGroupNameEN;
    private javax.swing.JLabel jlbGroups;
    private javax.swing.JLabel jlbID;
    private javax.swing.JLabel jlbMeasure;
    private javax.swing.JLabel jlbMinQTY;
    private javax.swing.JLabel jlbNotes;
    private javax.swing.JTextField jtxtArticleNameBG;
    private javax.swing.JTextField jtxtArticleNameDE;
    private javax.swing.JTextField jtxtArticleNameEN;
    private javax.swing.JTextField jtxtCode;
    private javax.swing.JTextField jtxtGroupNameBG;
    private javax.swing.JTextField jtxtGroupNameDE;
    private javax.swing.JTextField jtxtGroupNameEN;
    private javax.swing.JTextField jtxtID;
    private javax.swing.JTextField jtxtIDArticle;
    private javax.swing.JTextField jtxtMinQTY;
    private javax.swing.JTextField jtxtNotes;
    // End of variables declaration//GEN-END:variables
}
