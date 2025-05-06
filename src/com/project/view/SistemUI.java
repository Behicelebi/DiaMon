package com.project.view;


import com.project.kullanicilar.Kullanici;
import com.project.main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class SistemUI extends JPanel implements ActionListener {
    int WIDTH;
    int HEIGHT;
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    Kullanici doktor;
    ArrayList<Kullanici> hastalar = new ArrayList<>();
    int gun = 6, ay = 5, yil = 2025, saat = 0;
    JButton birSaat = new JButton("+1 Saat"), birGun = new JButton("+1 Gün");
    JButton hastaEkle = new JButton("Hasta Ekle");
    JTextField TC_Giris = new JTextField(), adGiris = new JTextField(), soyadGiris = new JTextField(), emailGiris = new JTextField(), dogumGiris = new JTextField();
    JComboBox<String> cinsiyetGiris = new JComboBox<String>();
    JPasswordField sifreGiris = new JPasswordField();
    JButton girisYap = new JButton("Giriş Yap");
    JButton geriButton = new JButton("Geri");
    JButton profilSecimi = new JButton("Seç");
    JButton cikisButton = new JButton("Çıkış Yap");
    final int kullanici_limit = 11, sifre_limit = 15;
    boolean ekliyor = false;
    File selectedFile = null;

    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);

        cikisButton.setBounds(50,30,100,20);
        cikisButton.setFont(new Font("Calibri",Font.BOLD,15));
        cikisButton.setHorizontalAlignment(SwingConstants.CENTER);
        cikisButton.setFocusable(false);
        cikisButton.addActionListener(this);
        this.add(cikisButton);

        birSaat.setBounds(1000,90,100,20);
        birSaat.setFont(new Font("Calibri",Font.BOLD,15));
        birSaat.setHorizontalAlignment(SwingConstants.CENTER);
        birSaat.setFocusable(false);
        birSaat.addActionListener(this);
        this.add(birSaat);

        birGun.setBounds(1000,120,100,20);
        birGun.setFont(new Font("Calibri",Font.BOLD,15));
        birGun.setHorizontalAlignment(SwingConstants.CENTER);
        birGun.setFocusable(false);
        birGun.addActionListener(this);
        this.add(birGun);

        hastaEkle.setBounds(1000,300,100,20);
        hastaEkle.setFont(new Font("Calibri",Font.BOLD,15));
        hastaEkle.setHorizontalAlignment(SwingConstants.CENTER);
        hastaEkle.setFocusable(false);
        hastaEkle.addActionListener(this);
        this.add(hastaEkle);

        TC_Giris.setPreferredSize(new Dimension(10,300));
        TC_Giris.setBounds(WIDTH/2-115,230,250,20);
        TC_Giris.setFont(new Font("Calibri",Font.PLAIN,15));
        TC_Giris.setVisible(false);
        TC_Giris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= kullanici_limit)
                    super.insertString(offs, str, a);
            }
        });
        this.add(TC_Giris);

        adGiris.setPreferredSize(new Dimension(10,300));
        adGiris.setBounds(WIDTH/2-115,280,250,20);
        adGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        adGiris.setVisible(false);
        adGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= 50)
                    super.insertString(offs, str, a);
            }
        });
        this.add(adGiris);

        soyadGiris.setPreferredSize(new Dimension(10,300));
        soyadGiris.setBounds(WIDTH/2-115,330,250,20);
        soyadGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        soyadGiris.setVisible(false);
        soyadGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= 50)
                    super.insertString(offs, str, a);
            }
        });
        this.add(soyadGiris);

        sifreGiris.setPreferredSize(new Dimension(10,300));
        sifreGiris.setBounds(WIDTH/2-115,380,250,20);
        sifreGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        sifreGiris.setVisible(false);
        sifreGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= sifre_limit)
                    super.insertString(offs, str, a);
            }
        });
        this.add(sifreGiris);

        emailGiris.setPreferredSize(new Dimension(10,300));
        emailGiris.setBounds(WIDTH/2-115,430,250,20);
        emailGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        emailGiris.setVisible(false);
        emailGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= 100)
                    super.insertString(offs, str, a);
            }
        });
        this.add(emailGiris);

        dogumGiris.setPreferredSize(new Dimension(10,300));
        dogumGiris.setBounds(WIDTH/2-115,480,250,20);
        dogumGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        dogumGiris.setVisible(false);
        dogumGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= 20)
                    super.insertString(offs, str, a);
            }
        });
        this.add(dogumGiris);

        cinsiyetGiris.setPreferredSize(new Dimension(10,300));
        cinsiyetGiris.setBounds(WIDTH/2-115,530,250,20);
        cinsiyetGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        cinsiyetGiris.setVisible(false);
        cinsiyetGiris.setFocusable(false);
        cinsiyetGiris.addItem("Erkek");
        cinsiyetGiris.addItem("Kadın");
        this.add(cinsiyetGiris);

        profilSecimi.setPreferredSize(new Dimension(10,300));
        profilSecimi.setBounds(WIDTH/2-115,580,250,20);
        profilSecimi.setFont(new Font("Calibri",Font.PLAIN,15));
        profilSecimi.setVisible(false);
        profilSecimi.setFocusable(false);
        profilSecimi.addActionListener(this);
        this.add(profilSecimi);

        girisYap.setBounds(WIDTH/2+35,630,100,25);
        girisYap.setFont(new Font("Calibri",Font.BOLD,15));
        girisYap.setHorizontalAlignment(SwingConstants.CENTER);
        girisYap.setFocusable(false);
        girisYap.setVisible(false);
        girisYap.addActionListener(this);
        this.add(girisYap);

        geriButton.setBounds(WIDTH/2-115,630,100,25);
        geriButton.setFont(new Font("Calibri",Font.BOLD,15));
        geriButton.setHorizontalAlignment(SwingConstants.CENTER);
        geriButton.setFocusable(false);
        geriButton.setVisible(false);
        geriButton.addActionListener(this);
        this.add(geriButton);
    }
    public void initialize(){
        String sql = "SELECT ad, soyad, email, dogum_tarihi, cinsiyet, profil_resmi FROM KULLANICI " +
                "WHERE tc_no = ? AND sifre_hash = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))";
        String sql1 = "SELECT K.ad, K.soyad, K.email, K.dogum_tarihi, K.cinsiyet, K.profil_resmi FROM KULLANICI K, HASTA_DOKTOR H " +
                "WHERE K.tc_no = H.hasta_tc AND H.doktor_tc = ?";
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ps1 = conn.prepareStatement(sql1)
        ) {
            ps.setString(1, Main.enUserName);
            ps.setString(2, Main.enPassword);
            ResultSet rs = ps.executeQuery();
            rs.next();
            doktor = new Kullanici(rs.getString("ad"),rs.getString("soyad"),rs.getString("email"),rs.getString("dogum_tarihi"),rs.getString("cinsiyet"), ImageIO.read(rs.getBinaryStream("profil_resmi")));

            ps1.setString(1,Main.enUserName);
            ResultSet rs1 = ps1.executeQuery();
            hastalar.clear();
            while(rs1.next()){
                hastalar.add(new Kullanici(rs1.getString("ad"),rs1.getString("soyad"),rs1.getString("email"),rs1.getString("dogum_tarihi"),rs1.getString("cinsiyet"), ImageIO.read(rs1.getBinaryStream("profil_resmi"))));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,25));
        g.drawString("Diyabet Sistemi",500,40);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.drawString("Tarih: " + gun + "." + ay + "." + yil, 1000,50);
        g.drawString("Saat: " + saat + ":00:00", 1000,70);

        if(!ekliyor){
            g.drawImage(doktor.profil_resmi,20,140,this);
            g.drawString("Ad Soyad: " + doktor.ad + " " + doktor.soyad, 150,150);
            g.drawString("Cinsiyet: " + doktor.cinsiyet, 150,170);
            g.drawString("Doğum Tarihi: " + doktor.dogum_tarihi, 150,190);
            g.drawString("E-Posta: " + doktor.email, 150,210);
            g.drawString("Rol: DOKTOR", 150,230);

            for (int i = 0; i < hastalar.size(); i++) {
                g.drawImage(hastalar.get(i).profil_resmi,20,260 + 120*i,this);
                g.drawString("Ad Soyad: " + hastalar.get(i).ad + " " + hastalar.get(i).soyad, 150,270 + 120*i);
                g.drawString("Cinsiyet: " + hastalar.get(i).cinsiyet, 150,290 + 120*i);
                g.drawString("Doğum Tarihi: " + hastalar.get(i).dogum_tarihi, 150,310 + 120*i);
                g.drawString("E-Posta: " + hastalar.get(i).email, 150,330 + 120*i);
                g.drawString("Rol: HASTA", 150,350 + 120*i);
            }
        } else {
            g.drawString("TC Kimlik:", WIDTH/2-115,220);
            g.drawString("Ad:", WIDTH/2-115,270);
            g.drawString("Soyad:", WIDTH/2-115,320);
            g.drawString("Şifre:", WIDTH/2-115,370);
            g.drawString("EMail:", WIDTH/2-115,420);
            g.drawString("Dogum Tarihi:", WIDTH/2-115,470);
            g.drawString("Cinsiyet:", WIDTH/2-115,520);
            g.drawString("Profil Resmi:", WIDTH/2-115,570);
            if(selectedFile != null){g.drawString("Seçilen Dosya: " + selectedFile.getName(), WIDTH/2-115,620);}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == hastaEkle){
            ekliyor = true;
            TC_Giris.setVisible(true);
            adGiris.setVisible(true);
            soyadGiris.setVisible(true);
            sifreGiris.setVisible(true);
            emailGiris.setVisible(true);
            dogumGiris.setVisible(true);
            cinsiyetGiris.setVisible(true);
            profilSecimi.setVisible(true);
            girisYap.setVisible(true);
            geriButton.setVisible(true);
            hastaEkle.setVisible(false);
            repaint();
        } else if (e.getSource() == geriButton) {
            ekliyor = false;
            TC_Giris.setVisible(false);
            TC_Giris.setText("");
            adGiris.setVisible(false);
            adGiris.setText("");
            soyadGiris.setVisible(false);
            soyadGiris.setText("");
            sifreGiris.setVisible(false);
            sifreGiris.setText("");
            emailGiris.setVisible(false);
            emailGiris.setText("");
            dogumGiris.setVisible(false);
            dogumGiris.setText("");
            cinsiyetGiris.setVisible(false);
            profilSecimi.setVisible(false);
            selectedFile = null;
            girisYap.setVisible(false);
            geriButton.setVisible(false);
            hastaEkle.setVisible(true);
            repaint();
        } else if (e.getSource() == profilSecimi) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Resim Dosyası Seç");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Resim Dosyaları", "jpg", "jpeg", "png"));

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            }
            repaint();
        } else if (e.getSource() == girisYap) {
            String sql = "INSERT INTO KULLANICI (tc_no, ad, soyad, sifre_hash, email, dogum_tarihi, cinsiyet, profil_resmi, rol) " +
                    "VALUES (?, ?, ?, HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?)), ?, ?, ?, ?, 'HASTA')";
            String sql1 = "INSERT INTO HASTA_DOKTOR (doktor_tc, hasta_tc)"+
                    "VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
                PreparedStatement ps = conn.prepareStatement(sql); PreparedStatement ps1 = conn.prepareStatement(sql1)) {

                ps1.setString(1,Main.enUserName);
                ps1.setString(2,TC_Giris.getText());

                FileInputStream fis = new FileInputStream(selectedFile);
                ps.setString(1, TC_Giris.getText());
                ps.setString(2, adGiris.getText());
                ps.setString(3, soyadGiris.getText());
                ps.setString(4, Arrays.toString(sifreGiris.getPassword()));
                ps.setString(5, emailGiris.getText());
                ps.setString(6, dogumGiris.getText());
                if(cinsiyetGiris.getSelectedIndex() == 0){
                    ps.setString(7, "E");
                }else{
                    ps.setString(7, "K");
                }
                ps.setBinaryStream(8, fis, (int) selectedFile.length());

                int affectedRows = ps.executeUpdate();
                ps1.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Kullanıcı başarıyla eklendi.");
                } else {
                    System.out.println("Kullanıcı eklenemedi.");
                }
                conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            initialize();
            ekliyor = false;
            TC_Giris.setVisible(false);
            TC_Giris.setText("");
            adGiris.setVisible(false);
            adGiris.setText("");
            soyadGiris.setVisible(false);
            soyadGiris.setText("");
            sifreGiris.setVisible(false);
            sifreGiris.setText("");
            emailGiris.setVisible(false);
            emailGiris.setText("");
            dogumGiris.setVisible(false);
            dogumGiris.setText("");
            cinsiyetGiris.setVisible(false);
            profilSecimi.setVisible(false);
            selectedFile = null;
            girisYap.setVisible(false);
            geriButton.setVisible(false);
            hastaEkle.setVisible(true);
            repaint();
        } else if (e.getSource() == cikisButton) {
            Main.frame.switchScreen(0);
        }
    }
}
