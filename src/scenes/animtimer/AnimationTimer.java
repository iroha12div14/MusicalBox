package scenes.animtimer;

/**
 * アニメーション用のタイマー。
 * <br/>
 * フレームレートを指定することで時刻を60フレーム単位に翻訳して、
 * 加算式・減算式で出力する
 */
public class AnimationTimer {
    private final int FRAME_RATE;   // フレームレート
    private final int SET;          // タイマーの上限時刻(1秒=60[F]単位)
    private int frame;              // 経過フレーム(1秒=FRAME_RATE[F]単位)
    private final boolean LOOP;     // 周期的か

    /**
     * コンストラクタ setに0を指定すると上限なしの加算式タイマーになる
     * @param frameRate FPS
     * @param set       タイマーの設定フレーム数(60[F]単位)
     * @param loop      ループの有無
     */
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

    /**
     * タイマーの時間を経過させる
     */
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

    /**
     * タイマーの時間をリセットする
     */
    public void reset() {
        frame = getSetValue();
    }

    /**
     * 60フレーム基準でタイマーの時刻を出す
     * @return タイマーの時刻
     */
    public int getTimer() {
        return getDecTimer();
    }
    /**
     * 60フレーム基準でタイマーの時刻を出す（減算式）
     * @return タイマーの時刻
     */
    public int getDecTimer() {
        return Math.round( (float) frame * 60 / FRAME_RATE);
    }
    /**
     * 60フレーム基準でタイマーの時刻を出す（加算式）
     * @return タイマーの時刻
     */
    public int getIncTimer() {
        return SET != 0
                ? Math.round( (float) (getSetValue() - frame) * 60 / FRAME_RATE)
                : getDecTimer();
    }

    /**
     * タイマーの経過率
     * @return 経過率（0.0F ~ 1.0F）
     */
    public float getProgress() {
        return (float) (getSetValue() - frame) / getSetValue();
    }

    /**
     * タイマーが0（もしくは初期設定値）になったかを判定
     */
    public boolean isZero() {
        return !LOOP ? frame == 0 : frame == getSetValue();
    }

    /**
     * タイマーの時刻を0にする
     */
    public void setZero() {
        frame = 0;
    }

    /**
     * タイマーの初期値を返す（60[F]単位）
     */
    public int getSet() {
        return SET;
    }
    /**
     * タイマーの初期値を返す（設定FPS単位）
     */
    private int getSetValue() {
        return SET * FRAME_RATE / 60;
    }
}
