package draw.digit;

// 依存パッケージ: draw
import draw.Draw;
import draw.DrawTrapezium;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

// セグメントナンバーの描画
public class DigitNumber {
    // 台形描画用のクラス
    private final DrawTrapezium dtz = new DrawTrapezium();

    // セグメントの描画 size=100 が基準値
    public void drawSegment(Graphics2D g2d, int number, int size, int posX, int posY, Color segColorON, Color segColorOFF) {
        Map<segPosition, Boolean> segState = updateSegment(number);
        for(segPosition seg : segPosition.values()){
            Map<Draw.Param, Integer> segParam1 = segParam(seg, size, posX, posY, false);
            Map<Draw.Param, Integer> segParam2 = segParam(seg, size, posX, posY, true);
            Map<Draw.Side, Integer> segSide = segSide(seg);

            Color segColor = (segState.get(seg)) ? segColorON : segColorOFF;
            dtz.fill(g2d, segColor, segParam1, segSide);
            dtz.fill(g2d, segColor, segParam2, segSide);
            dtz.draw(g2d, segColorOFF, segParam1, segSide);
            dtz.draw(g2d, segColorOFF, segParam2, segSide);
        }
    }
    // セグメント色を指定しなければ補完
    public void drawSegment(Graphics2D g2d, int number, int size, int posX, int posY) {
        drawSegment(g2d, number, size, posX, posY, SEGMENT_COLOR, SEGMENT_COLOR_OFF);
    }
    public void drawSegment(Graphics2D g2d, int number, int size, int posX, int posY, Color segColorON) {
        drawSegment(g2d, number, size, posX, posY, segColorON, SEGMENT_COLOR_OFF);
    }

    // セグメントの状態の更新
    // 一旦全部OFFにしてから必要な場所をONにする感じ 正直ちょっと手間
    private Map<segPosition, Boolean> updateSegment(int number){
        initSegment();
        for(segPosition seg : numberToSegmentLight(number)) {
            segmentState.put(seg, true);
        }
        return segmentState;
    }
    // イニシャライザ
    private void initSegment(){
        for(segPosition seg : segPosition.values()){
            segmentState.put(seg, false);
        }
    }
    // コンストラクタ ついでに初期化
    public DigitNumber(){
        initSegment();
    }

    // セグメントの原寸サイズとデフォルト色を拾えるようにした
    public int getSegmentWidth() {
        return SEGMENT_WIDTH;
    }
    public int getSegmentHeight() {
        return SEGMENT_HEIGHT;
    }
    public int getSegmentBold() {
        return SEGMENT_BOLD;
    }
    public Color getSegmentColor() {
        return SEGMENT_COLOR;
    }
    public Color getSegmentColorOff() {
        return SEGMENT_COLOR_OFF;
    }

    // セグメントの点灯状態
    private final Map<segPosition, Boolean> segmentState = new HashMap<>();
    // セグメントの点灯位置定義
    enum segPosition {
        TOP,
        LEFT_TOP,
        LEFT_BOTTOM,
        CENTER,
        RIGHT_TOP,
        RIGHT_BOTTOM,
        BOTTOM
    }
    // 省略語の登録
    final static segPosition T  = segPosition.TOP;
    final static segPosition LT = segPosition.LEFT_TOP;
    final static segPosition LB = segPosition.LEFT_BOTTOM;
    final static segPosition C  = segPosition.CENTER;
    final static segPosition RT = segPosition.RIGHT_TOP;
    final static segPosition RB = segPosition.RIGHT_BOTTOM;
    final static segPosition B  = segPosition.BOTTOM;

    // ナンバーに対する点灯セグメントの一覧
    private segPosition[] numberToSegmentLight(int num) {
        return switch(num) {
            case 0 -> new segPosition[] {T, LT, LB, RT, RB, B};
            case 1 -> new segPosition[] {RT, RB};
            case 2 -> new segPosition[] {T, LB, C, RT, B};
            case 3 -> new segPosition[] {T, C, RT, RB, B};
            case 4 -> new segPosition[] {LT, C, RT, RB};
            case 5 -> new segPosition[] {T, LT, C, RB, B};
            case 6 -> new segPosition[] {T, LT, LB, C, RB, B};
            case 7 -> new segPosition[] {T, LT, RT, RB};
            case 8 -> new segPosition[] {T, LT, LB, C, RT, RB, B};
            case 9 -> new segPosition[] {T, LT, C, RT, RB, B};
            default -> new segPosition[] {};
        };
    }
    // セグメントの点灯位置定義に対する描画パラメータ
    private Map<Draw.Param, Integer> segParam(segPosition seg, int size, int paddingX, int paddingY, boolean reverse) {
        // switch文でenumを使う場合は省略語だと判定できないっぽい
        int dtzX = switch(seg) {
            case TOP, LEFT_TOP, LEFT_BOTTOM, CENTER, BOTTOM -> SEGMENT_BOLD / 2;
            case RIGHT_TOP, RIGHT_BOTTOM -> SEGMENT_WIDTH - SEGMENT_BOLD / 2;
        };
        int dtzY = switch (seg) {
            case TOP, LEFT_TOP, RIGHT_TOP -> SEGMENT_BOLD / 2;
            case LEFT_BOTTOM, CENTER, RIGHT_BOTTOM -> SEGMENT_HEIGHT / 2;
            case BOTTOM -> SEGMENT_HEIGHT - SEGMENT_BOLD / 2;
        };
        int dtzWT = switch (seg) {
            case TOP, CENTER, BOTTOM -> SEGMENT_WIDTH - SEGMENT_BOLD;
            case LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM -> (SEGMENT_HEIGHT - SEGMENT_BOLD) / 2;
        };
        int dtzWB = dtzWT - SEGMENT_BOLD;
        // 高さパラメータに負の値を入れると基準点をそのままに逆向きに描画できる裏技的仕様
        int dtzH = (!reverse) ? SEGMENT_BOLD / 2 : -SEGMENT_BOLD / 2;

        // パラメータの挿入
        Map<Draw.Param, Integer> param = new HashMap<>();
        param.put(dtz.X, paddingX + resize(dtzX, size));
        param.put(dtz.Y, paddingY + resize(dtzY, size));
        param.put(dtz.WT, resize(dtzWT, size));
        param.put(dtz.WB, resize(dtzWB, size));
        param.put(dtz.H, resize(dtzH, size));

        return param;
    }
    private Map<Draw.Side, Integer> segSide(segPosition s) {
        Map<Draw.Side, Integer> segSide = new HashMap<>();
        segSide.put(dtz.SIDE_X, dtz.LEFT);
        segSide.put(dtz.SIDE_Y, dtz.TOP);
        if(s == T || s == C || s == B) {
            segSide.put(dtz.DIR, dtz.HORIZONTAL);
        }
        else if(s == LT || s == LB || s == RT || s == RB) {
            segSide.put(dtz.DIR, dtz.VERTICAL);
        }

        return segSide;
    }
    // パラメータのリサイズ用
    private int resize(int val, int size) {
        return Math.round((float) (val * size) / 100);
    }

    // セグメント色 点灯・消灯
    private static final Color SEGMENT_COLOR = new Color(255,60,60);
    private static final Color SEGMENT_COLOR_OFF = new Color(20,20,40);
    // セグメントの高さ、幅、太さ
    private static final int SEGMENT_HEIGHT = 100;
    private static final int SEGMENT_WIDTH = 60;
    private static final int SEGMENT_BOLD = 12;
}
