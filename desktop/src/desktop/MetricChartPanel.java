package desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class MetricChartPanel extends JPanel {

    private final List<Double> history = new ArrayList<>();
    private static final int MAX = 30;
    private final double critical;
    private final String label;
    private double lastValue = 0;
    private double minValue = 0;
    private double maxValue = 100;
    private double avgValue = 0;

    // Couleurs ultra-professionnelles
    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CHART = new Color(30, 41, 59);
    private static final Color GRID_COLOR = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color LINE_COLOR = new Color(59, 130, 246);
    private static final Color AREA_COLOR = new Color(59, 130, 246, 35);
    private static final Color CRITICAL_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(251, 191, 36);
    private static final Color POINT_COLOR = new Color(16, 185, 129);
    public MetricChartPanel(String label, double critical) {
        this.label = label;
        this.critical = critical;
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    public void addValue(double v) {
        lastValue = v;
        history.add(v);
        if (history.size() > MAX) history.remove(0);
        
        if (!history.isEmpty()) {
            minValue = history.stream().min(Double::compare).orElse(0.0);
            maxValue = history.stream().max(Double::compare).orElse(100.0);
            avgValue = history.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }
        
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Marges optimisées
        int left = 70;
        int right = w - 40;
        int top = 90;
        int bottom = h - 70;

        // Header avec stats
        drawProfessionalHeader(g2, w);

        if (history.size() < 2) {
            drawNoData(g2, w, h);
            return;
        }

        // Fond du graphique
        drawChartBackground(g2, left, right, top, bottom);

        // Grille professionnelle
        drawProfessionalGrid(g2, left, right, top, bottom);

        // Zone critique
        drawCriticalZone(g2, left, right, top, bottom);

        // Labels Y
        drawYLabels(g2, left, top, bottom);

        // Labels X (temps)
        drawXLabels(g2, left, right, bottom);

        // Ligne critique
        drawCriticalLine(g2, left, right, top, bottom);

        // Zone sous la courbe
        drawAreaUnderCurve(g2, left, right, top, bottom);

        // Courbe principale
        drawProfessionalCurve(g2, left, right, top, bottom);

        // Points de données
        drawDataPoints(g2, left, right, top, bottom);

        // Point actuel avec effet glow
        drawCurrentPoint(g2, left, right, top, bottom);

        // Légende
        drawProfessionalLegend(g2, left, right, bottom);
    }

    private void drawProfessionalHeader(Graphics2D g2, int w) {
        // Titre
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.setColor(TEXT_PRIMARY);
        g2.drawString(label + " (%)", 20, 35);

        // Stats en temps réel
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(TEXT_SECONDARY);
        
        int statsX = 20;
        int statsY = 60;
        
        g2.drawString("Actuel: ", statsX, statsY);
        g2.setColor(getColorForValue(lastValue));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(String.format("%.2f", lastValue), statsX + 55, statsY);
        
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.drawString("Moy: ", statsX + 130, statsY);
        g2.setColor(TEXT_PRIMARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(String.format("%.2f", avgValue), statsX + 170, statsY);
        
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.drawString("Min: ", statsX + 240, statsY);
        g2.setColor(POINT_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(String.format("%.2f", minValue), statsX + 275, statsY);
        
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.drawString("Max: ", statsX + 345, statsY);
        g2.setColor(CRITICAL_COLOR);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString(String.format("%.2f", maxValue), statsX + 385, statsY);
    }

    private void drawChartBackground(Graphics2D g2, int left, int right, int top, int bottom) {
        g2.setColor(BG_CHART);
        g2.fillRoundRect(left - 10, top - 10, right - left + 20, bottom - top + 20, 15, 15);
    }

    private void drawNoData(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g2.setColor(TEXT_SECONDARY);
        String msg = "En attente de donnees...";
        FontMetrics fm = g2.getFontMetrics();
        int msgWidth = fm.stringWidth(msg);
        g2.drawString(msg, (w - msgWidth) / 2, h / 2);
    }

    private void drawProfessionalGrid(Graphics2D g2, int left, int right, int top, int bottom) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1));
        
        // Lignes horizontales
        for (int i = 0; i <= 10; i++) {
            int y = top + i * (bottom - top) / 10;
            if (i == 0 || i == 10) {
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawLine(left, y, right, y);
        }
        
        // Lignes verticales
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 6; i++) {
            int x = left + i * (right - left) / 6;
            g2.drawLine(x, top, x, bottom);
        }
    }

    private void drawCriticalZone(Graphics2D g2, int left, int right, int top, int bottom) {
        int yCrit = bottom - (int) (critical / 100 * (bottom - top));
        g2.setColor(new Color(239, 68, 68, 15));
        g2.fillRect(left, top, right - left, yCrit - top);
    }

    private void drawYLabels(Graphics2D g2, int left, int top, int bottom) {
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        for (int i = 0; i <= 10; i++) {
            int y = bottom - i * (bottom - top) / 10;
            String label = (i * 10) + "%";
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, left - labelWidth - 15, y + 5);
        }
    }

    private void drawXLabels(Graphics2D g2, int left, int right, int bottom) {
        g2.setColor(TEXT_SECONDARY);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        String[] timeLabels = {"-60s", "-50s", "-40s", "-30s", "-20s", "-10s", "Now"};
        for (int i = 0; i <= 6; i++) {
            int x = left + i * (right - left) / 6;
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(timeLabels[i]);
            g2.drawString(timeLabels[i], x - labelWidth / 2, bottom + 25);
        }
    }

    private void drawCriticalLine(Graphics2D g2, int left, int right, int top, int bottom) {
        int yCrit = bottom - (int) (critical / 100 * (bottom - top));
        
        g2.setColor(CRITICAL_COLOR);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{10, 5}, 0));
        g2.drawLine(left, yCrit, right, yCrit);
        
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("Seuil critique", right - 110, yCrit - 10);
    }

    private void drawAreaUnderCurve(Graphics2D g2, int left, int right, int top, int bottom) {
        if (history.size() < 2) return;

        Path2D.Double areaPath = new Path2D.Double();
        int n = history.size();
        
        areaPath.moveTo(left, bottom);
        
        for (int i = 0; i < n; i++) {
            int x = left + i * (right - left) / MAX;
            int y = bottom - (int) (history.get(i) / 100 * (bottom - top));
            areaPath.lineTo(x, y);
        }
        
        int xLast = left + (n - 1) * (right - left) / MAX;
        areaPath.lineTo(xLast, bottom);
        areaPath.closePath();
        
        g2.setColor(AREA_COLOR);
        g2.fill(areaPath);
    }

    private void drawProfessionalCurve(Graphics2D g2, int left, int right, int top, int bottom) {
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int n = history.size();
        for (int i = 1; i < n; i++) {
            int x1 = left + (i - 1) * (right - left) / MAX;
            int x2 = left + i * (right - left) / MAX;

            int y1 = bottom - (int) (history.get(i - 1) / 100 * (bottom - top));
            int y2 = bottom - (int) (history.get(i) / 100 * (bottom - top));

            g2.setColor(getColorForValue(history.get(i)));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawDataPoints(Graphics2D g2, int left, int right, int top, int bottom) {
        int n = history.size();
        for (int i = 0; i < n; i += 3) {
            int x = left + i * (right - left) / MAX;
            int y = bottom - (int) (history.get(i) / 100 * (bottom - top));
            
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillOval(x - 2, y - 2, 4, 4);
        }
    }

    private void drawCurrentPoint(Graphics2D g2, int left, int right, int top, int bottom) {
        int n = history.size();
        int x = left + (n - 1) * (right - left) / MAX;
        int y = bottom - (int) (lastValue / 100 * (bottom - top));
        
        // Effet glow
        for (int i = 4; i > 0; i--) {
            int alpha = 15 * (5 - i);
            g2.setColor(new Color(16, 185, 129, alpha));
            g2.fillOval(x - (4 + i * 2), y - (4 + i * 2), 8 + i * 4, 8 + i * 4);
        }
        
        // Point principal
        g2.setColor(POINT_COLOR);
        g2.fillOval(x - 6, y - 6, 12, 12);
        
        // Bordure
        g2.setColor(TEXT_PRIMARY);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(x - 6, y - 6, 12, 12);
        
        // Valeur actuelle
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String valueStr = String.format("%.2f%%", lastValue);
        FontMetrics fm = g2.getFontMetrics();
        int strWidth = fm.stringWidth(valueStr);
        
        // Fond de la bulle
        g2.setColor(new Color(30, 41, 59, 240));
        g2.fillRoundRect(x - strWidth / 2 - 12, y - 40, strWidth + 24, 26, 8, 8);
        
        // Bordure de la bulle
        g2.setColor(getColorForValue(lastValue));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x - strWidth / 2 - 12, y - 40, strWidth + 24, 26, 8, 8);
        
        // Texte
        g2.setColor(TEXT_PRIMARY);
        g2.drawString(valueStr, x - strWidth / 2, y - 21);
    }

    private void drawProfessionalLegend(Graphics2D g2, int left, int right, int bottom) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        int legendY = bottom + 50;
        int spacing = 140;
        
        // Légende valeur
        g2.setColor(LINE_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(left, legendY, left + 35, legendY);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Valeur", left + 45, legendY + 5);
        
        // Légende point actuel
        g2.setColor(POINT_COLOR);
        g2.fillOval(left + spacing, legendY - 5, 10, 10);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Actuel", left + spacing + 20, legendY + 5);
        
        // Légende critique
        g2.setColor(CRITICAL_COLOR);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[]{10, 5}, 0));
        g2.drawLine(left + spacing * 2, legendY, left + spacing * 2 + 35, legendY);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString("Critique", left + spacing * 2 + 45, legendY + 5);
    }

    private Color getColorForValue(double value) {
        if (value >= critical) {
            return CRITICAL_COLOR;
        } else if (value >= critical * 0.8) {
            return WARNING_COLOR;
        } else {
            return LINE_COLOR;
        }
    }
}