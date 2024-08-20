package draw.slider;

import draw.DrawLine;
import draw.DrawOval;
import draw.DrawPolygon;
import draw.DrawRect;

import java.awt.*;

public class DrawSlider {
    private final DrawPolygon rect = new DrawRect();
    private final DrawLine line = new DrawLine();

    private final Color frameColor = new Color(250, 150, 0);

    public void drawSlider(Graphics2D g2d, int x, int y, int width, int scale, int pointer) {
        rect.fill(g2d, Color.WHITE,
                rect.makeParam(x, y + 9, width, 2)
        );
        rect.fill(g2d, Color.WHITE,
                rect.makeParam(x, y, 2, 20),
                rect.makeSide(rect.CENTER, rect.TOP)
        );
        rect.fill(g2d, Color.WHITE,
                rect.makeParam(x + width, y, 2, 20),
                rect.makeSide(rect.CENTER, rect.TOP)
        );
        for(int i = 0; i < scale; i++) {
            int xs = width * i / scale;
            line.draw(g2d, Color.WHITE,
                    line.makeParam(x + xs, y + 5, x + xs, y + 15)
            );
        }
        int px = width * pointer / scale;
        int h = pointer == 0 || pointer == scale ? 24 : 16;
        rect.fill(g2d, frameColor,
                rect.makeParam(x + px, y + 10, 6, h),
                rect.makeSide(rect.CENTER, rect.CENTER)
        );
        rect.fill(g2d, Color.WHITE,
                rect.makeParam(x + px, y + 10, 2, h - 4),
                rect.makeSide(rect.CENTER, rect.CENTER)
        );
    }
}
