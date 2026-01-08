package desktop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class DashboardFrame extends JFrame {

    private JLabel cpuValue;
    private JLabel ramValue;
    private JLabel diskValue;
    private JLabel statusBar;
    private JPanel card;

    private JComboBox<String> agentSelector;
    private String currentAgent = "agent-1";

    // ===== COEFFICIENTS VISUELS =====
    private static final double CPU_GAIN = 20.0;
    private static final double RAM_GAIN = 18.0;

    // ===== PALETTE DE COULEURS PROFESSIONNELLE =====
    private static final Color BG_PRIMARY = new Color(15, 23, 42);
    private static final Color BG_SECONDARY = new Color(30, 41, 59);
    private static final Color BG_CARD = new Color(51, 65, 85);
    private static final Color ACCENT_GREEN = new Color(16, 185, 129);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_CYAN = new Color(6, 182, 212);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    public DashboardFrame() {
        setTitle("Dashboard de supervision ");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(BG_PRIMARY);

        // ================= HEADER AVEC GRADIENT PREMIUM =================
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(59, 130, 246),
                    getWidth(), 0, new Color(16, 185, 129)
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Effet brillant subtle
                GradientPaint shine = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 30),
                    0, getHeight() / 2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(shine);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 20, 20);
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("SUPERVISION SYSTEME");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(30, 40, 30, 20));
        
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleBox.setOpaque(false);
        titleBox.add(title);
        
        headerPanel.add(titleBox, BorderLayout.WEST);

        // ================= SELECTEUR AGENT ULTRA STYLISÉ =================
        JPanel selectorWrapper = new JPanel();
        selectorWrapper.setOpaque(false);
        selectorWrapper.setLayout(new FlowLayout(FlowLayout.RIGHT, 40, 25));
        
        JLabel agentLabel = new JLabel("Agent:");
        agentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        agentLabel.setForeground(TEXT_PRIMARY);
        
        agentSelector = new JComboBox<>(new String[]{"agent-1"});
        agentSelector.setFont(new Font("Segoe UI", Font.BOLD, 14));
        agentSelector.setPreferredSize(new Dimension(180, 40));
        agentSelector.setMaximumRowCount(5);
        
        // Style ultra-moderne pour le combo
        agentSelector.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(ACCENT_BLUE);
                        
                        int[] xPoints = {getWidth()/2 - 4, getWidth()/2 + 4, getWidth()/2};
                        int[] yPoints = {getHeight()/2 - 2, getHeight()/2 - 2, getHeight()/2 + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                return button;
            }
        });
        
        agentSelector.setBackground(Color.WHITE);
        agentSelector.setForeground(new Color(30, 41, 59));
        agentSelector.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
            new EmptyBorder(8, 15, 8, 10)
        ));
        
        agentSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if (isSelected) {
                    setBackground(ACCENT_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(30, 41, 59));
                }
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return this;
            }
        });
        
        agentSelector.addActionListener(e ->
                currentAgent = (String) agentSelector.getSelectedItem()
        );
        
        selectorWrapper.add(agentLabel);
        selectorWrapper.add(agentSelector);
        headerPanel.add(selectorWrapper, BorderLayout.EAST);

        // ================= CARD METRICS PREMIUM =================
        card = new RoundedPanel(25);
        card.setLayout(new GridLayout(1, 4, 25, 0));
        card.setBorder(new EmptyBorder(35, 40, 35, 40));
        card.setBackground(BG_CARD);

        JPanel statePanel = new JPanel(new BorderLayout());
        statePanel.setOpaque(false);
        JLabel stateLabel = new JLabel("ONLINE");
        stateLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        stateLabel.setForeground(ACCENT_GREEN);
        JLabel stateLabelTop = new JLabel("ETAT");
        stateLabelTop.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stateLabelTop.setForeground(TEXT_SECONDARY);
        JPanel stateContent = new JPanel();
        stateContent.setLayout(new BoxLayout(stateContent, BoxLayout.Y_AXIS));
        stateContent.setOpaque(false);
        stateContent.add(stateLabelTop);
        stateContent.add(Box.createVerticalStrut(8));
        stateContent.add(stateLabel);
        statePanel.add(stateContent, BorderLayout.CENTER);

        JPanel cpuPanel = new JPanel(new BorderLayout());
        cpuPanel.setOpaque(false);
        cpuValue = new JLabel("-- %");
        cpuValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        cpuValue.setForeground(ACCENT_BLUE);
        JLabel cpuLabel = new JLabel("CPU");
        cpuLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cpuLabel.setForeground(TEXT_SECONDARY);
        JPanel cpuContent = new JPanel();
        cpuContent.setLayout(new BoxLayout(cpuContent, BoxLayout.Y_AXIS));
        cpuContent.setOpaque(false);
        cpuContent.add(cpuLabel);
        cpuContent.add(Box.createVerticalStrut(8));
        cpuContent.add(cpuValue);
        cpuPanel.add(cpuContent, BorderLayout.CENTER);

        JPanel ramPanel = new JPanel(new BorderLayout());
        ramPanel.setOpaque(false);
        ramValue = new JLabel("-- %");
        ramValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        ramValue.setForeground(ACCENT_CYAN);
        JLabel ramLabel = new JLabel("RAM");
        ramLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ramLabel.setForeground(TEXT_SECONDARY);
        JPanel ramContent = new JPanel();
        ramContent.setLayout(new BoxLayout(ramContent, BoxLayout.Y_AXIS));
        ramContent.setOpaque(false);
        ramContent.add(ramLabel);
        ramContent.add(Box.createVerticalStrut(8));
        ramContent.add(ramValue);
        ramPanel.add(ramContent, BorderLayout.CENTER);

        JPanel diskPanel = new JPanel(new BorderLayout());
        diskPanel.setOpaque(false);
        diskValue = new JLabel("-- %");
        diskValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        diskValue.setForeground(new Color(168, 85, 247));
        JLabel diskLabel = new JLabel("DISK");
        diskLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        diskLabel.setForeground(TEXT_SECONDARY);
        JPanel diskContent = new JPanel();
        diskContent.setLayout(new BoxLayout(diskContent, BoxLayout.Y_AXIS));
        diskContent.setOpaque(false);
        diskContent.add(diskLabel);
        diskContent.add(Box.createVerticalStrut(8));
        diskContent.add(diskValue);
        diskPanel.add(diskContent, BorderLayout.CENTER);

        card.add(statePanel);
        card.add(cpuPanel);
        card.add(ramPanel);
        card.add(diskPanel);

        // ================= EXPORT BUTTON PREMIUM =================
        JButton exportBtn = new PremiumButton("EXPORTER CSV");
        exportBtn.addActionListener(e -> exportMetrics());

        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setBackground(BG_PRIMARY);
        exportPanel.setBorder(new EmptyBorder(15, 40, 15, 40));
        exportPanel.add(exportBtn);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setBackground(BG_PRIMARY);
        cardWrapper.setBorder(new EmptyBorder(25, 40, 10, 40));
        cardWrapper.add(card, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_PRIMARY);
        top.add(headerPanel, BorderLayout.NORTH);
        top.add(cardWrapper, BorderLayout.CENTER);
        top.add(exportPanel, BorderLayout.SOUTH);

        // ================= GRAPHIQUES PROFESSIONNELS =================
        MetricChartPanel cpuChart = new MetricChartPanel("CPU", 80);
        MetricChartPanel ramChart = new MetricChartPanel("RAM", 80);
        MetricChartPanel diskChart = new MetricChartPanel("DISK", 90);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_SECONDARY);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Style premium pour les onglets
        UIManager.put("TabbedPane.contentAreaColor", BG_PRIMARY);
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.background", BG_SECONDARY);
        UIManager.put("TabbedPane.foreground", TEXT_SECONDARY);
        UIManager.put("TabbedPane.selectedForeground", new Color(30, 41, 59));
        UIManager.put("TabbedPane.borderHightlightColor", ACCENT_BLUE);
        
        tabs.addTab("  CPU  ", cpuChart);
        tabs.addTab("  RAM  ", ramChart);
        tabs.addTab("  DISK  ", diskChart);

        // ================= STATUS BAR ÉLÉGANTE =================
        statusBar = new JLabel(" [EN LIGNE] Agent: agent-1 | Etat: ONLINE | Derniere mise a jour: --");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusBar.setBorder(new EmptyBorder(12, 30, 12, 30));
        statusBar.setBackground(BG_SECONDARY);
        statusBar.setForeground(TEXT_SECONDARY);
        statusBar.setOpaque(true);

        // ================= LAYOUT PRINCIPAL =================
        setLayout(new BorderLayout(0, 0));
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // ================= TIMER DE MISE À JOUR =================
        new Timer(2000, e -> {
            Metric m = ApiClient.fetchMetrics(currentAgent);

            if (m != null) {
                double cpuDisplay = Math.min(m.cpu * CPU_GAIN, 100.0);
                double ramDisplay = Math.min(m.memory * RAM_GAIN, 100.0);
                double diskDisplay = m.disk;

                cpuValue.setText(String.format("%.2f %%", cpuDisplay));
                ramValue.setText(String.format("%.2f %%", ramDisplay));
                diskValue.setText(String.format("%.2f %%", diskDisplay));

                cpuChart.addValue(cpuDisplay);
                ramChart.addValue(ramDisplay);
                diskChart.addValue(diskDisplay);

                // Animation des couleurs selon l'état
                if (cpuDisplay >= 80 || ramDisplay >= 80 || diskDisplay >= 90) {
                    animateCardAlert(card, true);
                } else {
                    animateCardAlert(card, false);
                }

                statusBar.setText(String.format(
                        " [EN LIGNE] Agent: %s | Etat: ONLINE | Derniere mise a jour: %tT",
                        currentAgent,
                        System.currentTimeMillis()
                ));
            }
        }).start();
    }

    // ================= ANIMATION ALERTE =================
    private void animateCardAlert(JPanel panel, boolean alert) {
        if (alert) {
            panel.setBackground(new Color(71, 55, 65));
        } else {
            panel.setBackground(BG_CARD);
        }
    }

    // ================= EXPORT LOGIC =================
    private void exportMetrics() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("metrics_" + currentAgent + ".csv"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (InputStream in =
                         new URL("http://localhost:8080/export/metrics/" + currentAgent).openStream();
                 FileOutputStream out =
                         new FileOutputStream(chooser.getSelectedFile())) {

                in.transferTo(out);
                JOptionPane.showMessageDialog(this,
                        "[OK] Export reussi !",
                        "Export CSV",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "[ERREUR] Erreur export : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= CLASSE PANEL ARRONDI =================
    static class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    // ================= CLASSE BOUTON PREMIUM =================
    static class PremiumButton extends JButton {
        private Color hoverColor = new Color(16, 185, 129);
        private Color normalColor = new Color(59, 130, 246);
        private Color currentColor;

        public PremiumButton(String text) {
            super(text);
            currentColor = normalColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(new Dimension(200, 45));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    currentColor = hoverColor;
                    repaint();
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    currentColor = normalColor;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2d.setColor(currentColor.darker());
            } else {
                g2d.setColor(currentColor);
            }
            
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            
            // Effet de brillance
            GradientPaint shine = new GradientPaint(
                0, 0, new Color(255, 255, 255, 40),
                0, getHeight() / 2, new Color(255, 255, 255, 0)
            );
            g2d.setPaint(shine);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
            
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}