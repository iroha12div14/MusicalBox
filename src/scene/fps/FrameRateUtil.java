package scene.fps;

import scene.fps.time.ClockHandMilli;
import calc.CalcUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import javax.swing.*;

/**
 *  FPSの測定と調整
 */
public class FrameRateUtil {
    //----------- インスのタンス -------------------------------------------//
    // ミリ秒取得
    private final ClockHandMilli milli = new ClockHandMilli();
    // 演算用
    private final CalcUtil calc = new CalcUtil();

    //----------- 使うやつ -------------------------------------------//

    /**
     * タイマーのディレイ値を調整し、場面のFPSを調整する。
     * @param timer Timer型のインスタンス。
     */
    public void setDelayAndStartTimer(Timer timer) {
        updatePerFrame();
        timer.setDelay(delay);
        timer.start();
    }

    /**
     * コンストラクタ
     * @param targetFPS 目標FPS
     */
    public FrameRateUtil(int targetFPS) {
        // 目標FPS、delaysの枠数
        this.targetFPS = targetFPS;

        lapTime = -1;
        prevMilli = -1;
        pastTime = 0;
        pastFrame = 0;

        timePaddingMicro = 0;

        // 処理時間が不明なので0とおいてディレイを設定
        delay = makeDelays(0);

        // FPS計測用
        pastMilliLogSize = (int) (targetFPS * LogSecond);

        isPause = false; // 一時停止
    }

    /**
     * 一時停止を設定
     * @param set 停止の有無
     */
    public void setPause(boolean set) {
        isPause = set;
    }

    // 経過時間の取得(ミリ秒、フレーム)
    public int getPastTime() {
        return pastTime;
    }
    public int getPastFrame() {
        return pastFrame;
    }

    //----------- 裏ではたらくやつ -------------------------------------------//
    // 毎フレームの更新内容
    private void updatePerFrame() {
        int nowMilli = milli.getTime();
        lapTime = calc.mod(nowMilli - prevMilli, 1000);
        prevMilli = nowMilli; // lapTime取得できたので更新

        pastMilliLog.add(lapTime);
        if(pastMilliLog.size() > pastMilliLogSize) {
            pastMilliLog.remove(0);
        }

        if( !isPause ) {
            pastFrame++;
            pastTime += lapTime;
        }

        // ディレイを設定
        int logicPastTime = 1000 * pastFrame / targetFPS;   // 理論経過時間
        int fix = pastTime - logicPastTime;                 // 遅延時間
        delay = makeDelays(fix);
    }

    // ディレイ値の算出
    private int makeDelays(int fix) {
        // 基準時間[マイクロ秒]
        int logicTimePerFrameMicro = calc.div(1000000, targetFPS);

        // 処理時間を考慮して次にディレイとして確保する時間
        // 何故かこの式だと丁度良くなる(なんで？)
        int delaySumMicro = logicTimePerFrameMicro + timePaddingMicro - (fix + 1) * 1000;

        // ディレイ値を設定（0以上の値）
        int d = calc.div(delaySumMicro, 1000);
        delay = Math.max(d, 0);

        // ディレイで埋めきれない端数は次回に持ち越し
        timePaddingMicro = delaySumMicro - d * 1000;
        return delay;
    }

    //----------- フィールド -------------------------------------------//
    // 目標FPS
    private final int targetFPS;

    // ディレイ値
    private int delay;

    // 1フレームで経過した時間、1フレーム前のミリ秒時刻、経過した合計時間、経過したフレーム時刻
    private int lapTime;
    private int prevMilli;
    private int pastTime;
    private int pastFrame;

    // ディレイで補完しきれない分の端数時間[マイクロ秒]
    private int timePaddingMicro;

    // 近傍n秒間の経過ミリ秒の履歴、履歴の記録秒数、履歴の記録枠数
    private final List<Integer> pastMilliLog = new ArrayList<>();
    private final float LogSecond = 1.5F;
    private final int pastMilliLogSize;

    private boolean isPause;

    //----------- デバッグ出力用諸々 -------------------------------------------//
    public String msgLapTime() {
        return "LapTime:" + lapTime + "[ms]";
    }
    public String msgPastFrame() {
        return "Frame:" + getPastFrame();
    }
    public String msgPastTime() {
        float time = calc.div(getPastTime(), 1000, 1);
        return "Time:" + time + "[ms]";
    }
    public String msgFPSAve() {
        float fps = calc.div(1000 * getPastFrame(), getPastTime(), 1);
        return "FPS:" + fps + "(Ave)";
    }

    /**
     * FPSを表示する。
     * @param secondView FPSの更新間隔の表示の有無
     * @return FPSの表示（文字列）
     */
    public String msgFPS(boolean secondView) {
        int logSum = (pastMilliLog.size() == pastMilliLogSize)
                ? pastMilliLog.stream()
                    .mapToInt(Integer::intValue)
                    .sum()
                : -1;
        int fps = calc.div(1000 * pastMilliLogSize, logSum);
        return "FPS:"
                + ( (Math.abs(fps) < 1000) ? fps : -1)
                + ( (secondView) ? ("(" + LogSecond + "s)") : "");
    }
    public String msgLogicPastTime() {
        int logicPastTime = calc.div(1000 * getPastFrame(), targetFPS);
        return "LogicTime:" + logicPastTime + "[ms]";
    }

    /**
     * 遅延時間を表示する。
     * @param viewLimitMilli 表示の上限時間[ms]。0にすると上限なし。
     * @return 自演時間の表示（文字列）
     */
    public String msgLatency(int viewLimitMilli) {
        int logicPastTime = calc.div(1000 * getPastFrame(), targetFPS);
        int latency = getPastTime() - logicPastTime;
        return "Latency:"
                + ( (latency >= 0 && latency < 10) ? " " : "")
                + ( (latency <= viewLimitMilli || viewLimitMilli == 0)
                    ? latency
                    : (">" + viewLimitMilli) )
                + "[ms]";
    }
    public String msgPastMilliLog() {
        return "PastLog:" + pastMilliLog;
    }
    public String msgDelay() {
        return "Delay:" + delay + "[ms]";
    }
    public String msgPaddingMicro() {
        return "Padding:" + timePaddingMicro + "[micro sec]";
    }
}
