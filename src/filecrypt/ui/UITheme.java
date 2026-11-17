package filecrypt.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

/**
 * UI Theme and styling constants optimized for high contrast, maximum legibility,
 * and modern aesthetics.
 */
public class UITheme {

    // High Contrast Palette
    public static final Color PRIMARY = new Color(29, 78, 216);        // Deep Royal Blue #1D4ED8
    public static final Color PRIMARY_HOVER = new Color(30, 64, 175);  // #1E40AF
    public static final Color SUCCESS = new Color(5, 150, 105);        // Deep Emerald #059669
    public static final Color SUCCESS_HOVER = new Color(4, 120, 87);   // #047857
    public static final Color ACCENT = new Color(109, 40, 217);        // Deep Purple #6D28D9
    public static final Color ACCENT_HOVER = new Color(91, 33, 182);
    public static final Color DANGER = new Color(220, 38, 38);         // Deep Red #DC2626
    public static final Color DANGER_HOVER = new Color(185, 28, 28);
    public static final Color NEUTRAL_BTN = new Color(71, 85, 105);    // Slate #475569
    public static final Color NEUTRAL_BTN_HOVER = new Color(51, 65, 85);

    // Backgrounds & Surface
    public static final Color BG_APP = new Color(241, 245, 249);       // Slate 100
    public static final Color BG_PANEL = new Color(255, 255, 255);     // Crisp White
    public static final Color BG_HEADER = new Color(15, 23, 42);       // Midnight Slate #0F172A

    // High Legibility Text Colors
    public static final Color TEXT_DARK = new Color(15, 23, 42);       // Very Dark Slate #0F172A
    public static final Color TEXT_MUTED = new Color(51, 65, 85);      // Slate 700 - clearly legible
    public static final Color TEXT_WHITE = new Color(255, 255, 255);   // Pure White
    public static final Color TEXT_ACCENT = new Color(56, 189, 248);   // Sky Blue #38BDF8
    public static final Color BORDER_COLOR = new Color(203, 213, 225); // Slate 300

    // Typography (Clear, readable sizes)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 19);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_CODE = new Font("Consolas", Font.PLAIN, 14);
    public static final Font FONT_STATUS = new Font("Segoe UI", Font.BOLD, 12);

    /**
     * Creates a high-contrast titled panel border.
     */
    public static Border createPanelBorder(String title) {
        Border line = BorderFactory.createLineBorder(BORDER_COLOR, 1);
        return BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(line, BorderFactory.createEmptyBorder(6, 10, 8, 10)),
                " " + title + " ",
                0,
                0,
                FONT_SECTION,
                TEXT_DARK
        );
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    /**
     * Custom Modern Button with anti-aliased custom painting.
     * Guaranteed to look vibrant and 100% readable on all Windows systems.
     */
    public static JButton createButton(String text, Color bgColor, Color hoverColor, Color fgColor) {
        JButton btn = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                setForeground(fgColor);
                setFont(FONT_BOLD);

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color currentBg;
                if (!isEnabled()) {
                    currentBg = new Color(203, 213, 225);
                } else if (isPressed) {
                    currentBg = hoverColor.darker();
                } else if (isHovered) {
                    currentBg = hoverColor;
                } else {
                    currentBg = bgColor;
                }

                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Subtle border
                g2.setColor(currentBg.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        return btn;
    }

    /**
     * Light styled button (e.g. for Secondary actions like Copy, Reload, Clear).
     */
    public static JButton createSecondaryButton(String text) {
        Color bg = new Color(241, 245, 249);
        Color hover = new Color(226, 232, 240);
        Color fg = TEXT_DARK;
        JButton btn = createButton(text, bg, hover, fg);
        btn.setFont(FONT_BOLD);
        return btn;
    }
}
