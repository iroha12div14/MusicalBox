package scenes.animtimer;

// アニメーション用のタイマー
// フレームレートさえ指定すれば時刻を60フレーム単位に翻訳して
// 加算・減算どちら式でも出力できる
public class AnimationTimer {
    private final int FRAME_RATE;   // フレームレート
    private final int SET;          // タイマーの上限時刻(1秒=60[F]単位)
    private int frame;              // 経過フレーム(1秒=FRAME_RATE[F]単位)
    private final boolean LOOP;     // 周期的か

    // コン setに0を指定すると上限なしの加算式タイマーになる
    public AnimationTimer(int frameRate, int set, boolean loop) {
        FRAME_RATE = Math.max(frameRate, 1);
        SET = Math.max(set, 0);
        LOOP = loop;

        reset();
    }
    public AnimationTimer(int frameRate, int set) {
        this(frameRate, set, false);
    }
    public AnimationTimer(int frameRate) {
        this(frameRate, 0, false);
    }

    // 時間の経過
    public void pass() {
        if(SET != 0) {
            if (!LOOP && frame > 0) {
                frame--;
            } else if (LOOP) {
                if(frame > 1) {
                    frame--;
                } else {
                    reset(); // frame = 0 と余りは一緒
                }
            }
        } else {
            frame++;
        }
    }

    // 時間のリセット
    public void reset() {
        frame = getSetValue();
    }

    // 60フレーム基準でタイマーの時刻を出す(減算式、加算式)
    public int getTimer() {
        return getDecTimer();
    }
    public int getDecTimer() {
        return Math.round( (float) frame * 60 / FRAME_RATE);
    }
    public int getIncTimer() {
        return SET != 0
                ? Math.round( (float) (getSetValue() - frame) * 60 / FRAME_RATE)
                : getDecTimer();
    }

    // タイマーの経過率 0.0F → 1.0F
    public float getProgress() {
        return (float) (getSetValue() - frame) / getSetValue();
    }

    // 特定の条件を満たすか
    public boolean isZero() {
        return !LOOP ? frame == 0 : frame == getSetValue();
    }

    // タイマーの時刻を0にする
    public void setZero() {
        frame = 0;
    }

    // 定数のゲッタ
    public int getSet() {
        return SET;
    }
    private int getSetValue() {
        return SET * FRAME_RATE / 60;
    }
}
