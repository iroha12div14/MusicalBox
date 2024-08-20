package playmusic.judge;

import calc.CalcUtil;
import playmusic.note.NoteObject;
import playmusic.parts.PartsKeyboard;
import playmusic.drawer.PlayMusicDrawer;
import wav.KeySoundContainer;
import wav.KeySoundPlayer;

import java.util.ArrayList;
import java.util.List;

// 判定の処理と定義
public class JudgeUtil {
    // インスタンスあれこれ
    private final PartsKeyboard keyboard;
    private final KeySoundPlayer player;
    private final PlayMusicDrawer drawer;

    private final CalcUtil calc = new CalcUtil();

    // 直前に出した判定
    private int judgeState;
    // 判定回数
    private final int[] judgeCount;
    // コンボ数
    private int comboCount;
    private int comboMax;

    // 判定範囲(ミリ秒)
    // 判定後ろ寄りで設計 {12, 7, 2, -2, -10, -16}
    private final int[] judgeArea = new int[]{200, 120, 40, -40, -160, -260};

    // 判定状態
    public static final int JUDGE_PERFECT = 0;
    public static final int JUDGE_GREAT   = 1;
    public static final int JUDGE_GOOD    = 2;
    public static final int JUDGE_OOPS    = 3;
    public static final int JUDGE_MISS    = 4;
    public static final int JUDGE_AUTO    = 5;

    // 判定枠定義
    public final int jGoodFast    = 0;
    public final int jGreatFast   = 1;
    public final int jPerfectFast = 2;
    public final int jPerfectSlow = 3;
    public final int jGreatSlow   = 4;
    public final int jGoodSlow    = 5;

    // 達成率算出の配点 PERFECT:100pt, GREAT:60pt, GOOD:20pt, OOPS,MISS:0pt
    private final int[] achievementAllotPoint = {100, 60, 20, 0, 0};

    private final List<Integer> timings = new ArrayList<>();

    // コンストラクタ
    public JudgeUtil(
            KeySoundContainer container,
            PlayMusicDrawer drawer,
            PartsKeyboard keyboard,
            float keySoundMasterVolume
    ) {
        player = new KeySoundPlayer(container);
        player.setMasterVolume(keySoundMasterVolume); // 主音量を設定
        this.drawer = drawer;
        this.keyboard = keyboard;

        judgeCount = new int[]{0, 0, 0, 0, 0, 0};
        comboCount = 0;

        judgeState = 3; // OOPS判定を仮で入れている
    }

    // キーを押した時点の、ノートが判定線に到達するまでの残り時間から、判定を行う
    public void judgeNote(
            List<NoteObject> score, // 楽譜
            int remainTime,         // 到達残り時間
            int pitch,              // 音程
            int scoreKind,          // 楽譜のパート種別
            boolean autoPlayPart,   // そのパートが自動演奏されているかの有無
            boolean judgeAuto       // 自動再生か(AUTO判定表示の有無)
    ) {
        int judgeState = getJudge(remainTime, autoPlayPart);
        judgeCount[judgeState]++;

        // 判定アニメーション
        // AUTO判定以外の場合か、AUTO判定でも自動再生の場合は判定を出す
        if( judgeState != JUDGE_AUTO || judgeAuto ) {
            setJudgeState(judgeState);
            drawer.startJudgeAnimTimer();
        }

        // PERFECT, GREAT, GOOD, AUTO なら正しい音を鳴らす
        if( judgeState == JUDGE_PERFECT || judgeState == JUDGE_GREAT || judgeState == JUDGE_GOOD || judgeState == JUDGE_AUTO ) {
            if( judgeState != JUDGE_AUTO ) {
                player.startManualAudio(pitch, scoreKind);  // 手動演奏用のClip枠

                // キーボードのメモリ[楽譜の種別]にキー押下[音程]を設定し、[楽譜の種別]側のキーボードアニメーションを開始
                keyboard.memoryPushKey(scoreKind, pitch);
                drawer.startKeyboardAnimTimer(pitch);

                timings.add(remainTime);
            }
            else {
                player.startAutoAudio(pitch, scoreKind);    // 自動再生用のClip枠
            }
        }
        // OOPS なら間違った音を鳴らす
        else if( judgeState == JUDGE_OOPS ) {
            int randomPitch = player.randomizePitch(pitch);  // 音程のランダム化
            player.startManualAudio(randomPitch, scoreKind); // 手動演奏用のClip枠

            // キーボードのメモリ[楽譜の種別]にキー押下[音程]を設定し、[楽譜の種別]側キーボードアニメーションを開始
            keyboard.memoryPushKey(scoreKind, randomPitch);
            drawer.startKeyboardAnimTimer(randomPitch);
        }

        // コンボの処理 PERFECT, GREAT, GOODなら加算、POORなら0にする
        comboCount = switch (judgeState) {
            case JUDGE_PERFECT, JUDGE_GREAT, JUDGE_GOOD
                    -> comboCount + 1;
            case JUDGE_MISS
                    -> 0;
            default
                    -> comboCount;
        };
        if(comboCount > comboMax) {
            comboMax = comboCount;
        }

        // OOPS判定以外なら、判定対象ノーツを削除する
        if(judgeState != JUDGE_OOPS){
            score.remove(0);
        }
    }

    // 判定を分類する
    private int getJudge(int remainTime, boolean auto) {
        int judgeState;
        if( !auto ){
            // 遅判定(負) ≦ 残り時間 ≦ 早判定(正)
            // 判定範囲のいい感じの書き方が思いつかずこうなった
            if(judgeArea[jPerfectSlow] <= remainTime && remainTime <= judgeArea[jPerfectFast]) {
                judgeState = JUDGE_PERFECT;
            } else if(judgeArea[jGreatSlow] <= remainTime && remainTime <= judgeArea[jGreatFast]) {
                judgeState = JUDGE_GREAT;
            } else if(judgeArea[jGoodSlow] <= remainTime && remainTime <= judgeArea[jGoodFast]) {
                judgeState = JUDGE_GOOD;
            } else if(0 < remainTime) { // judgeArea[jGoodFast] < remainTime
                judgeState = JUDGE_OOPS;
            } else {                    // remainTime < judgeArea[jGoodSlow]
                judgeState = JUDGE_MISS;
            }
        }
        // 自動演奏時は無条件でAUTO
        else {
            judgeState = JUDGE_AUTO;
        }
        return judgeState;
    }

    // 判定状態の設定
    private void setJudgeState(int j) {
        judgeState = j;
    }
    // 直前の判定状態の取得
    public int getJudgeState() {
        return judgeState;
    }

    // ミス判定境界
    public int getMissLimit() {
        return judgeArea[jGoodSlow] - 1;
    }

    // 判定回数の取得(配列)
    public int[] getJudgeCount() {
        return judgeCount;
    }

    // コンボ数の取得
    public int getCombo() {
        return comboCount;
    }
    public int getMaxCombo() {
        return comboMax;
    }

    // 達成率の算出
    public int getAchievementPoint() {
        int achievementPoint = 0;
        for(int j = 0; j < judgeCount.length - 1; j++) {
            achievementPoint += achievementAllotPoint[j] * judgeCount[j];
        }
        return achievementPoint;
    }
    private int getJudgeSum() {
        int judgeSum = 0;
        for(int j = 0; j < judgeCount.length - 1; j++) {
            judgeSum += judgeCount[j]; // OOPSを含める
        }
        return judgeSum;
    }
    public float getAchievement() {
        int achievementPoint = getAchievementPoint();
        int judgeSum = getJudgeSum();
        return judgeSum != 0 ? (float) achievementPoint / judgeSum : 0.0F;
    }
    public String getAchievementStr(float achievement, String hd, String tl) {
        int acvInt = (int) achievement;
        int acvUdt = calc.getDotUnder(achievement, 2);
        String acvUdt2 = calc.paddingZero(acvUdt, 2);
        return hd + "：" + acvInt + "." + acvUdt2 + tl;
    }

    // タイミング平均
    public float getTimingAverage() {
        int timingSum = timings.stream()
                .mapToInt(intValue -> intValue)
                .sum();
        return calc.div(timingSum, timings.size(), 2);
    }
    // タイミング標準偏差[ms]
    public float getTimingSTDEV() {
        float timingAverage = getTimingAverage();
        double varianceMulJ = 0.0F;
        for(int timing : timings) {
            varianceMulJ += calc.pow2((float) timing - timingAverage);
        }
        return (float) Math.sqrt(varianceMulJ / timings.size() );
    }

    // 無音の再生
    public void startNoSound() {
        player.startNoSound();
    }
}
