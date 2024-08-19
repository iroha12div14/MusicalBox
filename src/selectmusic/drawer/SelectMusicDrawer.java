package selectmusic.drawer;

import calc.CalcUtil;
import data.DataCaster;
import data.DataElements;
import draw.*;
import font.FontUtil;
import key.KeyController;
import time.fps.FrameRateUtil;

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

    private final FrameRateUtil fru;
    private final KeyController key;

    // 画面サイズ
    private final int DISPLAY_WIDTH;
    private final int DISPLAY_HEIGHT;

    // カーソル移動もろもろ
    private static final int TITLEBAR_COUNT = 11;
    private final int stripePattern;

    // 演奏パート
    private static final int AUTO_PLAY = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int ALL_PART  = 3;
    private final String[] partStrs = new String[]{"自動再生", "メロディ", "伴奏", "メロディ＆伴奏"};
    private final int[] difficultyMaxes = new int[]{0, 5, 5, 10};

    // アニメーションタイマー
    private static final int TITLEBAR_ANIM_TIMER_SET = 12;
    private static final int DIRECTOR_ANIM_TIMER_SET = 50;
    private static final int FADE_IN_ANIM_TIMER_SET = 30;
    private static final int STRIPE_ANIM_TIMER_SET = 8;
    private static final int SCENE_TRANSITION_ANIM_TIMER_SET = 160;
    private int titlebarAnimTimer = 0; // ANIM_TIMER_SET → 0
    private int directorAnimTimer = 0; // ANIM_TIMER_SET → 0, loop
    private int fadeInAnimTimer = 30;
    private int sceneTransitionAnimTimer = 0;
    private final int stripeAtPower = (int) calc.pow2(STRIPE_ANIM_TIMER_SET);

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
    private final int musicCount;
    private final String[] musicTitles;
    private final int[] musicTempos;
    private final int[] mainDifficulties;
    private final int[] subDifficulties;

    // ---------------------------------------------------------------------- //

    // コンストラクタ
    public SelectMusicDrawer(
            Map<Integer, Object> data,
            FrameRateUtil fru,
            KeyController key,
            String[] musicTitles,
            int[] musicTempos,
            int[] mainDifficulties,
            int[] subDifficulties
    ) {
        // インスタンスの中継
        this.fru = fru;
        this.key = key;

        // dataの受け渡し
        DataCaster caster = new DataCaster();
        DataElements elem = new DataElements();
        DISPLAY_WIDTH  = caster.getIntData(data, elem.DISPLAY_WIDTH);
        DISPLAY_HEIGHT = caster.getIntData(data, elem.DISPLAY_HEIGHT);

        // 楽曲選択で用いる内容の受け渡し
        this.musicTitles = musicTitles;
        this.musicTempos = musicTempos;
        this.mainDifficulties = mainDifficulties;
        this.subDifficulties = subDifficulties;
        this.musicCount = musicTitles.length;

        // シーン転換時のアニメーション(ランダムで2種)
        stripePattern = new Random().nextInt(2);
    }

    // ---------------------------------------------------------------------- //

    // 背景を描く
    public void drawBack(Graphics2D g2d) {
        rect.fill(g2d, backColor,
                rect.makeParam(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT)
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
        int turn = titlebarMovDir * 360 * titlebarAnimTimer / TITLEBAR_ANIM_TIMER_SET;
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
        int titlebarW = DISPLAY_WIDTH;
        rect.fill(g2d, cursorInnerColor,
                rect.makeParam(TITLEBAR_X, TITLEBAR_CENTER_Y, titlebarW, TITLEBAR_HEIGHT),
                rect.makeSide(rect.LEFT, rect.CENTER)
        );
        rect.fill(g2d, cursorFrameColor,
                rect.makeParam(TITLEBAR_X - 3, TITLEBAR_CENTER_Y, titlebarW, TITLEBAR_HEIGHT + 6),
                rect.makeSide(rect.LEFT, rect.CENTER)
        ); // カーソルが合ってる部分
    }
    // 曲名バーを描く
    public void drawTitleBar(Graphics2D g2d, int playPart, int cursor, int titlebarMovDir) {
        int titlebarW = DISPLAY_WIDTH;

        int musicCount = musicTitles.length;
        int barCenterPoint = TITLEBAR_COUNT / 2;
        float barProgress = (float) titlebarAnimTimer / TITLEBAR_ANIM_TIMER_SET;

        Color barColor = partColor[playPart];
        Map<Draw.Side, Integer> barSide = rect.makeSide(rect.LEFT, rect.CENTER);

        int x = TITLEBAR_X;
        for(int i = 0; i < TITLEBAR_COUNT; i++) {
            // バーの描画
            int y = TITLEBAR_CENTER_Y
                    + (i-barCenterPoint) * TITLEBAR_MOVE_Y
                    + (int) (titlebarMovDir * TITLEBAR_MOVE_Y * barProgress);
            rect.fill(g2d, barColor,
                    rect.makeParam(x, y, titlebarW, TITLEBAR_HEIGHT),
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
                rect.makeParam(0, 0, DISPLAY_WIDTH, EXPLAIN_HEIGHT)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(3, 3, DISPLAY_WIDTH - 6, EXPLAIN_HEIGHT - 6)
        );

        g2d.setColor(Color.WHITE);
        g2d.setFont(explainBoldFont);
        g2d.drawString("[あそびかた]", STRING_TITLE_X, 24);
        g2d.setFont(explainFont);
        g2d.drawString("↑キー、↓キー で 曲を 探します。", STRING_DESCRIPTION_X, 46);
        g2d.drawString("Spaceキー で 演奏パートを 変更します。", STRING_DESCRIPTION_X, 66);
        g2d.drawString("Enterキー で 演奏したい曲を 選びます。", STRING_DESCRIPTION_X, 86);

        g2d.setFont(FPSFont);
        g2d.drawString(fru.msgFPS(false) + ", " + fru.msgLatency(500), 270, 14);
    }
    // 曲情報パーツを描く
    public void drawMusicDescBack(Graphics2D g2d) {
        int y = DISPLAY_HEIGHT - MUSIC_DESC_HEIGHT;
        rect.fill(g2d, boxFrameColor,
                rect.makeParam(0, y, DISPLAY_WIDTH, MUSIC_DESC_HEIGHT)
        );
        rect.fill(g2d, boxInnerColor,
                rect.makeParam(3, y + 3, DISPLAY_WIDTH - 6, MUSIC_DESC_HEIGHT - 6)
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
        int descY = DISPLAY_HEIGHT - MUSIC_DESC_HEIGHT;

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
        float dtrProgress = (float) directorAnimTimer / DIRECTOR_ANIM_TIMER_SET;
        int x = TITLEBAR_HEIGHT + (DISPLAY_WIDTH - TITLEBAR_HEIGHT) / 2;
        int yt = DIRECTOR_CENTER_Y - DIRECTOR_PADDING_Y + (int) (DIRECTOR_MOVE_Y * dtrProgress);
        int yb = DIRECTOR_CENTER_Y + DIRECTOR_PADDING_Y - (int) (DIRECTOR_MOVE_Y * dtrProgress);
        int alpha = (int) (200 * dtrProgress);
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
    // フェードインの描写
    public void drawFadeIn(Graphics2D g2d) {
        int x = DISPLAY_WIDTH;
        int w = x * fadeInAnimTimer / FADE_IN_ANIM_TIMER_SET;
        rect.fill(g2d, Color.BLACK,
                rect.makeParam(x, 0, w, DISPLAY_HEIGHT),
                rect.makeSide(rect.RIGHT, rect.TOP)
        );
    }
    // シーン転換を描く
    public void drawSceneTransition(Graphics2D g2d) {
        int stripe = STRIPE_ANIM_TIMER_SET;
        int timer = sceneTransitionAnimTimer;

        if(timer > 0 && timer < stripeAtPower) {
            int tDiv = timer / stripe;
            int tMod = timer % stripe;
            for (int i = 0; i < stripe; i++) {
                int d1, d2, wh1, wh2, xy1, xy2;
                if(stripePattern == 0) {
                    d1 = DISPLAY_WIDTH;
                    d2 = DISPLAY_HEIGHT;
                } else {
                    d1 = DISPLAY_HEIGHT;
                    d2 = DISPLAY_WIDTH;
                }
                wh1 = d1 / stripe;
                if(tDiv == i) {
                    wh2 = d2 * tMod / stripe;
                } else if(tDiv > i) {
                    wh2 = d2;
                } else {
                    continue;
                }
                xy1 = wh1 * i;
                xy2 = 0;
                if(stripePattern == 0) {
                    rect.fill(g2d, Color.BLACK, rect.makeParam(xy1, xy2, wh1, wh2));
                } else {
                    rect.fill(g2d, Color.BLACK, rect.makeParam(xy2, xy1, wh2, wh1));
                }
            }
        } else if( timer >= stripeAtPower) {
            rect.fill(g2d, Color.BLACK, rect.makeParam(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT));
        }
    }
    // シーン転換後のタイトル表示
    public void drawTitleAfterSceneTransition(Graphics2D g2d, int playPart, int cursor) {
        rect.fill(g2d, Color.BLACK,
                rect.makeParam(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT)
        );

        String musicTitleStr = musicTitles[getPointer(musicCount, cursor)];
        int musicTitleStrWidth = font.strWidth(g2d, sceneTransTitleFont, musicTitleStr);
        g2d.setColor(Color.WHITE);
        g2d.setFont(sceneTransTitleFont);
        g2d.drawString(musicTitleStr, DISPLAY_WIDTH / 2 - musicTitleStrWidth / 2, 200);

        String playPartStr = "演奏パート：" + partStrs[playPart];
        int playPartStrWidth   = font.strWidth(g2d, playPartFont, playPartStr);
        g2d.setColor(partColor[playPart]);
        g2d.setFont(playPartFont);
        g2d.drawString(playPartStr, DISPLAY_WIDTH / 2 - playPartStrWidth / 2, 230);

        if(playPart != AUTO_PLAY) {
            int level = getDifLevel(playPart, getPointer(musicCount, cursor));
            String difLevelStr = "難しさ：" + getDifStarAndBlank(level, playPart, 20);
            int difLevelStrWidth = font.strWidth(g2d, playPartFont, difLevelStr);
            g2d.drawString(difLevelStr, DISPLAY_WIDTH / 2 - difLevelStrWidth / 2, 250);
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
            case ALL_PART  -> mainDifficulty + subDifficulty;
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

    // ---------------------------------------------------------------------- //

    // アニメーションタイマーの経過
    public void decAnimTimer(boolean sceneTransition, boolean keyRelease) {
        // フェードイン
        if(fadeInAnimTimer > 0) {
            fadeInAnimTimer--;
        }

        // ディレクタ
        if (key.isAnyKeyPress()) {
            directorAnimTimer = 0;
        }

        // 曲名バー
        if (titlebarAnimTimer > 0) {
            titlebarAnimTimer--;
        }

        // 一定時間操作をしていない場合の動作
        if ( keyRelease ) {
            directorAnimTimer = calc.mod(directorAnimTimer - 1, DIRECTOR_ANIM_TIMER_SET);
        }

        // シーン転換
        if( sceneTransition && !isOverSceneTransitionAnimTimer() ) {
            sceneTransitionAnimTimer++;
        }
    }

    public void startTitlebarAnimTimer() {
        titlebarAnimTimer = TITLEBAR_ANIM_TIMER_SET;
    }
    public int getTitlebarAnimTimer() {
        return titlebarAnimTimer;
    }
    public boolean isOverSceneTransitionAnimTimer() {
        return sceneTransitionAnimTimer >= SCENE_TRANSITION_ANIM_TIMER_SET;
    }
    public boolean isOverStripeAtPow() {
        return sceneTransitionAnimTimer >= stripeAtPower;
    }

}
