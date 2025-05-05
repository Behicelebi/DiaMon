package com.project.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SistemUI extends JPanel {
    int WIDTH;
    int HEIGHT;
    public Image profil_resim;
    private static final Logger logger = Logger.getLogger(SistemUI.class.getName());
    SistemUI(int WIDTH, int HEIGHT){
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.setPreferredSize(new Dimension(this.WIDTH,this.HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.setLayout(null);
        try {
            profil_resim = ImageIO.read(new File("textures/profil.png"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading texture", e);
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
        g.drawImage(profil_resim,10,150,this);
        g.drawString("İsim: Niko OneShot", 150,150);
        g.drawString("Cinsiyet: Other", 150,170);
        g.drawString("Doğum Tarihi: 09.12.2016", 150,190);
        g.drawString("E-Posta: niko@gmail.com", 150,210);
    }
}
