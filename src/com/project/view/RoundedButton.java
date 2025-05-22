package com.project.view;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private int radius = 25;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setFont(new Font("Arial", Font.BOLD, 14));
        setPreferredSize(new Dimension(160, 50));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.CENTER);
        setMargin(new Insets(5, 10, 0, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ButtonModel model = getModel();
        boolean isPressed = model.isArmed() && model.isPressed();

        if (!isPressed) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
        }
        g2.setColor(isPressed ? new Color(138, 136, 136) : Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }

}

