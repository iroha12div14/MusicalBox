package scenes.draw;

import java.awt.*;
import java.util.Map;

/**
 * 楕円の描画
 */
public class DrawOval extends DrawPolygon {
    @Override
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        g2d.setColor(c);
        g2d.drawOval(param.get(X), param.get(Y), param.get(W), param.get(H));
    }
    @Override
    public void fill(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        g2d.setColor(c);
        g2d.fillOval(param.get(X), param.get(Y), param.get(W), param.get(H));
    }
}
