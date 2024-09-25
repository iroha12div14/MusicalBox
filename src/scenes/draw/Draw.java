package scenes.draw;

import java.awt.*;
import java.util.Map;

/**
 * 図形の描画
 */
public interface Draw {
    /**
     * 辺だけ描画
     * @param g2d   ？
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    void draw(Graphics2D g2d, Color c, Map<Param, Integer> param);

    /**
     * 中を塗って描画
     * @param g2d   ？
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    void fill(Graphics2D g2d, Color c, Map<Param, Integer> param);

    /**
     * 描画パラメータ（位置と大きさ）
     */
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

    /**
     * 描画パラメータ（X:左右寄せ、Y:上下寄せ、DIR:台形描画であれば水平か垂直か）
     */
    enum Side {
        X,
        Y,
        DIR
    }
}
