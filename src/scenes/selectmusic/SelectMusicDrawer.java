package scenes.selectmusic;

import calc.CalcUtil;
import scenes.draw.*;
import scenes.font.FontUtil;
import scenes.animtimer.AnimationTimer;

import java.awt.*;
import java.util.Map;
import java.util.Random;

public class SelectMusicDrawer {
    // インスタンス諸々
    private final DrawPolygon rect = new DrawRect();
    private final DrawPolygon circle = new DrawOval();
    private final DrawArc arc = new DrawArc();
    private final DrawTrapezium dtz = new DrawTrapezium();
    private final FontUtil font = new FontUtil();

    private final CalcUtil calc = new CalcUtil();

    // 画面サイズ
    private int displayWidth;
    private int displayHeight;

    // カーソル移動もろもろ
    private static final int TITLEBAR_COUNT = 11;
    private final int stripePattern;

    // 演奏パート
    private static final int AUTO_PLAY = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int BOTH_PART = 3;
    private final String[] partStrs = new String[]{"自動再生", "メロディ", "伴奏", "メロディ＆伴奏"};
    private final int[] difficultyMaxes = new int[]{0, 5, 5, 10};

    // アニメーションタイマー
    private AnimationTimer titlebarAnimTimer;     // 12
    private AnimationTimer directorAnimTimer;     // 50 (LOOP)
    private AnimationTimer fadeInAnimTimer;       // 30
    private AnimationTimer fadeOutAnimTimer;      // 64
    private AnimationTimer sceneTransitionAnimTimer; // 160

    // 寸法と移動量いろいろ
    private static final int TITLEBAR_X = 30;
    private static final int TITLEBAR_CENTER_Y = 250;
    private static final int TITLEBAR_HEIGHT = 30;
    private static final int TITLEBAR_MOVE_Y = 40;

    private static final int POINTER_RADIUS = 20;
    private static final int POINTER_CENTER_X = 13;
    private static final int POINTER_CENTER_Y = TITLEBAR_CENTER_Y;
    private static final int POINTER_CENTER_Y2 = 250;
    private static final int POINTER_H = 280;

    private static final int DIRECTOR_CENTER_Y = TITLEBAR_CENTER_Y;
    private static final int DIRECTOR_MOVE_Y = 20;
    private static final int DIRECTOR_PADDING_Y = 70;
    private static final int DIRECTOR_WIDTH = 30;
    private static final int DIRECTOR_HEIGHT = 20;

    private static final int EXPLAIN_HEIGHT = 100;
    private static final int MUSIC_DESC_HEIGHT = 100;

    private static final int STRIPE = 8;

    private static final int STRING_TITLE_X = 10;
    private static final int STRING_DESCRIPTION_X = 20;

    // 文字フォント
    private final Font musicbarTitleFont   = font.Meiryo(26, font.BOLD);
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

    private final Color[] partColor      = new Color[]{
            new Color(215,225,215, 155),
            new Color(255,225,205, 155),
            new Color(215,245,255, 155),
            new Color(245,225,255, 155)
    };

    // 曲データ
    private int musicCount;
    private String[] musicTitles;
    private int[] musicTempos;
    private int[] mainDifficulties;
    private int[] subDifficulties;

    // ---------------------------------------------------------------------- //

    // コンストラクタ
    public SelectMusicDrawer() {
        // シーン転換時のアニメーション(ランダムで2種)
        stripePattern = new Random().nextInt(2);
    }

    // ---------------------------------------------------------------------- //

    // 背景を描く
    public void drawBack(Graphics2D g2d) {
        rect.fill(g2d, backColor,
                rect.makeParam(0, 0, displayWidth, displayHeight)
        ); // 黒背景
    }
    // ポインタを描く
    public void drawPointer(Graphics2D g2d, int titlebarMovDir) {
        rect.fill(g2d, cursorFrameColor,
                rect.makeParam(POINTER_CENTER_X + 1, POINTER_CENTER_Y2, POINTER_RADIUS/2 - 6, POINTER_H),
                rect.makeSide(rect.CENTER, rect.CENTER)
        );
        circle.fill(g2d, Color.BLACK,
                circle.makeParam(POINTER_CENTER_X, POINTER_CENTER_Y, POINTER_RADIUS + 8, POINTER_RADIUS + 8),
                circle.makeSide(circle.CENTER, circle.CENTER)
        );
        circle.fill(g2d, Color.WHITE,
                circle.makeParam(POINTER_CENTER_X, POINTER_CENTER_Y, POINTER_RADIUS, POINTER_RADIUS),
                circle.makeSide(circle.CENTER, circle.CENTER)
        );
        float progress = titlebarAnimTimer.getProgress();
        int turn = (int) (titlebarMovDir * 360 * progress);
        arc.fill(g2d, Color.BLACK,
                arc.makeParamArc(
                        POINTER_CENTER_X, POINTER_CENTER_Y, POINTER_RADIUS-2, POINTER_RADIUS-2,
                        150 - turn, 210 - turn
                ), arc.makeSide(arc.CENTER, arc.CENTER)
        );
        arc.fill(g2d, Color.BLACK,
                arc.makeParamArc(
                        POINTER_CENTER_X, POINTER_CENTER_Y, POINTER_RADIUS-2, POINTER_RADIUS-2,
                        -30 - turn, 30 - turn
                ), arc.makeSide(arc.CENTER, arc.CENTER)
        ); // ポインタが回転するやつ
    }
    // カーソルを描く
    public void drawCursor(Graphics2D g2d) {
        rect.fill(g2d, cursorInnerColor,
                rect.makeParam(TITLEBAR_X, TITLEBAR_CENTER_Y, displayWidth, TITLEBAR_HEIGHT),
                rect.makeSide(rect.LEFT, rect.CENTER)
        );
        rect.fill(g2d, cursorFrameColor,
                rect.makeParam(TITLEBAR_X - 3, TITLEBAR_CENTER_Y, displayWidth, TITLEBAR_HEIGHT + 6),
                rect.makeSide(rect.LEFT, rect.CENTER)
        ); // カーソルが合ってる部分
    }
    // 曲名バーを描く
    public void drawTitleBar(Graphics2D g2d, int playPart, int cursor, int titlebarMovDir) {
        int musicCount = musicTitles.length;
        int barCenterPoint = TITLEBAR_COUNT / 2;
        float progress = 1 - titlebarAnimTimer.getProgress();

        Color barColor = partColor[playPart];
        Map<Draw.Side, Integer> barSide = rect.makeSide(rect.LEFT, rect.CENTER);

        int x = TITLEBAR_X;
        for(int i = 0; i < TITLEBAR_COUNT; i++) {
            // バーの描画
            int y = TITLEBAR_CENTER_Y
                    + (i-barCenterPoint) * TITLEBAR_MOVE_Y
                    + (int) (titlebarMovDir * TITLEBAR_MOVE_Y * progress);
            rect.fill(g2d, barColor,
                    rect.makeParam(x, y, displayWidth, TITLEBAR_HEIGHT),
                    barSide
            );

            // 文字の描画
            int pointer = getPointer(musicCount, cursor, i);
            String musicTitle = musicTitles[pointer];
            g2d.setColor(Color.BLACK);
            g2d.setFont(musicbarTitleFont);
            g2d.drawString(musicTitle, x + 7, y + 10);
        }
    }
    // 説明パーツを描く
    public void drawExplain(Graphics2D g2d) {
        rect.fill(g2d, boxFrameColor,
                rect.makeParam(0, 0, displayWidth, EXPLAIN_HEIGHT)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(3, 3, displayWidth - 6, EXPLAIN_HEIGHT - 6)
        );

        g2d.setColor(Color.WHITE);
        g2d.setFont(explainBoldFont);
        g2d.drawString("[あそびかた]", STRING_TITLE_X, 24);
        g2d.setFont(explainFont);
        g2d.drawString("↑キー、↓キー で 曲を 探します。", STRING_DESCRIPTION_X, 46);
        g2d.drawString("Spaceキー で 演奏パートを 変更します。", STRING_DESCRIPTION_X, 66);
        g2d.drawString("Enterキー で 演奏したい曲を 選びます。", STRING_DESCRIPTION_X, 86);
    }
    // フレームレート表示
    public void drawFrameRate(Graphics2D g2d, String msgFPS, String msgLatency) {
        g2d.setFont(FPSFont);
        g2d.drawString(msgFPS + ", " + msgLatency, 270, 14);
    }
    // 曲情報パーツを描く
    public void drawMusicDescBack(Graphics2D g2d) {
        int y = displayHeight - MUSIC_DESC_HEIGHT;
        rect.fill(g2d, boxFrameColor,
                rect.makeParam(0, y, displayWidth, MUSIC_DESC_HEIGHT)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(3, y + 3, displayWidth - 6, MUSIC_DESC_HEIGHT - 6)
        );
        g2d.setColor(Color.WHITE);
        g2d.setFont(explainBoldFont);
        g2d.drawString("[この楽曲について]", STRING_TITLE_X, y + 23);
    }
    public void drawMusicDesc(Graphics2D g2d, int playPart, int cursor) {
        int pointer = getPointer(musicCount, cursor);
        String musicTitle = musicTitles[pointer];
        int musicTempo = musicTempos[pointer];
        int difLevel = getDifLevel(playPart, pointer);
        String musicDifStarAndBlank = getDifStarAndBlank(difLevel, playPart, 23);
        String partStr = partStrs[playPart];
        int descY = displayHeight - MUSIC_DESC_HEIGHT;

        g2d.setColor(Color.WHITE);
        g2d.setFont(explainFont);
        g2d.drawString("タイトル: " + musicTitle + "  (♩= " + musicTempo + ")", STRING_DESCRIPTION_X, descY + 45);
        g2d.drawString("演奏パート: " + partStr, STRING_DESCRIPTION_X, descY + 65);
        if (playPart != AUTO_PLAY) {
            g2d.drawString("難しさ: " + musicDifStarAndBlank, STRING_DESCRIPTION_X, descY + 85);
        }
    }
    // ディレクタを描く
    public void drawDirector(Graphics2D g2d) {
        float progress = 1.0F - directorAnimTimer.getProgress(); // 1.0F → 0.0F (LOOP)
        int x = TITLEBAR_HEIGHT + (displayWidth - TITLEBAR_HEIGHT) / 2;
        int yt = DIRECTOR_CENTER_Y - DIRECTOR_PADDING_Y + (int) (DIRECTOR_MOVE_Y * progress);
        int yb = DIRECTOR_CENTER_Y + DIRECTOR_PADDING_Y - (int) (DIRECTOR_MOVE_Y * progress);
        int alpha = progress == 1.0F ? 0 : (int) (200 * progress);
        Color color = new Color(255, 255, 255, alpha); // 色情報だけど固定値ではないのでここに記述

        dtz.fill(g2d, color,
                dtz.makeParam(x, yt, 0, DIRECTOR_WIDTH, DIRECTOR_HEIGHT),
                dtz.makeSide(dtz.CENTER, dtz.BOTTOM, dtz.HORIZONTAL)
        );
        dtz.fill(g2d, color,
                dtz.makeParam(x, yb, 0, 30, -20),
                dtz.makeSide(dtz.CENTER, dtz.BOTTOM, dtz.HORIZONTAL)
        );
    }
    // フェードインの描画
    public void drawFadeIn(Graphics2D g2d) {
        int x = displayWidth;
        float progress = 1.0F - fadeInAnimTimer.getProgress();
        int w = (int) (x * progress);
        rect.fill(g2d, Color.BLACK,
                rect.makeParam(x, 0, w, displayHeight),
                rect.makeSide(rect.RIGHT, rect.TOP)
        );
    }
    // フェードアウトの描画
    public void drawFadeOut(Graphics2D g2d) {
        float progress = STRIPE * fadeOutAnimTimer.getProgress();
        if(progress < STRIPE) {
            int stripeCount = (int) progress;           // ストライプの本数
            float stripeMod = progress - stripeCount;   // ストライプの端切れ

            float[] w1Mul = {(float) stripeCount / STRIPE, 1.0F};
            float[] h1Mul = {1.0F, (float) stripeCount / STRIPE};

            float[] xMul  = {w1Mul[0], 0.0F};
            float[] yMul  = {0.0F, h1Mul[1]};
            float[] w2Mul = {1.0F / STRIPE, stripeMod};
            float[] h2Mul = {stripeMod, 1.0F / STRIPE};

            rect.fill(g2d, Color.BLACK,
                    rect.makeParam(0, 0,
                            (int) (displayWidth * w1Mul[stripePattern]),
                            (int) (displayHeight * h1Mul[stripePattern])
                    )
            );// 端切れじゃない分を埋める
            rect.fill(g2d, Color.BLACK,
                    rect.makeParam(
                            (int) (displayWidth * xMul[stripePattern]),
                            (int) (displayHeight * yMul[stripePattern]),
                            (int) (displayWidth * w2Mul[stripePattern]),
                            (int) (displayHeight * h2Mul[stripePattern])
                    )
            ); // 端切れを描く
        } else {
            rect.fill(g2d, Color.BLACK, rect.makeParam(0, 0, displayWidth, displayHeight));
        }
    }
    // フェードアウト後のタイトル表示
    public void drawTitleAfterFadeOut(Graphics2D g2d, int playPart, int cursor) {
        rect.fill(g2d, Color.BLACK,
                rect.makeParam(0, 0, displayWidth, displayHeight)
        );

        String musicTitleStr = musicTitles[getPointer(musicCount, cursor)];
        int musicTitleStrWidth = font.strWidth(g2d, sceneTransTitleFont, musicTitleStr);
        g2d.setColor(Color.WHITE);
        g2d.setFont(sceneTransTitleFont);
        g2d.drawString(musicTitleStr, displayWidth / 2 - musicTitleStrWidth / 2, 200);

        String playPartStr = "演奏パート：" + partStrs[playPart];
        int playPartStrWidth   = font.strWidth(g2d, playPartFont, playPartStr);
        g2d.setColor(partColor[playPart]);
        g2d.setFont(playPartFont);
        g2d.drawString(playPartStr, displayWidth / 2 - playPartStrWidth / 2, 230);

        if(playPart != AUTO_PLAY) {
            int level = getDifLevel(playPart, getPointer(musicCount, cursor));
            String difLevelStr = "難しさ：" + getDifStarAndBlank(level, playPart, 20);
            int difLevelStrWidth = font.strWidth(g2d, playPartFont, difLevelStr);
            g2d.drawString(difLevelStr, displayWidth / 2 - difLevelStrWidth / 2, 250);
        }
    }

    // ---------------------------------------------------------------------- //

    // ポインタを取得
    private int getPointer(int musicCount, int cursor, int i) {
        int titlebarCenterPoint = TITLEBAR_COUNT / 2;
        return calc.mod(cursor + (i-titlebarCenterPoint), musicCount);
    }
    private int getPointer(int musicCount, int cursor) {
        return calc.mod(cursor, musicCount);
    }

    // 演奏レベルの取得
    private int getDifLevel(int playPart, int pointer) {
        int mainDifficulty = mainDifficulties[pointer];
        int subDifficulty  = subDifficulties[pointer];
        return switch (playPart) {
            case MAIN_PART -> mainDifficulty;
            case SUB_PART  -> subDifficulty;
            case BOTH_PART -> mainDifficulty + subDifficulty;
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

    // 描画サイズの設定
    public void setDisplaySize(int displayWidth, int displayHeight) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    // 曲データの設定
    public void setMusicsDesc(String[] titles, int[] tempos, int[] mainDifs, int[] subDifs) {
        musicCount = titles.length;
        musicTitles = titles;
        musicTempos = tempos;
        mainDifficulties = mainDifs;
        subDifficulties = subDifs;
    }

    // ---------------------------------------------------------------------- //

    // アニメーションタイマーの設定
    public void setAnimationTimers(int frameRate) {
        titlebarAnimTimer  = new AnimationTimer(frameRate, 12, false);
        titlebarAnimTimer.setZero();
        directorAnimTimer  = new AnimationTimer(frameRate, 50, true);
        fadeInAnimTimer    = new AnimationTimer(frameRate, 30, false);
        fadeOutAnimTimer   = new AnimationTimer(frameRate, 64, false);
        sceneTransitionAnimTimer = new AnimationTimer(frameRate, 160, false);
    }

    // アニメーションタイマーの経過
    public void decAnimTimer(boolean sceneTransition, boolean keyRelease) {
        fadeInAnimTimer.pass(); // フェードイン
        titlebarAnimTimer.pass(); // 曲名バー

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

    public void startTitlebarAnimTimer() {
        titlebarAnimTimer.reset();
    }

    public int getTitlebarAnimTimer() {
        return titlebarAnimTimer.getDecTimer();
    }

    public boolean isEndFadeOut() {
        return fadeOutAnimTimer.isZero();
    }
    public boolean isEndSceneTransition() {
        return sceneTransitionAnimTimer.isZero();
    }

}
