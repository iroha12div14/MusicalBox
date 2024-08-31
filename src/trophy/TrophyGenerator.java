package trophy;

import data.DataCaster;
import data.DataElements;

import java.util.*;

public class TrophyGenerator {
    /**
     * 未取得トロフィーの一覧に追加する
     * @param ownTrophies 取得済みトロフィー
     * @param getTrophies 追加される未取得トロフィーの一覧
     * @param terms トロフィーの取得条件
     * @param trophy 対象のトロフィー
     */
    private void addTrophy(List<Integer> ownTrophies, List<Integer> getTrophies, boolean terms, int trophy) {
        if(terms && !ownTrophies.contains(trophy) ) {
            getTrophies.add(trophy);
        }
    }

    /**
     * トロフィーの獲得の有無を全部網羅してチェックする。
     * あとは返した側でdataに書き込んだり、外部テキストに出力したり、これをキーとしてトロフィー名を画面に表示したり
     * @param maxCombo 最大コンボ
     * @param judgeCount 判定数
     * @param achievement 達成率
     * @param allPlayCount 累計演奏ゲーム回数
     * @param achievementPoint Achievement Point
     * @param data data
     * @return 獲得したトロフィーのリスト（整数値リスト）
     */
    public List<Integer> getTrophyByResult(
            int maxCombo,               // 最大コンボ
            int[] judgeCount,           // 判定数
            float achievement,          // 達成率
            int allPlayCount,           // 累計演奏ゲーム回数
            int achievementPoint,       // Achievement Point
            Map<Integer, Object> data   // data
    ) {
        List<Integer> ownTrophies = cast.getIntListData(data, elem.TROPHY);
        List<Integer> getTrophies = new ArrayList<>();

        // 最大コンボ系
        addTrophy(ownTrophies, getTrophies, isMaxCombo30Plus(maxCombo), TrophyList.MAX_COMBO_30PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo100Plus(maxCombo), TrophyList.MAX_COMBO_100PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo300Plus(maxCombo), TrophyList.MAX_COMBO_300PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo1000Plus(maxCombo), TrophyList.MAX_COMBO_1000PLUS);

        // 判定数系
        addTrophy(ownTrophies, getTrophies, isPerfectJudge30Plus(judgeCount), TrophyList.PERFECT_30PLUS);
        addTrophy(ownTrophies, getTrophies, isPerfectJudge100Plus(judgeCount), TrophyList.PERFECT_100PLUS);
        addTrophy(ownTrophies, getTrophies, isPerfectJudge300Plus(judgeCount), TrophyList.PERFECT_300PLUS);
        addTrophy(ownTrophies, getTrophies, isPerfectJudge1000Plus(judgeCount), TrophyList.PERFECT_1000PLUS);
        addTrophy(ownTrophies, getTrophies,
                isMissJudge1000PlusAndMaxCombo0(maxCombo, judgeCount),
                TrophyList.MISS_1000PLUS_MAX_COMBO_0
        );

        // フルコン系
        addTrophy(ownTrophies, getTrophies,
                isFCAndAcv98PerPlus(judgeCount, achievement),
                TrophyList.FC_ACV_98PLUS
        );
        addTrophy(ownTrophies, getTrophies, isAcv100Per(achievement), TrophyList.ACV_100);
        addTrophy(ownTrophies, getTrophies,
                isGachaPlay(judgeCount, achievement),
                TrophyList.FC_ACV_LT5_OOPS_1000PLUS
        );
        addTrophy(ownTrophies, getTrophies,
                isJudgeLineInBrazil(judgeCount, achievement),
                TrophyList.FC_ACV_LT25_OOPS_0
        );

        // やりこみ系
        addTrophy(ownTrophies, getTrophies, isAllPlayCount10plus(allPlayCount), TrophyList.ALL_PLAY_COUNT_10PLUS);
        addTrophy(ownTrophies, getTrophies, isAllPlayCount100plus(allPlayCount), TrophyList.ALL_PLAY_COUNT_100PLUS);
        addTrophy(ownTrophies, getTrophies, isAllPlayCount1000plus(allPlayCount), TrophyList.ALL_PLAY_COUNT_1000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint10000plus(achievementPoint), TrophyList.ACV_POINT_10000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint100000plus(achievementPoint), TrophyList.ACV_POINT_100000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint1000000plus(achievementPoint), TrophyList.ACV_POINT_1000000PLUS);


        return getTrophies;
    }

    /**
     * 曲別のトロフィーを取得した場合、ハッシュ値を返す
     * @param hash ハッシュ値
     * @param judgeCount 判定数
     * @param achievement 達成率
     * @param playPart 演奏パート
     * @return ハッシュ値（文字列、取得していないなら""になる）
     */
    public String getTrophyOfMusic(
            Map<Integer, Object> data,
            String hash,
            int[] judgeCount,
            float achievement,
            int playPart
    ) {
        List<String> musicTrophies = cast.getStrListData(data, elem.MUSIC_TROPHY);
        if(!musicTrophies.contains(hash) && isAllPartFCAndAcv90PerPlus(judgeCount, achievement, playPart) ) {
            return hash;
        } else {
            return NONE;
        }
    }

    public boolean isNONE(String str) {
        return str.equals(NONE);
    }

    // ------------------------------------------ //
    // リザルトでチェックする系の実績

    // 最大コンボ系
    public boolean isMaxCombo30Plus(int maxCombo) {
        return maxCombo >= 30;
    }
    public boolean isMaxCombo100Plus(int maxCombo) {
        return maxCombo >= 100;
    }
    public boolean isMaxCombo300Plus(int maxCombo) {
        return maxCombo >= 300;
    }
    public boolean isMaxCombo1000Plus(int maxCombo) {
        return maxCombo >= 1000;
    }

    // 判定数系
    public boolean isPerfectJudge30Plus(int[] judgeCount) {
        return judgeCount[JUDGE_PERFECT] >= 30;
    }
    public boolean isPerfectJudge100Plus(int[] judgeCount) {
        return judgeCount[JUDGE_PERFECT] >= 100;
    }
    public boolean isPerfectJudge300Plus(int[] judgeCount) {
        return judgeCount[JUDGE_PERFECT] >= 300;
    }
    public boolean isPerfectJudge1000Plus(int[] judgeCount) {
        return judgeCount[JUDGE_PERFECT] >= 1000;
    }
    public boolean isMissJudge1000PlusAndMaxCombo0(int maxCombo, int[] judgeCount) {
        return judgeCount[JUDGE_MISS] >= 1000 && maxCombo == 0;
    }

    // フルコン系
    private boolean isFullCombo(int[] judgeCount) {
        int missJudge = judgeCount[JUDGE_MISS];
        return missJudge == 0;
    }
    public boolean isFCAndAcv98PerPlus(int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(judgeCount);
        return isFC && achievement >= 98.00F;
    }
    public boolean isAcv100Per(float achievement) {
        return achievement == 100.00F;
    }
    public boolean isGachaPlay(int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(judgeCount);
        return isFC && achievement < 5.00F && judgeCount[JUDGE_OOPS] >= 999;
    }
    public boolean isJudgeLineInBrazil(int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(judgeCount);
        return isFC && achievement < 25.00F && judgeCount[JUDGE_OOPS] == 0;
    }

    // （曲別）メロディ＆伴奏パートで達成率90%以上のフルコン
    public boolean isAllPartFCAndAcv90PerPlus(int[] judgeCount, float achievement, int playPart) {
        boolean isFC = isFullCombo(judgeCount);
        return isFC && achievement >= 90.00F & playPart == BOTH_PART;
    }

    // やり込み系
    public boolean isAllPlayCount10plus(int allPlayCount) {
        return allPlayCount >= 10;
    }
    public boolean isAllPlayCount100plus(int allPlayCount) {
        return allPlayCount >= 100;
    }
    public boolean isAllPlayCount1000plus(int allPlayCount) {
        return allPlayCount >= 1000;
    }
    public boolean isAcvPoint10000plus(int achievementPoint) {
        return achievementPoint >= 10000;
    }
    public boolean isAcvPoint100000plus(int achievementPoint) {
        return achievementPoint >= 100000;
    }
    public boolean isAcvPoint1000000plus(int achievementPoint) {
        return achievementPoint >= 1000000;
    }

    // ------------------------------------------ //

    public final String NONE = "";

    //
    private final DataCaster cast = new DataCaster();
    private final DataElements elem = new DataElements();

    // 判定状態
    private static final int JUDGE_PERFECT = 0;
    private static final int JUDGE_GREAT   = 1;
    private static final int JUDGE_GOOD    = 2;
    private static final int JUDGE_OOPS    = 3;
    private static final int JUDGE_MISS    = 4;
    private static final int JUDGE_AUTO    = 5;

    // 演奏パート
    private static final int BOTH_PART = 3;

}
