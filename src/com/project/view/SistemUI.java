package com.project.view;

import com.project.model.Kullanici;
import com.project.main.Main;
import com.project.util.EmailSender;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class SistemUI extends JPanel implements ActionListener , MouseWheelListener {
    enum Screen {MAIN, HASTA_EKLE, HASTA_PROFIL}
    Screen currentScreen = Screen.MAIN;
    int WIDTH;
    int HEIGHT;
    public boolean doktorMu = false; //false=HASTA, true=DOKTOR
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    Kullanici kullanici;
    Rectangle kullaniciRect = new Rectangle(10,130,620,130);
    ArrayList<Kullanici> relations = new ArrayList<>();
    ArrayList<Rectangle> relationsRects = new ArrayList<>();
    RoundedButton hastaEkle = new RoundedButton("Hasta Ekle"), girisYap = new RoundedButton("Hasta Ekle"), geriButton = new RoundedButton("Geri"), profilSecimi = new RoundedButton("Seç"), cikisButton = new RoundedButton("Çıkış Yap"), selectDate = new RoundedButton("Tarih Seç"), olcumGir = new RoundedButton("Kayıt Et"), oneriYap = new RoundedButton("Öneri Yap"), diyetYap = new RoundedButton("Diyet Yap"), egzersizYap = new RoundedButton("Egzersiz Yap"), graphGoster = new RoundedButton("Kan şekeri grafiği"), dogumSecimButton = new RoundedButton("Doğum Tarihi Seç"), sifreDegistirButton = new RoundedButton("Şifre Değiştir");
    JTextField TC_Giris = new JTextField(), adGiris = new JTextField(), soyadGiris = new JTextField(), emailGiris = new JTextField(), olcumGiris = new JTextField(), sifreDegistir = new JTextField();
    JComboBox<String> cinsiyetGiris = new JComboBox<>(), belirti_1_giris = new JComboBox<>(), belirti_2_giris = new JComboBox<>(), belirti_3_giris = new JComboBox<>(), belirtiFiltreleme = new JComboBox<>(), diyetGecmis = new JComboBox<>(), egzersizGecmis = new JComboBox<>(), tarihSec = new JComboBox<>(), olcumFiltreleme = new JComboBox<>();
    JPasswordField sifreGiris = new JPasswordField();
    final String doktorUser = "doktor_login", hastaUser = "hasta_login", doktorPassword = "doktor123", hastaPassword = "hasta123";
    String dogumSqlDate = "",gunlukUyari = "",gunlukUyariAciklama="";
    int kullanici_limit = 11, sifre_limit = 15, hastaError = 0, secilenHasta = 0, olcumGirildiMi = -1, diyetYapildi = -1, egzersizYapildi = -1, sifreDegistirildiMi=-1;
    File selectedFile = null;
    Date[] selectedDateTime;
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
    Image background;

    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
        this.addMouseWheelListener(this);

        try {background = ImageIO.read(new File("textures/saglik_bakanligi.png"));}
        catch (IOException e) {throw new RuntimeException(e);}

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        selectedDateTime = new Date[]{cal.getTime()};

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                for (int i = 0; i< relationsRects.size(); i++) {
                    if (relationsRects.get(i).contains(e.getPoint()) && relationsRects.get(i).y >= 130 && kullanici.rol.equals("DOKTOR") && currentScreen == Screen.MAIN) {
                        belirtiFiltreleme.setVisible(false);
                        olcumFiltreleme.setVisible(false);
                        currentScreen = Screen.HASTA_PROFIL;
                        cikisButton.setText("Geri");
                        hastaEkle.setVisible(false);
                        graphGoster.setVisible(true);
                        secilenHasta = i;
                        if(!relations.get(secilenHasta).oneriGirdiMi){
                            oneriYap.setVisible(true);
                            diyetGecmis.setVisible(true);
                            egzersizGecmis.setVisible(true);
                        }
                        if(relations.get(secilenHasta).olcumler.get(0) < 70){
                            if(relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                                diyetGecmis.setSelectedItem("Dengeli Beslenme");
                                egzersizGecmis.setSelectedIndex(0);
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 70 && relations.get(secilenHasta).olcumler.get(0) < 110) {
                            if(relations.get(secilenHasta).belirtiler.contains("Kilo Kaybı") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk")){
                                diyetGecmis.setSelectedItem("Az Şekerli Diyet");
                                egzersizGecmis.setSelectedItem("Yürüyüş");
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Polifaji") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                diyetGecmis.setSelectedItem("Dengeli Beslenme");
                                egzersizGecmis.setSelectedItem("Yürüyüş");
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 110 && relations.get(secilenHasta).olcumler.get(0) < 180) {
                            if(relations.get(secilenHasta).belirtiler.contains("Bulanık Görme") && relations.get(secilenHasta).belirtiler.contains("Nöropati")){
                                diyetGecmis.setSelectedItem("Az Şekerli Diyet");
                                egzersizGecmis.setSelectedItem("Klinik Egzersiz");
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Poliüri") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                diyetGecmis.setSelectedItem("Şekersiz Diyet");
                                egzersizGecmis.setSelectedItem("Klinik Egzersiz");
                            }
                            if (relations.get(secilenHasta).belirtiler.contains("Nöropati") && relations.get(secilenHasta).belirtiler.contains("Yorgunluk") && relations.get(secilenHasta).belirtiler.contains("Bulanık Görme")) {
                                diyetGecmis.setSelectedItem("Az Şekerli Diyet");
                                egzersizGecmis.setSelectedItem("Yürüyüş");
                            }
                        } else if (relations.get(secilenHasta).olcumler.get(0) >= 180) {
                            if (relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi")) {
                                diyetGecmis.setSelectedItem("Şekersiz Diyet");
                                egzersizGecmis.setSelectedItem("Klinik Egzersiz");
                            }
                            if(relations.get(secilenHasta).belirtiler.contains("Yaraların Yavaş İyileşmesi") && relations.get(secilenHasta).belirtiler.contains("Polidipsi") && relations.get(secilenHasta).belirtiler.contains("Polifaji")){
                                diyetGecmis.setSelectedItem("Şekersiz Diyet");
                                egzersizGecmis.setSelectedItem("Yürüyüş");
                            }
                        }
                        tarihSec.setVisible(true);
                        tarihSec.removeAllItems();
                        tarihUpdate(relations.get(secilenHasta));
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

        hastaEkle.setBounds(520,145,100,20);
        hastaEkle.setFont(new Font("Calibri",Font.BOLD,15));
        hastaEkle.setHorizontalAlignment(SwingConstants.CENTER);
        hastaEkle.setFocusable(false);
        hastaEkle.addActionListener(this);
        this.add(hastaEkle);

        TC_Giris.setPreferredSize(new Dimension(10,300));
        TC_Giris.setBounds(WIDTH/2-115,128,250,25);
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
        olcumGiris.setBounds(WIDTH/2-515,103,250,25);
        olcumGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        olcumGiris.setVisible(false);
        olcumGiris.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (str.matches("\\d+") && getLength() + str.length() <= 3) {
                    super.insertString(offs, str, a);
                }
            }
        });
        this.add(olcumGiris);

        sifreDegistir.setPreferredSize(new Dimension(10,300));
        sifreDegistir.setBounds(660,668,135,25);
        sifreDegistir.setFont(new Font("Calibri",Font.PLAIN,15));
        sifreDegistir.setVisible(false);
        sifreDegistir.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= sifre_limit)
                    super.insertString(offs, str, a);
            }
        });
        this.add(sifreDegistir);

        sifreDegistirButton.setBounds(820,670,110,20);
        sifreDegistirButton.setFont(new Font("Calibri",Font.BOLD,15));
        sifreDegistirButton.setHorizontalAlignment(SwingConstants.CENTER);
        sifreDegistirButton.setVisible(false);
        sifreDegistirButton.setFocusable(false);
        sifreDegistirButton.addActionListener(this);
        this.add(sifreDegistirButton);

        belirti_1_giris.setPreferredSize(new Dimension(10,300));
        belirti_1_giris.setBounds(WIDTH/2-515,178,250,25);
        belirti_1_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_1_giris.setVisible(false);
        belirti_1_giris.setFocusable(false);
        belirti_1_giris.addItem("YOK");
        belirti_1_giris.addActionListener(e -> {
            if(belirti_1_giris.getSelectedIndex() != 0 && (belirti_1_giris.getSelectedIndex() == belirti_2_giris.getSelectedIndex() || belirti_1_giris.getSelectedIndex() == belirti_3_giris.getSelectedIndex())){belirti_1_giris.setSelectedIndex(0);}
        });
        this.add(belirti_1_giris);

        belirti_2_giris.setPreferredSize(new Dimension(10,300));
        belirti_2_giris.setBounds(WIDTH/2-515,233,250,25);
        belirti_2_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_2_giris.setVisible(false);
        belirti_2_giris.setFocusable(false);
        belirti_2_giris.addItem("YOK");
        belirti_2_giris.addActionListener(e -> {
            if(belirti_2_giris.getSelectedIndex() != 0 && (belirti_2_giris.getSelectedIndex() == belirti_1_giris.getSelectedIndex() || belirti_2_giris.getSelectedIndex() == belirti_3_giris.getSelectedIndex())){belirti_2_giris.setSelectedIndex(0);}
        });
        this.add(belirti_2_giris);

        belirti_3_giris.setPreferredSize(new Dimension(10,300));
        belirti_3_giris.setBounds(WIDTH/2-515,283,250,25);
        belirti_3_giris.setFont(new Font("Calibri",Font.PLAIN,15));
        belirti_3_giris.setVisible(false);
        belirti_3_giris.setFocusable(false);
        belirti_3_giris.addItem("YOK");
        belirti_3_giris.addActionListener(e -> {
            if(belirti_3_giris.getSelectedIndex() != 0 && (belirti_3_giris.getSelectedIndex() == belirti_1_giris.getSelectedIndex() || belirti_3_giris.getSelectedIndex() == belirti_2_giris.getSelectedIndex())){belirti_3_giris.setSelectedIndex(0);}
        });
        this.add(belirti_3_giris);

        belirtiFiltreleme.setPreferredSize(new Dimension(10,300));
        belirtiFiltreleme.setBounds(20,300,250,25);
        belirtiFiltreleme.setFont(new Font("Calibri",Font.PLAIN,15));
        belirtiFiltreleme.setVisible(false);
        belirtiFiltreleme.setFocusable(false);
        belirtiFiltreleme.addItem("YOK");
        belirtiFiltreleme.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                olcumFiltreleme.setSelectedIndex(0);
                int temp = 0;
                for (int i = 0; i < relations.size(); i++) {
                    if(belirtiFiltreleme.getSelectedIndex()!=0 && relations.get(i).belirtiler.contains(belirtiFiltreleme.getSelectedItem())){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (belirtiFiltreleme.getSelectedIndex()!=0) {
                        relationsRects.get(i).setBounds(650,-2000,620,130);
                    } else if (belirtiFiltreleme.getSelectedIndex()==0) {
                        relationsRects.get(i).setBounds(650,270 + 140*(i-1),620,130);
                    }
                }
                repaint();
            }
        });
        this.add(belirtiFiltreleme);

        olcumFiltreleme.setPreferredSize(new Dimension(10,300));
        olcumFiltreleme.setBounds(20,350,250,25);
        olcumFiltreleme.setFont(new Font("Calibri",Font.PLAIN,15));
        olcumFiltreleme.setVisible(false);
        olcumFiltreleme.setFocusable(false);
        olcumFiltreleme.addItem("YOK");
        olcumFiltreleme.addItem("< 70 mg/dL");
        olcumFiltreleme.addItem("70-110 mg/dL");
        olcumFiltreleme.addItem("111-150 mg/dL");
        olcumFiltreleme.addItem("151-200 mg/dL");
        olcumFiltreleme.addItem("> 200 mg/dL");
        olcumFiltreleme.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                belirtiFiltreleme.setSelectedIndex(0);
                int temp = 0;
                for (int i = 0; i < relations.size(); i++) {
                    if(olcumFiltreleme.getSelectedIndex()==1 && relations.get(i).olcumler.get(0)<70){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (olcumFiltreleme.getSelectedIndex()==2 && relations.get(i).olcumler.get(0)>=70 && relations.get(i).olcumler.get(0)<=110){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (olcumFiltreleme.getSelectedIndex()==3 && relations.get(i).olcumler.get(0)>=111 && relations.get(i).olcumler.get(0)<=150){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (olcumFiltreleme.getSelectedIndex()==4 && relations.get(i).olcumler.get(0)>=151 && relations.get(i).olcumler.get(0)<=200){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (olcumFiltreleme.getSelectedIndex()==5 && relations.get(i).olcumler.get(0)>200){
                        relationsRects.get(i).setBounds(650,270 + 140*(temp-1),620,130);
                        temp++;
                    } else if (olcumFiltreleme.getSelectedIndex()!=0) {
                        relationsRects.get(i).setBounds(650,-2000,620,130);
                    } else if (olcumFiltreleme.getSelectedIndex()==0) {
                        relationsRects.get(i).setBounds(650,270 + 140*(i-1),620,130);
                    }
                }
                repaint();
            }
        });
        this.add(olcumFiltreleme);

        olcumGir.setBounds(1140,310,110,20);
        olcumGir.setFont(new Font("Calibri",Font.BOLD,15));
        olcumGir.setHorizontalAlignment(SwingConstants.CENTER);
        olcumGir.setFocusable(false);
        olcumGir.addActionListener(this);
        this.add(olcumGir);

        diyetYap.setBounds(20,610,120,20);
        diyetYap.setFont(new Font("Calibri",Font.BOLD,15));
        diyetYap.setHorizontalAlignment(SwingConstants.CENTER);
        diyetYap.setFocusable(false);
        diyetYap.setVisible(false);
        diyetYap.addActionListener(this);
        this.add(diyetYap);

        egzersizYap.setBounds(20,660,120,20);
        egzersizYap.setFont(new Font("Calibri",Font.BOLD,15));
        egzersizYap.setHorizontalAlignment(SwingConstants.CENTER);
        egzersizYap.setFocusable(false);
        egzersizYap.setVisible(false);
        egzersizYap.addActionListener(this);
        this.add(egzersizYap);

        oneriYap.setBounds(20,550,100,20);
        oneriYap.setFont(new Font("Calibri",Font.BOLD,15));
        oneriYap.setHorizontalAlignment(SwingConstants.CENTER);
        oneriYap.setVisible(false);
        oneriYap.setFocusable(false);
        oneriYap.addActionListener(this);
        this.add(oneriYap);

        graphGoster.setBounds(20,500,150,20);
        graphGoster.setFont(new Font("Calibri",Font.BOLD,15));
        graphGoster.setHorizontalAlignment(SwingConstants.CENTER);
        graphGoster.setVisible(false);
        graphGoster.setFocusable(false);
        graphGoster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame graphFrame = new JFrame("Kan Şekeri Grafiği");
                graphFrame.setSize(600, 400);
                graphFrame.setLocationRelativeTo(null);
                graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                ChartPanel chartPanel = createChartPanel(kullanici);
                graphFrame.setContentPane(chartPanel);

                graphFrame.setVisible(true);
            }
        });
        this.add(graphGoster);

        adGiris.setPreferredSize(new Dimension(10,300));
        adGiris.setBounds(WIDTH/2-115,178,250,25);
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
        soyadGiris.setBounds(WIDTH/2-115,228,250,25);
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
        sifreGiris.setBounds(WIDTH/2-115,278,250,25);
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
        emailGiris.setBounds(WIDTH/2-115,328,250,25);
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
        dogumSecimButton.setBounds(WIDTH/2-115,378,250,25);
        dogumSecimButton.setFont(new Font("Calibri",Font.PLAIN,15));
        dogumSecimButton.setVisible(false);
        dogumSecimButton.setFocusable(false);
        dogumSecimButton.addActionListener(this);
        this.add(dogumSecimButton);

        cinsiyetGiris.setPreferredSize(new Dimension(10,300));
        cinsiyetGiris.setBounds(WIDTH/2-115,448,250,25);
        cinsiyetGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        cinsiyetGiris.setVisible(false);
        cinsiyetGiris.setFocusable(false);
        cinsiyetGiris.addItem("Erkek");
        cinsiyetGiris.addItem("Kadın");
        this.add(cinsiyetGiris);

        profilSecimi.setPreferredSize(new Dimension(10,300));
        profilSecimi.setBounds(WIDTH/2-115,503,250,25);
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

        tarihSec.setPreferredSize(new Dimension(10,300));
        tarihSec.setBounds(20,370,180,25);
        tarihSec.setFont(new Font("Calibri",Font.PLAIN,15));
        tarihSec.setVisible(false);
        tarihSec.setFocusable(false);
        tarihSec.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String sql3 = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ?";
                    String sql4 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ?";
                    try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA
                         PreparedStatement ps = conn.prepareStatement(sql3);
                         PreparedStatement ps1 = conn.prepareStatement(sql4)){

                        if(kullanici.rol.equals("HASTA")){ps.setString(1, String.valueOf(kullanici.tc_no));}
                        else if (kullanici.rol.equals("DOKTOR")) {ps.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));}
                        ResultSet rs = ps.executeQuery();
                        boolean flag = false;
                        while(rs.next()){
                            if(rs.getString("tarih").equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                                if(rs.getBoolean("yapildi_mi")){diyetYapildi = 1;}
                                else {diyetYapildi = 0;}
                                flag=true;
                            }
                        }
                        if(!flag){diyetYapildi = -1;}

                        if(kullanici.rol.equals("HASTA")){ps1.setString(1, String.valueOf(kullanici.tc_no));}
                        else if (kullanici.rol.equals("DOKTOR")) {ps1.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));}
                        rs = ps1.executeQuery();
                        flag = false;
                        while(rs.next()){
                            if(rs.getString("tarih").equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                                if(rs.getBoolean("yapildi_mi")){egzersizYapildi = 1;}
                                else {egzersizYapildi = 0;}
                                flag=true;
                            }
                        }
                        if(!flag){egzersizYapildi = -1;}
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    if(kullanici.rol.equals("DOKTOR")){
                        String sql5 = "SELECT * FROM HASTA_UYARI H, UYARI_TURU U WHERE H.hasta_tc = ? AND H.tarih = ? AND U.uyari_turu_id = H.uyari_turu_id";
                        try (Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); // DOKTOR
                             PreparedStatement ps2 = conn.prepareStatement(sql5)) {
                            ps2.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                            ps2.setDate(2, java.sql.Date.valueOf(tarihReformat(String.valueOf(tarihSec.getSelectedItem()))));
                            ResultSet rs2 = ps2.executeQuery();
                            gunlukUyari = "";
                            gunlukUyariAciklama = "";
                            if(rs2.next()){
                                gunlukUyari = rs2.getString("tur_adi");
                                gunlukUyariAciklama = rs2.getString("tur_mesaji");
                            }
                        } catch (SQLException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                    repaint();
                }
            }
        });
        this.add(tarihSec);

        diyetGecmis.setPreferredSize(new Dimension(10,300));
        diyetGecmis.setBounds(20,610,260,25);
        diyetGecmis.setFont(new Font("Calibri",Font.PLAIN,15));
        diyetGecmis.setVisible(false);
        diyetGecmis.setFocusable(false);
        this.add(diyetGecmis);

        egzersizGecmis.setPreferredSize(new Dimension(10,300));
        egzersizGecmis.setBounds(20,660,260,25);
        egzersizGecmis.setFont(new Font("Calibri",Font.PLAIN,15));
        egzersizGecmis.setVisible(false);
        egzersizGecmis.setFocusable(false);
        this.add(egzersizGecmis);

        diyetGecmis.addItem("YOK");
        egzersizGecmis.addItem("YOK");
        String sql = "SELECT tur_adi FROM BELIRTI_TURU ORDER BY tur_adi";
        String sql1 = "SELECT tur_adi FROM DIYET_TURU ORDER BY tur_adi";
        String sql2 = "SELECT tur_adi FROM EGZERSIZ_TURU ORDER BY tur_adi";
        try (Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password); //ADMIN GIRISI
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                belirti_1_giris.addItem(rs.getString("tur_adi"));
                belirti_2_giris.addItem(rs.getString("tur_adi"));
                belirti_3_giris.addItem(rs.getString("tur_adi"));
                belirtiFiltreleme.addItem(rs.getString("tur_adi"));
            }
            rs = ps1.executeQuery();
            while(rs.next()){diyetGecmis.addItem(rs.getString("tur_adi"));}
            rs = ps2.executeQuery();
            while(rs.next()){egzersizGecmis.addItem(rs.getString("tur_adi"));}
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private ChartPanel createChartPanel(Kullanici kullanici) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        XYSeries series = new XYSeries("Kan Şekeri");

        if ("HASTA".equals(kullanici.rol)) {
            for (int i = 0; i < kullanici.olcumTarihleri.size(); i++) {
                if (kullanici.olcumTarihleri.get(i).substring(0, 10)
                        .equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))) {
                    LocalDateTime dateTime = LocalDateTime.parse(kullanici.olcumTarihleri.get(i), formatter);
                    series.add(dateTime.getHour(), kullanici.olcumler.get(i));
                }
            }
        } else {
            for (int i = 0; i < relations.get(secilenHasta).olcumTarihleri.size(); i++) {
                if (relations.get(secilenHasta).olcumTarihleri.get(i).substring(0, 10)
                        .equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))) {
                    LocalDateTime dateTime = LocalDateTime.parse(relations.get(secilenHasta).olcumTarihleri.get(i), formatter);
                    series.add(dateTime.getHour(), relations.get(secilenHasta).olcumler.get(i));
                }
            }
        }

        XYSeriesCollection xyCollection = new XYSeriesCollection(series);
        XYBarDataset barDataset = new XYBarDataset(xyCollection, 0.9);

        JFreeChart chart = ChartFactory.createXYBarChart(
                "Kan Şekeri Grafiği",
                "SAAT",
                false,
                "ÖLÇÜMLER",
                barDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        NumberAxis domain = (NumberAxis) chart.getXYPlot().getDomainAxis();
        domain.setAutoRange(false);
        domain.setRange(0, 23);
        domain.setTickUnit(new NumberTickUnit(1));
        domain.setLowerMargin(0);
        domain.setUpperMargin(0);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setOpaque(false);

        String diyetTemp, egzersizTemp;

        if(kullanici.rol.equals("HASTA")){
            diyetTemp = kullanici.diyetOneri;
            egzersizTemp = kullanici.egzersizOneri;
        }else{
            diyetTemp = relations.get(secilenHasta).diyetOneri;
            egzersizTemp = relations.get(secilenHasta).egzersizOneri;
        }

        JLabel diyetLabel = new JLabel("Önerilen Diyet: " + diyetTemp);
        diyetLabel.setBorder(new EmptyBorder(0, 5, 2, 0));
        diyetLabel.setHorizontalAlignment(SwingConstants.LEFT);
        diyetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        diyetLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        diyetLabel.setForeground(Color.BLACK);

        JLabel egzersizLabel = new JLabel("Önerilen Egzersiz: " + egzersizTemp);
        egzersizLabel.setBorder(new EmptyBorder(0, 5, 5, 0));
        egzersizLabel.setHorizontalAlignment(SwingConstants.LEFT);
        egzersizLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        egzersizLabel.setFont(new Font("Consolas", Font.PLAIN, 13));
        egzersizLabel.setForeground(Color.BLACK);

        southPanel.add(diyetLabel);
        southPanel.add(egzersizLabel);
        chartPanel.add(southPanel, BorderLayout.SOUTH);

        return chartPanel;
    }


    public void initialize(){
        String sql = "SELECT tc_no, ad, soyad, email, FORMAT(dogum_tarihi, 'dd-MM-yyyy') AS dogum_tarihi, cinsiyet, profil_resmi, rol FROM KULLANICI " +
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
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(kullanici.rol.equals("HASTA")){
            hastaBilgiEkle(kullanici);
        }

        String sql1 = "";
        if(kullanici.rol.equals("DOKTOR")){
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, FORMAT(K.dogum_tarihi, 'dd-MM-yyyy') AS dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.hasta_tc AND H.doktor_tc = ? ORDER BY K.ad, K.soyad";
        } else if (kullanici.rol.equals("HASTA")) {
            sql1 = "SELECT K.tc_no, K.ad, K.soyad, K.email, FORMAT(K.dogum_tarihi, 'dd-MM-yyyy') AS dogum_tarihi, K.cinsiyet, K.profil_resmi, K.rol FROM KULLANICI K, HASTA_DOKTOR H " +
                    "WHERE K.tc_no = H.doktor_tc AND H.hasta_tc = ? ORDER BY K.ad, K.soyad";
        }
        try (
                Connection conn = DriverManager.getConnection(Main.url, username, password); // boolean
                PreparedStatement ps = conn.prepareStatement(sql1)
        ) {
            ps.setString(1,Main.enUserName);
            ResultSet rs1 = ps.executeQuery();
            relations.clear();
            relationsRects.clear();
            int i = 0;
            while(rs1.next()){
                relations.add(new Kullanici(rs1.getLong("tc_no"), rs1.getString("ad"),rs1.getString("soyad"),rs1.getString("email"),rs1.getString("dogum_tarihi"),rs1.getString("cinsiyet"), ImageIO.read(rs1.getBinaryStream("profil_resmi")), rs1.getString("rol")));
                relationsRects.add(new Rectangle(650,270 + 140*(i-1),620,130));
                if(relations.get(i).rol.equals("HASTA")){
                    hastaBilgiEkle(relations.get(i));
                }
                i++;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(kullanici.rol.equals("DOKTOR")){
            graphGoster.setVisible(false);
            hastaEkle.setVisible(true);
            tarihSec.setVisible(false);
            olcumGiris.setVisible(false);
            olcumGiris.setText("");
            belirtiFiltreleme.setSelectedIndex(0);
            belirtiFiltreleme.setVisible(true);
            olcumFiltreleme.setSelectedIndex(0);
            olcumFiltreleme.setVisible(true);
            olcumGiris.setBounds(WIDTH/2-515,128,250,25);
            olcumGir.setVisible(false);
            diyetYap.setVisible(false);
            egzersizYap.setVisible(false);
        }
        else if (kullanici.rol.equals("HASTA")) {
            belirtiFiltreleme.setVisible(false);
            olcumFiltreleme.setVisible(false);
            sifreDegistir.setVisible(true);
            sifreDegistirButton.setVisible(true);
            tarihSec.removeAllItems();
            tarihSec.setVisible(true);
            tarihUpdate(kullanici);
            graphGoster.setVisible(true);
            diyetYapildi = -1;
            egzersizYapildi = -1;
            hastaEkle.setVisible(false);
            olcumGiris.setVisible(true);
            olcumGiris.setText("");
            olcumGiris.setBounds(980,308,100,25);
            olcumGir.setVisible(true);
            if(kullanici.oneriGirdiMi && !kullanici.diyetOneri.equals("Yok")){
                String sql2 = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ? AND tarih = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps = conn.prepareStatement(sql2)) {
                    ps.setString(1, String.valueOf(kullanici.tc_no));
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                    ResultSet rs = ps.executeQuery();
                    diyetYap.setVisible(true);
                    if(rs.next()){diyetYap.setVisible(false);}
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            if(kullanici.oneriGirdiMi && !kullanici.egzersizOneri.equals("Yok")){
                String sql2 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ? AND tarih = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps = conn.prepareStatement(sql2)) {
                    ps.setString(1, String.valueOf(kullanici.tc_no));
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                    ResultSet rs = ps.executeQuery();
                    egzersizYap.setVisible(true);
                    if(rs.next()){egzersizYap.setVisible(false);}
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            String sql2 = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ?";
            String sql3 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ?";
            try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA
                 PreparedStatement ps = conn.prepareStatement(sql2);
                 PreparedStatement ps1 = conn.prepareStatement(sql3)){
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    if(rs.getString("tarih").equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                        if(rs.getBoolean("yapildi_mi")){diyetYapildi = 1;}
                        else {diyetYapildi = 0;}
                    }
                }
                ps1.setString(1, String.valueOf(kullanici.tc_no));
                rs = ps1.executeQuery();
                while(rs.next()){
                    if(rs.getString("tarih").equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                        if(rs.getBoolean("yapildi_mi")){egzersizYapildi = 1;}
                        else {egzersizYapildi = 0;}
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void hastaBilgiEkle(Kullanici kullanici){
        String sql1 = "SELECT * FROM HASTA_OLCUM WHERE hasta_tc = ? ORDER BY olcum_tarihi";
        String sql2 = "SELECT * FROM HASTA_BELIRTI WHERE hasta_tc = ?";
        String sql3 = "SELECT * FROM BELIRTI_TURU WHERE belirti_turu_id = ?";
        String sql4 = "SELECT * FROM UYARI_TURU WHERE uyari_turu_id = ?";
        String sql5 = "SELECT D.tur_adi FROM HASTA_DIYET H, DIYET_TURU D WHERE H.hasta_tc = ? AND H.diyet_turu_id = D.diyet_turu_id";
        String sql6 = "SELECT E.tur_adi FROM HASTA_EGZERSIZ H, EGZERSIZ_TURU E WHERE H.hasta_tc = ? AND H.egzersiz_turu_id = E.egzersiz_turu_id";
        String sql7 = "SELECT * FROM HASTA_INSULIN H, UYARI_TURU U WHERE H.hasta_tc = ? AND U.uyari_turu_id = H.uyari_turu_id ORDER BY insulin_tarihi";
        try (
                Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA GIRISI
                PreparedStatement ps = conn.prepareStatement(sql1);
                PreparedStatement ps1 = conn.prepareStatement(sql2);
                PreparedStatement ps2 = conn.prepareStatement(sql3);
                PreparedStatement ps3 = conn.prepareStatement(sql4);
                PreparedStatement ps4 = conn.prepareStatement(sql5);
                PreparedStatement ps5 = conn.prepareStatement(sql6);
                PreparedStatement ps6 = conn.prepareStatement(sql7)
        ) {

            ps.setString(1, String.valueOf(kullanici.tc_no));
            ps1.setString(1, String.valueOf(kullanici.tc_no));

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

            ps4.setString(1,String.valueOf(kullanici.tc_no));
            ps5.setString(1,String.valueOf(kullanici.tc_no));
            ResultSet rs2 = ps4.executeQuery();
            ResultSet rs3 = ps5.executeQuery();
            if(rs2.next()){
                kullanici.diyetOneri = rs2.getString("tur_adi");
                kullanici.oneriGirdiMi = true;
            }
            if (rs3.next()){
                kullanici.egzersizOneri = rs3.getString("tur_adi");
                kullanici.oneriGirdiMi = true;
            }

            ps6.setString(1, String.valueOf(kullanici.tc_no));
            ResultSet rs4 = ps6.executeQuery();
            while(rs4.next()){
                kullanici.insulinDegerleri.add(rs4.getInt("insulin_degeri"));
                kullanici.insulinTarihleri.add(rs4.getString("insulin_tarihi"));
                kullanici.insulinUyarilar.add(rs4.getString("tur_adi"));
                kullanici.insulinUyariAciklamalar.add(rs4.getString("tur_mesaji"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        diyetEgzersizCheck(kullanici);
    }

    public void diyetEgzersizCheck(Kullanici kullanici){
        String sql = "SELECT * FROM HASTA_DIYET_CHECK WHERE hasta_tc = ?";
        String sql1 = "SELECT * FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ?";
        try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); //HASTA
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement ps1 = conn.prepareStatement(sql1)) {

            ps.setString(1,String.valueOf(kullanici.tc_no));
            ResultSet rs = ps.executeQuery();
            int yes=0,no=0;
            while(rs.next()){
                if(rs.getBoolean("yapildi_mi")){yes++;}
                else {no++;}
            }
            if(yes+no!=0){kullanici.diyetYuzdesi = 100*yes/(yes+no);}
            else {kullanici.diyetYuzdesi=0;}

            ps1.setString(1,String.valueOf(kullanici.tc_no));
            rs = ps1.executeQuery();
            yes=0;
            no=0;
            while(rs.next()){
                if(rs.getBoolean("yapildi_mi")){yes++;}
                else {no++;}
            }
            if(yes+no!=0){kullanici.egzersizYuzdesi = 100*yes/(yes+no);}
            else {kullanici.egzersizYuzdesi=0;}
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public int ortalamaHesapla(int[] array){
        int acc=0,sum=0;
        for (int j : array) {
            sum += j;
            if (j != 0) {
                acc++;
            }
        }
        if(acc!=0){return sum/acc;}
        return 0;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void drawProfil(Graphics g, Graphics2D g2d, Kullanici kullanici){
        float[] dashPattern = {6, 4};
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
        g2d.setColor(Color.WHITE);
        g2d.drawLine(320, 266, 320, HEIGHT);
        g2d.drawLine(2,266,WIDTH,266);
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.BLUE);
        g2d.fillRoundRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height, 40, 40);
        g2d.setColor(new Color(0, 160, 224, 255));
        g2d.drawRoundRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height, 40, 40);
        g.drawImage(kullanici.profil_resmi,20,150,this);
        g.setColor(Color.WHITE);
        g.drawString("Ad Soyad: " + kullanici.ad + " " + kullanici.soyad, 150,150);
        g.drawString("TC Kimlik: " + kullanici.tc_no, 150,170);
        g.drawString("Cinsiyet: " + kullanici.cinsiyet, 150,190);
        g.drawString("Doğum Tarihi: " + kullanici.dogum_tarihi, 150,210);
        g.drawString("E-Posta: " + kullanici.email, 150,230);
        g.drawString("Rol: " + kullanici.rol, 150,250);
        if(!kullanici.belirtiler.isEmpty()){g.drawString("Belirti 1: " + kullanici.belirtiler.get(0), 20,290);}
        if(kullanici.belirtiler.size() > 1){g.drawString("Belirti 2: " + kullanici.belirtiler.get(1), 20,310);}
        if(kullanici.belirtiler.size() > 2){g.drawString("Belirti 3: " + kullanici.belirtiler.get(2), 20,330);}
        g.drawString("Tarih Seç: ", 20,360);
        g.setFont(new Font("Consolas",Font.PLAIN,13));
        g.drawString("Ölçüm Değerleri:",340,290);
        int temp = 1,sabah=0,ogle=0,ikindi=0,aksam=0,gece=0;
        for (int i = 0; i < kullanici.olcumTarihleri.size(); i++) {
            if(kullanici.olcumTarihleri.get(i).substring(0,10).equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                if(this.kullanici.rol.equals("HASTA")){g.drawString(kullanici.olcumTarihleri.get(i).substring(11,21)+" -> " + kullanici.olcumler.get(i) + " mg/dL",340,290+temp*17);}
                else{g.drawString(kullanici.olcumTarihleri.get(i).substring(11,21)+" -> " + kullanici.olcumler.get(i) + " mg/dL ("+kullanici.olcumUyarilar.get(i)+")",340,290+temp*17);}
                temp++;
                if(Integer.parseInt(kullanici.olcumTarihleri.get(i).substring(11,13)) == 7){sabah=kullanici.olcumler.get(i);}
                else if (Integer.parseInt(kullanici.olcumTarihleri.get(i).substring(11,13)) == 12) {ogle=kullanici.olcumler.get(i);}
                else if (Integer.parseInt(kullanici.olcumTarihleri.get(i).substring(11,13)) == 15) {ikindi=kullanici.olcumler.get(i);}
                else if (Integer.parseInt(kullanici.olcumTarihleri.get(i).substring(11,13)) == 18) {aksam=kullanici.olcumler.get(i);}
                else if (Integer.parseInt(kullanici.olcumTarihleri.get(i).substring(11,13)) == 22) {gece=kullanici.olcumler.get(i);}
            }
        }
        g.drawString("Kan Şekeri Ortalamaları:",660,290);
        g.drawString("Sabah: "+sabah+" mg/dL",660,307);
        g.drawString("Öğlen: "+ortalamaHesapla(new int[]{ogle, sabah})+" mg/dL",660,324);
        g.drawString("İkindi: "+ortalamaHesapla(new int[]{ikindi, ogle, sabah})+" mg/dL",660,341);
        g.drawString("Akşam: "+ortalamaHesapla(new int[]{aksam, ikindi, ogle, sabah})+" mg/dL",660,358);
        g.drawString("Gece: "+ortalamaHesapla(new int[]{gece, aksam, ikindi, ogle, sabah})+" mg/dL",660,375);

        g.drawString("İnsülin Değerleri:",660,438);
        temp = 0;
        for (int i = 0; i < kullanici.insulinTarihleri.size(); i++) {
            if(kullanici.insulinTarihleri.get(i).substring(0,10).equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){
                g.drawString(kullanici.insulinTarihleri.get(i).substring(11,21)+" -> " + kullanici.insulinDegerleri.get(i) + " ml (" + kullanici.insulinUyarilar.get(i)+")",660,455+temp*34);
                if(!kullanici.insulinUyarilar.get(i).equals("Uyarı Yok")){g.drawString(kullanici.insulinUyariAciklamalar.get(i),660,472+temp*34);}
                temp++;
            }
        }

        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.drawString("Verilen Diyet: " + kullanici.diyetOneri, 20,600);
        g.drawString("Verilen Egzersiz: " + kullanici.egzersizOneri, 20,650);

        if(diyetYapildi==1){
            g.setColor(Color.GREEN);
            g.drawString("(YAPILDI) %" + kullanici.diyetYuzdesi,150,625);
        } else if (diyetYapildi==0) {
            g.setColor(Color.RED);
            g.drawString("(YAPILMADI) %" + kullanici.diyetYuzdesi,150,625);
        }

        if(egzersizYapildi==1){
            g.setColor(Color.GREEN);
            g.drawString("(YAPILDI) %" + kullanici.egzersizYuzdesi,150,675);
        } else if (egzersizYapildi==0) {
            g.setColor(Color.RED);
            g.drawString("(YAPILMADI) %" + kullanici.egzersizYuzdesi,150,675);
        }

        if(olcumGirildiMi == 1){
            g.setColor(Color.GREEN);
            g.drawString("Ölçüm Girildi", 980,360);
        } else if(olcumGirildiMi == 0){
            g.setColor(Color.RED);
            g.drawString("Ölçüm Girilemedi", 980,360);
        } else if(olcumGirildiMi == 2){
            g.setColor(Color.GREEN);
            g.drawString("Ölçüm Girildi", 980,360);
            g.drawString("(Ortalama Hesabına Katılmadı)", 980,380);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,13));
        if(this.kullanici.rol.equals("HASTA")){
            g.drawString("Ölçüm Giriş: ", 980,290);
            g.drawString("mg/dL", 1090,325);
            g.drawString("Şifre Değiştir:",660,660);
        } else{
            g.drawString("Günlük Uyarı: "+gunlukUyari,660,675);
            g.drawString(gunlukUyariAciklama,660,695);
        }
        if(sifreDegistirildiMi == 1){
            g.setColor(Color.GREEN);
            g.drawString("Şifre Değiştirildi", 960,680);
        } else if(sifreDegistirildiMi == 0){
            g.setColor(Color.RED);
            g.drawString("Şifre Değiştirilemedi", 960,680);
        }
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.setColor(Color.WHITE);
    }
    public void draw(Graphics g){
        g.drawImage(background,0,0,WIDTH,HEIGHT,null);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.setColor(Color.WHITE);

        if(currentScreen == Screen.MAIN){

            for (int i = 0; i < relations.size(); i++) {
                if (kullanici.rol.equals("DOKTOR")) {g.setColor(Color.BLUE);}
                else if (kullanici.rol.equals("HASTA")){g.setColor(new Color(3, 92, 0));}
                g2d.setStroke(new BasicStroke(2f));
                g2d.fillRoundRect(relationsRects.get(i).x, relationsRects.get(i).y, relationsRects.get(i).width, relationsRects.get(i).height, 40, 40);
                if(kullanici.rol.equals("HASTA")){g2d.setColor(new Color(54, 210, 1, 255));}
                else if (kullanici.rol.equals("DOKTOR")) {g2d.setColor(new Color(0, 160, 224, 255));}
                g2d.drawRoundRect(relationsRects.get(i).x, relationsRects.get(i).y, relationsRects.get(i).width, relationsRects.get(i).height, 40, 40);
                g.setColor(Color.WHITE);
                g.drawImage(relations.get(i).profil_resmi,660, relationsRects.get(i).y + 20,this);
                g.drawString("Ad Soyad: " + relations.get(i).ad + " " + relations.get(i).soyad, 786, relationsRects.get(i).y + 20);
                g.drawString("TC Kimlik: " + relations.get(i).tc_no, 786, relationsRects.get(i).y + 40);
                g.drawString("Cinsiyet: " + relations.get(i).cinsiyet, 786, relationsRects.get(i).y + 60);
                g.drawString("Doğum Tarihi: " + relations.get(i).dogum_tarihi, 786, relationsRects.get(i).y + 80);
                g.drawString("E-Posta: " + relations.get(i).email, 786, relationsRects.get(i).y + 100);
                g.drawString("Rol: " + relations.get(i).rol, 786, relationsRects.get(i).y + 120);
            }

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(0,-38,WIDTH,160, 40, 40);

            float[] dashPattern = {6, 4};
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(4,-38,WIDTH - 10,158, 40, 40);
            g2d.drawLine(640, 128, 640, HEIGHT);
            if(kullanici.rol.equals("HASTA")){
                drawProfil(g,g2d,kullanici);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
                g2d.drawLine(WIDTH/2, 400, WIDTH, 400);
                g2d.drawLine(WIDTH/2, 640, WIDTH, 640);
            }
            g2d.setStroke(new BasicStroke(2f));

            if (kullanici.rol.equals("DOKTOR")) {
                g.setColor(Color.WHITE);
                g.drawString("Belirtiye Göre Filtrele:",20,290);
                g.drawString("Kan Seviyelerine Göre Filtrele:",20,340);
                g2d.setColor(new Color(3, 92, 0));
            }
            else if (kullanici.rol.equals("HASTA")){g2d.setColor(Color.BLUE);}
            g2d.fillRoundRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height, 40, 40);
            if(kullanici.rol.equals("DOKTOR")){g2d.setColor(new Color(54, 210, 1, 255));}
            else if (kullanici.rol.equals("HASTA")) {g2d.setColor(new Color(0, 160, 224, 255));}
            g2d.drawRoundRect(kullaniciRect.x,kullaniciRect.y, kullaniciRect.width, kullaniciRect.height, 40, 40);
            g.drawImage(kullanici.profil_resmi,28,150,this);
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
            g.drawString("mg/dL", WIDTH/2-260,145);
            g.drawString("Ad:", WIDTH/2-115,170);
            g.drawString("Soyad:", WIDTH/2-115,220);
            g.drawString("Şifre:", WIDTH/2-115,270);
            g.drawString("EMail:", WIDTH/2-115,320);
            g.drawString("Dogum Tarihi:", WIDTH/2-115,370);
            g.drawString("Seçilen Tarih: " + dogumSqlDate,WIDTH/2-115,420);
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
            g2d.setColor(Color.WHITE);
            float[] dashPattern = {6, 4};
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(0,-38,WIDTH,160, 40, 40);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(4,-38,WIDTH - 10,158, 40, 40);
            g2d.drawLine(640, 128, 640, HEIGHT);
            g2d.drawLine(WIDTH/2, 400, WIDTH, 400);
            g2d.drawLine(WIDTH/2, 640, WIDTH, 640);
            drawProfil(g,g2d,relations.get(secilenHasta));
            g2d.setStroke(new BasicStroke());
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.BOLD,30));
        g.drawString("Diyabet Sistemi",513,70);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        g.drawString("Current Date: " + formatter.format(selectedDateTime[0]), 960,40);

    }

    public void insulinCheck(LocalDateTime ldt, LocalDateTime ldt2, Kullanici kullanici){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        HashMap<Integer, Integer> indexes = new HashMap<>();
        int[] hours = new int[]{7,12,15,18,22};
        for (int i = 0; i < kullanici.olcumTarihleri.size(); i++) {
            LocalDateTime dateTime = LocalDateTime.parse(kullanici.olcumTarihleri.get(i), formatter);
            if(dateTime.getDayOfMonth() == ldt.getDayOfMonth() && (dateTime.getHour() == 7 || dateTime.getHour() == 12 || dateTime.getHour() == 15 || dateTime.getHour() == 18 || dateTime.getHour() == 22)){indexes.put(dateTime.getHour(),i);}
        }
        String sql = "INSERT INTO HASTA_INSULIN (hasta_tc, insulin_tarihi, uyari_turu_id, insulin_degeri) VALUES (?, ?, ?, ?)";
        String sql1 = "SELECT * FROM HASTA_INSULIN WHERE hasta_tc = ? AND insulin_tarihi = ?";
        try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement ps1 = conn.prepareStatement(sql1)){

            ps.setString(1, String.valueOf(kullanici.tc_no));

            ps1.setString(1,String.valueOf(kullanici.tc_no));
            for (int i = 0; i < hours.length; i++) {
                ps1.setString(2, String.valueOf(Timestamp.valueOf(ldt.withHour(hours[i]))));
                ResultSet rs = ps1.executeQuery();
                if(rs.next()){}
                else {
                    if(ldt2.getDayOfMonth() > ldt.getDayOfMonth() || ldt2.getHour() > hours[i]){
                        ps.setTimestamp(2,Timestamp.valueOf(ldt.withHour(hours[i])));
                        int sum = 0, acc = 0;
                        for (int j = i; j >= 0; j--) {
                            if(indexes.containsKey(hours[j])){
                                sum += kullanici.olcumler.get(indexes.get(hours[j]));
                                acc++;
                            }
                        }

                        if(acc == i+1){ps.setString(3, "2");}
                        else if (acc == 0) {ps.setString(3, "8");}
                        else {
                            if(indexes.containsKey(hours[i])){ps.setString(3, "9");}
                            else {ps.setString(3, "10");}
                        }

                        if(acc != 0){
                            if(sum/acc <= 110){ps.setString(4, "0");}
                            else if(sum/acc >= 111 && sum/acc <= 150){ps.setString(4, "1");}
                            else if(sum/acc >= 151 && sum/acc <= 200){ps.setString(4, "2");}
                            else if(sum/acc > 200){ps.setString(4, "3");}
                        } else {
                            ps.setString(4, "0");
                        }

                        ps.executeUpdate();
                        conn.commit();
                        initialize();
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

    public String tarihReformat(String tarih){return tarih.substring(6,10)+"-"+tarih.substring(3,6)+tarih.substring(0,2);}
    public String tarihReformat2(String tarih){return tarih.substring(8,10)+"-"+tarih.substring(5,8)+tarih.substring(0,4);}

    public void tarihUpdate(Kullanici kullanici){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate smallestDate=null,highestDate=null;
        for (int i = 0; i < kullanici.olcumTarihleri.size(); i++) {
            LocalDate tempDate = LocalDate.parse(tarihReformat2(kullanici.olcumTarihleri.get(i)), formatter);
            if(smallestDate==null || smallestDate.isAfter(tempDate)){smallestDate = tempDate;}
            if(highestDate==null || highestDate.isBefore(tempDate)){highestDate = tempDate;}
        }
        String sql = "SELECT hasta_tc, FORMAT(insulin_tarihi, 'dd-MM-yyyy') AS insulin_tarihi FROM HASTA_INSULIN WHERE hasta_tc = ? ORDER BY insulin_tarihi";
        String sql1 = "SELECT hasta_tc, FORMAT(tarih, 'dd-MM-yyyy') AS tarih FROM HASTA_DIYET_CHECK WHERE hasta_tc = ? ORDER BY tarih";
        String sql2 = "SELECT hasta_tc, FORMAT(tarih, 'dd-MM-yyyy') AS tarih FROM HASTA_EGZERSIZ_CHECK WHERE hasta_tc = ? ORDER BY tarih";
        String sql3 = "SELECT H.hasta_tc, FORMAT(H.tarih, 'dd-MM-yyyy') AS tarih, H.uyari_turu_id, U.uyari_turu_id, U.tur_adi, U.tur_mesaji FROM HASTA_UYARI H, UYARI_TURU U WHERE H.hasta_tc = ? AND U.uyari_turu_id=H.uyari_turu_id ORDER BY tarih";
        try(Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
            PreparedStatement ps = conn.prepareStatement(sql);
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            PreparedStatement ps3 = conn.prepareStatement(sql3)) {

            ps.setString(1, String.valueOf(kullanici.tc_no));
            ps1.setString(1, String.valueOf(kullanici.tc_no));
            ps2.setString(1, String.valueOf(kullanici.tc_no));
            ps3.setString(1, String.valueOf(kullanici.tc_no));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String utilDate = rs.getString("insulin_tarihi");
                LocalDate tempDate = LocalDate.parse(utilDate, formatter);
                if(smallestDate==null || smallestDate.isAfter(tempDate)){smallestDate = tempDate;}
                if(highestDate==null || highestDate.isBefore(tempDate)){highestDate = tempDate;}
            }
            rs = ps1.executeQuery();
            while(rs.next()){
                String utilDate = rs.getString("tarih");
                LocalDate tempDate = LocalDate.parse(utilDate, formatter);
                if(smallestDate==null || smallestDate.isAfter(tempDate)){smallestDate = tempDate;}
                if(highestDate==null || highestDate.isBefore(tempDate)){highestDate = tempDate;}
            }
            rs = ps2.executeQuery();
            while(rs.next()){
                String utilDate = rs.getString("tarih");
                LocalDate tempDate = LocalDate.parse(utilDate, formatter);
                if(smallestDate==null || smallestDate.isAfter(tempDate)){smallestDate = tempDate;}
                if(highestDate==null || highestDate.isBefore(tempDate)){highestDate = tempDate;}
            }
            if(this.kullanici.rol.equals("DOKTOR")){
                rs = ps3.executeQuery();
                while(rs.next()){
                    String utilDate = rs.getString("tarih");
                    LocalDate tempDate = LocalDate.parse(utilDate, formatter);
                    if(smallestDate==null || smallestDate.isAfter(tempDate)){smallestDate = tempDate;}
                    if(highestDate==null || highestDate.isBefore(tempDate)){highestDate = tempDate;}
                }
            }
            if(smallestDate!=null){
                LocalDate current = smallestDate;
                while (!current.isAfter(highestDate)) {
                    tarihSec.addItem(current.format(formatter));
                    current = current.plusDays(1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void kontrolleriYap(Kullanici kullanici, Date insertedDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(!sdf.format(selectedDateTime[0]).equals(sdf.format(insertedDate))){

            //DIYET KONTROL
            if(kullanici.oneriGirdiMi && !kullanici.diyetOneri.equals("Yok")){
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
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(insertedDate.getTime())));
                    ResultSet rs1 = ps.executeQuery();
                    if((currentScreen == Screen.MAIN && kullanici.rol.equals("HASTA")) || (currentScreen == Screen.HASTA_PROFIL && kullanici.rol.equals("DOKTOR"))){diyetYap.setVisible(true);}
                    if(rs1.next()){diyetYap.setVisible(false);}
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }

            //EGZERSIZ KONTROL
            if(kullanici.oneriGirdiMi && !kullanici.egzersizOneri.equals("Yok")){
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
                    ps.setString(2, String.valueOf(new java.sql.Timestamp(insertedDate.getTime())));
                    ResultSet rs1 = ps.executeQuery();
                    if((currentScreen == Screen.MAIN && kullanici.rol.equals("HASTA")) || (currentScreen == Screen.HASTA_PROFIL && kullanici.rol.equals("DOKTOR"))){egzersizYap.setVisible(true);}
                    if(rs1.next()){egzersizYap.setVisible(false);}
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }

            //GUNLUK OLCUM KONTROL
            String sql5 = "SELECT hasta_tc, olcum_tarihi FROM HASTA_OLCUM WHERE hasta_tc = ? AND CAST(olcum_tarihi AS DATE) = ?";
            String sql6 = "INSERT INTO HASTA_UYARI (hasta_tc, tarih, uyari_turu_id) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                 PreparedStatement ps = conn.prepareStatement(sql5);) {
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                ResultSet rs = ps.executeQuery();
                int olcumSayisi = 0;
                while(rs.next()){olcumSayisi++;}
                try (Connection conn1 = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps1 = conn1.prepareStatement(sql6)){
                    ps1.setString(1,String.valueOf(kullanici.tc_no));
                    ps1.setString(2,String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                    if(olcumSayisi == 0){
                        ps1.setString(3,"6");
                        ps1.executeUpdate();
                        conn1.commit();
                    } else if (olcumSayisi < 3) {
                        ps1.setString(3,"7");
                        ps1.executeUpdate();
                        conn1.commit();
                    }
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }

        //INSULIN ONERISI
        if(!selectedDateTime[0].equals(insertedDate)){
            LocalDateTime ldt = selectedDateTime[0].toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime ldt2 = insertedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if(this.kullanici.rol.equals("HASTA")){selectedDateTime[0] = insertedDate;}
            insulinCheck(ldt, ldt2, kullanici);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == hastaEkle){
            currentScreen = Screen.HASTA_EKLE;
            belirtiFiltreleme.setVisible(false);
            olcumFiltreleme.setVisible(false);
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
            belirtiFiltreleme.setSelectedIndex(0);
            belirtiFiltreleme.setVisible(true);
            olcumFiltreleme.setSelectedIndex(0);
            olcumFiltreleme.setVisible(true);
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
            dogumSqlDate = "";
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

            RoundedButton okButton = new RoundedButton("OK");
            okButton.addActionListener(ev -> {
                dateChooser.setDate(calendar.getDate());
                Date utilDate = dateChooser.getDate();
                dogumSqlDate = tarihReformat2(String.valueOf(new java.sql.Date(utilDate.getTime())));
                dialog.dispose();
                repaint();
            });

            dialog.add(okButton, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(Main.frame);
            dialog.setVisible(true);
        } else if (e.getSource() == girisYap) {
            if(TC_Giris.getText().length() == 11 && !adGiris.getText().isEmpty() && !soyadGiris.getText().isEmpty() && !sifreGiris.getPassword().equals("") && !emailGiris.getText().isEmpty() && !dogumSqlDate.isEmpty() && selectedFile != null && !olcumGiris.getText().isEmpty()) {
                String sql = "INSERT INTO KULLANICI (tc_no, ad, soyad, sifre, email, dogum_tarihi, cinsiyet, profil_resmi, rol) " +
                        "VALUES (?, ?, ?, HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?)), ?, ?, ?, ?, 'HASTA')";
                String sql1 = "INSERT INTO HASTA_DOKTOR (doktor_tc, hasta_tc)" +
                        "VALUES (?, ?)";
                String sql2 = "INSERT INTO HASTA_BELIRTI (hasta_tc, belirti_turu_id)" +
                        "VALUES (?, ?)";
                String sql3 = "INSERT INTO HASTA_OLCUM (hasta_tc, olcum_tarihi, uyari_turu_id, olcum_degeri)" +
                        "VALUES (?, ?, ?, ?)";
                String sql4 = "SELECT * FROM BELIRTI_TURU WHERE tur_adi = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); //DOKTOR
                     PreparedStatement ps = conn.prepareStatement(sql);
                     PreparedStatement ps1 = conn.prepareStatement(sql1);
                     PreparedStatement ps2 = conn.prepareStatement(sql2);
                     PreparedStatement ps3 = conn.prepareStatement(sql3);
                     PreparedStatement ps4 = conn.prepareStatement(sql4)) {

                    FileInputStream fis = new FileInputStream(selectedFile);
                    ps.setString(1, TC_Giris.getText());
                    ps.setString(2, adGiris.getText());
                    ps.setString(3, soyadGiris.getText());
                    ps.setString(4, new String(sifreGiris.getPassword()));
                    ps.setString(5, emailGiris.getText());
                    ps.setString(6, String.valueOf(tarihReformat(dogumSqlDate)));
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
                            ps4.setString(1,String.valueOf(belirti_1_giris.getSelectedItem()));
                            ResultSet rs = ps4.executeQuery();
                            if(rs.next()){ps2.setString(2,rs.getString("belirti_turu_id"));}
                            ps2.executeUpdate();
                        }
                        if(belirti_2_giris.getSelectedIndex() != 0){
                            ps4.setString(1,String.valueOf(belirti_2_giris.getSelectedItem()));
                            ResultSet rs = ps4.executeQuery();
                            if(rs.next()){ps2.setString(2,rs.getString("belirti_turu_id"));}
                            ps2.executeUpdate();
                        }
                        if(belirti_3_giris.getSelectedIndex() != 0){
                            ps4.setString(1,String.valueOf(belirti_3_giris.getSelectedItem()));
                            ResultSet rs = ps4.executeQuery();
                            if(rs.next()){ps2.setString(2,rs.getString("belirti_turu_id"));}
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
                        String mailIcerigi = String.format("""
                            Merhaba %s %s,
                
                            Diyabet Takip Sistemi hesabınız başarıyla oluşturuldu.
                
                              • Kullanıcı adınız : %s (TC Kimlik Numaranız)
                              • Geçici şifreniz  : %s
                
                            İlk girişinizden sonra isterseniz şifrenizi değiştirebilirsiniz.
                
                            Bu e-posta gizli bilgiler içerir; lütfen kimseyle paylaşmayın.
                            Sağlıklı günler dileriz.
                
                            Sağlık Bakanlığı Diyabet Takip Sistemi Destek Ekibi
                        """, adGiris.getText(), soyadGiris.getText(), TC_Giris.getText(), new String(sifreGiris.getPassword()));
                        EmailSender.sendEmail(emailGiris.getText(), "Diyabet Sistemi Girişiniz Başarılı", mailIcerigi);
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
                    sifreDegistirildiMi = -1;
                    sifreDegistir.setVisible(false);
                    sifreDegistir.setText("");
                    sifreDegistirButton.setVisible(false);
                    Main.frame.switchScreen(0);
                }
            }
            else if (currentScreen == Screen.HASTA_PROFIL) {
                belirtiFiltreleme.setSelectedIndex(0);
                belirtiFiltreleme.setVisible(true);
                olcumFiltreleme.setSelectedIndex(0);
                olcumFiltreleme.setVisible(true);
                oneriYap.setVisible(false);
                graphGoster.setVisible(false);
                diyetGecmis.setVisible(false);
                egzersizGecmis.setVisible(false);
                tarihSec.setVisible(false);
                hastaEkle.setVisible(true);
                diyetGecmis.setSelectedIndex(0);
                egzersizGecmis.setSelectedIndex(0);
                cikisButton.setText("Çıkış Yap");
                currentScreen = Screen.MAIN;
                tarihSec.removeAllItems();
                repaint();
            }
        } else if (e.getSource() == selectDate) {
            JDialog dialog = new JDialog(Main.frame, "Gün ve Saat Seç", true);
            dialog.setLayout(new BorderLayout());

            Date initialDate = selectedDateTime[0];
            Calendar initialCal = Calendar.getInstance();
            initialCal.setTime(initialDate);
            int initialHourLimit = initialCal.get(Calendar.HOUR_OF_DAY);

            JCalendar calendar = new JCalendar();
            calendar.setDate(initialDate);
            calendar.setMinSelectableDate(initialDate);
            LocalDateTime ldt_temp = selectedDateTime[0].toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime nextDay = ldt_temp.plusDays(1);
            Date nextDayDate = Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant());
            calendar.setMaxSelectableDate(new java.sql.Date(nextDayDate.getTime()));

            JPanel timePanel = new JPanel();

            SpinnerNumberModel hourModel = new SpinnerNumberModel(initialHourLimit, initialHourLimit, 23, 1);
            JSpinner hourSpinner = new JSpinner(hourModel);

            Runnable updateHourModel = () -> {
                Calendar selected = Calendar.getInstance();
                selected.setTime(calendar.getDate());

                boolean isSameDay = selected.get(Calendar.YEAR) == initialCal.get(Calendar.YEAR) && selected.get(Calendar.DAY_OF_YEAR) == initialCal.get(Calendar.DAY_OF_YEAR);

                int currentSpinnerValue = (int) hourSpinner.getValue();

                if (isSameDay) {
                    int safeHour = Math.max(initialHourLimit, currentSpinnerValue);
                    hourSpinner.setModel(new SpinnerNumberModel(safeHour, initialHourLimit, 23, 1));
                } else {
                    int safeHour = Math.max(0, currentSpinnerValue);
                    hourSpinner.setModel(new SpinnerNumberModel(safeHour, 0, 23, 1));
                }
            };

            updateHourModel.run();
            calendar.getDayChooser().addPropertyChangeListener("day", evt -> updateHourModel.run());

            timePanel.add(new JLabel("Saat:"));
            timePanel.add(hourSpinner);

            RoundedButton okButton = new RoundedButton("OK");
            okButton.addActionListener(ev -> {
                Date dateOnly = calendar.getDate();

                Calendar newDateTime = Calendar.getInstance();
                newDateTime.setTime(dateOnly);
                newDateTime.set(Calendar.HOUR_OF_DAY, (int) hourSpinner.getValue());
                newDateTime.set(Calendar.MINUTE, 0);
                newDateTime.set(Calendar.SECOND, 0);
                newDateTime.set(Calendar.MILLISECOND, 0);

                Date insertedDate = newDateTime.getTime();
                if(kullanici.rol.equals("HASTA")){
                    kontrolleriYap(kullanici,insertedDate);
                    tarihSec.removeAllItems();
                    tarihUpdate(kullanici);
                } else if (kullanici.rol.equals("DOKTOR")) {
                    for (Kullanici relation : relations) {
                        kontrolleriYap(relation, insertedDate);
                    }
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
                if(rs.next() || olcumGiris.getText().isEmpty()){
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
                        int temp_tarih = tarihSec.getSelectedIndex();
                        initialize();
                        LocalDateTime ldt = selectedDateTime[0].toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if(ldt.getHour()==7 || ldt.getHour()==12 || ldt.getHour()==15 || ldt.getHour()==18 || ldt.getHour()==22){olcumGirildiMi = 1;}
                        else{olcumGirildiMi = 2;}
                        insulinCheck(ldt, ldt, kullanici);
                        tarihSec.setSelectedIndex(temp_tarih);
                        repaint();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource() == oneriYap) {
            if(diyetGecmis.getSelectedIndex()!=0 || egzersizGecmis.getSelectedIndex()!=0){
                String sql = "INSERT INTO HASTA_EGZERSIZ (hasta_tc, egzersiz_turu_id) VALUES (?, ?)";
                String sql1 = "INSERT INTO HASTA_DIYET (hasta_tc, diyet_turu_id) VALUES (?, ?)";
                String sql2 = "SELECT * FROM DIYET_TURU WHERE tur_adi = ?";
                String sql3 = "SELECT * FROM EGZERSIZ_TURU WHERE tur_adi = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, doktorUser, doktorPassword); //DOKTOR GIRISI
                     PreparedStatement ps = conn.prepareStatement(sql);
                     PreparedStatement ps1 = conn.prepareStatement(sql1);
                     PreparedStatement ps2 = conn.prepareStatement(sql2);
                     PreparedStatement ps3 = conn.prepareStatement(sql3)) {

                    ps.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                    ps1.setString(1, String.valueOf(relations.get(secilenHasta).tc_no));
                    if(diyetGecmis.getSelectedIndex()!=0){
                        ps2.setString(1,String.valueOf(diyetGecmis.getSelectedItem()));
                        ResultSet rs = ps2.executeQuery();
                        rs.next();
                        ps1.setString(2, rs.getString("diyet_turu_id"));
                        ps1.executeUpdate();
                        relations.get(secilenHasta).oneriGirdiMi = true;
                        relations.get(secilenHasta).diyetOneri = String.valueOf(diyetGecmis.getSelectedItem());
                    }
                    if(egzersizGecmis.getSelectedIndex()!=0){
                        ps3.setString(1,String.valueOf(egzersizGecmis.getSelectedItem()));
                        ResultSet rs = ps3.executeQuery();
                        rs.next();
                        ps.setString(2, rs.getString("egzersiz_turu_id"));
                        ps.executeUpdate();
                        relations.get(secilenHasta).oneriGirdiMi = true;
                        relations.get(secilenHasta).egzersizOneri = String.valueOf(egzersizGecmis.getSelectedItem());
                    }
                    conn.commit();
                    oneriYap.setVisible(false);
                    diyetGecmis.setVisible(false);
                    egzersizGecmis.setVisible(false);
                    repaint();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if(e.getSource() == diyetYap){
            String sql = "INSERT INTO HASTA_DIYET_CHECK (hasta_tc, tarih, yapildi_mi) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                ps.setString(3, "1");
                ps.executeUpdate();
                conn.commit();
                diyetYap.setVisible(false);
                LocalDate dateOnly = selectedDateTime[0].toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(String.valueOf(dateOnly).equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){diyetYapildi = 1;}
                diyetEgzersizCheck(kullanici);
                tarihSec.removeAllItems();
                tarihUpdate(kullanici);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == egzersizYap) {
            String sql = "INSERT INTO HASTA_EGZERSIZ_CHECK (hasta_tc, tarih, yapildi_mi) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, String.valueOf(kullanici.tc_no));
                ps.setString(2, String.valueOf(new java.sql.Timestamp(selectedDateTime[0].getTime())));
                ps.setString(3, "1");
                ps.executeUpdate();
                conn.commit();
                egzersizYap.setVisible(false);
                LocalDate dateOnly = selectedDateTime[0].toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(String.valueOf(dateOnly).equals(tarihReformat(String.valueOf(tarihSec.getSelectedItem())))){egzersizYapildi = 1;}
                diyetEgzersizCheck(kullanici);
                tarihSec.removeAllItems();
                tarihUpdate(kullanici);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        } else if (e.getSource() == sifreDegistirButton) {
            if(!sifreDegistir.getText().isEmpty()){
                String sql = "UPDATE KULLANICI SET sifre = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?)) WHERE tc_no = ?";
                try (Connection conn = DriverManager.getConnection(Main.url, hastaUser, hastaPassword); // HASTA
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, sifreDegistir.getText());
                    ps.setString(2, String.valueOf(kullanici.tc_no));
                    ps.executeUpdate();
                    conn.commit();
                    sifreDegistir.setText("");
                    sifreDegistirildiMi=1;
                    repaint();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }else {
                sifreDegistirildiMi=0;
                repaint();
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (!relationsRects.isEmpty() && currentScreen == Screen.MAIN) {
            Rectangle firstRect = relationsRects.get(0);
            Rectangle lastRect = relationsRects.get(relationsRects.size() - 1);

            int upperLimit = 130;
            int lowerLimit = 720;

            boolean canScrollDown = firstRect.y <= upperLimit && notches > 0 && lastRect.y + 130 >= lowerLimit;
            boolean canScrollUp = firstRect.y < upperLimit && notches < 0 && lastRect.y + 150 >= lowerLimit;

            boolean scrollDebug = relationsRects.size() > 4 && (lastRect.y + 150 < lowerLimit || firstRect.y > upperLimit);

            if (canScrollDown || canScrollUp || scrollDebug) {
                for (Rectangle rect : relationsRects) {
                    rect.y -= notches * 20;
                }
                repaint();
            }
        }
    }
}
