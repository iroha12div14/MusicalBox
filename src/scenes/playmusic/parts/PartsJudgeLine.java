package scenes.playmusic.parts;

import java.awt.*;

public class PartsJudgeLine {
    // レーン幅の情報が必要なのでとりあえずインスタンスを用意する
    private final PartsLane lane = new PartsLane();

    private final int bold = 8;
    private final int width = lane.getFullWidth();

    private final Color color = new Color(150, 10, 10);

    // ゲッタ類
    public int getWidth() {
        return width;
    }
    public int getBold() {
        return bold;
    }
    public Color getColor() {
        return color;
    }
}
