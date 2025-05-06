package com.project.view;


import com.project.main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SistemUI extends JPanel {
    int WIDTH;
    int HEIGHT;
    public String ad, soyad, email, dogum_tarihi, cinsiyet;
    public Image profil_resim;
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
    }
    public void initialize(){
        String sql = "SELECT ad, soyad, email, dogum_tarihi, cinsiyet FROM KULLANICI " +
                "WHERE tc_no = ? AND sifre_hash = HASHBYTES('SHA2_256', CONVERT(NVARCHAR(MAX), ?))";
        try (
                Connection conn = DriverManager.getConnection(Main.url, Main.username, Main.password);
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, Main.enUserName);
            ps.setString(2, Main.enPassword) ;

            ResultSet rs = ps.executeQuery();
            rs.next();
            ad = rs.getString("ad");
            soyad = rs.getString("soyad");
            email = rs.getString("email");
            dogum_tarihi = rs.getString("dogum_tarihi");
            cinsiyet = rs.getString("cinsiyet");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas",Font.PLAIN,30));
        g.drawString("Diyabet Sistemi",450,100);
        g.setFont(new Font("Consolas",Font.PLAIN,15));
        //g.drawImage(profil_resim,10,150,this);
        g.drawString("İsim: " + ad, 150,150);
        g.drawString("Cinsiyet: " + cinsiyet, 150,170);
        g.drawString("Doğum Tarihi: " + dogum_tarihi, 150,190);
        g.drawString("E-Posta: " + email, 150,210);
    }
}
