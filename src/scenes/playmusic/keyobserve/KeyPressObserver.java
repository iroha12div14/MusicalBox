package scenes.playmusic.keyobserve;

import scenes.playmusic.judge.JudgeUtil;
import scenes.playmusic.note.NoteObject;

import java.util.List;

/**
 * キー入力の監視と判定発生をチェック
 */
public class KeyPressObserver {
    JudgeUtil judgeUtil;

    private final static int MAIN_SCORE = 0;
    private final static int SUB_SCORE = 1;

    private static final int NONE      = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int BOTH_PART = 3;

    // コン
    public KeyPressObserver(JudgeUtil judgeUtil) {
        this.judgeUtil = judgeUtil;
    }

    /**
     * キー押下監視
     * @param assignKeyPress    そのパートに充てられたキーのどれか1つでも押しているか
     * @param playPart          演奏しているパート
     * @param score             楽譜
     * @param scoreKind         楽譜のパート種別
     * @param nowTime           現時刻
     * @param judgeAuto         自動再生か(AUTO判定表示の有無)
     */
    public void observeKeyPress(
            boolean assignKeyPress, int playPart, List<NoteObject> score,
            int scoreKind, int nowTime, boolean judgeAuto
    ) {
        // そのパートが自動演奏されているかの有無
        boolean autoPlayPart
                =  playPart == NONE
                || playPart == SUB_PART  && scoreKind == MAIN_SCORE
                || playPart == MAIN_PART && scoreKind == SUB_SCORE;

        // 画面上にノーツが存在する場合
        if( !score.isEmpty() ){
            NoteObject note = score.get(0);         // ノートオブジェクト
            int arriveTime  = note.arriveTime();    // ノートの判定線到達時刻
            int pitch = note.pitch();               // 音程
            int remainTime = arriveTime - nowTime;  // 正なら判定線未到達、負なら到達済み

            // 条件1: キー押下時(PREFECT, GREAT, GOOD, OOPS)で、なおかつ演奏するパートである
            boolean terms1 = assignKeyPress && !autoPlayPart;

            // 条件2: キー非押下時(MISS, AUTO)で、なおかつ
            boolean terms2 = !assignKeyPress;
            // 条件2a: 自動演奏されるパートで判定線と重なる、もしくは
            // 条件2b: 演奏するパートで一定時間を過ぎた
            boolean terms2a = autoPlayPart && remainTime <= 0;
            boolean terms2b = !autoPlayPart && remainTime <= judgeUtil.getLostLimit();

            // 条件1 もしくは 条件2かつ(条件2aまたは条件2b) を満たすとき、ノートに対する判定が発生する
            if ( terms1 || terms2 && ( terms2a || terms2b ) ) {
                judgeUtil.judgeNote(score, remainTime, pitch, scoreKind, autoPlayPart, judgeAuto);
            }
        }
    }
}
