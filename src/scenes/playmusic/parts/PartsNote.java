package scenes.playmusic.parts;

import java.awt.*;

/**
 * ノートの大きさや色などの定義
 */
public class PartsNote {
    // レーン幅の情報が必要なのでとりあえずインスタンスを用意する
    private final PartsLane lane = new PartsLane();

    private static final int WHITE = 0;
    private static final int BLACK = 1;

    // --------------------------------- //

    // ノートの寸法
    private final int whiteWidth = lane.getWidth() + 2;
    private final int blackWidth = lane.getWidth() - 2;
    private final int height = 8;

    // ノートの色 [keyKind][ScoreKind]
    private final Color[][] colors = new Color[][]{
            {new Color(220, 220, 220, 255), new Color(220, 220, 120, 255)},
            {new Color( 30, 120, 250, 255), new Color(180, 120, 220, 255)}
    };
    private final Color[][] autoPlayColors = {
            {new Color(220, 220, 220, 25), new Color(220, 220, 120, 25)},
            {new Color( 30, 120, 250, 25), new Color(180, 120, 220, 25)}
    };

    // ゲッタ類
    public int getHeight() {
        return height;
    }
    public int getWidth(int keyKind) {
        return switch (keyKind) {
            case WHITE -> whiteWidth;
            case BLACK -> blackWidth;
            default -> 300; // このサイズで出たらバグ
        };
    }
    public Color getColors(int keyKind, int scoreKind, boolean autoPlay) {
        return !autoPlay
                ? colors[keyKind][scoreKind]
                : autoPlayColors[keyKind][scoreKind];
    }
}
