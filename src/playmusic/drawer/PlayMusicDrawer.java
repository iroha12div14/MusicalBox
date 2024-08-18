package playmusic.drawer;

import draw.*;
import font.FontUtil;
import playmusic.note.NoteObject;
import playmusic.parts.*;
import playmusic.timeline.PunchCard;
import time.fps.FrameRateUtil;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayMusicDrawer {
    // 各パーツのインスタンス
    private final PartsLane lane = new PartsLane();
    private final PartsJudgeLine judgeLine = new PartsJudgeLine();
    private final PartsNote note = new PartsNote();
    private final PartsJudge judge = new PartsJudge();

    private final PartsKeyboard keyboard;
    private final FrameRateUtil fru;

    // 座標のインスタンス
    private final PartsPosition pos = new PartsPosition();

    // 描画用インスタンス
    private final DrawLine drawLine = new DrawLine();
    private final DrawPolygon drawRect = new DrawRect();
    private final DrawTrapezium drawTz = new DrawTrapezium();
    private final DrawArc drawArc = new DrawArc();
    private final FontUtil font = new FontUtil();

    // 画面サイズ
    private final int DISPLAY_WIDTH;
    private final int DISPLAY_HEIGHT;

    // タイマー
    private static final int FADE_IN_ANIM_TIMER_SET = 30;
    private static final int FADE_OUT_ANIM_TIMER_SET = 60;
    private int judgeAnimTimer = 0;
    private final int[] keyboardAnimTimer = new int[32];
    private int fadeInAnimTimer = FADE_IN_ANIM_TIMER_SET;
    private int fadeOutAnimTimer = FADE_OUT_ANIM_TIMER_SET;
    private int scrollSpeedAnimTimer = 0;

    // 楽譜のパート種別と演奏パート
    private static final int UNDEFINED = -1;
    private static final int MAIN_SCORE = 0;
    private static final int SUB_SCORE  = 1;
    private static final int NONE_PART = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int ALL_PART  = 3;

    // 判定種別
    private static final int JUDGE_PERFECT = 0;
    private static final int JUDGE_GREAT   = 1;
    private static final int JUDGE_GOOD    = 2;
    private static final int JUDGE_POOR    = 4;

    // 色定義
    private final Color backGray = new Color(55, 55, 55);

    private final Color colorHowToPlayFrame    = new Color(255,160,80);
    private final Color colorHowToPlayInner    = new Color(20,10,0);
    private final Color colorHowToPlaySeparate = new Color(255,160,80, 60);
    private final Color colorHowToPlayStartMsg = new Color(255,210,210);

    private final Color colorResultFullCombo       = new Color(40, 30, 20, 255);
    private final Color colorResultFullComboShadow = new Color(0, 10, 20, 255);
    private final Color colorResultCombo           = new Color(255, 255, 255, 255);

    private final Color colorNoteMainWhite = note.getColors(0, false)[MAIN_SCORE];
    private final Color colorNoteMainBlack = note.getColors(1, false)[MAIN_SCORE];
    private final Color colorNoteSubWhite  = note.getColors(0, false)[SUB_SCORE];
    private final Color colorNoteSubBlack  = note.getColors(1, false)[SUB_SCORE];
    private final Color colorJudgeLine     = judgeLine.getColor();
    private final Color colorAutoPlay      = backGray;

    private final Color colorCombo = new Color(255, 255, 255, 100);
    private final Color colorAchievement = new Color(255, 255, 255, 100);

    // フォント定義
    private final Font fontMusicTitle = font.Meiryo(20);
    private final Font fontMusicTempo = font.Meiryo(14);
    private final Font fontPlayPart   = font.Meiryo(14);

    private final Font fontScrollSpeed = font.Arial(12, font.BOLD);

    private final Font fontHowToPlayH1 = font.Meiryo(24, font.BOLD);
    private final Font fontHowToPlayP1 = font.Meiryo(12);
    private final Font fontHowToPlayP2 = font.Meiryo(16);

    private final Font fontResultJudge       = font.Meiryo(16, font.BOLD);
    private final Font fontResultAchievement = font.Meiryo(20);
    private final Font fontResultFullCombo   = font.Arial(40, font.BOLD);
    private final Font fontResultCombo       = font.Arial(10);

    private final Font fontFPS = font.Arial(10);

    // 表示テキスト
    private final String[] playPartStr = {"自動再生", "メロディ", "伴奏", "全演奏"};

    // コンストラクタ
    public PlayMusicDrawer(PartsKeyboard keyboard, FrameRateUtil fru, int displayWidth, int displayHeight) {
        this.keyboard = keyboard;
        this.fru = fru;

        DISPLAY_WIDTH  = displayWidth;
        DISPLAY_HEIGHT = displayHeight;

        Arrays.fill(keyboardAnimTimer, 0); // 全部0で埋める書き方らしい
    }

    // 背景の描画
    public void drawBack(Graphics2D g2d) {
        drawRect.fill(g2d, new Color(215, 215, 215),
                drawRect.makeParam(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT)
        );
        drawRect.fill(g2d, backGray,
                drawRect.makeParam(3, 3, DISPLAY_WIDTH-6, DISPLAY_HEIGHT-6)
        );
        drawRect.fill(g2d, Color.BLACK,
                drawRect.makeParam(pos.laneX(), pos.judgeLineY(), judgeLine.getWidth(), lane.getHeight()),
                drawRect.makeSide(drawRect.LEFT, drawRect.BOTTOM)
        );
    }

    // 判定線の描画
    public void drawJudgeLine(Graphics2D g2d) {
        int x = pos.laneX();
        int y = pos.judgeLineY();
        int width = judgeLine.getWidth();
        int bold = judgeLine.getBold();
        drawRect.fill(g2d, judgeLine.getColor(),
                drawRect.makeParam(x, y, width, bold),
                drawRect.makeSide(drawRect.LEFT, drawRect.BOTTOM)
        );
    }

    // ノーツの描画
    // noteUnitMov は1ミリ秒あたりの移動量
    public void drawNotes(
            Graphics2D g2d,
            List<NoteObject> score, // 楽譜
            int scoreKind,          // 楽譜のパート種別
            int pastTime,           // 経過時間
            float noteUnitMov,      // 1ミリ秒あたりの移動量
            int playPart            // 演奏パート
    ) {
        for(NoteObject noteObject : score) { // 名前被り防止でnoteと書いてない
            // 到達時刻、音程、ノートの種類、アルペジオノートであれば結線の接続先
            int arriveTime   = noteObject.arriveTime();
            int pitch        = noteObject.pitch();
            String noteKind  = noteObject.kind();
            int arpConnectTo = noteObject.arpConnectTo();

            // 音程の白鍵・黒鍵、到達までの残り時間
            int keyKind = keyboard.getKeyKind(pitch);
            int remainTime = arriveTime - pastTime;

            // ノートの寸法、座標、色
            int noteX = pos.noteX(pitch);
            int noteY = pos.noteY(remainTime, noteUnitMov);
            int noteW = note.getWidth(keyKind);
            int noteH = note.getHeight();
            int noteWT = noteW - noteH;
            boolean autoPlay = (playPart == NONE_PART
                    || (playPart == MAIN_PART && scoreKind == SUB_SCORE)
                    || (playPart == SUB_PART  && scoreKind == MAIN_SCORE));
            Color color = note.getColors(keyKind, autoPlay)[scoreKind];

            // ノーツの描画
            Map<Draw.Param, Integer> noteParam;
            Map<Draw.Param, Integer> noteParam2;
            Map<Draw.Side, Integer> noteSide;
            switch (noteKind) {
                // アルペジオノート
                case PunchCard.MA, PunchCard.MP, PunchCard.MN, PunchCard.SA, PunchCard.SP, PunchCard.SN:
                    // パラメータを設定
                    noteParam  = drawTz.makeParam(noteX, noteY, noteWT, noteW, noteH/2);
                    noteParam2 = drawTz.makeParam(noteX, noteY, noteWT, noteW, -noteH/2);
                    noteSide   = drawTz.makeSide(drawTz.CENTER, drawTz.BOTTOM, drawTz.HORIZONTAL); // 中央下寄せ水平
                    // 台形を鏡合わせに2つ描画して6角形をつくる
                    drawTz.fill(g2d, color, noteParam, noteSide);
                    drawTz.fill(g2d, color, noteParam2, noteSide);
                    break;

                default: // 通常ノーツ
                    // パラメータを設定
                    noteParam = drawRect.makeParam(noteX, noteY, noteW, noteH);
                    noteSide  = drawRect.makeSide(drawRect.CENTER, drawRect.CENTER); // 中央寄せ
                    // 長方形を描画する
                    drawRect.fill(g2d, color, noteParam, noteSide);
                    break;
            }
            // アルペジオノーツの結線の描画
            if(arpConnectTo != UNDEFINED) {
                if (noteKind.equals(PunchCard.MA) || noteKind.equals(PunchCard.MP)
                        || noteKind.equals(PunchCard.SA) || noteKind.equals(PunchCard.SP)) {
                    // 始点と終点を設定
                    int startX = pos.arpLineStartX(noteX, noteW);
                    int startY = pos.arpLineStartY(noteY);
                    int connectX = pos.arpLineConnectToX(arpConnectTo);
                    int connectY = pos.arpLineConnectToY(noteY, noteUnitMov);

                    // パラメータを設定し、結線を描画する
                    Color lineColor = !autoPlay ? Color.WHITE : color;
                    Map<Draw.Param, Integer> lineParam = drawLine.makeParam(startX, startY, connectX, connectY);
                    drawLine.draw(g2d, lineColor, lineParam);
                }
            }
        }
    }

    // 鍵盤の描画
    public void drawKeyBoard(Graphics2D g2d) {
        // 鍵盤の黒背景
        int x  = pos.laneX();
        int y1 = pos.judgeLineY();
        int y2 = pos.keyRectY();
        int w  = lane.getFullWidth();
        int h1 = y2 - y1;
        int h2 = keyboard.getHeight(0);
        drawRect.fill(g2d, backGray,
                drawRect.makeParam(x, y1, w, h1),
                drawRect.makeSide(drawRect.LEFT, drawRect.TOP)
        );
        drawRect.fill(g2d, Color.BLACK,
                drawRect.makeParam(x, y2, w, h2),
                drawRect.makeSide(drawRect.LEFT, drawRect.TOP)
        );

        int pitchCount = keyboard.getPitchCount();
        // 白鍵の描画
        for(int p = 0; p < pitchCount; p++) {
            if(keyboard.getKeyKind(p) == PartsKeyboard.WHITE) {
                drawKeyRect(g2d, p);
            }
        }
        // 黒鍵の描画
        for(int p = 0; p < pitchCount; p++) {
            if(keyboard.getKeyKind(p) == PartsKeyboard.BLACK) {
                drawKeyRect(g2d, p);
            }
        }
    }
    // 鍵盤のキーの描画
    private void drawKeyRect(Graphics2D g2d, int pitch) {
        int keyKind = keyboard.getKeyKind(pitch);
        int x = pos.keyRectX(pitch);
        int y = pos.keyRectY();
        int w = keyboard.getWidth(keyKind);
        int h = keyboard.getHeight(keyKind);
        boolean motionKeyPush = getKeyboardAnimTimer(pitch) > 0;

        drawRect.fill(g2d,
                keyboard.getKeyColor(keyKind, motionKeyPush),
                drawRect.makeParam(x, y, w, h),
                drawRect.makeSide(drawRect.CENTER, drawRect.TOP)
        );
    }

    // 判定アニメーションの描画
    public void drawJudgement(Graphics2D g2d, int j, int combo, String acvStr, int judgeSubDisplay) {
        if(getJudgeAnimTimer() > 0) {
            String judgeStr = judge.getJudgeText(j);
            Font judgeFont = judge.getJudgeFont();
            Font comboFont = judge.getComboFont();
            Font achivementFont = judge.getAchievementFont();
            Color[] color = judge.getJudgeColor(j);

            int timer = getJudgeAnimTimer();
            int judgeX = pos.getJudgeX(j);
            int judgeY = pos.getJudgeY(timer);

            // 判定
            g2d.setFont(judgeFont);
            if(timer % 9 < 3) {
                g2d.setColor( color[0] );
            } else if(timer % 9 < 6) {
                g2d.setColor( color[1] );
            } else {
                g2d.setColor( color[2] );
            }
            g2d.drawString(judgeStr, judgeX, judgeY);

            // 判定のサブ表示 0:非表示, 1:コンボ, 2:達成率, 3:コンボと達成率両方
            if(judgeSubDisplay == 1 || judgeSubDisplay == 3) {
                // コンボ
                if (j == JUDGE_PERFECT || j == JUDGE_GREAT || j == JUDGE_GOOD) { // PERFECT, GREAT, GOOD
                    String comboStr = (combo < 10 ? "  " : combo < 100 ? " " : "") + combo + " combo";
                    int comboW = font.strWidth(g2d, comboFont, comboStr);

                    int comboX = DISPLAY_WIDTH / 2 - comboW / 2;
                    int comboY = pos.getComboY();

                    g2d.setFont(comboFont);
                    g2d.setColor(colorCombo);
                    g2d.drawString(comboStr, comboX, comboY);
                }
            }
            if(judgeSubDisplay == 2 || judgeSubDisplay == 3) {
                // 達成率
                int acvW = font.strWidth(g2d, achivementFont, acvStr);
                int acvX = DISPLAY_WIDTH / 2 - acvW / 2;
                int acvY = judgeSubDisplay == 2 ? pos.getComboY() : pos.getComboY() + 15;
                g2d.setFont(achivementFont);
                g2d.setColor(colorAchievement);
                g2d.drawString(acvStr, acvX, acvY);
            }
        }
    }

    // スクロール速度表示の描画
    public void drawScrollSpeed(Graphics2D g2d, float noteUnitMov) {
        if(scrollSpeedAnimTimer > 0) {
            float speed = (float) Math.round(noteUnitMov * 100) / 10;
            g2d.setColor(Color.WHITE);
            g2d.setFont(fontScrollSpeed);
            g2d.drawString("Scroll: " + speed + "x", 170, 385);
        }
    }

    // 曲プログレスバー
    public void drawMusicProgress(Graphics2D g2d, int nowTime, int musicEndTime) {
        int x = pos.laneX();
        int y = (pos.keyRectY() + pos.judgeLineY()) / 2;
        int w = judgeLine.getWidth();

        int wp = (int) (w * Math.min( (float) nowTime / musicEndTime, 1) );
        int r = (pos.keyRectY() - pos.judgeLineY()) / 2 - 2;

        drawRect.fill(g2d, Color.BLACK,
                drawRect.makeParam(x, y, w, r),
                drawRect.makeSide(drawRect.LEFT, drawRect.CENTER)
        );
        drawRect.fill(g2d, new Color(255, 125, 55),
                drawRect.makeParam(x, y, wp, r),
                drawRect.makeSide(drawRect.LEFT, drawRect.CENTER)
        );
    }

    // 外枠と曲中表示の描画
    public void drawFrame(Graphics2D g2d, String musicTitle, int musicTempo, int playPart) {
        int x = pos.laneX();
        int y = pos.judgeLineY();
        int w = lane.getFullWidth();
        int h = lane.getHeight();
        drawRect.fill(g2d, backGray,
                drawRect.makeParam(3, 0, DISPLAY_WIDTH-6, y-h)
        );
        drawRect.fill(g2d, new Color(215, 215, 215),
                drawRect.makeParam(0, 0, DISPLAY_WIDTH, 3)
        );
        drawRect.draw(g2d, Color.WHITE,
                drawRect.makeParam(x, y, w, h),
                drawRect.makeSide(drawRect.LEFT, drawRect.BOTTOM)
        );

        g2d.setFont(fontMusicTitle);
        g2d.drawString(musicTitle, 20, 35);
        int padding = font.strWidth(g2d, fontMusicTitle, musicTitle);
        g2d.setFont(fontPlayPart);
        g2d.drawString(" (" + playPartStr[playPart] + ")", 20 + padding, 35);

        g2d.setFont(fontMusicTempo);
        g2d.drawString("♩ = " + musicTempo, 330, 35);

        g2d.setFont(fontFPS);
        g2d.drawString(fru.msgFPS(false) + ", " + fru.msgLatency(500), 270, 14);
    }

    // あそびかた説明
    public void drawHowToPlay(Graphics2D g2d, int playPart) {
        int wCenter = DISPLAY_WIDTH / 2;
        int hCenter = DISPLAY_HEIGHT / 2;

        // 枠と背景
        drawRect.fill(g2d, colorHowToPlayFrame,
                drawRect.makeParam(wCenter, hCenter - 10, 300, 260),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );
        drawRect.fill(g2d, colorHowToPlayInner,
                drawRect.makeParam(wCenter, hCenter - 10, 300-6, 260-6),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );
        // 仕切り
        drawRect.fill(g2d, colorHowToPlaySeparate,
                drawRect.makeParam(wCenter, hCenter - 5, 2, 200 - 50),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );

        // 題字
        g2d.setColor(Color.WHITE);
        g2d.setFont(fontHowToPlayH1);
        g2d.drawString("あそびかた", wCenter - 60, hCenter - 100);

        // 説明セクション(メロディ側)
        if(playPart == MAIN_PART || playPart == ALL_PART) {
            // 説明文
            int xExpR = wCenter + 20;
            g2d.setColor(Color.WHITE);
            g2d.setFont(fontHowToPlayP1);
            g2d.drawString("白・青の音符が",      xExpR, hCenter - 60);
            g2d.drawString("下の赤線に届いたら",   xExpR, hCenter - 10);
            g2d.drawString("Ｊ Ｋ Ｌ の",         xExpR, hCenter + 40);
            g2d.drawString("好きなキーを押そう！", xExpR, hCenter + 60);
            // ノートと判定線の模型
            drawRect.fill(g2d, colorNoteMainWhite,
                    drawRect.makeParam(wCenter + 60, hCenter - 40, 22, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
            drawRect.fill(g2d, colorNoteMainBlack,
                    drawRect.makeParam(wCenter + 90, hCenter - 40, 22, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
            drawRect.fill(g2d, colorJudgeLine,
                    drawRect.makeParam(wCenter + 80, hCenter + 10, 100, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
        } else {
            int xExpR = wCenter + 20;
            g2d.setColor(colorAutoPlay);
            g2d.setFont(fontHowToPlayP1);
            g2d.drawString("メロディ側は",      xExpR, hCenter - 10);
            g2d.drawString("自動演奏されます。", xExpR, hCenter + 10);
        }

        // 説明セクション(伴奏側)
        if(playPart == SUB_PART || playPart == ALL_PART) {
            // 説明文
            int xExpL = wCenter - 130;
            g2d.setColor(Color.WHITE);
            g2d.setFont(fontHowToPlayP1);
            g2d.drawString("黄・紫の音符が",      xExpL, hCenter - 60);
            g2d.drawString("下の赤線に届いたら",   xExpL, hCenter - 10);
            g2d.drawString("Ｓ Ｄ Ｆ の",         xExpL, hCenter + 40);
            g2d.drawString("好きなキーを押そう！", xExpL, hCenter + 60);
            // ノートと判定線の模型
            drawRect.fill(g2d, colorNoteSubWhite,
                    drawRect.makeParam(wCenter - 90, hCenter - 40, 22, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
            drawRect.fill(g2d, colorNoteSubBlack,
                    drawRect.makeParam(wCenter - 60, hCenter - 40, 22, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
            drawRect.fill(g2d, colorJudgeLine,
                    drawRect.makeParam(wCenter - 70, hCenter + 10, 100, 8),
                    drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
            );
        } else {
            int xExpL = wCenter - 130;
            g2d.setColor(colorAutoPlay);
            g2d.setFont(fontHowToPlayP1);
            g2d.drawString("伴奏側は",          xExpL, hCenter - 10);
            g2d.drawString("自動演奏されます。", xExpL, hCenter + 10);
        }

        g2d.setColor(colorHowToPlayStartMsg);
        g2d.setFont(fontHowToPlayP2);
        g2d.drawString("SPACEキーで演奏が始まります", wCenter - 115, hCenter + 100);
    }

    // リザルト
    public void drawResult(Graphics2D g2d, int[] judgeCount, int maxCombo, String acvStr) {
        int wCenter = DISPLAY_WIDTH / 2;
        int hCenter = DISPLAY_HEIGHT / 2;

        // 枠と背景
        drawRect.fill(g2d, colorHowToPlayFrame,
                drawRect.makeParam(wCenter, hCenter - 10, 300, 260),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );
        drawRect.fill(g2d, colorHowToPlayInner,
                drawRect.makeParam(wCenter, hCenter - 10, 300-6, 260-6),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );
        // 仕切り
        drawRect.fill(g2d, colorHowToPlaySeparate,
                drawRect.makeParam(wCenter, hCenter + 45, 300 - 60, 2),
                drawRect.makeSide(drawRect.CENTER, drawRect.CENTER)
        );

        // 題字
        g2d.setColor(Color.WHITE);
        g2d.setFont(fontHowToPlayH1);
        g2d.drawString("演奏終了！", wCenter - 50, hCenter - 100);

        // フルコンボ表示
        if(judgeCount[JUDGE_POOR] == 0
                && judgeCount[JUDGE_PERFECT] + judgeCount[JUDGE_GREAT] + judgeCount[JUDGE_GOOD] != 0) {
            g2d.setFont(fontResultFullCombo);
            g2d.setColor(colorResultFullComboShadow);
            g2d.drawString("Full Combo!!", wCenter - 120 + 2, hCenter - 10 + 2);
            g2d.setColor(colorResultFullCombo);
            g2d.drawString("Full Combo!!", wCenter - 120, hCenter - 10);
        }

        // 判定文字と回数
        g2d.setFont(fontResultJudge);
        int x1 = wCenter - 90;
        int x2 = wCenter + 80;
        for(int j = 0; j < judgeCount.length - 1; j++) {
            int y = hCenter - 65 + 25 * j;
            g2d.setColor(judge.getJudgeColor(j)[0]);
            g2d.drawString(judge.getJudgeText(j), x1, y);

            String jCntStr = "" + judgeCount[j];
            int jCntStrWidth = g2d.getFontMetrics(fontResultJudge).stringWidth(jCntStr);
            g2d.drawString(jCntStr, x2 - jCntStrWidth, y);
        }

        // 達成率表示
        g2d.setColor(colorHowToPlayStartMsg);
        g2d.setFont(fontResultAchievement);
        g2d.drawString(acvStr, wCenter - 40, hCenter + 70);

        // コンボ数
        String comboStr = (maxCombo < 10 ? "  " : maxCombo < 100 ? " " : "") + maxCombo;
        g2d.setColor(colorResultCombo);
        g2d.setFont(fontResultCombo);
        g2d.drawString("MaxCombo", wCenter - 108, hCenter + 60);
        g2d.drawString(comboStr, wCenter - 90, hCenter + 72);

        // 下の説明文
        g2d.setColor(Color.WHITE);
        g2d.setFont(fontHowToPlayP1);
        g2d.drawString("Enterキーを押すと、選曲画面に戻ります。", wCenter - 110, hCenter + 100);
    }

    // フェードインモーション
    public void drawFadeIn(Graphics2D g2d) {
        for(int s = 0; s < 8; s++) {
            int w = DISPLAY_WIDTH * fadeInAnimTimer / FADE_IN_ANIM_TIMER_SET;
            drawRect.fill(g2d, Color.BLACK,
                    drawRect.makeParam(0, DISPLAY_HEIGHT * s / 8, w, DISPLAY_HEIGHT / 16)
            );
            drawRect.fill(g2d, Color.BLACK,
                    drawRect.makeParam(DISPLAY_WIDTH, (DISPLAY_HEIGHT * (s * 2 + 1) / 16), w, DISPLAY_HEIGHT / 16),
                    drawRect.makeSide(drawRect.RIGHT, drawRect.TOP)
            );
        }
    }

    // フェードアウトモーション
    public void drawFadeOut(Graphics2D g2d) {
        int w = DISPLAY_WIDTH / 2;
        int h = DISPLAY_HEIGHT / 2;
        int r = (w + h) * 2;
        drawArc.fill(g2d, Color.BLACK,
                drawArc.makeParamArc(w, h, r, r, 90, 90 + (int) (400 * (1.0F - (float) fadeOutAnimTimer / FADE_OUT_ANIM_TIMER_SET)) ),
                drawArc.makeSide(drawArc.CENTER, drawArc.CENTER)
        );
    }

    // ------------------------------------------------------------------------ //

    // アニメーションタイマーの経過
    public void decAnimTimer(boolean isMusicEnd) {
        if(judgeAnimTimer > 0) {
                judgeAnimTimer--;
        }
        for(int k = 0; k < keyboardAnimTimer.length; k++) {
            if (keyboardAnimTimer[k] > 0) {
                keyboardAnimTimer[k]--;
            }
        }
        if(fadeInAnimTimer > 0) {
            fadeInAnimTimer--;
        }
        if(fadeOutAnimTimer > 0 && isMusicEnd) {
            fadeOutAnimTimer--;
        }
        if(scrollSpeedAnimTimer > 0) {
            scrollSpeedAnimTimer--;
        }
    }

    // アニメーションタイマーの設定と取得
    public void startJudgeAnimTimer() {
        judgeAnimTimer = 25;
    }
    public void startKeyboardAnimTimer(int pitch) {
         keyboardAnimTimer[pitch] = 10;
    }
    private int getJudgeAnimTimer() {
        return judgeAnimTimer;
    }
    public int getKeyboardAnimTimer(int pitch) {
        return keyboardAnimTimer[pitch];
    }
    public boolean isFadeTimeOut() {
        return fadeOutAnimTimer == 0;
    }
    public void startScrollSpeedAnimTimer() {
        scrollSpeedAnimTimer = 20;
    }
}
