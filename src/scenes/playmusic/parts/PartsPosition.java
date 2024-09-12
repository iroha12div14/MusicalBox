package scenes.playmusic.parts;

/**
 * 各種パーツの座標定義
 */
public class PartsPosition {
    // パーツのコンストラクタ
    private final PartsLane lane = new PartsLane();
    private final PartsNote note = new PartsNote();
    private final PartsKeyboard keyboard = new PartsKeyboard();

    private static final int LANE_X = 10;
    private static final int JUDGE_LINE_Y = 420;
    private static final int[] JUDGE_STRING_X_PADDING = {68, 48, 34, 34, 34, 34};
    private static final int JUDGE_Y = 290;

    // 座標のゲッタ
    public int laneX() {
        return LANE_X;
    }

    public int judgeLineY() {
        return JUDGE_LINE_Y;
    }

    public int noteX(int p) {
        int pos = keyboard.getKeyPositionDefine(p);
        return laneX() + (pos + 1) * lane.getWidth() / 2;
    }
    public int noteY(int remainTime, float noteUnitMov) {
        int stdY = judgeLineY() - note.getHeight()/2;
        return Math.min(stdY - Math.round(remainTime * noteUnitMov), stdY); // 判定線より下には行かない
    }
    public int arpLineStartX(int x, int w) {
        return x + w/2;
    }
    public int arpLineStartY(int y) {
        return y;
    }
    public int arpLineConnectToX(int arp) {
        int keyKind = keyboard.getKeyKind(arp);
        return noteX(arp) - note.getWidth(keyKind)/2;
    }
    public int arpLineConnectToY(int y, float noteUnitMov) {
        return Math.round(y - (float) 2 * 1000 / 60 * noteUnitMov); // 60分の2秒だけ上側に引く
    }

    public int keyRectX(int p) {
        return noteX(p);
    }
    public int keyRectY() {
        return judgeLineY() + 10;
    }

    public int getJudgeStringXPadding(int j) {
        return JUDGE_STRING_X_PADDING[j];
    }
    public int getJudgeY() {
        return JUDGE_Y;
    }
    public int getJudgeX(int j) {
        return 200 - getJudgeStringXPadding(j);
    }
    public int getJudgeY(int animTimer) {
        return Math.min(getJudgeY() - (animTimer - 21) * 2, getJudgeY());
    }
    public int getComboY() {
        return JUDGE_Y + 20;
    }
}
