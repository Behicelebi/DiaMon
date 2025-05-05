package com.project.view;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame{
    int WIDTH = 1200;
    int HEIGHT = 800;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)screenSize.getWidth()/2-(WIDTH/2);
    int screenHeight = (int)screenSize.getHeight()/2-(HEIGHT/2);

    Panel panel = new Panel(WIDTH,HEIGHT);
    SistemUI sistemUI = new SistemUI(WIDTH,HEIGHT);

    public Frame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Diyabet Takip Sistemi");
        this.setResizable(false);
        this.setLocation(screenWidth,screenHeight);
        this.setSize(WIDTH,HEIGHT);
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }

    public void switchScreen(){
        Frame frame = this;
        frame.getContentPane().remove(panel);
        frame.getContentPane().add(sistemUI);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
        sistemUI.requestFocus();
    }
}
