package playmusic.timeline;

import findstr.FindStrUtil;

import java.util.List;
import java.util.Map;

public class SequenceGetter {
    private final FindStrUtil fsu = new FindStrUtil();
    int pitchCount = 32; // 現状containerからしか取得できない矛盾が起きているので直値を入れている

    public int[] getScorePlayCount(
            List<Map<String, Integer>> sequence,
            List<String> correction
    ) {
        int[] ScorePlayCount = getScorePlayCountEmpty();
        for(Map<String, Integer> notes : sequence) {
            for(String s : correction ) {
                int pitch = notes.get(s);
                if( !fsu.isNotFind(pitch) ) {
                    ScorePlayCount[pitch]++;
                }
            }
        }
        return ScorePlayCount;
    }
    public int[] getScorePlayCountEmpty() {
        return new int[pitchCount]; // これで全部0が補完されるらしい
    }
}
