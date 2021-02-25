/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Main.*;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author hieul
 */
public class pos extends javax.swing.JFrame implements  Runnable, ThreadFactory{

    /**
     * Creates new form pos
     */
    public pos() {
        initComponents();
        connect();
        initWebcam();
        nhanvien();
    }

    Connection con;
    PreparedStatement pst;
    PreparedStatement pst1;
    PreparedStatement pst2;
    PreparedStatement pst3;
    ResultSet rs;
    DefaultTableModel model = new DefaultTableModel();
    
    private WebcamPanel panel = null;
    private Webcam webcam = null;
    
    private static final long  serialVersionUID = 644148915740838178L;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    
    public void connect() 
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/vinmart","root","");
        } 
        catch (ClassNotFoundException ex) {
            Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void hoadon()
    {
        String tongtien = txt_tongtien.getText();
        String pay = txtpay.getText();
        String due = txtdue.getText();
        long millis=System.currentTimeMillis();  
        java.sql.Date date=new java.sql.Date(millis);
        String manhanvien = "";
        
        String hoten = (String) nhanvien.getSelectedItem();
        hoten = hoten.replaceAll(" ", "%20");
        String urlLink = "http://localhost:88/vinmart/API/getEmployee.php?hoten="+hoten;
            URL url ;
            try {
                url = new URL(urlLink);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "" ;
                StringBuffer conntent = new StringBuffer();
                while((line = br.readLine()) != null)
                {
                    conntent.append(line);
                }
                JSONObject manv = new JSONObject(conntent.toString());
                for (int i = 0; i < manv.length(); i++) {
                    Object [] obj = new Object[1];
                    obj[0]=manv.getJSONObject(""+i+"").get("manhanvien").toString();
                    
                    manhanvien = (String) obj[0];
                }
                br.close();
            } 
            catch (Exception e) {
            }
        
            String mahoadon = "";
            String urlLink1 = "http://localhost:88/vinmart/API/insertHD.php?manhanvien="+manhanvien+"&date="+date+"&tongtien="+tongtien+"&pay="+pay+"&due="+due;
            URL url1 ;
            try {
                url1 = new URL(urlLink1);
                HttpURLConnection con = (HttpURLConnection) url1.openConnection();
                con.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "" ;
                StringBuffer conntent = new StringBuffer();
                while((line = br.readLine()) != null)
                {
                    conntent.append(line);
                }
                mahoadon = conntent.toString();
                br.close();
            } 
            catch (Exception e) {
            }
            
            String mahanghoa = "";
            String soluong = "";
            String dongia = "";
            int thanh_tien = 0;
            
            for(int i=0;i<jTable1.getRowCount();i++)
            {
                mahanghoa = (String)jTable1.getValueAt(i, 0);
                
                dongia = (String)jTable1.getValueAt(i, 2);
                soluong = (String)jTable1.getValueAt(i, 3);
                thanh_tien = (int)jTable1.getValueAt(i, 4);
                String urlLink2 = "http://localhost:88/vinmart/API/insertCTHD.php?mahoadon="+mahoadon+"&mahanghoa="+mahanghoa+"&dongia="+dongia+"&soluong="+soluong+"&thanh_tien="+thanh_tien;
                URL url2 ;
                try {
                    url2 = new URL(urlLink2);
                    HttpURLConnection con = (HttpURLConnection) url2.openConnection();
                    con.setRequestMethod("GET");
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    br.close();
                } 
                catch (Exception e) {
                }
            }
            
            String urlLink3 = "http://localhost:88/vinmart/API/updateKH.php?mahoadon="+mahoadon;
            URL url3 ;
            try {
                url3 = new URL(urlLink3);
                HttpURLConnection con = (HttpURLConnection) url3.openConnection();
                con.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                br.close();
            } 
            catch (Exception e) {
            }
            JOptionPane.showMessageDialog(this, "Thanh toán thành công");
            
            HashMap m = new HashMap();
            m.put("Mã HĐ", mahoadon);
            
            try {
                JasperDesign jdesign = JRXmlLoader.load("D:\\laptrinh\\java\\Vinmart\\src\\main\\report1.jrxml") ;
                JasperReport ireport = JasperCompileManager.compileReport(jdesign);
                JasperPrint jprint = JasperFillManager.fillReport(ireport, m,con);
                
                JasperViewer.viewReport(jprint);
//                JasperPrintManager.printReport(jprint, false);
            } catch (JRException ex) {
                Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
            }
//        String manhanvien;
//        try {
//            pst = (PreparedStatement) con.prepareStatement("select manhanvien from nhanvien where hoten=?");
//            pst.setString(1, nv);
//            rs = pst.executeQuery();
//            while (rs.next()) {
//                manhanvien = rs.getString("manhanvien");
//            } 
//        } 
//        catch (Exception e) {
//        }
//        
//        int lastidinsert = 0;
//        try {
//            String query = "INSERT INTO hoadon(manhanvien,ngaylap,tonggia,thanh_toan,due) VALUES (?,?,?,?,?)";
//            pst1 = (PreparedStatement) con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
//            pst1.setString(1, manhanvien);
//            pst1.setDate(2, (Date) date);
//            pst1.setString(3, tongtien);
//            pst1.setString(4, pay);
//            pst1.setString(5, due);
//            pst1.executeUpdate();
//            ResultSet generatekey = pst1.getGeneratedKeys();
//            
//            if(generatekey.next())
//            {
//                lastidinsert = generatekey.getInt(1);
//            }
//            
//            int rows = jTable1.getRowCount();
//            String query1 = "INSERT INTO chitiet_hd(mahoadon,mahanghoa,dongia,soluong,thanh_tien) VALUES (?,?,?,?,?)";
//            pst2 = (PreparedStatement) con.prepareStatement(query1);
//            String mahanghoa = "";
//            String soluong = "";
//            String dongia = "";
//            int thanh_tien = 0;
//            
//            for(int i=0;i<jTable1.getRowCount();i++)
//            {
//                mahanghoa = (String)jTable1.getValueAt(i, 0);
//                
//                dongia = (String)jTable1.getValueAt(i, 2);
//                soluong = (String)jTable1.getValueAt(i, 3);
//                thanh_tien = (int)jTable1.getValueAt(i, 4);
//                
//                pst2.setInt(1, lastidinsert);
//                pst2.setString(2, mahanghoa);
//                pst2.setString(3, dongia);
//                pst2.setString(4, soluong);
//                pst2.setInt(5, thanh_tien);
//                pst2.executeUpdate();
//            }
//            String query2 = "UPDATE khohang INNER JOIN chitiet_hd ON khohang.barcode = chitiet_hd.mahanghoa SET khohang.soluong = khohang.soluong - chitiet_hd.soluong where mahoadon = ?";
//            pst3 = (PreparedStatement) con.prepareStatement(query2);
//            pst3.setInt(1, lastidinsert);
//            pst3.executeUpdate();
//            
//            pst.addBatch();
//            pst1.addBatch();
//            pst2.addBatch();
//            pst3.addBatch();
//            JOptionPane.showMessageDialog(this, "Thanh toán thành công");
//            
//            HashMap m = new HashMap();
//            m.put("Mã HĐ", lastidinsert);
//            
//            try {
//                JasperDesign jdesign = JRXmlLoader.load("D:\\laptrinh\\java\\Vinmart\\src\\main\\report1.jrxml") ;
//                JasperReport ireport = JasperCompileManager.compileReport(jdesign);
//                JasperPrint jprint = JasperFillManager.fillReport(ireport, m,con);
//                
//                JasperViewer.viewReport(jprint);
////                JasperPrintManager.printReport(jprint, false);
//            } catch (JRException ex) {
//                Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } 
//        catch (SQLException ex) {
//            Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    private void clearFields()
    {
        txtbarcode.setText(null);
        txt_tongtien.setText(null);
        txtdue.setText(null);
        txtgia.setText(null);
        txtpay.setText(null);
        txtsanpham.setText(null);
        txtsoluong.setText(null);
        nhanvien.setSelectedItem(null);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtbarcode = new javax.swing.JTextField();
        txtsanpham = new javax.swing.JTextField();
        txtgia = new javax.swing.JTextField();
        txtsoluong = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txt_tongtien = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtpay = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtdue = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        nhanvien = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon("C:\\Users\\hieul\\OneDrive\\Pictures\\Vinmart_logo_sieu_thi.png")); // NOI18N

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Barcode");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Tên sản phẩm");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Giá");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Số lượng");

        txtbarcode.setBackground(new java.awt.Color(51, 51, 51));
        txtbarcode.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtbarcode.setForeground(new java.awt.Color(255, 204, 0));
        txtbarcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtbarcodeKeyPressed(evt);
            }
        });

        txtsanpham.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        txtgia.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        txtsoluong.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtsoluong.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtsoluongKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(txtbarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(txtsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtgia, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtsoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtbarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsanpham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtgia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtsoluong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Tổng tiền");

        txt_tongtien.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setText("Nhận tiền");

        txtpay.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel9.setText("Tiền dư");

        txtdue.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        jTable1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Barcode", "Tên sản phẩm", "Giá", "Số lượng", "Thành tiền"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton2.setText("In Hóa đơn");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        nhanvien.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        nhanvien.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nhân viên", " " }));

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton3.setText("jButton3");
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 410, 290));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(146, 146, 146)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane1)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(85, 85, 85)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txt_tongtien, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                        .addComponent(txtpay)
                                        .addComponent(txtdue))
                                    .addComponent(jButton2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(jLabel7))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(jLabel8))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(29, 29, 29)
                                        .addComponent(jLabel9)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(122, 122, 122))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tongtien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtpay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtdue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtbarcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbarcodeKeyPressed
//        if(evt.getKeyCode() ==  KeyEvent.VK_ENTER)
//        {
//            String barcode = txtbarcode.getText();
//            try {
//                pst = (PreparedStatement) con.prepareStatement("select * from mathang where barcode =?");
//                pst.setString(1, barcode);
//                rs = pst.executeQuery();
//                
//                if(rs.next() == false)
//                {
//                    JOptionPane.showMessageDialog(this, "Không tìm thấy Barcode");
//                }
//                else
//                {
//                    String tenmh = rs.getString("tenmh");
//                    String dongia = rs.getString("dongia");
//                    
//                    txtsanpham.setText(tenmh.trim());
//                    txtgia.setText(dongia.trim());
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(pos.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        if(evt.getKeyCode() ==  KeyEvent.VK_ENTER)
        {
            String barcode = txtbarcode.getText();
            String urlLink = "http://localhost:88/vinmart/API/getproduct.php?barcode="+barcode;
            URL url ;
            try {
                url = new URL(urlLink);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "" ;
                StringBuffer conntent = new StringBuffer();
                while((line = br.readLine()) != null)
                {
                    conntent.append(line);
                }
                JSONObject mathang = new JSONObject(conntent.toString());
                for (int i = 0; i < mathang.length(); i++) {
                    Object [] obj = new Object[2];
                    obj[0]=mathang.getJSONObject(""+i+"").get("tenmh").toString();
                    obj[1]=mathang.getJSONObject(""+i+"").get("dongia").toString();
                    
                    String tenmh = (String) obj[0];
                    txtsanpham.setText(tenmh);
                    String dongia = (String) obj[1];
                    txtgia.setText(dongia);
                }
                br.close();
            } 
            catch (Exception e) {
            }
        }
    }//GEN-LAST:event_txtbarcodeKeyPressed

    private void txtsoluongKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsoluongKeyPressed
        if(evt.getKeyCode() ==  KeyEvent.VK_ENTER)
        {
            int gia = Integer.parseInt(txtgia.getText());
            int soluong = Integer.parseInt(txtsoluong.getText());
            
            int thanhtien = gia * soluong;
            model = (DefaultTableModel)jTable1.getModel();
            model.addRow(new Object[]{
                txtbarcode.getText(),
                txtsanpham.getText(),
                txtgia.getText(),
                txtsoluong.getText(),
                thanhtien,
            });
            
            int tongtien = 0;
            for(int i = 0; i<jTable1.getRowCount();i++)
            {
                tongtien = tongtien + Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            }
            
            txt_tongtien.setText(String.valueOf(tongtien));
            txtbarcode.setText("");
            txtsanpham.setText("");
            txtgia.setText("");
            txtsoluong.setText("");
            txtbarcode.requestFocus();
        }
    }//GEN-LAST:event_txtsoluongKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            model.removeRow(jTable1.getSelectedRow());
            int tongtien = 0;
            for(int i = 0; i<jTable1.getRowCount();i++)
            {
                tongtien = tongtien + Integer.parseInt(jTable1.getValueAt(i, 4).toString());
            }
            txt_tongtien.setText(String.valueOf(tongtien));
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int pay = Integer.parseInt(txtpay.getText());
        int tongtien = Integer.parseInt(txt_tongtien.getText());
        int due = pay - tongtien;
        txtdue.setText(String.valueOf(due));
        hoadon();
        clearFields();
    }//GEN-LAST:event_jButton2ActionPerformed
    private void nhanvien()
    {
//        try {
//            pst = (PreparedStatement) con.prepareStatement("select * from nhanvien");
//            rs = pst.executeQuery();
//            while (rs.next()) {
//                nhanvien.addItem(rs.getString("manhanvien".valueOf("hoten")));
//            }
//        } 
//        catch (Exception e) {
//        }
        
            String urlLink = "http://localhost:88/vinmart/API/getEmployee.php";
            URL url ;
            try {
                url = new URL(urlLink);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line = "" ;
                StringBuffer conntent = new StringBuffer();
                while((line = br.readLine()) != null)
                {
                    conntent.append(line);
                }
                JSONObject nv = new JSONObject(conntent.toString());
                for (int i = 0; i < nv.length(); i++) {
                    Object [] obj = new Object[1];
                    obj[0]=nv.getJSONObject(""+i+"").get("hoten").toString();
                    
                    String hoten = (String) obj[0];
                    nhanvien.addItem(hoten.toString());
                }
                br.close();
            } 
            catch (Exception e) {
            }
    }
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
            java.util.logging.Logger.getLogger(pos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new pos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> nhanvien;
    private javax.swing.JTextField txt_tongtien;
    private javax.swing.JTextField txtbarcode;
    private javax.swing.JTextField txtdue;
    private javax.swing.JTextField txtgia;
    private javax.swing.JTextField txtpay;
    private javax.swing.JTextField txtsanpham;
    private javax.swing.JTextField txtsoluong;
    // End of variables declaration//GEN-END:variables

    private void initWebcam()
    {
        Dimension size = WebcamResolution.QVGA.getSize();
        webcam = Webcam.getWebcams().get(0);
        webcam.setViewSize(size);
        
        panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);
        panel.setFPSDisplayed(true);
        jPanel2.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 530, 330));
        executor.execute(this);
    }
    @Override
    public void run() {
        do {            
            try {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            Result result = null;
            BufferedImage image =  null;
            
            if(webcam.isOpen())
            {
                if((image = webcam.getImage()) == null)
                {
                    continue;
                }
            }
            
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            try {
                result = new MultiFormatReader().decode(bitmap);
            } 
            catch (NotFoundException e) {
                //No result
            }
            if(result != null)
            {
                String rs;
                txtbarcode.setText(result.getText());
                rs = result.getText();
                
            }
        }
        while(true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "My Thread");
        t.setDaemon(true);
        return t;
    }

    private static class Demension {

        public Demension() {
        }
    }
}
