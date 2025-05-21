package shapeville.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WoodenButton extends JButton {
    private static final Color BG_COLOR = new Color(232, 194, 145);
    private static final Color BORDER_COLOR = new Color(160, 82, 45);
    private static final Color FONT_COLOR = new Color(101, 67, 33);
    private static final Font BUTTON_FONT = new Font("Serif", Font.BOLD, 16);
    private static final int ARC = 16;
    private boolean hovered = false;
    private String description = null;

    public WoodenButton(String text) {
        super(text);
        setupButton();
    }
    
    public WoodenButton(String text, String description) {
        super(text);
        this.description = description;
        setupButton();
    }
    
    private void setupButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(BUTTON_FONT);
        setForeground(FONT_COLOR);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setMargin(new Insets(8, 20, 8, 20));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 背景
        g2.setColor(BG_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        // 边框
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        // 悬浮高亮
        if (hovered) {
            g2.setColor(new Color(255, 255, 150, 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        }
        
        // 绘制文本
        String text = getText();
        g2.setColor(FONT_COLOR);
        g2.setFont(BUTTON_FONT);
        
        if (description != null) {
            // 如果有描述文本，则分两行绘制
            FontMetrics fm = g2.getFontMetrics();
            int titleWidth = fm.stringWidth(text);
            int titleHeight = fm.getAscent();
            int x = (getWidth() - titleWidth) / 2;
            int y = getHeight() / 2 - 5;  // 标题在上半部分
            
            // 绘制标题
            g2.drawString(text, x, y);
            
            // 绘制描述 (使用较小字体)
            Font descFont = new Font(BUTTON_FONT.getName(), Font.PLAIN, BUTTON_FONT.getSize() - 2);
            g2.setFont(descFont);
            fm = g2.getFontMetrics();
            int descWidth = fm.stringWidth(description);
            x = (getWidth() - descWidth) / 2;
            y = getHeight() / 2 + fm.getAscent() + 5; // 描述在下半部分
            g2.drawString(description, x, y);
        } else {
            // 只有标题文本，居中绘制
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2 - 3;
            g2.drawString(text, x, y);
        }
        
        g2.dispose();
    }
    
    public void setDescription(String description) {
        this.description = description;
        repaint();
    }
} 