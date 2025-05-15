package com.project.view;


import com.project.kullanicilar.Kullanici;
import com.project.main.Main;
import com.project.util.EmailSender;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.Date;

public class SistemUI extends JPanel implements ActionListener , MouseWheelListener {
    enum Screen {
        MAIN,
        HASTA_EKLE,
        HASTA_PROFIL
    }
    Screen currentScreen = Screen.MAIN;
    int WIDTH;
    int HEIGHT;
    public boolean doktorMu = false; //false=HASTA, true=DOKTOR
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    Kullanici kullanici;
    Rectangle kullaniciRect = new Rectangle(10,130,700,130);
    ArrayList<Kullanici> relations = new ArrayList<>();
    ArrayList<Rectangle> relationsRects = new ArrayList<>();
    JButton hastaEkle = new JButton("Hasta Ekle"), girisYap = new JButton("Hasta Ekle"), geriButton = new JButton("Geri"), profilSecimi = new JButton("Seç"), cikisButton = new JButton("Çıkış Yap"), selectDate = new JButton("Tarih Seç"), olcumGir = new JButton("Kayıt Et"), oneriYap = new JButton("Öneri Yap"), diyetYap = new JButton("Diyet Yap"), egzersizYap = new JButton("Egzersiz Yap");
    JTextField TC_Giris = new JTextField(), adGiris = new JTextField(), soyadGiris = new JTextField(), emailGiris = new JTextField(), olcumGiris = new JTextField();
    JComboBox<String> cinsiyetGiris = new JComboBox<String>(), belirti_1_giris = new JComboBox<String>(), belirti_2_giris = new JComboBox<String>(), belirti_3_giris = new JComboBox<String>(), olcumSecme = new JComboBox<>(), diyetGecmis = new JComboBox<String>(), egzersizGecmis = new JComboBox<String>();
    JPasswordField sifreGiris = new JPasswordField();
    JButton dogumSecimButton = new JButton("Doğum Tarihi Seç");
    final String doktorUser = "doktor_login", hastaUser = "hasta_login", doktorPassword = "doktor123", hastaPassword = "hasta123";
    Date dogumSqlDate = null;
    final int kullanici_limit = 11, sifre_limit = 15;
    int hastaError = 0;
    int secilenHasta = 0;
    File selectedFile = null;
    Date[] selectedDateTime = {new Date()};
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
    String onerilenDiyet = "Yok", onerilenEgzersiz = "Yok";
    int olcumGirildiMi = -1;

    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
        this.addMouseWheelListener(this);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDateTime = new Date[]{cal.getTime()};

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                for (int i = 0; i< relationsRects.size(); i++) {
                    if (relationsRects.get(i).contains(e.getPoint()) && relationsRects.get(i).y > 130 && kullanici.rol.equals("DOKTOR") && currentScreen == Screen.MAIN) {
                        currentScreen = Screen.HASTA_PROFIL;
                        cikisButton.setText("Geri");
                        olcumSecme.setVisible(true);
                        hastaEkle.setVisible(false);
                        secilenHasta = i;
                        for (int j = 0; j < relations.get(secilenHasta).olcumler.size(); j++) {
                            String eklencekString = String.valueOf(relations.get(secilenHasta).olcumler.get(j)) + " -> " + relations.get(secilenHasta).olcumTarihleri.get(j);
                            olcumSecme.addItem(eklencekString);
                        }
                        if(relations.get(secilenHasta).oneriGirdiMi == 0){oneriYap.setVisible(true);}
                        onerilenDiyet = "Yok";
                        onerilenEgzersiz = "Yok";
                        if(relations.get(secilenHasta).olcumler.get(0) < 70){
                            if(relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                                onerilenDiyet = "Dengeli Beslenme";
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 70 && relations.get(secilenHasta).olcumler.get(0) < 110) {
                            if(relations.get(secilenHasta).belirtiler.contains("Kilo Kaybı") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                                onerilenDiyet = "Az Şekerli Diyet";
                                onerilenEgzersiz = "Yürüyüş";
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                onerilenDiyet = "Dengeli Beslenme";
                                onerilenEgzersiz = "Yürüyüş";
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 110 && relations.get(secilenHasta).olcumler.get(0) < 180) {
                            if(relations.get(secilenHasta).belirtiler.contains("Bulanık Görme") && relations.get(secilenHasta).belirtiler.contains("Nöropati")){
                                onerilenDiyet = "Az Şekerli Diyet";
                                onerilenEgzersiz = "Klinik Egzersiz";
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Poliüri") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                onerilenDiyet = "Şekersiz Diyet";
                                onerilenEgzersiz = "Klinik Egzersiz";
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk") && relations.get(secilenHasta).belirtiler.contains("Bulanık Görme")) {
                                onerilenDiyet = "Az Şekerli Diyet";
                                onerilenEgzersiz = "Yürüyüş";
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 180) {
                            if(relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi") && relations.get(secilenHasta).belirtiler.contains("Polifaji")){
                                onerilenDiyet = "Şekersiz Diyet";
                                onerilenEgzersiz = "Klinik Egzersiz";
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                onerilenDiyet = "Şekersiz Diyet";
                                onerilenEgzersiz = "Yürüyüş";
                            }
                        }
                        diyetGecmis.setVisible(true);
                        egzersizGecmis.setVisible(true);
                        String sql = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ?";
                        String sql1 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ?";
                        try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                             PreparedStatement ps = conn.prepareStatement(sql);
                             PreparedStatement ps1 = conn.prepareStatement(sql1)) {
                            ps.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                            ResultSet rs = ps.executeQuery();
                            while(rs.next()){
                                String insertedString;
                                if(rs.getBoolean("yapildi_mi")){insertedString = rs.getString("tarih") + " -> YAPILDI";}
                                else{insertedString = rs.getString("tarih") + " -> YAPILMADI";}
                                diyetGecmis.addItem(insertedString);
                            }
                            ps1.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                            ResultSet rs1 = ps1.executeQuery();
                            while(rs1.next()){
                                String insertedString;
                                if(rs1.getBoolean("yapildi_mi")){insertedString = rs1.getString("tarih") + " -> YAPILDI";}
                                else{insertedString = rs1.getString("tarih") + " -> YAPILMADI";}
                                egzersizGecmis.addItem(insertedString);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        repaint();
                    }
                }
            }
        });

        cikisButton.setBounds(50,30,100,20);
        cikisButton.setFont(new Font("Calibri",Font.BOLD,15));
        cikisButton.setHorizontalAlignment(SwingConstants.CENTER);
        cikisButton.setFocusable(false);
        cikisButton.addActionListener(this);
        this.add(cikisButton);

        selectDate.setBounds(960,60,100,20);
        selectDate.setFont(new Font("Calibri",Font.BOLD,15));
        selectDate.setHorizontalAlignment(SwingConstants.CENTER);
        selectDate.setFocusable(false);
        selectDate.addActionListener(this);
        this.add(selectDate);

        hastaEkle.setBounds(1100,300,100,20);
        hastaEkle.setFont(new Font("Calibri",Font.BOLD,15));
        hastaEkle.setHorizontalAlignment(SwingConstants.CENTER);
        hastaEkle.setFocusable(false);
        hastaEkle.addActionListener(this);
        this.add(hastaEkle);

        TC_Giris.setPreferredSize(new Dimension(10,300));
        TC_Giris.setBounds(WIDTH/2-115,130,250,20);
        TC_Giris.setFont(new Font("Calibri",Font.PLAIN,15));
        TC_Giris.setVisible(false);
        TC_Giris.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (str.matches("\\d+") && getLength() + str.length() <= kullanici_limit) {
                    super.insertString(offs, str, a);
                }
            }
        });
        this.add(TC_Giris);

        olcumGiris.setPreferredSize(new Dimension(10,300));
        olcumGiris.setBounds(WIDTH/2-515,130,250,20);
        olcumGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        olcumGiris.setVisible(false);
        olcumGiris.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (str.matches("\\d+") && getLength() + str.length() <= 7) {
                    super.insertString(offs, str, a);
                }
            }
        });
        this.add(olcumGiris);

        belirti_1_giris.setPreferredSize(new Dimension(10,300));
        belirti_1_giris.setBounds(WIDTH/2-515,180,250,25);
        belirti_1_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_1_giris.setVisible(false);
        belirti_1_giris.setFocusable(false);
        belirti_1_giris.addItem("YOK");
        belirti_1_giris.addActionListener(e -> {
            if(belirti_1_giris.getSelectedIndex() != 0 && (belirti_1_giris.getSelectedIndex() == belirti_2_giris.getSelectedIndex() || belirti_1_giris.getSelectedIndex() == belirti_3_giris.getSelectedIndex())){belirti_1_giris.setSelectedIndex(0);}
        });
        this.add(belirti_1_giris);

        belirti_2_giris.setPreferredSize(new Dimension(10,300));
        belirti_2_giris.setBounds(WIDTH/2-515,235,250,25);
        belirti_2_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_2_giris.setVisible(false);
        belirti_2_giris.setFocusable(false);
        belirti_2_giris.addItem("YOK");
        belirti_2_giris.addActionListener(e -> {
            if(belirti_2_giris.getSelectedIndex() != 0 && (belirti_2_giris.getSelectedIndex() == belirti_1_giris.getSelectedIndex() || belirti_2_giris.getSelectedIndex() == belirti_3_giris.getSelectedIndex())){belirti_2_giris.setSelectedIndex(0);}
        });
        this.add(belirti_2_giris);

        belirti_3_giris.setPreferredSize(new Dimension(10,300));
        belirti_3_giris.setBounds(WIDTH/2-515,285,250,25);
        belirti_3_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_3_giris.setVisible(false);
        belirti_3_giris.setFocusable(false);
        belirti_3_giris.addItem("YOK");
        belirti_3_giris.addActionListener(e -> {
            if(belirti_3_giris.getSelectedIndex() != 0 && (belirti_3_giris.getSelectedIndex() == belirti_1_giris.getSelectedIndex() || belirti_3_giris.getSelectedIndex() == belirti_2_giris.getSelectedIndex())){belirti_3_giris.setSelectedIndex(0);}
        });
        this.add(belirti_3_giris);

        String sql = "SELECT tur_adi FROM BELIRTI_TURU ";
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password); //ADMIN GIRISI
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                belirti_1_giris.addItem(rs.getString("tur_adi"));
                belirti_2_giris.addItem(rs.getString("tur_adi"));
                belirti_3_giris.addItem(rs.getString("tur_adi"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        olcumGir.setBounds(330,580,120,20);
        olcumGir.setFont(new Font("Calibri",Font.BOLD,15));
        olcumGir.setHorizontalAlignment(SwingConstants.CENTER);
        olcumGir.setFocusable(false);
        olcumGir.addActionListener(this);
        this.add(olcumGir);

        diyetYap.setBounds(330,620,120,20);
        diyetYap.setFont(new Font("Calibri",Font.BOLD,15));
        diyetYap.setHorizontalAlignment(SwingConstants.CENTER);
        diyetYap.setFocusable(false);
        diyetYap.setVisible(false);
        diyetYap.addActionListener(this);
        this.add(diyetYap);

        egzersizYap.setBounds(330,650,120,20);
        egzersizYap.setFont(new Font("Calibri",Font.BOLD,15));
        egzersizYap.setHorizontalAlignment(SwingConstants.CENTER);
        egzersizYap.setFocusable(false);
        egzersizYap.setVisible(false);
        egzersizYap.addActionListener(this);
        this.add(egzersizYap);

        oneriYap.setBounds(20,510,100,20);
        oneriYap.setFont(new Font("Calibri",Font.BOLD,15));
        oneriYap.setHorizontalAlignment(SwingConstants.CENTER);
        oneriYap.setVisible(false);
        oneriYap.setFocusable(false);
        oneriYap.addActionListener(this);
        this.add(oneriYap);

        adGiris.setPreferredSize(new Dimension(10,300));
        adGiris.setBounds(WIDTH/2-115,180,250,20);
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
        soyadGiris.setBounds(WIDTH/2-115,230,250,20);
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
        sifreGiris.setBounds(WIDTH/2-115,280,250,20);
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
        emailGiris.setBounds(WIDTH/2-115,330,250,20);
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

        dogumSecimButton.setPreferredSize(new Dimension(10,300));
        dogumSecimButton.setBounds(WIDTH/2-115,380,250,20);
        dogumSecimButton.setFont(new Font("Calibri",Font.PLAIN,15));
        dogumSecimButton.setVisible(false);
        dogumSecimButton.setFocusable(false);
        dogumSecimButton.addActionListener(this);
        this.add(dogumSecimButton);

        cinsiyetGiris.setPreferredSize(new Dimension(10,300));
        cinsiyetGiris.setBounds(WIDTH/2-115,450,250,25);
        cinsiyetGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        cinsiyetGiris.setVisible(false);
        cinsiyetGiris.setFocusable(false);
        cinsiyetGiris.addItem("Erkek");
        cinsiyetGiris.addItem("Kadın");
        this.add(cinsiyetGiris);

        profilSecimi.setPreferredSize(new Dimension(10,300));
        profilSecimi.setBounds(WIDTH/2-115,505,250,20);
        profilSecimi.setFont(new Font("Calibri",Font.PLAIN,15));
        profilSecimi.setVisible(false);
        profilSecimi.setFocusable(false);
        profilSecimi.addActionListener(this);
        this.add(profilSecimi);

        girisYap.setBounds(WIDTH/2+35,555,100,25);
        girisYap.setFont(new Font("Calibri",Font.BOLD,15));
        girisYap.setHorizontalAlignment(SwingConstants.CENTER);
        girisYap.setFocusable(false);
        girisYap.setVisible(false);
        girisYap.addActionListener(this);
        this.add(girisYap);

        geriButton.setBounds(WIDTH/2-115,555,100,25);
        geriButton.setFont(new Font("Calibri",Font.BOLD,15));
        geriButton.setHorizontalAlignment(SwingConstants.CENTER);
        geriButton.setFocusable(false);
        geriButton.setVisible(false);
        geriButton.addActionListener(this);
        this.add(geriButton);

        olcumSecme.setPreferredSize(new Dimension(10,300));
        olcumSecme.setBounds(20,370,250,25);
        olcumSecme.setFont(new Font("Calibri",Font.PLAIN,15));
        olcumSecme.setVisible(false);
        olcumSecme.setFocusable(false);
        olcumSecme.addActionListener(e -> {
            repaint();
        });
        this.add(olcumSecme);

        diyetGecmis.setPreferredSize(new Dimension(10,300));
        diyetGecmis.setBounds(20,530,250,25);
        diyetGecmis.setFont(new Font("Calibri",Font.PLAIN,15));
        diyetGecmis.setVisible(false);
        diyetGecmis.setFocusable(false);
        this.add(diyetGecmis);

        egzersizGecmis.setPreferredSize(new Dimension(10,300));
        egzersizGecmis.setBounds(20,575,250,25);
        egzersizGecmis.setFont(new Font("Calibri",Font.PLAIN,15));
        egzersizGecmis.setVisible(false);
        egzersizGecmis.setFocusable(false);
        this.add(egzersizGecmis);
    }
    public void initialize(){
        String sql = "SELECT tc_no, ad, soyad, email, dogum_tarihi, cinsiyet, profil_resmi, rol FROM KULLANICI " +
                "WHERE tc_no = ? AND sifre = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))";
        String username = doktorMu ? doktorUser : hastaUser;
        String password = doktorMu ? doktorPassword : hastaPassword;
        try (
                Connection conn = DriverManager.getConnection(Main.url, username, password); // BOOLEAN
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, Main.enUserName);
            ps.setString(2, Main.enPassword);
            ResultSet rs = ps.executeQuery();
            rs.next();
            kullanici = new Kullanici(rs.getLong("tc_no"),rs.getString("ad"),rs.getString("soyad"),rs.getString("email"),rs.getString("dogum_tarihi"),rs.getString("cinsiyet"), ImageIO.read(rs.getBinaryStream("profil_resmi")), rs.getString("rol"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(kullanici.rol.equals("HASTA")){
            String sql1 = "SELECT * FROM HASTA_OLCUM WHERE hasta_tc = ?";
            String sql2 = "SELECT * FROM HASTA_BELIRTI WHERE hasta_tc = ?";
            String sql3 = "SELECT * FROM BELIRTI_TURU WHERE belirti_turu_id = ?";
            String sql4 = "SELECT * FROM UYARI_TURU WHERE uyari_turu_id = ?";
            String sql5 = "SELECT D.tur_adi FROM HASTA_DIYET H, DIYET_TURU D WHERE H.hasta_tc = ? AND H.diyet_turu_id = D.diyet_turu_id";
            String sql6 = "SELECT E.tur_adi FROM HASTA_EGZERSIZ H, EGZERSIZ_TURU E WHERE H.hasta_tc = ? AND H.egzersiz_turu_id = E.egzersiz_turu_id";
            try (
                    Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA GIRISI
                    PreparedStatement ps = conn.prepareStatement(sql1);
                    PreparedStatement ps1 = conn.prepareStatement(sql2);
                    PreparedStatement ps2 = conn.prepareStatement(sql3);
                    PreparedStatement ps3 = conn.prepareStatement(sql4);
                    PreparedStatement ps4 = conn.prepareStatement(sql5);
                    PreparedStatement ps5 = conn.prepareStatement(sql6)
            ) {

                ps.setString(1, Main.enUserName);
                ps1.setString(1, Main.enUserName);

                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    kullanici.olcumler.add(rs.getInt("olcum_degeri"));
                    kullanici.olcumTarihleri.add(rs.getString("olcum_tarihi"));
                    ps3.setString(1, String.valueOf(rs.getInt("uyari_turu_id")));
                    ResultSet rs1 = ps3.executeQuery();
                    rs1.next();
                    kullanici.olcumUyarilar.add(rs1.getString("tur_adi"));
                    kullanici.olcumUyariAciklamalar.add(rs1.getString("tur_mesaji"));
                }

                ResultSet rs1 = ps1.executeQuery();
                while (rs1.next()){
                    ps2.setString(1, String.valueOf(rs1.getInt("belirti_turu_id")));
                    ResultSet rs2 = ps2.executeQuery();
                    rs2.next();
                    kullanici.belirtiler.add(rs2.getString("tur_adi"));
                }

                ps4.setString(1,Main.enUserName);
                ps5.setString(1,Main.enUserName);
                ResultSet rs2 = ps4.executeQuery();
                ResultSet rs3 = ps5.executeQuery();
                if(rs2.next()){
                    kullanici.diyetOneri = rs2.getString("tur_adi");
                    kullanici.oneriGirdiMi = 1;
                }
                if (rs3.next()){
                    kullanici.egzersizOneri = rs3.getString("tur_adi");
                    kullanici.oneriGirdiMi = 1;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if(kullanici.oneriGirdiMi != 1){
                if(kullanici.olcumler.get(0) < 70){
                    if(kullanici.belirtiler.contains("Nöropati") && kullanici.belirtiler.contains("Polifaji") && kullanici.belirtiler.contains("Yorgunluk")){
                        kullanici.oneriGirdiMi = 0;
                    }
                } else if (kullanici.olcumler.get(0) >= 70 && kullanici.olcumler.get(0) < 110) {
                    if(kullanici.belirtiler.contains("Kilo Kaybı") && kullanici.belirtiler.contains("Yorgunluk")){
                        kullanici.oneriGirdiMi = 0;
                    } else if (kullanici.belirtiler.contains("Polifaji") && kullanici.belirtiler.contains("Polidipsi")) {
                        kullanici.oneriGirdiMi = 0;
                    }
                } else if (kullanici.olcumler.get(0) >= 110 && kullanici.olcumler.get(0) < 180) {
                    if(kullanici.belirtiler.contains("Bulanık Görme") && kullanici.belirtiler.contains("Nöropati")){
                        kullanici.oneriGirdiMi = 0;
                    } else if (kullanici.belirtiler.contains("Poliüri") && kullanici.belirtiler.contains("Polidipsi")) {
                        kullanici.oneriGirdiMi = 0;
                    } else if (kullanici.belirtiler.contains("Nöropati") && kullanici.belirtiler.contains("Yorgunluk") && kullanici.belirtiler.contains("Bulanık Görme")) {
                        kullanici.oneriGirdiMi = 0;
                    }
                } else if (kullanici.olcumler.get(0) >= 180) {
                    if(kullanici.belirtiler.contains("Yaraların Yavaş İyileşmesi") && kullanici.belirtiler.contains("Polidipsi") && kullanici.belirtiler.contains("Polifaji")){
                        kullanici.oneriGirdiMi = 0;
                    } else if (kullanici.belirtiler.contains("Yaraların Yavaş İyileşmesi") && kullanici.belirtiler.contains("Polidipsi")) {
                        kullanici.oneriGirdiMi = 0;
                    }
                }
            }
        }

        String sql1 = "";
        if(kullanici.rol.equals("DOKTOR")){
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, K.dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.hasta_tc AND H.doktor_tc = ?";
        } else if (kullanici.rol.equals("HASTA")) {
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, K.dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.doktor_tc AND H.hasta_tc = ?";
        }
        try (
                Connection conn = DriverManager.getConnection(Main.url, username, password); // boolean
                PreparedStatement ps = conn.prepareStatement(sql1);
        ) {
            ps.setString(1,Main.enUserName);
            ResultSet rs1 = ps.executeQuery();
            relations.clear();
            relationsRects.clear();
            int i = 0;
            while(rs1.next()){
                relations.add(new Kullanici(rs1.getLong("tc_no"), rs1.getString("ad"),rs1.getString("soyad"),rs1.getString("email"),rs1.getString("dogum_tarihi"),rs1.getString("cinsiyet"), ImageIO.read(rs1.getBinaryStream("profil_resmi")), rs1.getString("rol")));
                relationsRects.add(new Rectangle(10,270 + 140*i,700,130));
                if(relations.get(i).rol.equals("HASTA")){
                    String sql2 = "SELECT * FROM HASTA_OLCUM WHERE hasta_tc = ?";
                    String sql3 = "SELECT * FROM HASTA_BELIRTI WHERE hasta_tc = ?";
                    String sql4 = "SELECT * FROM BELIRTI_TURU WHERE belirti_turu_id = ?";
                    String sql5 = "SELECT * FROM UYARI_TURU WHERE uyari_turu_id = ?";
                    String sql6 = "SELECT D.tur_adi FROM HASTA_DIYET H, DIYET_TURU D WHERE H.hasta_tc = ? AND H.diyet_turu_id = D.diyet_turu_id";
                    String sql7 = "SELECT E.tur_adi FROM HASTA_EGZERSIZ H, EGZERSIZ_TURU E WHERE H.hasta_tc = ? AND H.egzersiz_turu_id = E.egzersiz_turu_id";
                    try (
                            Connection conn1 = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA GIRISI
                            PreparedStatement ps2 = conn1.prepareStatement(sql2);
                            PreparedStatement ps3 = conn1.prepareStatement(sql3);
                            PreparedStatement ps4 = conn1.prepareStatement(sql4);
                            PreparedStatement ps5 = conn1.prepareStatement(sql5);
                            PreparedStatement ps6 = conn.prepareStatement(sql6);
                            PreparedStatement ps7 = conn.prepareStatement(sql7)
                    ) {

                        ps2.setString(1, String.valueOf(relations.get(i).tc_no));
                        ps3.setString(1, String.valueOf(relations.get(i).tc_no));

                        ResultSet rs2 = ps2.executeQuery();
                        while(rs2.next()){
                            relations.get(i).olcumler.add(rs2.getInt("olcum_degeri"));
                            relations.get(i).olcumTarihleri.add(rs2.getString("olcum_tarihi"));
                            ps5.setString(1, String.valueOf(rs2.getInt("uyari_turu_id")));
                            ResultSet rs3 = ps5.executeQuery();
                            rs3.next();
                            relations.get(i).olcumUyarilar.add(rs3.getString("tur_adi"));
                            relations.get(i).olcumUyariAciklamalar.add(rs3.getString("tur_mesaji"));
                        }

                        ResultSet rs3 = ps3.executeQuery();
                        while (rs3.next()){
                            ps4.setString(1, String.valueOf(rs3.getInt("belirti_turu_id")));
                            ResultSet rs4 = ps4.executeQuery();
                            rs4.next();
                            relations.get(i).belirtiler.add(rs4.getString("tur_adi"));
                        }

                        ps6.setString(1,String.valueOf(relations.get(i).tc_no));
                        ps7.setString(1,String.valueOf(relations.get(i).tc_no));
                        ResultSet rs4 = ps6.executeQuery();
                        ResultSet rs5 = ps7.executeQuery();
                        if(rs4.next()){
                            relations.get(i).diyetOneri = rs4.getString("tur_adi");
                            relations.get(i).oneriGirdiMi = 1;
                        }
                        if(rs5.next()){
                            relations.get(i).egzersizOneri = rs5.getString("tur_adi");
                            relations.get(i).oneriGirdiMi = 1;
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    if(relations.get(i).oneriGirdiMi != 1){
                        if(relations.get(i).olcumler.get(0) < 70){
                            if(relations.get(i).belirtiler.contains("Nöropati") && relations.get(i).belirtiler.contains("Polifaji") && relations.get(i).belirtiler.contains("Yorgunluk")){
                                relations.get(i).oneriGirdiMi = 0;
                            }
                        } else if (relations.get(i).olcumler.get(0) >= 70 && relations.get(i).olcumler.get(0) < 110) {
                            if(relations.get(i).belirtiler.contains("Kilo Kaybı") && relations.get(i).belirtiler.contains("Yorgunluk")){
                                relations.get(i).oneriGirdiMi = 0;
                            } else if (relations.get(i).belirtiler.contains("Polifaji") && relations.get(i).belirtiler.contains("Polidipsi")) {
                                relations.get(i).oneriGirdiMi = 0;
                            }
                        } else if (relations.get(i).olcumler.get(0) >= 110 && relations.get(i).olcumler.get(0) < 180) {
                            if(relations.get(i).belirtiler.contains("Bulanık Görme") && relations.get(i).belirtiler.contains("Nöropati")){
                                relations.get(i).oneriGirdiMi = 0;
                            } else if (relations.get(i).belirtiler.contains("Poliüri") && relations.get(i).belirtiler.contains("Polidipsi")) {
                                relations.get(i).oneriGirdiMi = 0;
                            } else if (relations.get(i).belirtiler.contains("Nöropati") && relations.get(i).belirtiler.contains("Yorgunluk") && relations.get(i).belirtiler.contains("Bulanık Görme")) {
                                relations.get(i).oneriGirdiMi = 0;
                            }
                        } else if (relations.get(i).olcumler.get(0) >= 180) {
                            if(relations.get(i).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(i).belirtiler.contains("Polidipsi") && relations.get(i).belirtiler.contains("Polifaji")){
                                relations.get(i).oneriGirdiMi = 0;
                            } else if (relations.get(i).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(i).belirtiler.contains("Polidipsi")) {
                                relations.get(i).oneriGirdiMi = 0;
                            }
                        }
                    }
                }
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(kullanici.rol.equals("DOKTOR")){
            hastaEkle.setVisible(true);
            olcumSecme.setBounds(20,370,250,25);
            olcumSecme.setVisible(false);
            olcumSecme.removeAllItems();
            olcumGiris.setVisible(false);
            olcumGiris.setText("");
            olcumGiris.setBounds(WIDTH/2-515,130,250,20);
            olcumGir.setVisible(false);
            diyetYap.setVisible(false);
            egzersizYap.setVisible(false);
        }
        else if (kullanici.rol.equals("HASTA")) {
            hastaEkle.setVisible(false);
            olcumSecme.setBounds(20,500,250,25);
            olcumSecme.removeAllItems();
            olcumSecme.setVisible(true);
            for (int j = 0; j < kullanici.olcumler.size(); j++) {
                String eklencekString = String.valueOf(kullanici.olcumler.get(j)) + " -> " + kullanici.olcumTarihleri.get(j);
                olcumSecme.addItem(eklencekString);
            }
            olcumGiris.setVisible(true);
            olcumGiris.setText("");
            olcumGiris.setBounds(20,580,250,20);
            olcumGir.setVisible(true);
            if(kullanici.diyetOneri != null){
                String sql2 = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ? AND tarih = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps = conn.prepareStatement(sql2);) {
                    ps.setString(1, String.valueOf(kullanici.tc_no));
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){}
                    else {diyetYap.setVisible(true);}
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if(kullanici.egzersizOneri != null){
                String sql2 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ? AND tarih = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps = conn.prepareStatement(sql2);) {
                    ps.setString(1, String.valueOf(kullanici.tc_no));
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){}
                    else {egzersizYap.setVisible(true);}
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.setColor(Color.WHITE);

        if(currentScreen == Screen.MAIN){

            for (int i = 0; i < relations.size(); i++) {
                if (kullanici.rol.equals("DOKTOR")) {g.setColor(Color.BLUE);}
                else if (kullanici.rol.equals("HASTA")){g.setColor(Color.RED);}
                g.fillRect(relationsRects.get(i).x, relationsRects.get(i).y, relationsRects.get(i).width, relationsRects.get(i).height);
                g.setColor(Color.WHITE);
                g.drawImage(relations.get(i).profil_resmi,20, relationsRects.get(i).y + 20,this);
                g.drawString("Ad Soyad: " + relations.get(i).ad + " " + relations.get(i).soyad, 150, relationsRects.get(i).y + 20);
                g.drawString("TC Kimlik: " + relations.get(i).tc_no, 150, relationsRects.get(i).y + 40);
                g.drawString("Cinsiyet: " + relations.get(i).cinsiyet, 150, relationsRects.get(i).y + 60);
                g.drawString("Doğum Tarihi: " + relations.get(i).dogum_tarihi, 150, relationsRects.get(i).y + 80);
                g.drawString("E-Posta: " + relations.get(i).email, 150, relationsRects.get(i).y + 100);
                g.drawString("Rol: " + relations.get(i).rol, 150, relationsRects.get(i).y + 120);
            }

            g.setColor(Color.BLACK);
            g.fillRect(0,0,WIDTH,kullaniciRect.height + kullaniciRect.y);

            if (kullanici.rol.equals("DOKTOR")) {g.setColor(Color.RED);}
            else if (kullanici.rol.equals("HASTA")){g.setColor(Color.BLUE);}
            g.fillRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height);
            g.drawImage(kullanici.profil_resmi,20,150,this);
            g.setColor(Color.WHITE);
            g.drawString("Ad Soyad: " + kullanici.ad + " " + kullanici.soyad, 150,150);
            g.drawString("TC Kimlik: " + kullanici.tc_no, 150,170);
            g.drawString("Cinsiyet: " + kullanici.cinsiyet, 150,190);
            g.drawString("Doğum Tarihi: " + kullanici.dogum_tarihi, 150,210);
            g.drawString("E-Posta: " + kullanici.email, 150,230);
            g.drawString("Rol: " + kullanici.rol, 150,250);

        } else if(currentScreen == Screen.HASTA_EKLE){
            g.drawString("HASTA EKLEME",WIDTH/2-40,90);
            g.drawString("TC Kimlik:", WIDTH/2-115,120);
            g.drawString("Ölçüm Girişi:", WIDTH/2-515,120);
            g.drawString("Belirti 1:", WIDTH/2-515,170);
            g.drawString("Belirti 2:", WIDTH/2-515,225);
            g.drawString("Belirti 3:", WIDTH/2-515,280);
            g.drawString("mg/dL", WIDTH/2-260,140);
            g.drawString("Ad:", WIDTH/2-115,170);
            g.drawString("Soyad:", WIDTH/2-115,220);
            g.drawString("Şifre:", WIDTH/2-115,270);
            g.drawString("EMail:", WIDTH/2-115,320);
            g.drawString("Dogum Tarihi:", WIDTH/2-115,370);
            if(dogumSqlDate != null){g.drawString("Seçilen Tarih: " + dogumSqlDate,WIDTH/2-115,420);}
            g.drawString("Cinsiyet:", WIDTH/2-115,440);
            g.drawString("Profil Resmi:", WIDTH/2-115,495);
            if(selectedFile != null){g.drawString("Seçilen Dosya: " + selectedFile.getName(), WIDTH/2-115,545);}
            if(hastaError == 1){
                g.setColor(Color.GREEN);
                g.drawString("Hasta girişi başarılı", WIDTH/2-115,615);
            } else if (hastaError == -1) {
                g.setColor(Color.RED);
                g.drawString("Hasta girişi başarısız", WIDTH/2-115,615);
            }
        } else if (currentScreen == Screen.HASTA_PROFIL) {
            g.setColor(Color.BLUE);
            g.fillRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height);
            g.drawImage(relations.get(secilenHasta).profil_resmi,20,150,this);
            g.setColor(Color.WHITE);
            g.drawString("Ad Soyad: " + relations.get(secilenHasta).ad + " " + relations.get(secilenHasta).soyad, 150,150);
            g.drawString("TC Kimlik: " + relations.get(secilenHasta).tc_no, 150,170);
            g.drawString("Cinsiyet: " + relations.get(secilenHasta).cinsiyet, 150,190);
            g.drawString("Doğum Tarihi: " + relations.get(secilenHasta).dogum_tarihi, 150,210);
            g.drawString("E-Posta: " + relations.get(secilenHasta).email, 150,230);
            g.drawString("Rol: " + relations.get(secilenHasta).rol, 150,250);
            if(relations.get(secilenHasta).belirtiler.size() > 0){g.drawString("Belirti 1: " + relations.get(secilenHasta).belirtiler.get(0), 20,300);}
            if(relations.get(secilenHasta).belirtiler.size() > 1){g.drawString("Belirti 2: " + relations.get(secilenHasta).belirtiler.get(1), 20,320);}
            if(relations.get(secilenHasta).belirtiler.size() > 2){g.drawString("Belirti 3: " + relations.get(secilenHasta).belirtiler.get(2), 20,340);}
            g.drawString("Ölçüm Seç: ", 20,360);
            if(relations.get(secilenHasta).olcumTarihleri.size() > 0){g.drawString("Ölçüm Tarihi: " + relations.get(secilenHasta).olcumTarihleri.get(olcumSecme.getSelectedIndex()), 20,410);}
            if(relations.get(secilenHasta).olcumUyarilar.size() > 0){g.drawString(relations.get(secilenHasta).olcumUyarilar.get(olcumSecme.getSelectedIndex()) + ": " + relations.get(secilenHasta).olcumUyariAciklamalar.get(olcumSecme.getSelectedIndex()), 20,430);}
            g.drawString("Önerilen Diyet: " + onerilenDiyet, 20,470);
            g.drawString("Önerilen Egzersiz: " + onerilenEgzersiz, 20,490);
            g.drawString("Diyet Geçmişi:", 20,520);
            g.drawString("Egzersiz Geçmişi:", 20,570);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,25));
        g.drawString("Diyabet Sistemi",550,40);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.drawString("Current Date: " + formatter.format(selectedDateTime[0]), 960,40);
        if(kullanici.rol.equals("HASTA")){
            if(kullanici.belirtiler.size() > 0){g.drawString("Belirti 1: " + kullanici.belirtiler.get(0), 20,430);}
            if(kullanici.belirtiler.size() > 1){g.drawString("Belirti 2: " + kullanici.belirtiler.get(1), 20,450);}
            if(kullanici.belirtiler.size() > 2){g.drawString("Belirti 3: " + kullanici.belirtiler.get(2), 20,470);}
            g.drawString("Ölçüm Seç: ", 20,490);
            if(kullanici.olcumTarihleri.size() > 0){g.drawString("Ölçüm Tarihi: " + kullanici.olcumTarihleri.get(olcumSecme.getSelectedIndex()), 20,540);}
            g.drawString("Ölçüm Giriş: ", 20,565);
            g.drawString("mg/dL", 280,595);
            if(olcumGirildiMi == 1){
                g.setColor(Color.GREEN);
                g.drawString("Ölçüm Girildi", 450,595);
            } else if(olcumGirildiMi == 0){
                g.setColor(Color.RED);
                g.drawString("Ölçüm Girilemedi", 450,595);
            }
            g.setColor(Color.WHITE);
            if(kullanici.egzersizOneri != null){g.drawString("Önerilen Egzersiz: " + kullanici.egzersizOneri, 20,630);}
            if(kullanici.diyetOneri != null){g.drawString("Önerilen Diyet: " + kullanici.diyetOneri, 20,660);}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == hastaEkle){
            currentScreen = Screen.HASTA_EKLE;
            TC_Giris.setVisible(true);
            adGiris.setVisible(true);
            soyadGiris.setVisible(true);
            sifreGiris.setVisible(true);
            emailGiris.setVisible(true);
            dogumSecimButton.setVisible(true);
            cinsiyetGiris.setVisible(true);
            profilSecimi.setVisible(true);
            girisYap.setVisible(true);
            geriButton.setVisible(true);
            hastaEkle.setVisible(false);
            cikisButton.setVisible(false);
            olcumGiris.setVisible(true);
            belirti_1_giris.setVisible(true);
            belirti_2_giris.setVisible(true);
            belirti_3_giris.setVisible(true);
            repaint();
        } else if (e.getSource() == geriButton) {
            currentScreen = Screen.MAIN;
            initialize();
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
            dogumSecimButton.setVisible(false);
            dogumSqlDate = null;
            cinsiyetGiris.setVisible(false);
            cinsiyetGiris.setSelectedIndex(0);
            profilSecimi.setVisible(false);
            selectedFile = null;
            girisYap.setVisible(false);
            geriButton.setVisible(false);
            cikisButton.setVisible(true);
            olcumGiris.setVisible(false);
            olcumGiris.setText("");
            belirti_1_giris.setVisible(false);
            belirti_1_giris.setSelectedIndex(0);
            belirti_2_giris.setVisible(false);
            belirti_2_giris.setSelectedIndex(0);
            belirti_3_giris.setVisible(false);
            belirti_3_giris.setSelectedIndex(0);
            hastaError = 0;
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
        } else if (e.getSource() == dogumSecimButton) {
            JDateChooser dateChooser = new JDateChooser();
            JDialog dialog = new JDialog(Main.frame, "Pick a Date", true);
            dialog.setLayout(new BorderLayout());
            JCalendar calendar = new JCalendar();
            dialog.add(calendar, BorderLayout.CENTER);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(ev -> {
                dateChooser.setDate(calendar.getDate());
                java.util.Date utilDate = dateChooser.getDate();
                dogumSqlDate = new java.sql.Date(utilDate.getTime());
                dialog.dispose();
                repaint();
            });

            dialog.add(okButton, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(Main.frame);
            dialog.setVisible(true);
        } else if (e.getSource() == girisYap) {
            //Bu hata kontrol için sonra test ederim (etmedi)
            if(TC_Giris.getText().length() == 11 && !adGiris.getText().equals("") && !soyadGiris.getText().equals("") && !sifreGiris.getPassword().equals("") && !emailGiris.getText().equals("") && dogumSqlDate != null && selectedFile != null) {
                String sql = "INSERT INTO KULLANICI (tc_no, ad, soyad, sifre, email, dogum_tarihi, cinsiyet, profil_resmi, rol) " +
                        "VALUES (?, ?, ?, HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?)), ?, ?, ?, ?, 'HASTA')";
                String sql1 = "INSERT INTO HASTA_DOKTOR (doktor_tc, hasta_tc)" +
                        "VALUES (?, ?)";
                String sql2 = "INSERT INTO HASTA_BELIRTI (hasta_tc, belirti_turu_id)" +
                        "VALUES (?, ?)";
                String sql3 = "INSERT INTO HASTA_OLCUM (hasta_tc, olcum_tarihi, uyari_turu_id, olcum_degeri)" +
                        "VALUES (?, ?, ?, ?)";
                try (Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); //DOKTOR
                     PreparedStatement ps = conn.prepareStatement(sql);
                     PreparedStatement ps1 = conn.prepareStatement(sql1);
                     PreparedStatement ps2 = conn.prepareStatement(sql2);
                     PreparedStatement ps3 = conn.prepareStatement(sql3)) {

                    FileInputStream fis = new FileInputStream(selectedFile);
                    ps.setString(1, TC_Giris.getText());
                    ps.setString(2, adGiris.getText());
                    ps.setString(3, soyadGiris.getText());
                    ps.setString(4, new String(sifreGiris.getPassword()));
                    ps.setString(5, emailGiris.getText());
                    ps.setString(6, String.valueOf(dogumSqlDate));
                    if (cinsiyetGiris.getSelectedIndex() == 0) {
                        ps.setString(7, "E");
                    } else {
                        ps.setString(7, "K");
                    }
                    ps.setBinaryStream(8, fis, (int) selectedFile.length());

                    ps1.setString(1, Main.enUserName);
                    ps1.setString(2, TC_Giris.getText());

                    int affectedRows = ps.executeUpdate();
                    ps1.executeUpdate();
                    if (affectedRows > 0) {

                        ps2.setString(1,TC_Giris.getText());
                        if(belirti_1_giris.getSelectedIndex() != 0){
                            ps2.setString(2,String.valueOf(belirti_1_giris.getSelectedIndex()));
                            ps2.executeUpdate();
                        }
                        if(belirti_2_giris.getSelectedIndex() != 0){
                            ps2.setString(2,String.valueOf(belirti_2_giris.getSelectedIndex()));
                            ps2.executeUpdate();
                        }
                        if(belirti_3_giris.getSelectedIndex() != 0){
                            ps2.setString(2,String.valueOf(belirti_3_giris.getSelectedIndex()));
                            ps2.executeUpdate();
                        }

                        ps3.setString(1,TC_Giris.getText());
                        ps3.setTimestamp(2,new java.sql.Timestamp(selectedDateTime[0].getTime()));
                        if(Integer.valueOf(olcumGiris.getText()) < 70){
                            ps3.setString(3,"1");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 70 && Integer.valueOf(olcumGiris.getText()) <= 110) {
                            ps3.setString(3,"2");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 111 && Integer.valueOf(olcumGiris.getText()) <= 150) {
                            ps3.setString(3,"3");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 151 && Integer.valueOf(olcumGiris.getText()) <= 200) {
                            ps3.setString(3,"4");
                        } else if (Integer.valueOf(olcumGiris.getText()) > 200) {
                            ps3.setString(3,"5");
                        }
                        ps3.setString(4,olcumGiris.getText());
                        ps3.executeUpdate();

                        System.out.println("Kullanıcı başarıyla eklendi.");
                        EmailSender.sendEmail(emailGiris.getText(), "Diyabet Sistemi Girişiniz Başarılı", "Merhaba " + adGiris.getText() + " " + soyadGiris.getText()
                                + " !\nGiriş şifreniz: " + new String(sifreGiris.getPassword()) + "\n\nDiyabet Sistemi");
                    } else {
                        System.out.println("Kullanıcı eklenemedi.");
                    }
                    conn.commit();
                    hastaError = 1;
                    repaint();
                } catch (SQLException ex) {
                    hastaError = -1;
                    repaint();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                hastaError = -1;
                repaint();
            }
        } else if (e.getSource() == cikisButton) {
            if(currentScreen == Screen.MAIN){
                int result = JOptionPane.showConfirmDialog(null, "Çıkış yapmak istediğinize emin misiniz?", "Çıkış Yapma", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    olcumGirildiMi = -1;
                    Main.frame.switchScreen(0);
                }
            }
            else if (currentScreen == Screen.HASTA_PROFIL) {
                olcumSecme.setVisible(false);
                olcumSecme.removeAllItems();
                oneriYap.setVisible(false);
                hastaEkle.setVisible(true);
                diyetGecmis.setVisible(false);
                egzersizGecmis.setVisible(false);
                diyetGecmis.removeAllItems();
                egzersizGecmis.removeAllItems();
                cikisButton.setText("Çıkış Yap");
                currentScreen = Screen.MAIN;
                repaint();
            }
        } else if (e.getSource() == selectDate) {
            JDialog dialog = new JDialog(Main.frame, "Gün ve Saat Seç", true);
            dialog.setLayout(new BorderLayout());

            JCalendar calendar = new JCalendar();
            calendar.setDate(selectedDateTime[0]);
            calendar.setMinSelectableDate(selectedDateTime[0]);

            JPanel timePanel = new JPanel();
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDateTime[0]);

            Calendar now = Calendar.getInstance();
            int currentHour = now.get(Calendar.HOUR_OF_DAY);

            int initialHour = now.get(Calendar.HOUR_OF_DAY);
            SpinnerNumberModel hourModel = new SpinnerNumberModel(initialHour, currentHour, 23, 1);
            JSpinner hourSpinner = new JSpinner(hourModel);

            Runnable updateHourModel = () -> {
                Calendar selected = Calendar.getInstance();
                selected.setTime(calendar.getDate());

                boolean isToday =
                        selected.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                                selected.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);

                if (isToday) {
                    int safeHour = Math.max(currentHour, (int) hourSpinner.getValue());
                    hourSpinner.setModel(new SpinnerNumberModel(safeHour, currentHour, 23, 1));
                } else {
                    int safeHour = (int) hourSpinner.getValue();
                    hourSpinner.setModel(new SpinnerNumberModel(safeHour, 0, 23, 1));
                }
            };

            updateHourModel.run();

            calendar.getDayChooser().addPropertyChangeListener("day", evt -> updateHourModel.run());

            timePanel.add(new JLabel("Saat:"));
            timePanel.add(hourSpinner);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(ev -> {
                Date dateOnly = calendar.getDate();

                Calendar newDateTime = Calendar.getInstance();
                newDateTime.setTime(dateOnly);
                newDateTime.set(Calendar.HOUR_OF_DAY, (int) hourSpinner.getValue());
                newDateTime.set(Calendar.MINUTE, 0);
                newDateTime.set(Calendar.SECOND, 0);
                newDateTime.set(Calendar.MILLISECOND, 0);

                //KONTROL
                if(kullanici.rol.equals("HASTA")){
                    String sql = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ? AND tarih = ?";
                    String sql2 = "INSERT INTO HASTA_DIYET_CHECK (hasta_tc, tarih, yapildi_mi) VALUES (?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                        PreparedStatement ps = conn.prepareStatement(sql);
                         PreparedStatement ps1 = conn.prepareStatement(sql2)) {
                        ps.setString(1, String.valueOf(kullanici.tc_no));
                        ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                        ResultSet rs = ps.executeQuery();
                        if(rs.next()){}
                        else {
                            ps1.setString(1,String.valueOf(kullanici.tc_no));
                            ps1.setString(2,String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                            ps1.setString(3,"0");
                            ps1.executeUpdate();
                            conn.commit();
                        }
                        Date insertedDate = newDateTime.getTime();
                        ps.setString(2, String.valueOf(new java.sql.Timestamp(insertedDate.getTime())));
                        rs = ps.executeQuery();
                        if(rs.next()){diyetYap.setVisible(false);}
                        else{diyetYap.setVisible(true);}
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    String sql3 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ? AND tarih = ?";
                    String sql4 = "INSERT INTO HASTA_EGZERSIZ_CHECK (hasta_tc, tarih, yapildi_mi) VALUES (?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                         PreparedStatement ps = conn.prepareStatement(sql3);
                         PreparedStatement ps1 = conn.prepareStatement(sql4)) {
                        ps.setString(1, String.valueOf(kullanici.tc_no));
                        ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                        ResultSet rs = ps.executeQuery();
                        if(rs.next()){}
                        else {
                            ps1.setString(1,String.valueOf(kullanici.tc_no));
                            ps1.setString(2,String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                            ps1.setString(3,"0");
                            ps1.executeUpdate();
                            conn.commit();
                        }
                        Date insertedDate = newDateTime.getTime();
                        ps.setString(2, String.valueOf(new java.sql.Timestamp(insertedDate.getTime())));
                        rs = ps.executeQuery();
                        if(rs.next()){egzersizYap.setVisible(false);}
                        else{egzersizYap.setVisible(true);}
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else if (kullanici.rol.equals("DOKTOR")) {

                }
                dialog.dispose();
                selectedDateTime[0] = newDateTime.getTime();
                repaint();
            });

            dialog.add(calendar, BorderLayout.CENTER);
            dialog.add(timePanel, BorderLayout.NORTH);
            dialog.add(okButton, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(Main.frame);
            dialog.setVisible(true);
        } else if (e.getSource() == olcumGir) {
            String sql1 = "SELECT hasta_tc, olcum_tarihi FROM HASTA_OLCUM WHERE hasta_tc = ? AND olcum_tarihi = ?";
            try (Connection conn1 = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                 PreparedStatement ps1 = conn1.prepareStatement(sql1)) {

                ps1.setString(1, String.valueOf(kullanici.tc_no));
                ps1.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));

                ResultSet rs = ps1.executeQuery();
                if(rs.next() || olcumGiris.getText().equals("")){
                    olcumGirildiMi = 0;
                    repaint();
                }else {
                    String sql = "INSERT INTO HASTA_OLCUM (hasta_tc, olcum_tarihi, uyari_turu_id, olcum_degeri)" +
                            "VALUES (?, ?, ?, ?)";
                    try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                         PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setString(1, String.valueOf(kullanici.tc_no));
                        ps.setTimestamp(2,new java.sql.Timestamp(selectedDateTime[0].getTime()));
                        if(Integer.valueOf(olcumGiris.getText()) < 70){
                            ps.setString(3,"1");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 70 && Integer.valueOf(olcumGiris.getText()) <= 110) {
                            ps.setString(3,"2");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 111 && Integer.valueOf(olcumGiris.getText()) <= 150) {
                            ps.setString(3,"3");
                        } else if (Integer.valueOf(olcumGiris.getText()) >= 151 && Integer.valueOf(olcumGiris.getText()) <= 200) {
                            ps.setString(3,"4");
                        } else if (Integer.valueOf(olcumGiris.getText()) > 200) {
                            ps.setString(3,"5");
                        }
                        ps.setString(4,olcumGiris.getText());
                        ps.executeUpdate();
                        conn.commit();
                        initialize();
                        olcumGirildiMi = 1;
                        repaint();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource() == oneriYap) {
            String sql = "INSERT INTO HASTA_EGZERSIZ (hasta_tc, egzersiz_turu_id)" +
                    "VALUES (?, ?)";
            String sql1 = "INSERT INTO HASTA_DIYET (hasta_tc, diyet_turu_id)" +
                    "VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); //DOKTOR GIRISI
                 PreparedStatement ps = conn.prepareStatement(sql);
                 PreparedStatement ps1 = conn.prepareStatement(sql1)) {

                ps.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                ps1.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));

                if(relations.get(secilenHasta).olcumler.get(0) < 70){
                    if(relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                        ps1.setString(2,"3");
                    }
                } else if (relations.get(secilenHasta).olcumler.get(0) >= 70 && relations.get(secilenHasta).olcumler.get(0) < 110) {
                    if(relations.get(secilenHasta).belirtiler.contains("Kilo Kaybı") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                        ps1.setString(2,"1");
                        ps.setString(2,"1");
                    }
                    if (relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                        ps1.setString(2,"3");
                        ps.setString(2,"1");
                    }
                } else if (relations.get(secilenHasta).olcumler.get(0) >= 110 && relations.get(secilenHasta).olcumler.get(0) < 180) {
                    if(relations.get(secilenHasta).belirtiler.contains("Bulanık Görme") && relations.get(secilenHasta).belirtiler.contains("Nöropati")){
                        ps1.setString(2,"1");
                        ps.setString(2,"3");
                    }
                    if (relations.get(secilenHasta).belirtiler.contains("Poliüri") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                        ps1.setString(2,"2");
                        ps.setString(2,"1");
                    }
                    if (relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk") && relations.get(secilenHasta).belirtiler.contains("Bulanık Görme")) {
                        ps1.setString(2,"1");
                        ps.setString(2,"1");
                    }
                } else if (relations.get(secilenHasta).olcumler.get(0) >= 180) {
                    if(relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi") && relations.get(secilenHasta).belirtiler.contains("Polifaji")){
                        ps1.setString(2,"2");
                        ps.setString(2,"3");
                    }
                    if (relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                        ps1.setString(2,"2");
                        ps.setString(2,"1");
                    }
                }
                ps.executeUpdate();
                ps1.executeUpdate();
                conn.commit();
                relations.get(secilenHasta).oneriGirdiMi = 1;
                oneriYap.setVisible(false);
                repaint();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            String sql2 = "SELECT D.tur_adi FROM HASTA_DIYET H, DIYET_TURU D WHERE H.hasta_tc = ? AND H.diyet_turu_id = D.diyet_turu_id";
            String sql3 = "SELECT E.tur_adi FROM HASTA_EGZERSIZ H, EGZERSIZ_TURU E WHERE H.hasta_tc = ? AND H.egzersiz_turu_id = E.egzersiz_turu_id";
            try (
                    Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); // DOKTOR
                    PreparedStatement ps = conn.prepareStatement(sql2);
                    PreparedStatement ps1 = conn.prepareStatement(sql3)
            ) {
                ps.setString(1,String.valueOf(relations.get(secilenHasta).tc_no));
                ps1.setString(1,String.valueOf(relations.get(secilenHasta).tc_no));
                ResultSet rs2 = ps.executeQuery();
                ResultSet rs3 = ps1.executeQuery();
                if(rs2.next()){relations.get(secilenHasta).diyetOneri = rs2.getString("tur_adi");}
                if (rs3.next()){relations.get(secilenHasta).egzersizOneri = rs3.getString("tur_adi");}
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if(e.getSource() == diyetYap){
            String sql = "INSERT INTO HASTA_DIYET_CHECK (hasta_tc, tarih, yapildi_mi)" +
                    "VALUES (?, ?, ?)";
            try (
                    Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                    PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                ps.setString(3, "1");
                ps.executeUpdate();
                conn.commit();
                diyetYap.setVisible(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == egzersizYap) {
            String sql = "INSERT INTO HASTA_EGZERSIZ_CHECK (hasta_tc, tarih, yapildi_mi)" +
                    "VALUES (?, ?, ?)";
            try (
                    Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                    PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                ps.setString(3, "1");
                ps.executeUpdate();
                conn.commit();
                egzersizYap.setVisible(false);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if(relationsRects.size() != 0 && currentScreen == Screen.MAIN){
            if((relationsRects.get(0).y >= 270 && notches > 0) || relationsRects.get(0).y != 270){
                if((relationsRects.get(relationsRects.size()-1).y >= 270 && notches < 0) || relationsRects.get(relationsRects.size()-1).y != 270){
                    for (int i = 0; i < relationsRects.size(); i++) {
                        relationsRects.get(i).y -= notches*20;
                        repaint();
                    }
                }
            }
        }
    }
}
