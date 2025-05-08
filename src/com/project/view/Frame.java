package com.project.view;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame{
    int WIDTH = 1280;
    int HEIGHT = 720;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)screenSize.getWidth()/2-(WIDTH/2);
    int screenHeight = (int)screenSize.getHeight()/2-(HEIGHT/2);

    public Panel panel = new Panel(WIDTH,HEIGHT);
    public SistemUI sistemUI = new SistemUI(WIDTH,HEIGHT);

    public Frame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Diyabet Takip Sistemi");
        this.setResizable(false);
        this.setLocation(screenWidth,screenHeight);
        this.setSize(WIDTH,HEIGHT);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }

    public void switchScreen(int value){
        Frame frame = this;
        if(value == 1){
            frame.getContentPane().remove(panel);
            frame.getContentPane().add(sistemUI, BorderLayout.CENTER);
            panel.secti = 0;
            panel.hastaGiris.setVisible(true);
            panel.doktorGiris.setVisible(true);
            panel.kullaniciAdiGiris.setVisible(false);
            panel.kullaniciAdiGiris.setText("");
            panel.sifreGiris.setVisible(false);
            panel.sifreGiris.setText("");
            panel.girisYap.setVisible(false);
            panel.geriButton.setVisible(false);
            panel.girisHata = false;
            sistemUI.requestFocus();
            sistemUI.initialize();
        } else if (value == 0) {
            frame.getContentPane().remove(sistemUI);
            frame.getContentPane().add(panel, BorderLayout.CENTER);
            panel.requestFocus();
        }
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
    }
}
