package scenes.draw.slider;

import scenes.draw.DrawLine;
import scenes.draw.DrawPolygon;
import scenes.draw.DrawRect;
import scenes.draw.blueprint.Blueprint;

import java.awt.*;

/**
 * 目盛とその間にポインタを描画し、数値を視覚的に示すスライダーを描画する。
 */
public class DrawSlider {
    private final DrawPolygon rect = new DrawRect();
    private final DrawLine line = new DrawLine();

    private final Color frameColor = new Color(250, 150, 0);

    /**
     * スライダーの描画
     * @param x         X座標
     * @param y         Y座標
     * @param width     スライダーの幅
     * @param scale     目盛の数
     * @param pointer   ポインタ(0 ≦ pointer ≦ scale)
     */
    public void drawSlider(Graphics2D g2d, int x, int y, int width, int scale, int pointer) {
        Blueprint sliderLine = new Blueprint(x, y + 9, width, 2);
        sliderLine.fillPolygon(g2d, rect, Color.WHITE);

        Blueprint min = new Blueprint(x, y, 2, 20);
        min.setSide(Blueprint.CENTER, Blueprint.TOP);
        min.fillPolygon(g2d, rect, Color.WHITE);

        Blueprint max = new Blueprint(x + width, y, 2, 20);
        max.setSide(Blueprint.CENTER, Blueprint.TOP);
        max.fillPolygon(g2d, rect, Color.WHITE);

        for(int i = 0; i < scale; i++) {
            int xs = width * i / scale;
            // TODO: BluePrintに線オブジェクトの追加
            line.draw(g2d, Color.WHITE,
                    line.makeParam(x + xs, y + 5, x + xs, y + 15)
            );
        }
        int px = width * pointer / scale;
        int h = pointer == 0 || pointer == scale ? 24 : 16;
        Blueprint pointerFrame = new Blueprint(x + px, y + 10, 6, h);
        pointerFrame.setSide(Blueprint.CENTER, Blueprint.CENTER);
        pointerFrame.fillPolygon(g2d, rect, frameColor);

        Blueprint pointerInner = new Blueprint(x + px, y + 10, 2, h - 4);
        pointerInner.setSide(Blueprint.CENTER, Blueprint.CENTER);
        pointerInner.fillPolygon(g2d, rect, Color.WHITE);
    }
}
