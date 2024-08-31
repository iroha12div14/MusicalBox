package scenes.announce;

import scenes.animtimer.AnimationTimer;
import scenes.draw.blueprint.Blueprint;
import scenes.drawer.SceneDrawer;

import java.awt.*;
import java.util.List;

public class AnnounceDrawer extends SceneDrawer {
    private int state = 0;
    public static final int OPENING = 0; // 箱の拡大
    public static final int VIEWING = 1; // 表示
    public static final int CLOSING = 2; // 箱の縮小
    public static final int CLOSED  = 3; // 表示終了

    // アニメーションタイマー
    private AnimationTimer openBox, closeBox;

    // パーツの設計図
    private Blueprint background;
    private Blueprint boxFrame, boxInner;

    // 使用フォント
    private final Font titleFont           = font.Meiryo(24, font.BOLD);
    private final Font descriptionFont     = font.Meiryo(12, font.BOLD);
    private final Font description2Font    = font.Meiryo(10, font.BOLD);
    private final Font bracketedTrophyFont = font.Meiryo(18, font.BOLD);
    private final Font otherTrophyFont     = font.Meiryo(12);

    // 使用色
    private final Color boxFrameColor              = new Color(215, 215, 215);
    private final Color boxInnerColor              = new Color( 55,  55,  55);
    private final Color bracketedTrophyColor       = new Color(255, 160,  80);
    private final Color bracketedTrophyShadowColor = new Color(  0,  20,  40);
    private final Color otherTrophyColor           = new Color(255, 210, 210);

    public int getState() {
        return state;
    }
    public void proceedState() {
        state++;
    }
    public void endState() {
        state = CLOSED;
    }

    public boolean isOpenBoxZero() {
        return openBox.isZero();
    }
    public boolean isCloseBoxZero() {
        return closeBox.isZero();
    }

    // 背景の描画
    public void drawBackground(Graphics2D g2d) {
        background.fillPolygon(g2d, drawRect, Color.BLACK);
    }

    // 箱の描画
    public void drawBox(Graphics2D g2d) {
        float progress;
        if(state == OPENING) {
            progress = openBox.getProgress();
        }
        else if(state == VIEWING) {
            progress = 1.0F;
        }
        else if(state == CLOSING) {
            progress = 1 - closeBox.getProgress();
        }
        else {
            progress = 0;
        }
        boxFrame.setWidth((int) (240 * progress));
        boxFrame.setHeight((int) (300 * progress));
        boxFrame.fillPolygon(g2d, drawRect, boxFrameColor);
        boxInner.setWidth((int) ((240 - 6) * progress));
        boxInner.setHeight((int) ((300 - 6) * progress));
        boxInner.fillPolygon(g2d, drawRect, boxInnerColor);

        if(state == VIEWING) {
            font.setStr(g2d, titleFont, Color.WHITE);
            font.drawStr(g2d, "新規実績を解除！", 110, 140);
            font.setStr(g2d, description2Font, Color.GRAY);
            font.drawStr(g2d, "楽曲選択画面からTキーで一覧が見られます", 105, 158);

            font.setStr(g2d, descriptionFont, Color.WHITE);
            font.drawStr(g2d, "Enterキーで閉じて楽曲選択に戻る", 106, 380);
        }
    }

    // 解禁した実績の一覧
    public void drawTrophy(Graphics2D g2d, List<String> generalTrophy) {
        if(state == 1) {
            int y = 280;
            int count = 1;
            for(String trophy : generalTrophy) {
                String bracketedTrophy = "「" + trophy + "」";
                int bracketedTrophyWidth = font.strWidth(g2d, bracketedTrophyFont, bracketedTrophy);
                font.setStr(g2d, bracketedTrophyFont, bracketedTrophyShadowColor);
                font.drawStr(g2d, bracketedTrophy, displayWidth / 2 - bracketedTrophyWidth / 2 + 2, y + 2);
                font.setStr(g2d, bracketedTrophyFont, bracketedTrophyColor);
                font.drawStr(g2d, bracketedTrophy, displayWidth / 2 - bracketedTrophyWidth / 2, y);

                y -= 40;
                if(count >= 3) {
                    break;
                } else {
                    count++;
                }
            }

            int otherTrophyCount = generalTrophy.size() - 3;
            if(otherTrophyCount > 0) {
                font.setStr(g2d, otherTrophyFont, otherTrophyColor);
                font.drawStr(g2d, "他、" + otherTrophyCount + "件の実績を解禁", 180, 320);
            }
        }
    }

    @Override
    protected void setBlueprint() {
        background = new Blueprint(0, 0, displayWidth, displayHeight);

        boxFrame = new Blueprint(displayWidth / 2, displayHeight / 2, 240, 300);
        boxFrame.setSide(Blueprint.CENTER, Blueprint.CENTER);
        boxInner = new Blueprint(displayWidth / 2, displayHeight / 2, 240 - 6, 300 - 6);
        boxInner.setSide(Blueprint.CENTER, Blueprint.CENTER);
    }

    @Override
    protected void setAnimationTimer(int frameRate) {
        openBox  = new AnimationTimer(frameRate, 15);
        closeBox = new AnimationTimer(frameRate, 15);
    }

    @Override
    protected void pastAnimationTimer() {
        if(state == 0) {
            openBox.pass();
        }
        else if(state == 2) {
            closeBox.pass();
        }
    }
}
