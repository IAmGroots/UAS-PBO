/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package uas.pbo;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Hadiseptian
 */
public class App extends javax.swing.JFrame {

    /**
     * Creates new form App
     */
    static String username = "";
    static String role = "";
    String update_by = "";
    
    public App(String thisUsername, String thisRole) {
        initComponents();
        Connect();
        
        username = thisUsername;
        role = thisRole;
        update_by = username + " (" + role + ")";
        
        txtIdSepatu.setVisible(false);
        txtIdAdmin.setVisible(false);
        groupRadioButton.add(radioMale);
        groupRadioButton.add(radioFemale);
        groupRadioButton.add(radioUnisex);
        setInputData(false);
        setButton(true, true, true, false, false);
        setInputDataAdmin(false);
        setButtonAdmin(true, true, true, false, false);
        showTableSepatu();
    }
    
    String action = "";
    String actionAdmin = "";
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    Statement st;
    DefaultTableModel tblSepatuSementara;
    
    public void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost/db_uas_pbo", "root", "");
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setButton(boolean add, boolean edit, boolean delete, boolean save, boolean cancel) {
        btnAdd.setEnabled(add);
        btnEdit.setEnabled(edit);
        btnDelete.setEnabled(delete);
        btnSave.setEnabled(save);
        btnCancel.setEnabled(cancel);
    }
    
    public void setButtonAdmin(boolean add, boolean edit, boolean delete, boolean save, boolean cancel) {
        btnAddAdmin.setEnabled(add);
        btnEditAdmin.setEnabled(edit);
        btnDeleteAdmin.setEnabled(delete);
        btnSaveAdmin.setEnabled(save);
        btnCancelAdmin.setEnabled(cancel);
    }
    
    public void setInputData(boolean action) {
        txtMerek.setEnabled(action);
        txtModel.setEnabled(action);
        txtHarga.setEnabled(action);
        radioMale.setEnabled(action);
        radioFemale.setEnabled(action);
        radioUnisex.setEnabled(action);
        cbx39.setEnabled(action);
        cbx40.setEnabled(action);
        cbx41.setEnabled(action);
        cbx42.setEnabled(action);
        cbx43.setEnabled(action);
        cmbWarna.setEnabled(action);
    }
    
    public void setInputDataAdmin(boolean action) {
        txtIdAdmin.setEnabled(action);
        txtUsernameAdmin.setEnabled(action);
        txtPasswordAdmin.setEnabled(action);
        cmbRoleAdmin.setEnabled(action);
    }
    
    public void clearData(){
        txtIdSepatu.setText("");
        txtMerek.setText("");
        txtModel.setText("");
        txtHarga.setText("");
        groupRadioButton.clearSelection();
        cbx39.setSelected(false);
        cbx40.setSelected(false);
        cbx41.setSelected(false);
        cbx42.setSelected(false);
        cbx43.setSelected(false);
        cmbWarna.setSelectedIndex(0);
    }
    
    public void clearDataAdmin() {
        txtIdAdmin.setText("");
        txtUsernameAdmin.setText("");
        txtPasswordAdmin.setText("");
        cmbRoleAdmin.setSelectedIndex(0);
    }
    
    public boolean checkInputData(){
        if (txtMerek.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Merek Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            txtMerek.requestFocusInWindow();
            return false;
        }
        else if (txtModel.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Model Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            txtModel.requestFocusInWindow();
            return false;
        }
        else if (txtHarga.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Harga Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            txtHarga.requestFocusInWindow();
            return false;
        }
        else if (!(radioMale.isSelected() || radioFemale.isSelected() ||radioUnisex.isSelected())) {
            JOptionPane.showMessageDialog(null, "Rekomendasi Pengguna Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        else if (!(cbx39.isSelected() || cbx40.isSelected() || cbx41.isSelected() || cbx42.isSelected() || cbx43.isSelected())) {
            JOptionPane.showMessageDialog(null, "Ukuran Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        else if (cmbWarna.getSelectedItem().equals("- Silahkan Pilih -")) {
            JOptionPane.showMessageDialog(null, "Warna Sepatu Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            cmbWarna.requestFocusInWindow();
            return false;
        }
        return true;
    }
    
    public boolean checkInputDataAdmin(){
        if (txtUsernameAdmin.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Username Admin Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            txtUsernameAdmin.requestFocusInWindow();
            return false;
        }
        else if (txtPasswordAdmin.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Password Admin Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            txtPasswordAdmin.requestFocusInWindow();
            return false;
        }
        else if (cmbRoleAdmin.getSelectedItem().equals("- Silahkan Pilih -")) {
            JOptionPane.showMessageDialog(null, "Role Admin Wajib Diisi", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            cmbRoleAdmin.requestFocusInWindow();
            return false;
        }
        return true;
    }
    
    public void showTableSepatu() {
        tblSepatuSementara = new DefaultTableModel(new String[]{"ID Sepatu","Merek", "Model", "Ukuran", "Warna", "Harga", "Rekomendasi Pengguna", "Last Update", "Update By"}, 0);
        try {
            String sql = "SELECT * FROM tbsepatu";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            
            while (rs.next()) {
                tblSepatuSementara.addRow(new Object[] {rs.getString("id_sepatu"),
                                               rs.getString("merek"),
                                                rs.getString("model"),
                                                rs.getString("ukuran"),
                                                rs.getString("warna"),
                                                rs.getString("harga"),
                                                rs.getString("rekomendasi_pengguna"),
                                                rs.getString("update_at"),
                                                rs.getString("update_by")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblSepatu.setModel(tblSepatuSementara);
    }
    
    public void showTableSearching() {
        tblSepatuSementara = new DefaultTableModel(new String[]{"ID Sepatu","Merek", "Model", "Ukuran", "Warna", "Harga", "Rekomendasi Pengguna", "Last Update", "Update By"}, 0);
        try {
            String sql = "SELECT * FROM tbsepatu";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            
            while (rs.next()) {
                tblSepatuSementara.addRow(new Object[] {rs.getString("id_sepatu"),
                                               rs.getString("merek"),
                                                rs.getString("model"),
                                                rs.getString("ukuran"),
                                                rs.getString("warna"),
                                                rs.getString("harga"),
                                                rs.getString("rekomendasi_pengguna"),
                                                rs.getString("update_at"),
                                                rs.getString("update_by")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblResult.setModel(tblSepatuSementara);
    }
    
    public void showTableResult() {
        tblSepatuSementara = new DefaultTableModel(new String[]{"ID Sepatu","Merek", "Model", "Ukuran", "Warna", "Harga", "Rekomendasi Pengguna", "Last Update", "Update By"}, 0);
        try {
            String text = txtSearch.getText();
            String sql = "SELECT * FROM tbsepatu WHERE merek LIKE '%" + text + "%' OR model LIKE '%" + text + "%' OR ukuran LIKE '%" + text + "%' OR warna LIKE '%" + text + "%' OR harga LIKE '%" + text + "%'";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            
            while (rs.next()) {
                tblSepatuSementara.addRow(new Object[] {rs.getString("id_sepatu"),
                                               rs.getString("merek"),
                                                rs.getString("model"),
                                                rs.getString("ukuran"),
                                                rs.getString("warna"),
                                                rs.getString("harga"),
                                                rs.getString("rekomendasi_pengguna"),
                                                rs.getString("update_at"),
                                                rs.getString("update_by")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblResult.setModel(tblSepatuSementara);
    }
    
    public void showTableAdmin() {
        tblSepatuSementara = new DefaultTableModel(new String[]{"UID","Username", "Password", "Role"}, 0);
        try {
            String sql = "SELECT * FROM tbuser";
            st = con.createStatement();
            rs = st.executeQuery(sql);
            
            while (rs.next()) {
                tblSepatuSementara.addRow(new Object[] {rs.getString("uid"),
                                               rs.getString("username"),
                                                rs.getString("password"),
                                                rs.getString("role")});
            }
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblAdmin.setModel(tblSepatuSementara);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupRadioButton = new javax.swing.ButtonGroup();
        menuPanel = new javax.swing.JPanel();
        btnSearching = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        Logo = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tabPanel = new javax.swing.JTabbedPane();
        panelDashboard = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMerek = new javax.swing.JTextField();
        txtModel = new javax.swing.JTextField();
        txtHarga = new javax.swing.JTextField();
        cmbWarna = new javax.swing.JComboBox<>();
        radioMale = new javax.swing.JRadioButton();
        radioUnisex = new javax.swing.JRadioButton();
        radioFemale = new javax.swing.JRadioButton();
        cbx39 = new javax.swing.JCheckBox();
        cbx41 = new javax.swing.JCheckBox();
        cbx40 = new javax.swing.JCheckBox();
        cbx42 = new javax.swing.JCheckBox();
        cbx43 = new javax.swing.JCheckBox();
        txtIdSepatu = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSepatu = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        panelSearching = new javax.swing.JPanel();
        btnSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblResult = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        panelAdmin = new javax.swing.JPanel();
        txtIdAdmin = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUsernameAdmin = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPasswordAdmin = new javax.swing.JTextField();
        cmbRoleAdmin = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnAddAdmin = new javax.swing.JButton();
        btnEditAdmin = new javax.swing.JButton();
        btnDeleteAdmin = new javax.swing.JButton();
        btnSaveAdmin = new javax.swing.JButton();
        btnCancelAdmin = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAdmin = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        menuPanel.setBackground(new java.awt.Color(255, 255, 255));

        btnSearching.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnSearching.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/SearchButton.png"))); // NOI18N
        btnSearching.setText("Searching");
        btnSearching.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearching.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSearchingMouseClicked(evt);
            }
        });

        btnAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/User.png"))); // NOI18N
        btnAdmin.setText("Admin");
        btnAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAdminMouseClicked(evt);
            }
        });

        btnDashboard.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Home.png"))); // NOI18N
        btnDashboard.setText("Dashboard");
        btnDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDashboardMouseClicked(evt);
            }
        });

        Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Logo.png"))); // NOI18N
        Logo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LogoMouseClicked(evt);
            }
        });

        btnLogout.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Exit.png"))); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnLogoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout menuPanelLayout = new javax.swing.GroupLayout(menuPanel);
        menuPanel.setLayout(menuPanelLayout);
        menuPanelLayout.setHorizontalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSearching, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Logo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        menuPanelLayout.setVerticalGroup(
            menuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPanelLayout.createSequentialGroup()
                .addComponent(Logo, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDashboard)
                .addGap(18, 18, 18)
                .addComponent(btnSearching)
                .addGap(18, 18, 18)
                .addComponent(btnAdmin)
                .addGap(18, 18, 18)
                .addComponent(btnLogout)
                .addContainerGap(336, Short.MAX_VALUE))
        );

        getContentPane().add(menuPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 768));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Merek");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Model");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Ukuran");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Warna");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("Harga");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Rekomendasi Pengguna");

        txtMerek.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtModel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtHarga.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        cmbWarna.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbWarna.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Silahkan Pilih -", "Black", "White", "Blue", "Red", "Pink", "Yellow" }));
        cmbWarna.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        radioMale.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        radioMale.setText("Male");
        radioMale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        radioUnisex.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        radioUnisex.setText("Unisex");
        radioUnisex.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        radioFemale.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        radioFemale.setText("Female");
        radioFemale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbx39.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbx39.setText("39");
        cbx39.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbx41.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbx41.setText("41");
        cbx41.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbx40.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbx40.setText("40");
        cbx40.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbx42.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbx42.setText("42");
        cbx42.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        cbx43.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbx43.setText("43");
        cbx43.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        txtIdSepatu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        tblSepatu.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblSepatu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSepatu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tblSepatu.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblSepatu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSepatuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSepatu);

        btnAdd.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Add.png"))); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddMouseClicked(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Edit.png"))); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditMouseClicked(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Delete.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelMouseClicked(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Save.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSaveMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelDashboardLayout = new javax.swing.GroupLayout(panelDashboard);
        panelDashboard.setLayout(panelDashboardLayout);
        panelDashboardLayout.setHorizontalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDashboardLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelDashboardLayout.createSequentialGroup()
                        .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelDashboardLayout.createSequentialGroup()
                                .addComponent(txtIdSepatu, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panelDashboardLayout.createSequentialGroup()
                                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelDashboardLayout.createSequentialGroup()
                                        .addComponent(txtMerek, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel3)
                                        .addGap(224, 224, 224))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelDashboardLayout.createSequentialGroup()
                                        .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelDashboardLayout.createSequentialGroup()
                                                .addComponent(radioMale)
                                                .addGap(18, 18, 18)
                                                .addComponent(radioFemale)
                                                .addGap(18, 18, 18)
                                                .addComponent(radioUnisex)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(cmbWarna, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(panelDashboardLayout.createSequentialGroup()
                                                .addComponent(cbx39)
                                                .addGap(18, 18, 18)
                                                .addComponent(cbx40)
                                                .addGap(18, 18, 18)
                                                .addComponent(cbx41)
                                                .addGap(18, 18, 18)
                                                .addComponent(cbx42)
                                                .addGap(18, 18, 18)
                                                .addComponent(cbx43)))))
                                .addGap(94, 94, 94))))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDashboardLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1095, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        panelDashboardLayout.setVerticalGroup(
            panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDashboardLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(txtIdSepatu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMerek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtModel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbx39)
                    .addComponent(cbx43)
                    .addComponent(cbx42)
                    .addComponent(cbx41)
                    .addComponent(cbx40))
                .addGap(18, 18, 18)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbWarna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(radioMale)
                        .addComponent(radioFemale)
                        .addComponent(radioUnisex)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnCancel)
                    .addComponent(btnSave))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        tabPanel.addTab("tab1", panelDashboard);

        btnSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Search.png"))); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSearchMouseClicked(evt);
            }
        });

        tblResult.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblResult.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblResult.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tblResult.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane2.setViewportView(tblResult);

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Refresh.png"))); // NOI18N
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelSearchingLayout = new javax.swing.GroupLayout(panelSearching);
        panelSearching.setLayout(panelSearchingLayout);
        panelSearchingLayout.setHorizontalGroup(
            panelSearchingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchingLayout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(290, 290, 290))
            .addGroup(panelSearchingLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSearchingLayout.setVerticalGroup(
            panelSearchingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSearchingLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(panelSearchingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        tabPanel.addTab("tab2", panelSearching);

        txtIdAdmin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Username");

        txtUsernameAdmin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setText("Password");

        txtPasswordAdmin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        cmbRoleAdmin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cmbRoleAdmin.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "- Silahkan Pilih -", "superadmin", "admin" }));
        cmbRoleAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel9.setText("Role");

        btnAddAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnAddAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Add.png"))); // NOI18N
        btnAddAdmin.setText("Add");
        btnAddAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddAdminMouseClicked(evt);
            }
        });

        btnEditAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnEditAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Edit.png"))); // NOI18N
        btnEditAdmin.setText("Edit");
        btnEditAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditAdminMouseClicked(evt);
            }
        });

        btnDeleteAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnDeleteAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Delete.png"))); // NOI18N
        btnDeleteAdmin.setText("Delete");
        btnDeleteAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeleteAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteAdminMouseClicked(evt);
            }
        });

        btnSaveAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnSaveAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Save.png"))); // NOI18N
        btnSaveAdmin.setText("Save");
        btnSaveAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSaveAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSaveAdminMouseClicked(evt);
            }
        });

        btnCancelAdmin.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        btnCancelAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uas/pbo/assets/Cancel.png"))); // NOI18N
        btnCancelAdmin.setText("Cancel");
        btnCancelAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelAdminMouseClicked(evt);
            }
        });

        tblAdmin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblAdmin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblAdmin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tblAdmin.setRowHeight(16);
        tblAdmin.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tblAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAdminMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblAdmin);

        javax.swing.GroupLayout panelAdminLayout = new javax.swing.GroupLayout(panelAdmin);
        panelAdmin.setLayout(panelAdminLayout);
        panelAdminLayout.setHorizontalGroup(
            panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdminLayout.createSequentialGroup()
                .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAdminLayout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(18, 18, 18)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPasswordAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbRoleAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtUsernameAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtIdAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelAdminLayout.createSequentialGroup()
                                .addComponent(btnEditAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnAddAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelAdminLayout.createSequentialGroup()
                                .addComponent(btnSaveAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelAdminLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1095, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        panelAdminLayout.setVerticalGroup(
            panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdminLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelAdminLayout.createSequentialGroup()
                        .addComponent(btnAddAdmin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditAdmin)
                            .addComponent(btnDeleteAdmin, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSaveAdmin)
                            .addComponent(btnCancelAdmin)))
                    .addGroup(panelAdminLayout.createSequentialGroup()
                        .addComponent(txtIdAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUsernameAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPasswordAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addGroup(panelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbRoleAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        tabPanel.addTab("tab3", panelAdmin);

        getContentPane().add(tabPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(224, -50, 1140, 820));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDashboardMouseClicked
        tabPanel.setSelectedIndex(0);
        showTableSepatu();
    }//GEN-LAST:event_btnDashboardMouseClicked

    private void btnSearchingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSearchingMouseClicked
        tabPanel.setSelectedIndex(1);
        showTableSearching();
    }//GEN-LAST:event_btnSearchingMouseClicked

    private void btnAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAdminMouseClicked
        if (role.equals("superadmin")) {
            tabPanel.setSelectedIndex(2);
            showTableAdmin();
        }
        else if (role.equals("admin")) {
            JOptionPane.showMessageDialog(null, "Akses Ditolak, Role Anda Adalah Admin Bukan SuperAdmin", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            tabPanel.setSelectedIndex(0);
            showTableSepatu();
        }
        
    }//GEN-LAST:event_btnAdminMouseClicked

    private void btnAddMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddMouseClicked
        clearData();
        action = "ADD";
        setInputData(true);
        setButton(false, false, false, true, true);
        txtMerek.requestFocusInWindow();
    }//GEN-LAST:event_btnAddMouseClicked

    private void btnEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseClicked
        if (txtIdSepatu.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih sepatu terlebih dahulu", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        action = "EDIT";
        setInputData(true);
        setButton(false, false, false, true, true);
        txtMerek.requestFocusInWindow();
    }//GEN-LAST:event_btnEditMouseClicked

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        try {
            String idSepatu = txtIdSepatu.getText();
            
            //DELETE FROM `tbsepatu` WHERE `tbsepatu`.`id_sepatu` = 3;
            String sql = "DELETE FROM tbsepatu WHERE tbsepatu.id_sepatu = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, idSepatu);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        clearData();
        showTableSepatu();
    }//GEN-LAST:event_btnDeleteMouseClicked

    private void btnCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseClicked
        if (action.equals("ADD")) {
            clearData();
        }
        setInputData(false);
        setButton(true, true, true, false, false);
    }//GEN-LAST:event_btnCancelMouseClicked

    private void btnSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSaveMouseClicked
        if (!checkInputData()){
            return;
        }
        
        String merek, model, ukuran = "", warna = "", harga, rekomendasi_pengguna = "";
        
        merek = txtMerek.getText();
        model = txtModel.getText();
        harga = txtHarga.getText();
        
        if (cbx39.isSelected()){
            ukuran += "39 ";
        }
        if (cbx40.isSelected()){
            ukuran += "40 ";
        }
        if (cbx41.isSelected()){
            ukuran += "41 ";
        }
        if (cbx42.isSelected()){
            ukuran += "42 ";
        }
        if (cbx43.isSelected()){
            ukuran += "43 ";
        }
        
        warna = cmbWarna.getSelectedItem().toString();
        if (radioMale.isSelected()){
            rekomendasi_pengguna = "Male";
        }
        else if (radioFemale.isSelected()){
            rekomendasi_pengguna = "Female";
        }
        else if (radioUnisex.isSelected()){
            rekomendasi_pengguna = "Unisex";
        }
       
        if (action.equals("ADD")){
            try {
                //INSERT INTO `tbsepatu` (`id_sepatu`, `merek`, `model`, `ukuran`, `warna`, `harga`, `rekomendasi_pengguna`, `update_at`, `update_by`) VALUES ('', 'Adidas', '01', '39 40 41', 'Black', '100000', 'Male', '2023-06-10 16:21:29.000000', 'admin');
                String sql = "INSERT INTO tbsepatu (id_sepatu, merek, model, ukuran, warna, harga, rekomendasi_pengguna, update_at, update_by) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, merek);
                pst.setString(2, model);
                pst.setString(3, ukuran);
                pst.setString(4, warna);
                pst.setString(5, harga);
                pst.setString(6, rekomendasi_pengguna);
                pst.setObject(7, LocalDateTime.now());
                pst.setString(8, update_by);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (action.equals("EDIT")){
            try {
                String idSepatu = txtIdSepatu.getText();
                
                //UPDATE `tbsepatu` SET `merek` = 'Nike', `model` = '05', `ukuran` = '40 42', `warna` = 'Pink', `harga` = '200000', `rekomendasi_pengguna` = 'Unisex', `update_at` = '2023-06-08 16:21:29', `update_by` = 'superadmin' WHERE `tbsepatu`.`id_sepatu` = 3;
                String sql = "UPDATE tbsepatu SET merek = ?, model = ?, ukuran = ?, warna = ?, harga = ?, rekomendasi_pengguna = ?, update_at = ?, update_by = ? WHERE tbsepatu.id_sepatu = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, merek);
                pst.setString(2, model);
                pst.setString(3, ukuran);
                pst.setString(4, warna);
                pst.setString(5, harga);
                pst.setString(6, rekomendasi_pengguna);
                pst.setObject(7, LocalDateTime.now());
                pst.setString(8, update_by);
                pst.setString(9, idSepatu);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        clearData();
        setInputData(false);
        setButton(true, true, true, false, false);
        showTableSepatu();
    }//GEN-LAST:event_btnSaveMouseClicked

    private void LogoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LogoMouseClicked
        tabPanel.setSelectedIndex(0);
    }//GEN-LAST:event_LogoMouseClicked

    private void tblSepatuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSepatuMouseClicked
        clearData();
        int row = tblSepatu.getSelectedRow();
        txtIdSepatu.setText(tblSepatu.getValueAt(row, 0).toString());
        txtMerek.setText(tblSepatu.getValueAt(row, 1).toString());
        txtModel.setText(tblSepatu.getValueAt(row, 2).toString());
        String[] uk = tblSepatu.getValueAt(row, 3).toString().split(" ");
        for (String angka : uk) {
            if (angka.equals("39")) {
                cbx39.setSelected(true);
            }
            if (angka.equals("40")) {
                cbx40.setSelected(true);
            }
            if (angka.equals("41")) {
                cbx41.setSelected(true);
            }
            if (angka.equals("42")) {
                cbx42.setSelected(true);
            }
            if (angka.equals("43")) {
                cbx43.setSelected(true);
            }
        }
        cmbWarna.setSelectedItem(tblSepatu.getValueAt(row, 4).toString());
        txtHarga.setText(tblSepatu.getValueAt(row, 5).toString());
        String rk = tblSepatu.getValueAt(row, 6).toString();
        if (rk.equals("Male")) {
            radioMale.setSelected(true);
        }
        else if (rk.equals("Female")) {
            radioFemale.setSelected(true);
        }
        else if (rk.equals("Unisex")) {
            radioUnisex.setSelected(true);
        }
    }//GEN-LAST:event_tblSepatuMouseClicked

    private void btnSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSearchMouseClicked
        showTableResult();
        txtSearch.setText("");
    }//GEN-LAST:event_btnSearchMouseClicked

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        txtSearch.setText("");
        showTableSearching();
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void btnAddAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddAdminMouseClicked
        clearDataAdmin();
        actionAdmin = "ADD";
        setInputDataAdmin(true);
        setButtonAdmin(false, false, false, true, true);
        txtUsernameAdmin.requestFocusInWindow();
    }//GEN-LAST:event_btnAddAdminMouseClicked

    private void btnEditAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditAdminMouseClicked
        if (txtIdAdmin.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih admin terlebih dahulu", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        actionAdmin = "EDIT";
        setInputDataAdmin(true);
        setButtonAdmin(false, false, false, true, true);
        txtUsernameAdmin.requestFocusInWindow();
    }//GEN-LAST:event_btnEditAdminMouseClicked

    private void btnDeleteAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteAdminMouseClicked
        try {
            String idAdmin = txtIdAdmin.getText();

            String sql = "DELETE FROM tbuser WHERE tbuser.uid = ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, idAdmin);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        clearDataAdmin();
        showTableAdmin();
    }//GEN-LAST:event_btnDeleteAdminMouseClicked

    private void btnSaveAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSaveAdminMouseClicked
        if (!checkInputDataAdmin()){
            return;
        }
        
        String username, password, role;
        
        username = txtUsernameAdmin.getText();
        password = txtPasswordAdmin.getText();
        role = cmbRoleAdmin.getSelectedItem().toString();
       
        if (actionAdmin.equals("ADD")){
            try {
                String sqls = "INSERT INTO tbuser (uid, username, password, role) VALUES (NULL, ?, ?, ?)";
                pst = con.prepareStatement(sqls);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "MASUK", "Pemberitahuan", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (actionAdmin.equals("EDIT")){
            try {
                String idAdmin = txtIdAdmin.getText();
                String sqls = "UPDATE tbuser SET username = ?, password = ?, role = ? WHERE tbuser.uid = ?";
                pst = con.prepareStatement(sqls);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);
                pst.setString(4, idAdmin);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        clearDataAdmin();
        setInputDataAdmin(false);
        setButtonAdmin(true, true, true, false, false);
        showTableAdmin();
    }//GEN-LAST:event_btnSaveAdminMouseClicked

    private void btnCancelAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelAdminMouseClicked
        if (actionAdmin.equals("ADD")) {
            clearDataAdmin();
        }
        setInputDataAdmin(false);
        setButtonAdmin(true, true, true, false, false);
    }//GEN-LAST:event_btnCancelAdminMouseClicked

    private void tblAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAdminMouseClicked
        clearDataAdmin();
        int row = tblAdmin.getSelectedRow();
        txtIdAdmin.setText(tblAdmin.getValueAt(row, 0).toString());
        txtUsernameAdmin.setText(tblAdmin.getValueAt(row, 1).toString());
        txtPasswordAdmin.setText(tblAdmin.getValueAt(row, 2).toString());
        cmbRoleAdmin.setSelectedItem(tblAdmin.getValueAt(row, 3).toString());
    }//GEN-LAST:event_tblAdminMouseClicked

    private void btnLogoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLogoutMouseClicked
        Login login = new Login();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutMouseClicked

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
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new App(username, role).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Logo;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddAdmin;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancelAdmin;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteAdmin;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditAdmin;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveAdmin;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearching;
    private javax.swing.JCheckBox cbx39;
    private javax.swing.JCheckBox cbx40;
    private javax.swing.JCheckBox cbx41;
    private javax.swing.JCheckBox cbx42;
    private javax.swing.JCheckBox cbx43;
    private javax.swing.JComboBox<String> cmbRoleAdmin;
    private javax.swing.JComboBox<String> cmbWarna;
    private javax.swing.ButtonGroup groupRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel menuPanel;
    private javax.swing.JPanel panelAdmin;
    private javax.swing.JPanel panelDashboard;
    private javax.swing.JPanel panelSearching;
    private javax.swing.JRadioButton radioFemale;
    private javax.swing.JRadioButton radioMale;
    private javax.swing.JRadioButton radioUnisex;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTable tblAdmin;
    private javax.swing.JTable tblResult;
    private javax.swing.JTable tblSepatu;
    private javax.swing.JTextField txtHarga;
    private javax.swing.JTextField txtIdAdmin;
    private javax.swing.JTextField txtIdSepatu;
    private javax.swing.JTextField txtMerek;
    private javax.swing.JTextField txtModel;
    private javax.swing.JTextField txtPasswordAdmin;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUsernameAdmin;
    // End of variables declaration//GEN-END:variables
}
