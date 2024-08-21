package scenes.draw;

import java.awt.*;
import java.util.Map;

// 図形の描画
public interface Draw {
    // 辺だけ描画
    void draw(Graphics2D g2d, Color c, Map<Param, Integer> param);

    // 中を塗って描画
    void fill(Graphics2D g2d, Color c, Map<Param, Integer> param);

    // パラメータ
    enum Param {
        X,
        Y,
        X2,
        Y2,
        LENGTH,
        WIDTH,
        HEIGHT,
        RADIUS,
        ANGLE,
        ANGLE2,
        WIDTH_TOP,
        WIDTH_BOTTOM
    }

    enum Side {
        X,
        Y,
        DIR
    }
}
