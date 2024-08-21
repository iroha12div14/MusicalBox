package scenes.playmusic.parts;

import java.awt.*;

// 鍵盤の処理と定義
public class PartsKeyboard {
    // レーン幅の情報が必要なのでとりあえずインスタンスを用意する
    private final PartsLane lane = new PartsLane();

    public static final int WHITE = 0;
    public static final int BLACK = 1;

    // 絵的に押されているキーの一時記録
    private final int[] pushKeyPitch = {0, 0};

    // 鍵盤の白黒の定義
    private final int[] keyKind = new int[]{
            WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE,   WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE,   WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE,
            WHITE, BLACK, WHITE, BLACK, WHITE
    };

    // 鍵盤の寸法の定義
    private final int whiteKeyWidth = lane.getWidth() - 2;
    private final int whiteKeyHeight = 60;
    private final int blackKeyWidth = whiteKeyWidth * 2/3;
    private final int blackKeyHeight = whiteKeyHeight * 7/12;

    // 鍵盤の位置の定義
    private static final int[] keyPositionDefine = {
            0,  1,  2,
            4,  5,  6,  7,  8,    10, 11, 12, 13, 14, 15, 16,
            18, 19, 20, 21, 22,    24, 25, 26, 27, 28, 29, 30,
            32, 33, 34, 35, 36
    };

    // キーを押していない・押したときの鍵盤の色
    private final Color[][] keyPushColor = new Color[][]{
            {new Color(255, 255, 255), new Color(255,  60,  60)},
            {new Color(  0,   0,   0), new Color(  0,  60, 255)}
    };

    // 幅、高さ
    public int getWidth(int keyKind) {
        return switch (keyKind) {
            case WHITE -> whiteKeyWidth;
            case BLACK -> blackKeyWidth;
            default -> 300;
        };
    }
    public int getHeight(int keyKind) {
        return switch (keyKind) {
            case WHITE -> whiteKeyHeight;
            case BLACK -> blackKeyHeight;
            default -> 300;
        };
    }

    // 音源数
    public int getPitchCount() {
        return keyPositionDefine.length;
    }

    // 鍵盤キー位置
    public int getKeyPositionDefine(int pitch) {
        return keyPositionDefine[pitch];
    }

    // 鍵盤色（押下時・非押下時）
    // 引数のkeyPushがboolean型だけど(keyboardAnimTimer > 0)とか入れとくのが良さげ
    // Color color = getKeyColor( getKeyKind(pitch), keyboardAnimTimer > 0); みたいな
    public Color getKeyColor(int keyKind, boolean keyPush) {
        return (!keyPush) ? keyPushColor[keyKind][0] : keyPushColor[keyKind][1];
    }

    // 音程から鍵盤の白黒に変換
    public int getKeyKind(int pitch) {
        return keyKind[pitch];
    }

    // 絵的に押されているキーの一時記録、の入出力
    public void memoryPushKey(int trackKind, int targetPitch) {
        pushKeyPitch[trackKind] = targetPitch;
    }
    public int getPushKeyPitch(int trackKind) {
        return pushKeyPitch[trackKind];
    }
    public int[] getPushKey() {
        return pushKeyPitch;
    }

}
