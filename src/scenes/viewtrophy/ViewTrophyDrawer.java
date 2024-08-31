package scenes.viewtrophy;

import scenes.draw.blueprint.Blueprint;
import scenes.drawer.SceneDrawer;
import trophy.TrophyList;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ViewTrophyDrawer extends SceneDrawer {
    // トロフィー項目ごとの間隔
    private final int distance = 65;

    // 設計図
    private Blueprint background;
    private Blueprint titleFrame, titleInner;
    private Blueprint menuFrame, menuInner;
    private Blueprint trophyHide, menuFrameTop, menuFrameBottom;
    private Blueprint[] trophyBack, trophyBackTermsBox;
    private Blueprint scrollBar, scrollIcon;
    private Blueprint musicTrophyTerms;

    // フォント
    private final Font trophyNameFont = font.MSGothic(20, font.BOLD);
    private final Font termsFont = font.MSGothic(10);
    private final Font musicTermsFont = font.MSGothic(10);

    // 色
    private final Color backGray = new Color(55, 55, 55);
    private final Color backFrameColor = new Color(215, 215, 215);

    private final Color trophyNameColor = new Color(255,160,80);
    private final Color trophyNotGetColor = new Color(105, 105, 105);
    private final Color termsColor = new Color(255,210,210);
    private final Color generalTrophyBackColor = new Color(40, 30, 20, 255);
    private final Color generalTrophyBackShadowColor = new Color(0, 10, 20, 255);
    private final Color musicTrophyBackColor = new Color(30, 20, 40, 255);
    private final Color musicTrophyBackShadowColor = new Color(10, 20, 0, 255);
    private final Color scrollIconColor = new Color(155, 205, 155);

    // 背景
    public void drawBackground(Graphics2D g2d) {
        background.fillPolygon(g2d, drawRect, Color.BLACK);

        menuFrame.fillPolygon(g2d, drawRect, backFrameColor);
        menuInner.fillPolygon(g2d, drawRect, backGray);
    }
    // タイトル名
    public void drawTitle(Graphics2D g2d) {
        trophyHide.fillPolygon(g2d, drawRect, Color.BLACK);
        menuFrameTop.fillPolygon(g2d, drawRect, backFrameColor);
        menuFrameBottom.fillPolygon(g2d, drawRect, backFrameColor);

        titleFrame.fillPolygon(g2d, drawRect, backFrameColor);
        titleInner.fillPolygon(g2d, drawRect, backGray);

        font.setStr(g2d, font.MSGothic(28), Color.WHITE);
        font.drawStr(g2d, "解禁済み実績の一覧", 75, 33);
        font.setStr(g2d, font.MSGothic(12), Color.WHITE);
        font.drawStr(g2d, "↑↓キーで移動、Spaceキーで切り替え、Enterキーで楽曲選択に戻る", 15, 53);
    }
    // トロフィーの一覧
    public void drawGeneralTrophyView(Graphics2D g2d, List<Integer> trophies, int stdY) {
        int y = 115 - stdY;
        int tpb = 0;
        for(int trophyNum : TrophyList.member) {
            trophyBack[tpb] = new Blueprint(12, y - 21, 360, 46);
            trophyBack[tpb].fillPolygon(g2d, drawRect, generalTrophyBackShadowColor);
            trophyBack[tpb] = new Blueprint(10, y - 23, 360, 46);
            trophyBack[tpb].fillPolygon(g2d, drawRect, generalTrophyBackColor);
            trophyBackTermsBox[tpb] = new Blueprint(16, y + 5, 340, 16);
            trophyBackTermsBox[tpb].fillPolygon(g2d, drawRect, Color.BLACK);

            String trophyName;
            String termsStr;
            Color trophyStrColor;
            if(trophies.contains(trophyNum) ) {
                trophyName = "「" + TrophyList.getGenTrophy(trophyNum) + "」";
                termsStr = TrophyList.getGenTrophyTerms(trophyNum);
                trophyStrColor = trophyNameColor;
            } else {
                trophyName = "「？？？」";
                if(TrophyList.getGenTrophyMaskedTerms(trophyNum) == null) { // マスク表記されてない条件はこっち
                    termsStr = TrophyList.getGenTrophyTerms(trophyNum);
                } else {
                    termsStr = TrophyList.getGenTrophyMaskedTerms(trophyNum);
                }
                trophyStrColor = trophyNotGetColor;
            }
            font.setStr(g2d, trophyNameFont, generalTrophyBackShadowColor);
            font.drawStr(g2d, trophyName, 12, y+2);
            font.setStr(g2d, trophyNameFont, trophyStrColor);
            font.drawStr(g2d, trophyName, 10, y);

            font.setStr(g2d, termsFont, termsColor);
            font.drawStr(g2d, termsStr, 20, y + 17);

            y += distance;
            tpb++;
        }
    }
    public void drawMusicTrophyView(
            Graphics2D g2d,
            List<String> trophies,
            int stdY,
            List<String> hashes,
            Map<String, String> musicTitles
    ) {
        int y = 145 - stdY;

        musicTrophyTerms.setAnchorY(y - 60);
        musicTrophyTerms.fillPolygon(g2d, drawRect, Color.BLACK);
        font.setStr(g2d, musicTermsFont, termsColor);
        font.drawStr(g2d, TrophyList.getMusicTrophyTermsStr(), 10, y - 48);

        int tpb = 0;
        for(String hash : hashes) {

            if(TrophyList.getMusicTrophy(hash) != null) {
                String trophyName;
                String termsStr;
                Color trophyStrColor;
                if(trophies.contains(hash) ) {
                    trophyName = "「" + TrophyList.getMusicTrophy(hash) + "」";
                    trophyStrColor = trophyNameColor;
                } else {
                    trophyName = "「？？？」";
                    trophyStrColor = trophyNotGetColor;
                }
                termsStr = "対象楽曲：" + musicTitles.get(hash);

                trophyBack[tpb] = new Blueprint(12, y - 21, 360, 46);
                trophyBack[tpb].fillPolygon(g2d, drawRect, musicTrophyBackShadowColor);
                trophyBack[tpb] = new Blueprint(10, y - 23, 360, 46);
                trophyBack[tpb].fillPolygon(g2d, drawRect, musicTrophyBackColor);
                trophyBackTermsBox[tpb] = new Blueprint(16, y + 5, 340, 16);
                trophyBackTermsBox[tpb].fillPolygon(g2d, drawRect, Color.BLACK);

                font.setStr(g2d, trophyNameFont, musicTrophyBackShadowColor);
                font.drawStr(g2d, trophyName, 12, y + 2);
                font.setStr(g2d, trophyNameFont, trophyStrColor);
                font.drawStr(g2d, trophyName, 10, y);

                font.setStr(g2d, termsFont, termsColor);
                font.drawStr(g2d, termsStr, 20, y + 17);
                y += distance;
            }
                tpb++;
        }
    }
    // スクロールバー
    public void drawScrollBar(Graphics2D g2d, int stdY, int viewMode) {
        float progress = (float) stdY / getTrophyListHeight(viewMode);
        int iconY = (int) (102 + (displayHeight - 136) * progress);
        scrollBar.fillPolygon(g2d, drawRect, Color.BLACK);
        scrollIcon.setAnchorY(iconY);
        scrollIcon.fillPolygon(g2d, drawRect, scrollIconColor);
    }

    // トロフィーの一覧表示の長さ
    public int getTrophyListHeight(int viewMode) {
        return viewMode == 0
                ? distance * (TrophyList.member.size() - 6)
                : distance * (TrophyList.getMusicTrophyCount() - 6);
    }

    @Override
    protected void setBlueprint() {
        background = new Blueprint(0, 0, displayWidth, displayHeight);

        titleFrame = new Blueprint(0, 0, displayWidth, 60);
        titleInner = new Blueprint(3, 3, displayWidth - 6, 60 - 6);

        menuFrame = new Blueprint(0, 70, displayWidth, displayHeight - 70);
        menuInner = new Blueprint(3, 70 + 3, displayWidth - 6, displayHeight - 70 - 6);

        trophyHide = new Blueprint(0, 0, displayWidth, 70);
        menuFrameTop = new Blueprint(0, 70, displayWidth, 3);
        menuFrameBottom = new Blueprint(0, displayHeight - 3, displayWidth, 3);

        trophyBack = new Blueprint[TrophyList.member.size()];
        trophyBackTermsBox = new Blueprint[TrophyList.member.size()];

        scrollBar = new Blueprint(380, 90, 10, displayHeight - 110);
        scrollIcon = new Blueprint(382, 100, 6, 20);
        scrollIcon.setSide(Blueprint.LEFT, Blueprint.CENTER);

        musicTrophyTerms = new Blueprint(10, 85, 340, 16);
    }

    @Override
    protected void setAnimationTimer(int frameRate) { }

    @Override
    protected void pastAnimationTimer() { }
}
