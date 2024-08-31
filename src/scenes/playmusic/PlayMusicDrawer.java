package scenes.playmusic;

import scenes.draw.*;
import scenes.draw.blueprint.Blueprint;
import scenes.drawer.SceneDrawer;
import scenes.font.FontUtil;
import scenes.playmusic.note.NoteObject;
import scenes.playmusic.parts.*;
import scenes.playmusic.timeline.PunchCard;
import scenes.animtimer.AnimationTimer;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class PlayMusicDrawer extends SceneDrawer {
    // 各パーツのインスタンス
    private final PartsLane lane = new PartsLane();
    private final PartsJudgeLine judgeLine = new PartsJudgeLine();
    private final PartsNote note = new PartsNote();
    private final PartsJudge judge = new PartsJudge();

    private final PartsKeyboard keyboard;

    // 座標のインスタンス
    private final PartsPosition pos = new PartsPosition();

    // タイマー
    private AnimationTimer judgeAnimTimer;        // 24
    private AnimationTimer[] keyboardAnimTimer; // 10
    private AnimationTimer fadeInAnimTimer;       // 30
    private AnimationTimer fadeOutAnimTimer;      // 60
    private AnimationTimer scrollSpeedAnimTimer;  // 20

    // 設計図
    private Blueprint backgroundFrame, backgroundInner, laneBack;
    private Blueprint judgeLineBP;
    private Blueprint noteArpTop, noteArpBottom, noteBlueprint;
    private Blueprint keyboardBack, keyboardInner;
    private final Blueprint[] keyboardKeyWhite = new Blueprint[32];
    private final Blueprint[] keyboardKeyBlack = new Blueprint[32];
    private Blueprint musicProgressBack, musicProgress;
    private Blueprint noteHiddenGray, backgroundFrameTop, laneFrame;
    private Blueprint howToPlayFrame, howToPlayInner, howToPlaySeparate;
    private Blueprint howToPlayNote1, howToPlayNote2, howToPlayJudgeLine1;
    private Blueprint howToPlayNote3, howToPlayNote4, howToPlayJudgeLine2;
    private Blueprint resultFrame, resultInner, resultSeparate;
    private final Blueprint[] fadeIn1 = new Blueprint[8];
    private final Blueprint[] fadeIn2 = new Blueprint[8];
    private Blueprint fadeOut;

    // 楽譜のパート種別と演奏パート
    private static final int UNDEFINED = -1;
    private static final int MAIN_SCORE = 0;
    private static final int SUB_SCORE  = 1;
    private static final int NONE_PART = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int BOTH_PART = 3;

    // 判定種別
    private static final int JUDGE_PERFECT = 0;
    private static final int JUDGE_GREAT   = 1;
    private static final int JUDGE_GOOD    = 2;
    private static final int JUDGE_POOR    = 4;

    // 色定義
    private final Color backGray = new Color(55, 55, 55);
    private final Color colorBackFrame = new Color(215, 215, 215);

    private final Color colorMusicProgress = new Color(255, 125, 55);

    private final Color colorHowToPlayFrame    = new Color(255,160,80);
    private final Color colorHowToPlayInner    = new Color(20,10,0);
    private final Color colorHowToPlaySeparate = new Color(255,160,80, 60);
    private final Color colorHowToPlayStartMsg = new Color(255,210,210);

    private final Color colorResultFullCombo       = new Color(40, 30, 20, 255);
    private final Color colorResultFullComboShadow = new Color(0, 10, 20, 255);
    private final Color colorResultCombo           = new Color(255, 255, 255, 255);

    private final Color colorNoteMainWhite = note.getColors(0, MAIN_SCORE, false);
    private final Color colorNoteMainBlack = note.getColors(1, MAIN_SCORE, false);
    private final Color colorNoteSubWhite  = note.getColors(0, SUB_SCORE,  false);
    private final Color colorNoteSubBlack  = note.getColors(1, SUB_SCORE,  false);
    private final Color colorJudgeLine     = judgeLine.getColor();
    private final Color colorAutoPlay      = backGray;

    private final Color colorCombo = new Color(255, 255, 255, 100);
    private final Color colorAchievement = new Color(255, 255, 255, 100);

    // フォント定義
    private final Font fontMusicTitle = font.Meiryo(20, font.BOLD);
    private final Font fontMusicTempo = font.Meiryo(12);
    private final Font fontPlayPart   = font.Meiryo(12);

    private final Font fontScrollSpeed = font.Arial(12, font.BOLD);

    private final Font fontHowToPlayH1 = font.Meiryo(24, font.BOLD);
    private final Font fontHowToPlayP1 = font.Meiryo(12);
    private final Font fontHowToPlayP2 = font.Meiryo(16);

    private final Font fontResultJudge       = font.Meiryo(16, font.BOLD);
    private final Font fontResultAchievement = font.Meiryo(20, font.BOLD);
    private final Font fontResultFullCombo   = font.Arial(40, font.BOLD);
    private final Font fontResultCombo       = font.Arial(10);

    private final Font fontFPS = font.Arial(10);

    // 表示テキスト
    private final String[] playPartStr = {"自動再生", "メロディ", "伴奏", "メロディ＆伴奏"};

    // ------------------------------------------------------------------------ //

    // コンストラクタ
    public PlayMusicDrawer(PartsKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    // 背景の描画
    public void drawBack(Graphics2D g2d) {
        backgroundFrame.fillPolygon(g2d, drawRect, colorBackFrame);
        backgroundInner.fillPolygon(g2d, drawRect, backGray);
        laneBack.fillPolygon(g2d, drawRect, Color.BLACK);
    }

    // 判定線の描画
    public void drawJudgeLine(Graphics2D g2d) {
        judgeLineBP.fillPolygon(g2d, drawRect, judgeLine.getColor() );
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
            Color color = note.getColors(keyKind, scoreKind, autoPlay);

            // ノーツの描画
            switch (noteKind) {
                // アルペジオノート
                case PunchCard.MA, PunchCard.MP, PunchCard.MN, PunchCard.SA, PunchCard.SP, PunchCard.SN:
                    // 台形を鏡合わせに2つ描画して6角形をつくる
                    noteArpTop = new Blueprint(noteX, noteY, noteWT, noteW, noteH/2);
                    noteArpTop.setSide(Blueprint.CENTER, Blueprint.BOTTOM, Blueprint.HORIZONTAL);
                    noteArpTop.fillTrapezoid(g2d, color);
                    noteArpBottom = new Blueprint(noteX, noteY, noteWT, noteW, -noteH/2);
                    noteArpBottom.setSide(Blueprint.CENTER, Blueprint.BOTTOM, Blueprint.HORIZONTAL);
                    noteArpBottom.fillTrapezoid(g2d, color);
                    break;

                // 通常ノーツ
                default:
                    // 長方形を描画する
                    noteBlueprint = new Blueprint(noteX, noteY, noteW, noteH);
                    noteBlueprint.setSide(drawRect.CENTER, drawRect.CENTER);
                    noteBlueprint.fillPolygon(g2d, drawRect, color);
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
        keyboardBack.fillPolygon(g2d, drawRect, backGray);
        keyboardInner.fillPolygon(g2d, drawRect, Color.BLACK);

        int pitchCount = keyboard.getPitchCount();
        // 白鍵の描画
        for(int p = 0; p < pitchCount; p++) {
            if(keyboard.getKeyKind(p) == PartsKeyboard.WHITE) {
                drawKeyRect(g2d, p, keyboardKeyWhite[p]);
            }
        }
        // 黒鍵の描画
        for(int p = 0; p < pitchCount; p++) {
            if(keyboard.getKeyKind(p) == PartsKeyboard.BLACK) {
                drawKeyRect(g2d, p, keyboardKeyBlack[p]);
            }
        }
    }
    // 鍵盤のキーの描画
    private void drawKeyRect(Graphics2D g2d, int pitch, Blueprint bp) {
        int keyKind = keyboard.getKeyKind(pitch);
        boolean motionKeyPush = !keyboardAnimTimer[pitch].isZero();
        bp.setAnchorX(pos.keyRectX(pitch) );
        bp.setWidth(keyboard.getWidth(keyKind) );
        bp.setHeight(keyboard.getHeight(keyKind) );
        bp.fillPolygon(g2d, drawRect, keyboard.getKeyColor(keyKind, motionKeyPush) );
    }

    // 判定アニメーションの描画
    public void drawJudgement(Graphics2D g2d, int j, int combo, String acvStr, int judgeSubDisplay) {
        if( !judgeAnimTimer.isZero() ) {
            String judgeStr = judge.getJudgeText(j);
            Font judgeFont = judge.getJudgeFont();
            Font comboFont = judge.getComboFont();
            Font achivementFont = judge.getAchievementFont();
            Color[] color = judge.getJudgeColor(j);

            int timer = judgeAnimTimer.getTimer();
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

                    int comboX = displayWidth / 2 - comboW / 2;
                    int comboY = pos.getComboY();

                    font.setStr(g2d, comboFont, colorCombo);
                    font.drawStr(g2d, comboStr, comboX, comboY);
                }
            }
            if(judgeSubDisplay == 2 || judgeSubDisplay == 3) {
                // 達成率
                int acvW = font.strWidth(g2d, achivementFont, acvStr);
                int acvX = displayWidth / 2 - acvW / 2;
                int acvY = judgeSubDisplay == 2 ? pos.getComboY() : pos.getComboY() + 15;
                font.setStr(g2d, achivementFont, colorAchievement);
                font.drawStr(g2d, acvStr, acvX, acvY);
            }
        }
    }

    // スクロール速度表示の描画
    public void drawScrollSpeed(Graphics2D g2d, float noteUnitMov) {
        if( !scrollSpeedAnimTimer.isZero() ) {
            float speed = (float) Math.round(noteUnitMov * 100) / 10;
            font.setStr(g2d, fontScrollSpeed, Color.WHITE);
            font.drawStr(g2d, "Scroll: " + speed + "x", 170, 385);
        }
    }

    // 曲プログレスバー
    public void drawMusicProgress(Graphics2D g2d, int nowTime, int musicEndTime) {
        musicProgressBack.fillPolygon(g2d, drawRect, Color.BLACK);

        int wp = (int) (judgeLine.getWidth() * Math.min( (float) nowTime / musicEndTime, 1) );
        musicProgress.setWidth(wp);
        musicProgress.fillPolygon(g2d, drawRect, colorMusicProgress);
    }

    // 外枠と曲中表示の描画
    public void drawFrame(Graphics2D g2d, String musicTitle, int musicTempo, int playPart) {
        noteHiddenGray.fillPolygon(g2d, drawRect, backGray);
        backgroundFrameTop.fillPolygon(g2d, drawRect, colorBackFrame);
        laneFrame.drawPolygon(g2d, drawRect, Color.WHITE);

        font.setStr(g2d, fontMusicTitle, Color.WHITE);
        font.drawStr(g2d, musicTitle, 20, 35);
        int padding = font.strWidth(g2d, fontMusicTitle, musicTitle);
        font.setStr(g2d, fontPlayPart, Color.WHITE);
        font.drawStr(g2d, " (" + playPartStr[playPart] + ")", 20 + padding, 35);

        font.setStr(g2d, fontMusicTempo, Color.WHITE);
        font.drawStr(g2d, "♩ = " + musicTempo, 336, 35);
    }

    // FPS表示
    public void drawFrameRate(Graphics2D g2d, String msgFPS, String msgLatency) {
        font.setStr(g2d, fontFPS, Color.WHITE);
        font.drawStr(g2d, msgFPS + ", " + msgLatency, 270, 14);
    }

    // あそびかた説明
    public void drawHowToPlay(Graphics2D g2d, int playPart) {
        // 枠と背景
        howToPlayFrame.fillPolygon(g2d, drawRect, colorHowToPlayFrame);
        howToPlayInner.fillPolygon(g2d, drawRect, colorHowToPlayInner);
        howToPlaySeparate.fillPolygon(g2d, drawRect, colorHowToPlaySeparate);

        int wCenter = displayWidth / 2;
        int hCenter = displayHeight / 2;

        // 題字
        font.setStr(g2d, fontHowToPlayH1, Color.WHITE);
        font.drawStr(g2d, "あそびかた", wCenter - 60, hCenter - 100);

        // 説明セクション(メロディ側)
        if(playPart == MAIN_PART || playPart == BOTH_PART) {
            // 説明文
            int xExpR = wCenter + 20;
            font.setStr(g2d, fontHowToPlayP1, Color.WHITE);
            font.drawStr(g2d, "白・青の音符が",      xExpR, hCenter - 60);
            font.drawStr(g2d, "下の赤線に届いたら",   xExpR, hCenter - 10);
            font.drawStr(g2d, "Ｊ Ｋ Ｌ の",         xExpR, hCenter + 40);
            font.drawStr(g2d, "好きなキーを押そう！", xExpR, hCenter + 60);

            // ノートと判定線の模型
            howToPlayNote1.fillPolygon(g2d, drawRect, colorNoteMainWhite);
            howToPlayNote2.fillPolygon(g2d, drawRect, colorNoteMainBlack);
            howToPlayJudgeLine1.fillPolygon(g2d, drawRect, colorJudgeLine);
        }
        else {
            int xExpR = wCenter + 20;
            font.setStr(g2d, fontHowToPlayP1, colorAutoPlay);
            font.drawStr(g2d, "メロディ側は",      xExpR, hCenter - 10);
            font.drawStr(g2d, "自動演奏されます。", xExpR, hCenter + 10);
        }

        // 説明セクション(伴奏側)
        if(playPart == SUB_PART || playPart == BOTH_PART) {
            // 説明文
            int xExpL = wCenter - 130;
            font.setStr(g2d, fontHowToPlayP1, Color.WHITE);
            font.drawStr(g2d, "黄・紫の音符が",      xExpL, hCenter - 60);
            font.drawStr(g2d, "下の赤線に届いたら",   xExpL, hCenter - 10);
            font.drawStr(g2d, "Ｓ Ｄ Ｆ の",         xExpL, hCenter + 40);
            font.drawStr(g2d, "好きなキーを押そう！", xExpL, hCenter + 60);

            // ノートと判定線の模型
            howToPlayNote3.fillPolygon(g2d, drawRect, colorNoteSubWhite);
            howToPlayNote4.fillPolygon(g2d, drawRect, colorNoteSubBlack);
            howToPlayJudgeLine2.fillPolygon(g2d, drawRect, colorJudgeLine);
        }
        else {
            int xExpL = wCenter - 130;
            font.setStr(g2d, fontHowToPlayP1, colorAutoPlay);
            font.drawStr(g2d, "伴奏側は",          xExpL, hCenter - 10);
            font.drawStr(g2d, "自動演奏されます。", xExpL, hCenter + 10);
        }

        font.setStr(g2d, fontHowToPlayP2, colorHowToPlayStartMsg);
        font.drawStr(g2d, "SPACEキーで演奏が始まります", wCenter - 115, hCenter + 100);
    }

    // リザルト
    public void drawResult(Graphics2D g2d, int[] judgeCount, int maxCombo, String acvStr) {
        // 枠と背景
        resultFrame.fillPolygon(g2d, drawRect, colorHowToPlayFrame);
        resultInner.fillPolygon(g2d, drawRect, colorHowToPlayInner);
        resultSeparate.fillPolygon(g2d, drawRect, colorHowToPlaySeparate);

        int wCenter = displayWidth / 2;
        int hCenter = displayHeight / 2;

        // 題字
        font.setStr(g2d, fontHowToPlayH1, Color.WHITE);
        font.drawStr(g2d, "演奏終了！", wCenter - 50, hCenter - 100);

        // フルコンボ表示
        if(judgeCount[JUDGE_POOR] == 0
                && judgeCount[JUDGE_PERFECT] + judgeCount[JUDGE_GREAT] + judgeCount[JUDGE_GOOD] != 0) {
            font.setStr(g2d, fontResultFullCombo, colorResultFullComboShadow);
            font.drawStr(g2d, "Full Combo!!", wCenter - 120 + 2, hCenter - 10 + 2);
            font.setStr(g2d, fontResultFullCombo, colorResultFullCombo);
            font.drawStr(g2d, "Full Combo!!", wCenter - 120, hCenter - 10);
        }

        // 判定文字と回数
        int x1 = wCenter - 90;
        int x2 = wCenter + 80;
        for(int j = 0; j < judgeCount.length - 1; j++) {
            int y = hCenter - 65 + 25 * j;
            font.setStr(g2d, fontResultJudge, judge.getJudgeColor(j)[0]);
            font.drawStr(g2d, judge.getJudgeText(j), x1, y);

            String jCntStr = "" + judgeCount[j];
            int jCntStrWidth = g2d.getFontMetrics(fontResultJudge).stringWidth(jCntStr);
            font.drawStr(g2d, jCntStr, x2 - jCntStrWidth, y);
        }

        // 達成率表示
        font.setStr(g2d, fontResultAchievement, colorHowToPlayStartMsg);
        font.drawStr(g2d, acvStr, wCenter - 50, hCenter + 70);

        // コンボ数
        String comboStr = (maxCombo < 10 ? "  " : maxCombo < 100 ? " " : "") + maxCombo;
        font.setStr(g2d, fontResultCombo, colorResultCombo);
        font.drawStr(g2d, "MaxCombo", wCenter - 113, hCenter + 60);
        font.drawStr(g2d, comboStr, wCenter - 95, hCenter + 72);

        // 下の説明文
        font.setStr(g2d, fontHowToPlayP1, Color.WHITE);
        font.drawStr(g2d, "Enterキーを押すと、選曲画面に戻ります。", wCenter - 110, hCenter + 100);
    }

    // フェードインモーション
    public void drawFadeIn(Graphics2D g2d) {
        for(int s = 0; s < 8; s++) {
            float progress = 1.0F - fadeInAnimTimer.getProgress();
            int width = (int) (displayWidth * progress);
            fadeIn1[s].setWidth(width);
            fadeIn1[s].fillPolygon(g2d, drawRect, Color.BLACK);
            fadeIn2[s].setWidth(width);
            fadeIn2[s].fillPolygon(g2d, drawRect, Color.BLACK);
        }
    }

    // フェードアウトモーション
    public void drawFadeOut(Graphics2D g2d) {
        int angle = (int) (400 * fadeOutAnimTimer.getProgress() );
        fadeOut.setAngle2(90 + angle);
        fadeOut.fillArc(g2d, Color.BLACK);
    }

    // ------------------------------------------------------------------------ //

    // パーツの設計図の設定
    @Override
    public void setBlueprint() {
        int laneX = pos.laneX();
        int laneHeight = lane.getHeight();
        int laneFullWidth = lane.getFullWidth();
        int judgeLineY = pos.judgeLineY();
        int judgeLineWidth = judgeLine.getWidth();
        int judgeLineBold = judgeLine.getBold();
        int keyRectY = pos.keyRectY();
        int displayWCenter = displayWidth / 2;
        int displayHCenter = displayHeight / 2;

        // 背景とレーン背景
        backgroundFrame = new Blueprint(0, 0, displayWidth, displayHeight);
        backgroundInner = new Blueprint(3, 3, displayWidth - 6, displayHeight - 6);
        laneBack = new Blueprint(laneX, judgeLineY, judgeLineWidth, laneHeight);
        laneBack.setSide(Blueprint.LEFT, Blueprint.BOTTOM);

        // 判定線
        judgeLineBP = new Blueprint(laneX, judgeLineY, judgeLineWidth, judgeLineBold);
        judgeLineBP.setSide(Blueprint.LEFT, Blueprint.BOTTOM);

        // 鍵盤
        int keyRectHeight0 = keyRectY - judgeLineY;
        keyboardBack = new Blueprint(laneX, judgeLineY, laneFullWidth, keyRectHeight0);
        keyboardInner = new Blueprint(laneX, keyRectY, laneFullWidth, keyboard.getHeight(0) );
        for(int bp = 0; bp < keyboardKeyWhite.length; bp++) {
            int keyKind = keyboard.getKeyKind(bp);
            int keyRectX = pos.keyRectX(bp);
            int keyRectWidth = keyboard.getWidth(keyKind);
            int keyRectHeight = keyboard.getHeight(keyKind);
            if(keyboard.getKeyKind(bp) == PartsKeyboard.WHITE) {
                keyboardKeyWhite[bp] = new Blueprint(keyRectX, keyRectY, keyRectWidth, keyRectHeight);
                keyboardKeyWhite[bp].setSide(Blueprint.CENTER, Blueprint.TOP);
            } else {
                keyboardKeyBlack[bp] = new Blueprint(keyRectX, keyRectY, keyRectWidth, keyRectHeight);
                keyboardKeyBlack[bp].setSide(Blueprint.CENTER, Blueprint.TOP);
            }
        }

        // 楽曲進行バー
        int musicProgressY = (keyRectY + judgeLineY) / 2;
        int musicProgressH = (keyRectY - judgeLineY) / 2 - 2;
        musicProgressBack = new Blueprint(laneX, musicProgressY, judgeLineWidth, musicProgressH);
        musicProgressBack.setSide(Blueprint.LEFT, Blueprint.CENTER);
        musicProgress = new Blueprint(laneX, musicProgressY, 0, musicProgressH);
        musicProgress.setSide(Blueprint.LEFT, Blueprint.CENTER);

        // ノート隠しと上塗りの枠線
        noteHiddenGray = new Blueprint(3, 0, displayWidth - 6, judgeLineY - laneHeight);
        backgroundFrameTop = new Blueprint(0, 0, displayWidth, 3);
        laneFrame = new Blueprint(laneX, judgeLineY, laneFullWidth, laneHeight);
        laneFrame.setSide(Blueprint.LEFT, Blueprint.BOTTOM);

        // あそびかた説明の枠と仕切り
        howToPlayFrame = new Blueprint(displayWCenter, displayHCenter - 10, 300, 260);
        howToPlayFrame.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlayInner = new Blueprint(displayWCenter, displayHCenter - 10, 300 - 6, 260 - 6);
        howToPlayInner.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlaySeparate = new Blueprint(displayWCenter, displayHCenter - 5, 2, 200 - 50);
        howToPlaySeparate.setSide(Blueprint.CENTER, Blueprint.CENTER);

        // ノートと判定線の模型
        howToPlayNote1 = new Blueprint(displayWCenter + 60, displayHCenter - 40, 22, 8);
        howToPlayNote1.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlayNote2 = new Blueprint(displayWCenter + 90, displayHCenter - 40, 22, 8);
        howToPlayNote2.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlayJudgeLine1 = new Blueprint(displayWCenter + 80, displayHCenter + 10, 100, 8);
        howToPlayJudgeLine1.setSide(Blueprint.CENTER, Blueprint.CENTER);

        // ノートと判定線の模型
        howToPlayNote3 = new Blueprint(displayWCenter - 90, displayHCenter - 40, 22, 8);
        howToPlayNote3.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlayNote4 = new Blueprint(displayWCenter - 60, displayHCenter - 40, 22, 8);
        howToPlayNote4.setSide(Blueprint.CENTER, Blueprint.CENTER);
        howToPlayJudgeLine2 = new Blueprint(displayWCenter - 70, displayHCenter + 10, 100, 8);
        howToPlayJudgeLine2.setSide(Blueprint.CENTER, Blueprint.CENTER);

        // 結果表示の枠と仕切り
        resultFrame = new Blueprint(displayWCenter, displayHCenter - 10, 300, 260);
        resultFrame.setSide(Blueprint.CENTER, Blueprint.CENTER);
        resultInner = new Blueprint(displayWCenter, displayHCenter - 10, 300-6, 260-6);
        resultInner.setSide(Blueprint.CENTER, Blueprint.CENTER);
        resultSeparate = new Blueprint(displayWCenter, displayHCenter + 45, 300 - 60, 2);
        resultSeparate.setSide(Blueprint.CENTER, Blueprint.CENTER);

        for(int fi = 0; fi < fadeIn1.length; fi++) {
            fadeIn1[fi] = new Blueprint(0, displayHeight * fi / 8, 0, displayHeight / 16);
            fadeIn2[fi] = new Blueprint(displayWidth, (displayHeight * (fi * 2 + 1) / 16), 0, displayHeight / 16);
            fadeIn2[fi].setSide(Blueprint.RIGHT, Blueprint.TOP);
        }
        int fadeOutRadius = (displayWCenter + displayHCenter) * 2;
        fadeOut = new Blueprint(displayWCenter, displayHCenter, fadeOutRadius, fadeOutRadius, 90, 90);
        fadeOut.setSide(Blueprint.CENTER, Blueprint.CENTER);
    }

    // アニメーションタイマーの設定
    @Override
    public void setAnimationTimer(int frameRate) {
        judgeAnimTimer       = new AnimationTimer(frameRate, 24, false);
        judgeAnimTimer.setZero();
        fadeInAnimTimer      = new AnimationTimer(frameRate, 30, false);
        fadeOutAnimTimer     = new AnimationTimer(frameRate, 60, false);
        scrollSpeedAnimTimer = new AnimationTimer(frameRate, 20, false);
        scrollSpeedAnimTimer.setZero();
        keyboardAnimTimer = new AnimationTimer[32];
        for(int p = 0; p < 32; p++) {
            keyboardAnimTimer[p] = new AnimationTimer(frameRate, 10, false);
            keyboardAnimTimer[p].setZero();
        }
    }

    @Override
    protected void pastAnimationTimer() { } // こっちは使わない

    // アニメーションタイマーの経過
    public void passAnimTimer(boolean isMusicEnd) {
        judgeAnimTimer.pass();
        for (AnimationTimer timer : keyboardAnimTimer) {
            timer.pass();
        }
        fadeInAnimTimer.pass();
        if(isMusicEnd) {
            fadeOutAnimTimer.pass();
        }
        scrollSpeedAnimTimer.pass();
    }

    // アニメーションタイマーの設定と取得
    public void startJudgeAnimTimer() {
        judgeAnimTimer.reset();
    }
    public void startKeyboardAnimTimer(int pitch) {
        keyboardAnimTimer[pitch].reset();
    }
    public void startScrollSpeedAnimTimer() {
        scrollSpeedAnimTimer.reset();
    }
    public boolean isEndFadeOut() {
        return fadeOutAnimTimer.isZero();
    }
    public boolean isEndFadeIn() {
        return fadeInAnimTimer.isZero();
    }

    // ------------------------------------------------------------------------ //

    public String getPlayPartStr (int playPart) {
        return playPartStr[playPart];
    }

}
