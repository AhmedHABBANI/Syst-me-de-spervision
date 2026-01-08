package desktop;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CpuChartPanel extends JPanel {

    private final List<Double> history = new ArrayList<>();
    private static final int MAX = 30;
    private final double critical;
    private final String label;
    private double lastValue = 0;

    public CpuChartPanel(String label, double critical) {
        this.label = label;
        this.critical = critical;
        setBackground(Color.WHITE);
    }

    public void addValue(double v) {
        lastValue = v;
        history.add(v);
        if (history.size() > MAX) history.remove(0);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (history.size() < 2) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int left = 60;
        int right = w - 20;
        int top = 20;
        int bottom = h - 50;

        // grille
        g2.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 10; i++) {
            int y = top + i * (bottom - top) / 10;
            g2.drawLine(left, y, right, y);
        }

        // axes
        g2.setColor(Color.GRAY);
        g2.drawLine(left, top, left, bottom);
        g2.drawLine(left, bottom, right, bottom);

        // labels %
        g2.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= 10; i++) {
            int y = bottom - i * (bottom - top) / 10;
            g2.drawString((i * 10) + "%", 15, y + 5);
        }

        // seuil
        int yCrit = bottom - (int) (critical / 100 * (bottom - top));
        g2.setColor(new Color(255, 80, 80));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{6}, 0));
        g2.drawLine(left, yCrit, right, yCrit);
        g2.drawString("Seuil " + label, right - 120, yCrit - 5);

        // courbe
        g2.setStroke(new BasicStroke(3));
        g2.setColor(new Color(52, 152, 219));

        int n = history.size();
        for (int i = 1; i < n; i++) {
            int x1 = left + (i - 1) * (right - left) / MAX;
            int x2 = left + i * (right - left) / MAX;

            int y1 = bottom - (int) (history.get(i - 1) / 100 * (bottom - top));
            int y2 = bottom - (int) (history.get(i) / 100 * (bottom - top));

            g2.drawLine(x1, y1, x2, y2);
        }

        // point courant
        int x = left + (n - 1) * (right - left) / MAX;
        int y = bottom - (int) (lastValue / 100 * (bottom - top));
        g2.setColor(Color.RED);
        g2.fillOval(x - 4, y - 4, 8, 8);
    }
}
