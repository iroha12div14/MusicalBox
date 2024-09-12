package scenes.playmusic.timeline;

import java.util.List;
import java.util.Map;

/**
 * シーケンス情報を取得する
 */
public class SequenceGetter {
    private static final int UNDEFINED  = -1;

    /**
     * シーケンスデータから自動再生時にキー音が鳴動する回数を数える（音源コンテナのクリップ確保数の算出用）
     * @param sequence      シーケンス
     * @param correction    ノートの種類の一覧
     * @return 鳴動回数
     */
    public int[] getScorePlayCount(
            List<Map<String, Integer>> sequence,
            List<String> correction
    ) {
        int[] ScorePlayCount = getScorePlayCountEmpty();
        for(Map<String, Integer> notes : sequence) {
            for(String s : correction ) {
                int pitch = notes.get(s);
                if(pitch != UNDEFINED) {
                    ScorePlayCount[pitch]++;
                }
            }
        }
        return ScorePlayCount;
    }
    public int[] getScorePlayCountEmpty() {
        return new int[32]; // これで全部0が補完されるらしい
    }
}
