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
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    Kullanici kullanici;
    Rectangle kullaniciRect = new Rectangle(10,130,700,130);
    ArrayList<Kullanici> relations = new ArrayList<>();
    ArrayList<Rectangle> relationsRects = new ArrayList<>();
    JButton hastaEkle = new JButton("Hasta Ekle"), girisYap = new JButton("Hasta Ekle"), geriButton = new JButton("Geri"), profilSecimi = new JButton("Seç"), cikisButton = new JButton("Çıkış Yap"), selectDate = new JButton("Tarih Seç");
    JTextField TC_Giris = new JTextField(), adGiris = new JTextField(), soyadGiris = new JTextField(), emailGiris = new JTextField();
    JComboBox<String> cinsiyetGiris = new JComboBox<String>();
    JPasswordField sifreGiris = new JPasswordField();
    JButton dogumSecimButton = new JButton("Doğum Tarihi Seç");
    Date dogumSqlDate = null;
    final int kullanici_limit = 11, sifre_limit = 15;
    int hastaError = 0;
    int secilenHasta = 0;
    File selectedFile = null;
    Date[] selectedDateTime = {new Date()};
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");

    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
        this.addMouseWheelListener(this);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                for (int i = 0; i< relationsRects.size(); i++) {
                    if (relationsRects.get(i).contains(e.getPoint()) && relationsRects.get(i).y > 130 && kullanici.rol.equals("DOKTOR") && currentScreen == Screen.MAIN) {
                        currentScreen = Screen.HASTA_PROFIL;
                        cikisButton.setText("Geri");
                        secilenHasta = i;
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
        cinsiyetGiris.setBounds(WIDTH/2-115,450,250,20);
        cinsiyetGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        cinsiyetGiris.setVisible(false);
        cinsiyetGiris.setFocusable(false);
        cinsiyetGiris.addItem("Erkek");
        cinsiyetGiris.addItem("Kadın");
        this.add(cinsiyetGiris);

        profilSecimi.setPreferredSize(new Dimension(10,300));
        profilSecimi.setBounds(WIDTH/2-115,500,250,20);
        profilSecimi.setFont(new Font("Calibri",Font.PLAIN,15));
        profilSecimi.setVisible(false);
        profilSecimi.setFocusable(false);
        profilSecimi.addActionListener(this);
        this.add(profilSecimi);

        girisYap.setBounds(WIDTH/2+35,550,100,25);
        girisYap.setFont(new Font("Calibri",Font.BOLD,15));
        girisYap.setHorizontalAlignment(SwingConstants.CENTER);
        girisYap.setFocusable(false);
        girisYap.setVisible(false);
        girisYap.addActionListener(this);
        this.add(girisYap);

        geriButton.setBounds(WIDTH/2-115,550,100,25);
        geriButton.setFont(new Font("Calibri",Font.BOLD,15));
        geriButton.setHorizontalAlignment(SwingConstants.CENTER);
        geriButton.setFocusable(false);
        geriButton.setVisible(false);
        geriButton.addActionListener(this);
        this.add(geriButton);
    }
    public void initialize(){
        String sql = "SELECT tc_no, ad, soyad, email, dogum_tarihi, cinsiyet, profil_resmi, rol FROM KULLANICI " +
                "WHERE tc_no = ? AND sifre_hash = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))";
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
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

        String sql1 = "";
        if(kullanici.rol.equals("DOKTOR")){
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, K.dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.hasta_tc AND H.doktor_tc = ?";
        } else if (kullanici.rol.equals("HASTA")) {
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, K.dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.doktor_tc AND H.hasta_tc = ?";
        }
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
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
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(kullanici.rol.equals("DOKTOR")){hastaEkle.setVisible(true);}
        else if (kullanici.rol.equals("HASTA")) {hastaEkle.setVisible(false);}
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
            g.drawString("Ad:", WIDTH/2-115,170);
            g.drawString("Soyad:", WIDTH/2-115,220);
            g.drawString("Şifre:", WIDTH/2-115,270);
            g.drawString("EMail:", WIDTH/2-115,320);
            g.drawString("Dogum Tarihi:", WIDTH/2-115,370);
            if(dogumSqlDate != null){g.drawString("Seçilen Tarih: " + dogumSqlDate,WIDTH/2-115,420);}
            g.drawString("Cinsiyet:", WIDTH/2-115,440);
            g.drawString("Profil Resmi:", WIDTH/2-115,490);
            if(selectedFile != null){g.drawString("Seçilen Dosya: " + selectedFile.getName(), WIDTH/2-115,540);}
            if(hastaError == 1){
                g.setColor(Color.GREEN);
                g.drawString("Hasta girişi başarılı", WIDTH/2-115,610);
            } else if (hastaError == -1) {
                g.setColor(Color.RED);
                g.drawString("Hasta girişi başarısız", WIDTH/2-115,610);
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
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,25));
        g.drawString("Diyabet Sistemi",550,40);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.drawString("Current Date: " + formatter.format(selectedDateTime[0]), 960,40);
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
            profilSecimi.setVisible(false);
            selectedFile = null;
            girisYap.setVisible(false);
            geriButton.setVisible(false);
            cikisButton.setVisible(true);
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
            //if(TC_Giris.getText().length() == 11 && !adGiris.getText().equals("") && !soyadGiris.getText().equals("") && !sifreGiris.getPassword().equals("") && !emailGiris.getText().equals("") && dogumSqlDate != null && selectedFile != null){}
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
                ps.setString(4, new String(sifreGiris.getPassword()));
                ps.setString(5, emailGiris.getText());
                ps.setString(6, String.valueOf(dogumSqlDate));
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
        } else if (e.getSource() == cikisButton) {
            if(currentScreen == Screen.MAIN){
                int result = JOptionPane.showConfirmDialog(null, "Çıkış yapmak istediğinize emin misiniz?", "Çıkış Yapma", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    Main.frame.switchScreen(0);
                }
            }
            else if (currentScreen == Screen.HASTA_PROFIL) {
                cikisButton.setText("Çıkış Yap");
                currentScreen = Screen.MAIN;
                repaint();
            }
        } else if (e.getSource() == selectDate) {
            JDialog dialog = new JDialog(Main.frame, "Choose Date and Time", true);
            dialog.setLayout(new BorderLayout());

            JCalendar calendar = new JCalendar();
            calendar.setDate(selectedDateTime[0]);

            JPanel timePanel = new JPanel();
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDateTime[0]);

            SpinnerNumberModel hourModel = new SpinnerNumberModel(cal.get(Calendar.HOUR_OF_DAY), 0, 23, 1);
            SpinnerNumberModel minuteModel = new SpinnerNumberModel(cal.get(Calendar.MINUTE), 0, 59, 1);

            JSpinner hourSpinner = new JSpinner(hourModel);
            JSpinner minuteSpinner = new JSpinner(minuteModel);

            timePanel.add(new JLabel("Hour:"));
            timePanel.add(hourSpinner);
            timePanel.add(new JLabel("Minute:"));
            timePanel.add(minuteSpinner);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(ev -> {
                Date dateOnly = calendar.getDate();

                Calendar newDateTime = Calendar.getInstance();
                newDateTime.setTime(dateOnly);
                newDateTime.set(Calendar.HOUR_OF_DAY, (int) hourSpinner.getValue());
                newDateTime.set(Calendar.MINUTE, (int) minuteSpinner.getValue());
                newDateTime.set(Calendar.SECOND, 0);
                newDateTime.set(Calendar.MILLISECOND, 0);

                selectedDateTime[0] = newDateTime.getTime();
                dialog.dispose();
                repaint();
            });

            dialog.add(calendar, BorderLayout.CENTER);
            dialog.add(timePanel, BorderLayout.NORTH);
            dialog.add(okButton, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(Main.frame);
            dialog.setVisible(true);
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
