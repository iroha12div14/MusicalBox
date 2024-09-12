package scenes.draw;

import java.awt.*;
import java.util.Map;

/**
 * 長方形の描画
 */
public class DrawRect extends DrawPolygon {
    @Override
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        g2d.setColor(c);
        g2d.drawRect(param.get(X), param.get(Y), param.get(W), param.get(H));
    }
    @Override
    public void fill(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        g2d.setColor(c);
        g2d.fillRect(param.get(X), param.get(Y), param.get(W), param.get(H));
    }
}
