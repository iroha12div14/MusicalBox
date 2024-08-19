package playmusic.keyobserve;

import key.KeyController;
import playmusic.judge.JudgeUtil;
import playmusic.note.NoteObject;

import java.util.List;

public class KeyPressObserver {
    private final static int MAIN_SCORE = 0;
    private final static int SUB_SCORE = 1;

    private static final int NONE      = 0;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int ALL_PART  = 3;

    // キー押下監視
    public void observeKeyPress(
            KeyController key,      // キー操作クラス
            int[] assignKeys,       // そのパートの割り当てキー
            int playPart,           // 演奏しているパート
            List<NoteObject> score, // 楽譜
            int scoreKind,          // 楽譜のパート種別
            int nowTime,            // 現時刻
            JudgeUtil judgeUtil,    // ノート判定クラス
            boolean judgeAuto       // 自動再生か(AUTO判定表示の有無)
    ) {
        // そのパートが自動演奏されているかの有無
        boolean autoPlayPart
                =  playPart == NONE
                || playPart == SUB_PART  && scoreKind == MAIN_SCORE
                || playPart == MAIN_PART && scoreKind == SUB_SCORE;

        // 画面上にノーツが存在する場合
        if( !score.isEmpty() ){
            // そのパートに充てられたキーのどれか1つでも押しているなら、キー押下とみなす
            boolean assignKeyPress = false;
            for(int k : assignKeys) {
                if(key.getKeyPress(k) ) {
                    assignKeyPress = true;
                    break;
                }
            }
            NoteObject note = score.get(0);     // ノートオブジェクト
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
            boolean terms2b = !autoPlayPart && remainTime <= judgeUtil.getMissLimit();

            // 条件1 もしくは 条件2かつ(条件2aまたは条件2b) を満たすとき、ノートに対する判定が発生する
            if ( terms1 || terms2 && ( terms2a || terms2b ) ) {
                judgeUtil.judgeNote(
                        score,          // 楽譜
                        remainTime,     // 到達残り時間
                        pitch,          // 音程
                        scoreKind,      // 楽譜のパート種別
                        autoPlayPart,   // そのパートが自動演奏されているかの有無
                        judgeAuto       // 自動再生か(AUTO判定表示の有無)
                );
            }
        }
    }
}
