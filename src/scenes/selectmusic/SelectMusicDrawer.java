package scenes.selectmusic;

import calc.CalcUtil;
import scenes.draw.blueprint.Blueprint;
import scenes.drawer.SceneDrawer;
import scenes.animtimer.AnimationTimer;
import scenes.header.HeaderGetter;

import java.awt.*;
import java.util.*;

public class SelectMusicDrawer extends SceneDrawer {
    // インスタンス諸々
    private final CalcUtil calc = new CalcUtil();

    // カーソル移動もろもろ
    private static final int TITLE_BAR_COUNT = 11;
    private final int stripePattern;

    // 演奏パート
    private static final int AUTO_PLAY = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int BOTH_PART = 3;
    private final String[] partStrings = new String[]{"自動再生", "メロディ", "伴奏", "メロディ＆伴奏"};
    private final int[] difficultyMaxes = new int[]{0, 5, 5, 10};

    // アニメーションタイマー
    private AnimationTimer titleBarAnimTimer;     // 12
    private AnimationTimer directorAnimTimer;     // 50 (LOOP)
    private AnimationTimer fadeInAnimTimer;       // 30
    private AnimationTimer fadeOutAnimTimer;      // 64
    private AnimationTimer sceneTransitionAnimTimer; // 160

    // 描画の設計図
    private Blueprint background;
    private Blueprint cursorBarInner, cursorBarFrame;
    private Blueprint pointerBack, pointerBoard, pointerBar, pointerRotator1, pointerRotator2;
    private Blueprint titleBar, titleBarPlayState, titleBarPlayStateTip1, titleBarPlayStateTip2;
    private Blueprint explainBoxFrame, explainBoxInner;
    private Blueprint musicDescBoxFrame, musicDescBoxInner;
    private Blueprint directorTop, directorBottom;
    private Blueprint fadeIn, fadeOutFill, fadeOutBar;
    private Blueprint titleBack;

    // 移動量や間隔など設計図で書けない要素
    private static final int TITLE_BAR_STANDARD_Y = 250;
    private static final int TITLE_BAR_MOVE_Y = 40;
    private static final int DIRECTOR_MOVE_Y = 20;
    private static final int DIRECTOR_PADDING_Y = 70;
    private static final int STRIPE = 8;

    // 固定文字
    private final String [] playStateStrings = {"", "Played", "Full Combo", "All Perfect"};

    // 文字フォント
    private final Font musicbarTitleFont   = font.Meiryo(26, font.BOLD);
    private final Font acvFont             = font.Arial(14, font.BOLD);
    private final Font playStateFont       = font.Arial(12, font.BOLD);
    private final Font explainBoldFont     = font.Meiryo(16, font.BOLD);
    private final Font explainFont         = font.Meiryo(14);
    private final Font sceneTransTitleFont = font.Meiryo(36, font.BOLD);
    private final Font playPartFont        = font.Meiryo(16, font.BOLD);

    private final Font FPSFont = font.Arial(10);

    // 図形色、文字色
    private final Color backColor        = new Color(0, 0, 0, 215);
    private final Color cursorInnerColor = Color.WHITE;
    private final Color cursorFrameColor = new Color(255, 255, 255, 55);
    private final Color boxFrameColor    = new Color(215, 215, 215, 255);
    private final Color boxInnerColor    = new Color(55, 55, 55, 255);
    private final Color[] partColor      = new Color[] {
            new Color(215,225,215, 155),
            new Color(255,225,205, 155),
            new Color(215,245,255, 155),
            new Color(245,225,255, 155)
    };
    private final Color[] playStateColor = new Color[] {
            new Color(   0,  40,   0,   0),
            new Color( 200, 130,  80, 255),
            new Color(  80, 130, 200, 255),
            new Color( 200, 110, 200, 255),
    };

    // 曲データ
    private Map<String, Map<String, Object>> musicHeaders = new HashMap<>();
    private String[] hashes;
    private int musicCount;

    // 曲別記録
    private int[][] playStates;
    private float[][] achievements;

    // ---------------------------------------------------------------------- //

    // コンストラクタ
    public SelectMusicDrawer() {
        stripePattern = new Random().nextInt(2); // シーン転換時のアニメーション(ランダムで2種)
    }

    // ---------------------------------------------------------------------- //

    // 背景を描く
    public void drawBack(Graphics2D g2d) {
        background.fillPolygon(g2d, drawRect, backColor);
    }
    // ポインタを描く
    public void drawPointer(Graphics2D g2d, int titleBarMovDir) {
        pointerBar.fillPolygon(g2d, drawRect, cursorFrameColor); // なんか長い棒
        pointerBack.fillPolygon(g2d, drawCircle, Color.BLACK);
        pointerBoard.fillPolygon(g2d, drawCircle, Color.WHITE);

        // ポインタが回転するやつ
        float progress = -titleBarAnimTimer.getProgress();
        int turn = (int) (titleBarMovDir * 360 * progress);

        pointerRotator1.setAngle(150 - turn);
        pointerRotator1.setAngle2(210 - turn);
        pointerRotator1.fillArc(g2d, Color.BLACK);

        pointerRotator2.setAngle(-30 - turn);
        pointerRotator2.setAngle2(30 - turn);
        pointerRotator2.fillArc(g2d, Color.BLACK);
    }
    // カーソルを描く
    public void drawCursor(Graphics2D g2d) {
        cursorBarInner.fillPolygon(g2d, drawRect, cursorInnerColor); // 曲名バーと重なってる部分
        cursorBarFrame.fillPolygon(g2d, drawRect, cursorFrameColor); // 外枠部分
    }
    // 曲名バーを描く
    public void drawTitleBar(Graphics2D g2d, int playPart, int cursor, int titleBarMovDir) {
        int barCenterPoint = TITLE_BAR_COUNT / 2;
        float progress = 1 - titleBarAnimTimer.getProgress();

        Color barColor = partColor[playPart];
        for(int i = 0; i < TITLE_BAR_COUNT; i++) {
            // バーの描画
            int y = TITLE_BAR_STANDARD_Y
                    + (i-barCenterPoint) * TITLE_BAR_MOVE_Y
                    + (int) (titleBarMovDir * TITLE_BAR_MOVE_Y * progress);
            titleBar.setAnchorY(y);
            titleBar.fillPolygon(g2d, drawRect, barColor);

            // 曲名の描画
            int pointer = getPointer(cursor, i);
            Map<String, Object> header = getMusicHeader(pointer);
            String musicTitle = HeaderGetter.getTitle(header);
            font.setStr(g2d, musicbarTitleFont, Color.BLACK);
            font.drawStr(g2d, musicTitle, titleBar.X() + 7, y + 10);

            // 記録の描画
            if(playPart != 0) {
                // プレーした記録があるならアイコンと達成率の表示
                float acv = achievements[pointer][playPart - 1];
                if(acv > 0) {
                    // 達成率表示
                    String acvStr = calc.getStrFloatDotUnder(acv, 2) + "%";
                    int acvW = font.strWidth(g2d, acvFont, acvStr);
                    font.setStr(g2d, acvFont, Color.BLACK);
                    font.drawStr(g2d, acvStr, 395 - acvW, y + 13);

                    // プレー状態（アイコン）
                    int playState = playStates[pointer][playPart - 1];
                    Color lampColor = playStateColor[playState];
                    String playStateStr = playStateStrings[playState];
                    int playStateStrW = font.strWidth(g2d, playStateFont, playStateStr);
                    int playStateTipX = 392 - playStateStrW - 2;
                    titleBarPlayState.setAnchorX(playStateTipX);
                    titleBarPlayState.setAnchorY(y - 7);
                    titleBarPlayState.setWidth(playStateStrW);
                    titleBarPlayState.fillPolygon(g2d, drawRect, lampColor);
                    titleBarPlayStateTip1.setAnchorY(y - 7);
                    titleBarPlayStateTip1.fillPolygon(g2d, drawCircle, lampColor);
                    titleBarPlayStateTip2.setAnchorX(playStateTipX);
                    titleBarPlayStateTip2.setAnchorY(y - 7);
                    titleBarPlayStateTip2.fillPolygon(g2d, drawCircle, lampColor);
                    // プレー状態（文字部分）
                    font.setStr(g2d, playStateFont, Color.BLACK);
                    font.drawStr(g2d, playStateStr, 392 - playStateStrW, y - 2);
                }
            }
        }
    }
    // 説明パーツを描く
    public void drawExplain(Graphics2D g2d) {
        explainBoxFrame.fillPolygon(g2d, drawRect, boxFrameColor);
        explainBoxInner.fillPolygon(g2d, drawRect, boxInnerColor);

        font.setStr(g2d, explainBoldFont, Color.WHITE);
        font.drawStr(g2d, "[あそびかた]", explainBoxFrame.X() + 10, explainBoxFrame.Y() + 24);

        font.setStr(g2d, explainFont, Color.WHITE);
        font.drawStr(g2d, "↑キー、↓キー で 曲を 探します。", explainBoxFrame.X() + 20, explainBoxFrame.Y() + 46);
        font.drawStr(g2d, "スペースキー で 演奏パートを 変更します。", explainBoxFrame.X() + 20, explainBoxFrame.Y() + 66);
        font.drawStr(g2d, "Enterキー で 演奏したい曲を 選びます。", explainBoxFrame.X() + 20, explainBoxFrame.Y() + 86);
    }
    // フレームレート表示
    public void drawFrameRate(Graphics2D g2d, String msgFPS, String msgLatency) {
        font.setStr(g2d, FPSFont, Color.WHITE);
        font.drawStr(g2d, msgFPS + ", " + msgLatency, 270, 14);
    }
    // 曲情報パーツを描く
    public void drawMusicDescBack(Graphics2D g2d) {
        musicDescBoxFrame.fillPolygon(g2d, drawRect, boxFrameColor);
        musicDescBoxInner.fillPolygon(g2d, drawRect, boxInnerColor);

        font.setStr(g2d, explainBoldFont, Color.WHITE);
        font.drawStr(g2d, "[この楽曲について]", musicDescBoxFrame.X() + 10, musicDescBoxFrame.Y() + 23);
    }
    public void drawMusicDesc(Graphics2D g2d, int playPart, int cursor) {
        int pointer = getPointer(cursor);
        Map<String, Object> header = getMusicHeader(pointer);

        String musicTitle = HeaderGetter.getTitle(header);
        int musicTempo = HeaderGetter.getTempo(header);
        int difLevel = getDifLevel(playPart, pointer);
        String musicDifStarAndBlank = getDifStarAndBlank(difLevel, playPart, 23);
        String partStr = partStrings[playPart];
        int descY = musicDescBoxFrame.Y();

        font.setStr(g2d, explainFont, Color.WHITE);
        font.drawStr(g2d, "タイトル: " + musicTitle + "  (♩= " + musicTempo + ")", musicDescBoxFrame.X() + 10, descY + 45);
        font.drawStr(g2d, "演奏パート: " + partStr, musicDescBoxFrame.X() + 10, descY + 65);
        if (playPart != AUTO_PLAY) {
            font.drawStr(g2d, "難しさ: " + musicDifStarAndBlank, musicDescBoxFrame.X() + 10, descY + 85);
        }
    }
    // ディレクタを描く
    public void drawDirector(Graphics2D g2d) {
        float progress = 1.0F - directorAnimTimer.getProgress(); // 1.0F → 0.0F (LOOP)
        int yt = TITLE_BAR_STANDARD_Y - DIRECTOR_PADDING_Y + (int) (DIRECTOR_MOVE_Y * progress);
        int yb = TITLE_BAR_STANDARD_Y + DIRECTOR_PADDING_Y - (int) (DIRECTOR_MOVE_Y * progress);
        int alpha = progress == 1.0F ? 0 : (int) (200 * progress);
        Color color = new Color(255, 255, 255, alpha); // 色情報だけど固定値ではないのでここに記述

        directorTop.setAnchorY(yt);
        directorTop.fillTrapezoid(g2d, color);
        directorBottom.setAnchorY(yb);
        directorBottom.fillTrapezoid(g2d, color);
    }
    // フェードインの描画
    public void drawFadeIn(Graphics2D g2d) {
        float progress = 1.0F - fadeInAnimTimer.getProgress();
        int w = (int) (displayWidth * progress);
        fadeIn.setWidth(w);
        fadeIn.fillPolygon(g2d, drawRect, Color.BLACK);
    }
    // フェードアウトの描画
    public void drawFadeOut(Graphics2D g2d) {
        float progress = STRIPE * fadeOutAnimTimer.getProgress();
        int fillWidth, fillHeight;
        int barX, barY, barWidth, barHeight;
        if(progress < STRIPE) {
            int stripeCount = (int) progress;           // ストライプの本数
            float stripeMod = progress - stripeCount;   // ストライプの端切れ

            float[] w1Mul = {(float) stripeCount / STRIPE, 1.0F};
            float[] h1Mul = {1.0F, (float) stripeCount / STRIPE};

            float[] xMul  = {w1Mul[0], 0.0F};
            float[] yMul  = {0.0F, h1Mul[1]};
            float[] w2Mul = {1.0F / STRIPE, stripeMod};
            float[] h2Mul = {stripeMod, 1.0F / STRIPE};

            fillWidth  = (int) (displayWidth  * w1Mul[stripePattern]);
            fillHeight = (int) (displayHeight * h1Mul[stripePattern]);

            barX      = (int) (displayWidth  * xMul[stripePattern]);
            barY      = (int) (displayHeight * yMul[stripePattern]);
            barWidth  = (int) (displayWidth  * w2Mul[stripePattern]);
            barHeight = (int) (displayHeight * h2Mul[stripePattern]);
        }
        else {
            fillWidth  = displayWidth;
            fillHeight = displayHeight;

            barX       = 0;
            barY       = 0;
            barWidth   = 0;
            barHeight  = 0;
        }

        fadeOutFill = new Blueprint(0, 0, fillWidth, fillHeight);
        fadeOutFill.fillPolygon(g2d, drawRect, Color.BLACK); // 端切れじゃない分を埋める

        fadeOutBar = new Blueprint(barX, barY, barWidth, barHeight);
        fadeOutBar.fillPolygon(g2d, drawRect, Color.BLACK); // 端切れを描く
    }
    // フェードアウト後のタイトル表示
    public void drawTitleAfterFadeOut(Graphics2D g2d, int playPart, int cursor) {
        titleBack.fillPolygon(g2d, drawRect, Color.BLACK);

        int pointer = getPointer(cursor);
        Map<String, Object> header = getMusicHeader(pointer);

        String musicTitleStr = HeaderGetter.getTitle(header);
        int musicTitleStrWidth = font.strWidth(g2d, sceneTransTitleFont, musicTitleStr);
        font.setStr(g2d, sceneTransTitleFont, Color.WHITE);
        font.drawStr(g2d, musicTitleStr, displayWidth / 2 - musicTitleStrWidth / 2, 200);

        String playPartStr = "演奏パート：" + partStrings[playPart];
        int playPartStrWidth   = font.strWidth(g2d, playPartFont, playPartStr);
        font.setStr(g2d, playPartFont, partColor[playPart]);
        font.drawStr(g2d, playPartStr, displayWidth / 2 - playPartStrWidth / 2, 230);

        if(playPart != AUTO_PLAY) {
            int level = getDifLevel(playPart, pointer);
            String difLevelStr = "難しさ：" + getDifStarAndBlank(level, playPart, 20);
            int difLevelStrWidth = font.strWidth(g2d, playPartFont, difLevelStr);
            g2d.drawString(difLevelStr, displayWidth / 2 - difLevelStrWidth / 2, 250);
        }
    }

    // ---------------------------------------------------------------------- //

    // ポインタを取得
    private int getPointer(int cursor, int i) {
        int titleBarCenterPoint = TITLE_BAR_COUNT / 2;
        return calc.mod(cursor + (i-titleBarCenterPoint), musicCount);
    }
    private int getPointer(int cursor) {
        return calc.mod(cursor, musicCount);
    }

    // ヘッダ（楽曲データ）の取得
    private Map<String, Object> getMusicHeader(int pointer) {
        String hash = hashes[pointer];
        return musicHeaders.get(hash);
    }

    // 演奏レベルの取得
    private int getDifLevel(int playPart, int pointer) {
        Map<String, Object> header = getMusicHeader(pointer);
        int[] difLevel = HeaderGetter.getLevel(header);
        return switch (playPart) {
            case MAIN_PART -> difLevel[0];
            case SUB_PART  -> difLevel[1];
            case BOTH_PART -> difLevel[0] + difLevel[1];
            default -> 0;
        };
    }
    // 難しさの表記
    private String getDifStarAndBlank(int difLevel, int playPart, int limit) {
        if(difLevel <= limit) {
            String musicDifStar = "★".repeat(difLevel);
            int starBlankCount = Math.max(difficultyMaxes[playPart] - difLevel, 0);
            String musicDifStarBlank = "・".repeat(starBlankCount);
            return musicDifStar + musicDifStarBlank;
        } else {
            return "(★×" + difLevel + ")";
        }
    }

    // 曲データの設定
    public void setMusicHeaders(Map<String, Map<String, Object>> musicHeaders, String[] hashes) {
        musicCount = musicHeaders.size();
        this.musicHeaders = new HashMap<>(musicHeaders);
        this.hashes = hashes;
    }

    // プレー記録の設定
    public void setPlayRecord(int[][] playStates, float[][] achievements) {
        this.playStates = playStates;
        this.achievements = achievements;
    }

    // ---------------------------------------------------------------------- //

    // パーツの設計図の設定
    @Override
    public void setBlueprint() {
        background = new Blueprint(0, 0, displayWidth, displayHeight);

        int cursorBarX = 30;
        int titleBarCenterY = 250;
        int titleBarHeight = 30;
        int playStateX = 392;
        int playStateY = titleBarCenterY - 15;
        int playStateW = 200;
        int playStateH = 13;
        cursorBarInner = new Blueprint(cursorBarX, titleBarCenterY, displayWidth, titleBarHeight);
        cursorBarInner.setSide(Blueprint.LEFT, Blueprint.CENTER);
        cursorBarFrame = new Blueprint(cursorBarX - 3, titleBarCenterY, displayWidth, titleBarHeight + 6);
        cursorBarFrame.setSide(Blueprint.LEFT, Blueprint.CENTER);

        titleBar = new Blueprint(cursorBarX, titleBarCenterY, displayWidth, titleBarHeight);
        titleBar.setSide(Blueprint.LEFT, Blueprint.CENTER);

        titleBarPlayState = new Blueprint(playStateX - playStateW, playStateY, playStateW, playStateH);
        titleBarPlayState.setSide(Blueprint.LEFT, Blueprint.CENTER);
        titleBarPlayStateTip1 = new Blueprint(playStateX - 2, playStateY, playStateH, playStateH);
        titleBarPlayStateTip1.setSide(Blueprint.CENTER, Blueprint.CENTER);
        titleBarPlayStateTip2 = new Blueprint(playStateX - playStateW + 2, playStateY, playStateH, playStateH);
        titleBarPlayStateTip2.setSide(Blueprint.CENTER, Blueprint.CENTER);

        int pointerRadius = 20;
        int pointerCenterX = 13;
        int pointerCenterY2 = 250;
        int pointerBarWidth = 4;
        int pointerBarHeight = 280;
        pointerBar = new Blueprint(pointerCenterX + 1, pointerCenterY2, pointerBarWidth, pointerBarHeight);
        pointerBar.setSide(Blueprint.CENTER, Blueprint.CENTER);
        pointerBack = new Blueprint(pointerCenterX, titleBarCenterY, pointerRadius + 8, pointerRadius + 8);
        pointerBack.setSide(Blueprint.CENTER, Blueprint.CENTER);
        pointerBoard = new Blueprint(pointerCenterX, titleBarCenterY, pointerRadius, pointerRadius);
        pointerBoard.setSide(Blueprint.CENTER, Blueprint.CENTER);
        int turn = 0;
        pointerRotator1 = new Blueprint(
                pointerCenterX, titleBarCenterY, pointerRadius - 2, pointerRadius - 2,
                150 - turn, 210 - turn
        );
        pointerRotator1.setSide(Blueprint.CENTER, Blueprint.CENTER);
        pointerRotator2 = new Blueprint(
                pointerCenterX, titleBarCenterY, pointerRadius - 2, pointerRadius - 2,
                -30 - turn, 30 - turn
        );
        pointerRotator2.setSide(Blueprint.CENTER, Blueprint.CENTER);

        int explainHeight = 100;
        explainBoxFrame = new Blueprint(0, 0, displayWidth, explainHeight);
        explainBoxInner = new Blueprint(3, 3, displayWidth - 6, explainHeight - 6);

        int musicDescHeight = 100;
        int musicDescY = displayHeight - musicDescHeight;
        musicDescBoxFrame = new Blueprint(0, musicDescY, displayWidth, musicDescHeight);
        musicDescBoxInner = new Blueprint(3, musicDescY + 3, displayWidth - 6, musicDescHeight - 6);

        int directorX = titleBarHeight + (displayWidth - titleBarHeight) / 2;
        int directorPaddingHeight = 70;
        int directorTopY = titleBarCenterY - directorPaddingHeight;
        int directorBottomY = titleBarCenterY + directorPaddingHeight;
        int directorWidth = 30;
        int directorHeight = 20;
        directorTop = new Blueprint(directorX, directorTopY, 0, directorWidth, directorHeight);
        directorTop.setSide(Blueprint.CENTER, Blueprint.BOTTOM, Blueprint.HORIZONTAL);
        directorBottom = new Blueprint(directorX, directorBottomY, 0, directorWidth, -directorHeight);
        directorBottom.setSide(Blueprint.CENTER, Blueprint.BOTTOM, Blueprint.HORIZONTAL);

        int fadeInWidth = 0;
        fadeIn = new Blueprint(displayWidth, 0, fadeInWidth, displayHeight);
        fadeIn.setSide(Blueprint.RIGHT, Blueprint.TOP);

        titleBack = new Blueprint(0, 0, displayWidth, displayHeight);
    }

    // アニメーションタイマーの設定
    @Override
    public void setAnimationTimer(int frameRate) {
        titleBarAnimTimer = new AnimationTimer(frameRate, 12, false);
        titleBarAnimTimer.setZero();
        directorAnimTimer  = new AnimationTimer(frameRate, 50, true);
        fadeInAnimTimer    = new AnimationTimer(frameRate, 30, false);
        fadeOutAnimTimer   = new AnimationTimer(frameRate, 64, false);
        sceneTransitionAnimTimer = new AnimationTimer(frameRate, 160, false);
    }

    @Override
    protected void pastAnimationTimer() { } // 引数がある方を使う

    // アニメーションタイマーの経過
    public void pastAnimationTimer(boolean sceneTransition, boolean keyRelease) {
        fadeInAnimTimer.pass(); // フェードイン
        titleBarAnimTimer.pass(); // 曲名バー

        // ディレクタの動作
        if ( keyRelease ) {
            directorAnimTimer.pass();
        } else {
            directorAnimTimer.reset();
        }

        // シーン転換
        if( sceneTransition ) {
            fadeOutAnimTimer.pass();
            sceneTransitionAnimTimer.pass();
        }
    }

    // アニメーションタイマーの設定と取得
    public void startTitleBarAnimTimer() {
        titleBarAnimTimer.reset();
    }
    public int getTitleBarAnimTimer() {
        return titleBarAnimTimer.getDecTimer();
    }
    public boolean isEndFadeOut() {
        return fadeOutAnimTimer.isZero();
    }
    public boolean isEndSceneTransition() {
        return sceneTransitionAnimTimer.isZero();
    }

}
