package scenes.option;

import scenes.draw.DrawPolygon;
import scenes.draw.DrawRect;
import scenes.font.FontUtil;

import java.awt.*;

public class OptionDrawer {
    DrawPolygon rect = new DrawRect();
    FontUtil font = new FontUtil();

    private final int displayWidth;
    private final int displayHeight;

    private final Color boxFrameColor = new Color(215, 215, 215, 255);
    private final Color boxInnerColor = new Color(55, 55, 55, 255);

    // ------------------------------------------------------- //

    // コン
    public OptionDrawer(int displayWidth, int displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    // 背景
    public void drawBack(Graphics2D g2d) {
        rect.fill(g2d, Color.BLACK,
                rect.makeParam(0, 0, displayWidth, displayHeight)
        );
    }

    // 題字と操作説明と箱
    public void drawBoxTitle(Graphics2D g2d) {
        rect.fill(g2d, boxFrameColor,
                rect.makeParam(10, 10, displayWidth - 20, 110)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(10 + 3, 10 + 3, displayWidth - 26, 110 - 6)
        );

        font.setStr(g2d, font.MSGothic(28), Color.WHITE);
        font.drawStr(g2d, "オプション画面(仮設)", 65, 45);

        font.setStr(g2d, font.MSGothic(14), Color.WHITE);
        font.drawStr(g2d, "↑↓キーで項目の移動、←→キーで設定の変更", 20, 70);
        font.drawStr(g2d, "Spaceキーで変更を適用せずに終了", 20, 90);
        font.drawStr(g2d, "Enterキーで内容通りに変更して終了", 20, 110);
    }

    // メニューの箱
    public void drawBoxMenu(Graphics2D g2d) {
        rect.fill(g2d, boxFrameColor,
                rect.makeParam(10, 130, displayWidth - 20, 360)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(10 + 3, 130 + 3, displayWidth - 26, 360 - 6)
        );
    }

}
