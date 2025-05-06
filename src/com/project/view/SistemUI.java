package com.project.view;


import com.project.kullanicilar.Kullanici;
import com.project.main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SistemUI extends JPanel {
    int WIDTH;
    int HEIGHT;
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    Kullanici doktor;
    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
    }
    public void initialize(){
        String sql = "SELECT ad, soyad, email, dogum_tarihi, cinsiyet, profil_resmi FROM KULLANICI " +
                "WHERE tc_no = ? AND sifre_hash = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))";
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, Main.enUserName);
            ps.setString(2, Main.enPassword) ;

            ResultSet rs = ps.executeQuery();
            rs.next();
            doktor = new Kullanici(rs.getString("ad"),rs.getString("soyad"),rs.getString("email"),rs.getString("dogum_tarihi"),rs.getString("cinsiyet"), ImageIO.read(rs.getBinaryStream("profil_resmi")));
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
        g.drawImage(doktor.profil_resmi,20,140,this);
        g.drawString("İsim: " + doktor.ad, 150,150);
        g.drawString("Cinsiyet: " + doktor.cinsiyet, 150,170);
        g.drawString("Doğum Tarihi: " + doktor.dogum_tarihi, 150,190);
        g.drawString("E-Posta: " + doktor.email, 150,210);
        g.drawString("Rol: DOKTOR", 150,230);
    }
}
