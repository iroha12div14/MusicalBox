package draw.selector;

import draw.DrawPolygon;
import draw.DrawRect;
import draw.DrawTrapezium;
import font.FontUtil;

import java.awt.*;

public class DrawSelector {
    FontUtil font = new FontUtil();
    DrawPolygon rect = new DrawRect();
    DrawTrapezium tz = new DrawTrapezium();

    private final Color frameColor = new Color(250, 150, 0);

    // 項目内のセレクタ（横）
    public void drawSelector(Graphics2D g2d, String[] strArray, int pointer, int x, int y, Font f) {
        int i = 0;
        int height = font.strHeight(g2d, f);
        for(String str : strArray) {
            int width = font.strWidth(g2d, f, str);
            if(i == pointer) {
                rect.fill(g2d, frameColor,
                        rect.makeParam(x - 3, y + 4, width + 5, height + 4),
                        rect.makeSide(rect.LEFT, rect.BOTTOM)
                );
                rect.fill(g2d, Color.WHITE,
                        rect.makeParam(x - 1, y + 2, width + 1, height),
                        rect.makeSide(rect.LEFT, rect.BOTTOM)
                );
                font.setStr(g2d, f, Color.BLACK);
                font.drawStr(g2d, str, x, y);
            } else {
                font.setStr(g2d, f, Color.WHITE);
                font.drawStr(g2d, str, x, y);
            }
            x += width + 15;
            i++;
        }
    }

    // 項目のセレクタ（縦）
    public void drawVerticalSelector(Graphics2D g2d, String[] strArray, int cursor, int x, int y, Font f, int paddingY) {
        int cursorY = y + paddingY * cursor;
        tz.fill(g2d, Color.WHITE,
                tz.makeParam(x, cursorY, 16, 0, 16),
                tz.makeSide(tz.LEFT, tz.BOTTOM, tz.VERTICAL)
        );
        tz.draw(g2d, Color.BLACK,
                tz.makeParam(x, cursorY, 16, 0, 16),
                tz.makeSide(tz.LEFT, tz.BOTTOM, tz.VERTICAL)
        );
        for(String str : strArray) {
            font.setStr(g2d, f, Color.WHITE);
            font.drawStr(g2d, str, x + 24, y);
            y += paddingY;
        }
    }
}
