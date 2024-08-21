package scene.fps;

import scene.fps.time.ClockHandMilli;
import calc.CalcUtil;

import java.util.List;
import java.util.LinkedList;
import javax.swing.*;

// FPSの測定と調整
public class FrameRateUtil {
    //----------- インスのタンス -------------------------------------------//
    // ミリ秒取得
    private final ClockHandMilli milli = new ClockHandMilli();
    // 演算用
    private final CalcUtil calc = new CalcUtil();

    //----------- 使うやつ -------------------------------------------//
    // ディレイを設定してタイマーを始動
    public void setDelayAndStartTimer(Timer timer) {
        updatePerFrame();
        int nextDelay = getNextDelay();
        timer.setDelay(nextDelay);
        timer.start();
    }
    // コンストラクタ
    public FrameRateUtil(int targetFPS, int updateDelaysPerSecond) {
        // 目標FPS、delaysの枠数
        this.targetFPS = targetFPS;
        this.delaysSize = (updateDelaysPerSecond > 0 && updateDelaysPerSecond < targetFPS)
                ? calc.div(targetFPS, updateDelaysPerSecond)
                : 1;

        lapTime = -1;
        prevMilli = -1;
        pastTime = 0;
        pastFrame = 0;

        timePaddingMicro = 0;

        // 処理時間が不明なので0とおいてディレイリストを作成
        delays = makeDelays(0);
        updateDelaysSignal = true;

        // FPS計測用
        pastMilliLogSize = (int) (targetFPS * LogSecond);

        isPause = false; // 一時停止
    }

    // 一時停止を設定
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
        if(pastMilliLog.size() > pastMilliLogSize ) {
            pastMilliLog.remove(0);
        }

        if( !isPause ) {
            pastFrame++;
            pastTime += lapTime;
        }

        // ディレイリストが空になったら補充
        if(delays.isEmpty() ) {
            int logicPastTime = 1000 * pastFrame / targetFPS;
            int fix = pastTime - logicPastTime; // 遅延時間
            delays = makeDelays(fix);

            // リスト補充時にシグナルを出す
            updateDelaysSignal = true;
        } else {
            updateDelaysSignal = false;
        }
    }
    // ディレイ時間の取得　ただし取得した時点で元データから消滅するので注意
    private int getNextDelay(){
        int delay = delays.get(0);
        delays.remove(0);
        return delay;
    }
    // ディレイリストの生成
    private List<Integer> makeDelays(int fix) {
        // 基準時間
        int logicPastTimeMicro = calc.div(1000000 * delaysSize, targetFPS);
        // 処理時間を考慮して次にディレイとして確保する時間
        // 何故かdelaysCount分だけ引くと丁度良くなる(なんで？)
        int delaySumMicro = logicPastTimeMicro + timePaddingMicro - (fix + delaysSize) * 1000;

        // ディレイ値をいい感じに格納してくれる……はず
        List<Integer> delays = new LinkedList<>();
        int slot = delaysSize;
        for(; slot > 0; slot--) {
            int delay = calc.div(delaySumMicro, slot * 1000);
            delays.add( Math.max(delay, 0) );
            delaySumMicro -= delay * 1000;
        }
        // ディレイで埋めきれない端数は次回に持ち越し
        timePaddingMicro = delaySumMicro;
        return delays;
    }

    //----------- フィールド -------------------------------------------//
    // 目標FPS
    private final int targetFPS;

    // ディレイ値のリスト、リストの枠数、リストの生成時シグナル
    private List<Integer> delays = new LinkedList<>();
    private final int delaysSize;
    private boolean updateDelaysSignal;

    // 1フレームで経過した時間、1フレーム前のミリ秒時刻、経過した合計時間、経過したフレーム時刻
    private int lapTime;
    private int prevMilli;
    private int pastTime;
    private int pastFrame;

    // ディレイで補完しきれない分の端数時間[マイクロ秒]
    private int timePaddingMicro;

    // 近傍n秒間の経過ミリ秒の履歴、履歴の記録秒数、履歴の記録枠数
    private final List<Integer> pastMilliLog = new LinkedList<>();
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
    public String msgDelays() {
        return "Delays:" + delays;
    }
    public String msgDelaySum() {
        int delaySum = (isUpdateDelaysSignal() )
                ? delays.stream()
                    .mapToInt(Integer::intValue)
                    .sum()
                : -1;
        return "DelaySum:" + delaySum + "[ms]";
    }
    public String msgPaddingMicro() {
        return "Padding:" + timePaddingMicro + "[micro sec]";
    }
    // delays更新時にシグナルを出すやつ
    public boolean isUpdateDelaysSignal() {
        return updateDelaysSignal;
    }
}
