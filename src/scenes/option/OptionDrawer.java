package scenes.option;

import scenes.drawer.SceneDrawer;
import scenes.draw.blueprint.Blueprint;

import java.awt.*;

public class OptionDrawer extends SceneDrawer {
    // 設計図
    Blueprint background;
    Blueprint boxFrame, boxInner;
    Blueprint menuFrame, menuInner;

    // 使用色
    private final Color boxFrameColor = new Color(215, 215, 215, 255);
    private final Color boxInnerColor = new Color(55, 55, 55, 255);

    // ------------------------------------------------------- //

    // 背景
    public void drawBack(Graphics2D g2d) {
        background.fillPolygon(g2d, drawRect, Color.BLACK);
    }

    // 題字と操作説明と箱
    public void drawBoxTitle(Graphics2D g2d) {
        boxFrame.fillPolygon(g2d, drawRect, boxFrameColor);
        boxInner.fillPolygon(g2d, drawRect, boxInnerColor);

        font.setStr(g2d, font.MSGothic(28), Color.WHITE);
        font.drawStr(g2d, "オプション画面(仮設)", 65, 45);

        font.setStr(g2d, font.MSGothic(14), Color.WHITE);
        font.drawStr(g2d, "↑↓キーで項目の移動、←→キーで設定の変更", 20, 70);
        font.drawStr(g2d, "スペースキーで変更を適用せずに終了", 20, 90);
        font.drawStr(g2d, "Enterキーで内容通りに変更して終了", 20, 110);
    }

    // メニューの箱
    public void drawBoxMenu(Graphics2D g2d) {
        menuFrame.fillPolygon(g2d, drawRect, boxFrameColor);
        menuInner.fillPolygon(g2d, drawRect, boxInnerColor);
    }

    // 設計図の設定
    @Override
    public void setBlueprint() {
        background = new Blueprint(0, 0, windowSize.width, windowSize.height);

        boxFrame = new Blueprint(10, 10, windowSize.width - 20, 110);
        boxInner = new Blueprint(10 + 3, 10 + 3, windowSize.width - 26, 110 - 6);

        menuFrame = new Blueprint(10, 130, windowSize.width - 20, 360);
        menuInner = new Blueprint(10 + 3, 130 + 3, windowSize.width - 26, 360 - 6);
    }

    @Override
    protected void setAnimationTimer(int frameRate) { } // 使わない

    @Override
    protected void pastAnimationTimer() { } // 使わない

}
