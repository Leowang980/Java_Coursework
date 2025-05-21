package shapeville.bonus1;

import shapeville.utils.WoodenButton;
import javax.swing.*;
import java.awt.*;

public class CompoundShapeButton extends WoodenButton {
    private final int shapeType; // 0~5

    public CompoundShapeButton(int shapeType) {
        super("");
        this.shapeType = shapeType;
        setPreferredSize(new Dimension(180, 140));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 图形区域
        int w = getWidth();
        int h = getHeight();
        int margin = 24;
        int gw = w - 2 * margin;
        int gh = h - 2 * margin;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(210, 210, 210)); // 浅灰色填充

        switch (shapeType) {
            case 0: // L型
                g2.fillRect(margin, margin, gw / 2, gh);
                g2.fillRect(margin + gw / 2, margin + gh / 2, gw / 2, gh / 2);
                g2.setColor(Color.BLACK);
                g2.drawRect(margin, margin, gw / 2, gh);
                g2.drawRect(margin + gw / 2, margin + gh / 2, gw / 2, gh / 2);
                break;
            case 1: // T型
                g2.setColor(new Color(210, 210, 210));
                g2.fillRect(margin, margin, gw, gh / 2);
                g2.fillRect(margin + gw / 3, margin + gh / 2, gw / 3, gh / 2);
                g2.setColor(Color.BLACK);
                g2.drawRect(margin, margin, gw, gh / 2);
                g2.drawRect(margin + gw / 3, margin + gh / 2, gw / 3, gh / 2);
                break;
            case 2: // 反L型
                g2.setColor(new Color(210, 210, 210));
                g2.fillRect(margin + gw / 2, margin, gw / 2, gh);
                g2.fillRect(margin, margin + gh / 2, gw / 2, gh / 2);
                g2.setColor(Color.BLACK);
                g2.drawRect(margin + gw / 2, margin, gw / 2, gh);
                g2.drawRect(margin, margin + gh / 2, gw / 2, gh / 2);
                break;
            case 3: // 梯形+矩形组合
                Polygon poly = new Polygon();
                poly.addPoint(margin, margin + gh);
                poly.addPoint(margin, margin + gh / 2);
                poly.addPoint(margin + gw / 2, margin);
                poly.addPoint(margin + gw, margin);
                poly.addPoint(margin + gw, margin + gh);
                g2.setColor(new Color(210, 210, 210));
                g2.fillPolygon(poly);
                g2.setColor(Color.BLACK);
                g2.drawPolygon(poly);
                break;
            case 4: // 大矩形减小矩形
                g2.setColor(new Color(210, 210, 210));
                g2.fillRect(margin, margin, gw, gh);
                g2.setColor(Color.WHITE);
                g2.fillRect(margin + gw / 3, margin + gh / 3, gw / 3, gh / 3);
                g2.setColor(Color.BLACK);
                g2.drawRect(margin, margin, gw, gh);
                g2.drawRect(margin + gw / 3, margin + gh / 3, gw / 3, gh / 3);
                break;
            case 5: // 另一个L型
                g2.setColor(new Color(210, 210, 210));
                g2.fillRect(margin, margin, gw, gh / 2);
                g2.fillRect(margin, margin + gh / 2, gw / 2, gh / 2);
                g2.setColor(Color.BLACK);
                g2.drawRect(margin, margin, gw, gh / 2);
                g2.drawRect(margin, margin + gh / 2, gw / 2, gh / 2);
                break;
        }
        g2.dispose();
    }
}
 