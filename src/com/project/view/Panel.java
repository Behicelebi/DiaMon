package com.project.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.project.main.Main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class Panel extends JPanel implements ActionListener {
    int WIDTH;
    int HEIGHT;
    JTextField kullaniciAdiGiris = new JTextField();
    JPasswordField sifreGiris = new JPasswordField();
    RoundedButton hastaGiris = new RoundedButton("Hasta Giriş");
    RoundedButton doktorGiris = new RoundedButton("Doktor Giriş");
    RoundedButton girisYap = new RoundedButton("Giriş Yap");
    RoundedButton geriButton = new RoundedButton("Geri");
    int secti = 0;
    final int kullanici_limit = 11, sifre_limit = 15;
    boolean girisHata = false;
    private Dimension originalSize = new Dimension(1280, 720);
    Image background;

    Panel(int WIDTH, int HEIGHT){

        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);

        try {
            background = ImageIO.read(new File("textures/saglik_bakanligi.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hastaGiris.setBounds(WIDTH/2-125,310,250,25);
        hastaGiris.setFont(new Font("Calibri",Font.BOLD,15));
        hastaGiris.setHorizontalAlignment(SwingConstants.CENTER);
        hastaGiris.setFocusable(false);
        hastaGiris.addActionListener(this);
        this.add(hastaGiris);

        doktorGiris.setBounds(WIDTH/2-125,380,250,25);
        doktorGiris.setFont(new Font("Calibri",Font.BOLD,15));
        doktorGiris.setHorizontalAlignment(SwingConstants.CENTER);
        doktorGiris.setFocusable(false);
        doktorGiris.addActionListener(this);
        this.add(doktorGiris);

        girisYap.setBounds(WIDTH/2+25,430,100,25);
        girisYap.setFont(new Font("Calibri",Font.BOLD,15));
        girisYap.setHorizontalAlignment(SwingConstants.CENTER);
        girisYap.setFocusable(false);
        girisYap.setVisible(false);
        girisYap.addActionListener(this);
        this.add(girisYap);

        geriButton.setBounds(WIDTH/2-125,430,100,25);
        geriButton.setFont(new Font("Calibri",Font.BOLD,15));
        geriButton.setHorizontalAlignment(SwingConstants.CENTER);
        geriButton.setFocusable(false);
        geriButton.setVisible(false);
        geriButton.addActionListener(this);
        this.add(geriButton);

        kullaniciAdiGiris.setPreferredSize(new Dimension(10,300));
        kullaniciAdiGiris.setBounds(WIDTH/2-125,310,250,25);
        kullaniciAdiGiris.setFont(new Font("Calibri",Font.PLAIN,15));
        kullaniciAdiGiris.setVisible(false);
        kullaniciAdiGiris.setDocument(new PlainDocument(){
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if(getLength() + str.length() <= kullanici_limit)
                    super.insertString(offs, str, a);
            }
        });
        kullaniciAdiGiris.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;

                if (str.matches("\\d+") && getLength() + str.length() <= kullanici_limit) {
                    super.insertString(offs, str, a);
                }
            }
        });
        kullaniciAdiGiris.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    girisYap.doClick();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    geriButton.doClick();
                }
            }
        });
        this.add(kullaniciAdiGiris);

        sifreGiris.setPreferredSize(new Dimension(10,300));
        sifreGiris.setBounds(WIDTH/2-125,380,250,25);
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
        sifreGiris.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    girisYap.doClick();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    geriButton.doClick();
                }
            }
        });
        this.add(sifreGiris);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(background,0,0,WIDTH,HEIGHT,null);
        g2d.setColor(new Color(0,0,0,150));
        g2d.fillRoundRect(WIDTH/2-170,200,340,300, 40, 40);
        g2d.setColor(new Color(96, 96, 96,150));
        g2d.drawRoundRect(WIDTH/2-170,200,340,300, 40, 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,40));
        g.drawString("Sağlık Bakanlığı Diyabet Sistemi",WIDTH/2-355,100);
        if(secti == 1){
            g.setFont(new Font("Consolas",Font.PLAIN,20));
            g.drawString("Hasta Girişi",WIDTH/2-64,250);
            g.setFont(new Font("Consolas",Font.PLAIN,15));
            g.drawString("Kullanıcı Adı:", WIDTH/2-125,300);
            g.drawString("Şifre:", WIDTH/2-125,370);
        } else if (secti == 2) {
            g.setFont(new Font("Consolas",Font.PLAIN,20));
            g.drawString("Doktor Girişi",WIDTH/2-70,250);
            g.setFont(new Font("Consolas",Font.PLAIN,15));
            g.drawString("Kullanıcı Adı:", WIDTH/2-125,300);
            g.drawString("Şifre:", WIDTH/2-125,370);
        } else{
            g.setFont(new Font("Consolas",Font.PLAIN,20));
            g.drawString("Hoşgeldiniz",WIDTH/2-60,250);
            g.setFont(new Font("Consolas",Font.PLAIN,15));
            g.drawString("Lütfen uygun seçeneği seçiniz.",WIDTH/2-120,460);
        }
        g.setColor(Color.RED);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        if(girisHata){g.drawString("Hatalı TC veya Şifre girişi!",WIDTH/2-110,480);}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == hastaGiris){
            secti = 1;
            hastaGiris.setVisible(false);
            doktorGiris.setVisible(false);
            kullaniciAdiGiris.setVisible(true);
            sifreGiris.setVisible(true);
            girisYap.setVisible(true);
            geriButton.setVisible(true);
            repaint();
        } else if (e.getSource() == doktorGiris) {
            secti = 2;
            hastaGiris.setVisible(false);
            doktorGiris.setVisible(false);
            kullaniciAdiGiris.setVisible(true);
            sifreGiris.setVisible(true);
            girisYap.setVisible(true);
            geriButton.setVisible(true);
            repaint();
        } else if (e.getSource() == geriButton) {
            secti = 0;
            hastaGiris.setVisible(true);
            doktorGiris.setVisible(true);
            kullaniciAdiGiris.setVisible(false);
            kullaniciAdiGiris.setText("");
            sifreGiris.setVisible(false);
            sifreGiris.setText("");
            girisYap.setVisible(false);
            geriButton.setVisible(false);
            girisHata = false;
            repaint();
        } else if (e.getSource() == girisYap) {
            if(!kullaniciAdiGiris.getText().equals("") && !sifreGiris.getText().equals("")){
                Main.enUserName = kullaniciAdiGiris.getText();
                Main.enPassword = new String(sifreGiris.getPassword());
                String sql = "SELECT ad, soyad, rol FROM KULLANICI " +
                        "WHERE tc_no = ? AND sifre = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))"+
                        "AND rol = ?";
                try (
                        Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
                        PreparedStatement ps = conn.prepareStatement(sql)
                ) {
                    ps.setString(1, Main.enUserName);
                    ps.setString(2, Main.enPassword);
                    if(secti == 1){
                        ps.setString(3, "HASTA");
                    } else if (secti == 2) {
                        ps.setString(3, "DOKTOR");
                    }

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        conn.close();
                        if(secti == 1){Main.frame.sistemUI.doktorMu = false;}
                        else if (secti == 2) {Main.frame.sistemUI.doktorMu = true;}
                        Main.frame.switchScreen(1);
                    } else {
                        conn.close();
                        girisHata = true;
                        repaint();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                girisHata = true;
                repaint();
            }
        }
    }
}
