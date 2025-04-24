package Support;

import javax.swing.*;
import java.awt.*;

public class RoundedTextField extends JTextField {
    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setFont(new Font("Arial", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.GRAY);
        ((Graphics2D) g).drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
    }

    @Override
    public boolean contains(int x, int y) {
        int arc = 30;
        return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc).contains(x, y);
    }
}
