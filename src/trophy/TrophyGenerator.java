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
     * @param musicCount 楽曲数
     * @param data data
     * @return 獲得したトロフィーのリスト（整数値リスト）
     */
    public List<Integer> getTrophyByResult(
            int maxCombo,               // 最大コンボ
            int[] judgeCount,           // 判定数
            float achievement,          // 達成率
            int allPlayCount,           // 累計演奏ゲーム回数
            int achievementPoint,       // Achievement Point
            int musicCount,             // 楽曲数
            Map<Integer, Object> data   // data
    ) {
        List<Integer> ownTrophies = cast.getIntListData(data, elem.TROPHIES);
        List<Integer> getTrophies = new ArrayList<>();

        // 最大コンボ系
        addTrophy(ownTrophies, getTrophies, isMaxCombo30Plus(maxCombo), Trophies.MAX_COMBO_30PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo100Plus(maxCombo), Trophies.MAX_COMBO_100PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo300Plus(maxCombo), Trophies.MAX_COMBO_300PLUS);
        addTrophy(ownTrophies, getTrophies, isMaxCombo1000Plus(maxCombo), Trophies.MAX_COMBO_1000PLUS);

        // 判定数系
        addTrophy(ownTrophies, getTrophies, isPerfectJudge100Plus(judgeCount), Trophies.PERFECT_100PLUS);
        addTrophy(ownTrophies, getTrophies, isPerfectJudge300Plus(judgeCount), Trophies.PERFECT_300PLUS);
        addTrophy(ownTrophies, getTrophies, isPerfectJudge1000Plus(judgeCount), Trophies.PERFECT_1000PLUS);
        addTrophy(ownTrophies, getTrophies,
                isMissJudge1000PlusAndMaxCombo0(maxCombo, judgeCount),
                Trophies.MISS_1000PLUS_MAX_COMBO_0
        );

        // フルコン系
        addTrophy(ownTrophies, getTrophies,
                isFCAndAcv98PerPlus(maxCombo, judgeCount, achievement),
                Trophies.FC_ACV_98PLUS
        );
        addTrophy(ownTrophies, getTrophies, isAcv100Per(achievement), Trophies.ACV_100);
        addTrophy(ownTrophies, getTrophies,
                isGachaPlay(maxCombo, judgeCount, achievement),
                Trophies.FC_ACV_LT5_OOPS_1000PLUS
        );
        addTrophy(ownTrophies, getTrophies,
                isJudgeLineInBrazil(maxCombo, judgeCount, achievement),
                Trophies.FC_ACV_LT25_OOPS_0
        );

        // やりこみ系
        addTrophy(ownTrophies, getTrophies, isAllPlayCount10plus(allPlayCount), Trophies.ALL_PLAY_COUNT_10PLUS);
        addTrophy(ownTrophies, getTrophies, isAllPlayCount100plus(allPlayCount), Trophies.ALL_PLAY_COUNT_100PLUS);
        addTrophy(ownTrophies, getTrophies, isAllPlayCount1000plus(allPlayCount), Trophies.ALL_PLAY_COUNT_1000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint10000plus(achievementPoint), Trophies.ACV_POINT_10000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint100000plus(achievementPoint), Trophies.ACV_POINT_100000PLUS);
        addTrophy(ownTrophies, getTrophies, isAcvPoint1000000plus(achievementPoint), Trophies.ACV_POINT_1000000PLUS);


        return getTrophies;
    }

    /**
     * 曲別のトロフィーを取得した場合、ハッシュ値を返す
     * @param hash ハッシュ値
     * @param maxCombo 最大コンボ
     * @param judgeCount 判定数
     * @param achievement 達成率
     * @param playPart 演奏パート
     * @return ハッシュ値（文字列、取得していないなら""になる）
     */
    public String getTrophyOfMusic(
            Map<Integer, Object> data,
            String hash,
            int maxCombo,
            int[] judgeCount,
            float achievement,
            int playPart
    ) {
        List<String> musicTrophies = cast.getStrListData(data, elem.MUSIC_TROPHIES);
        if(!musicTrophies.contains(hash) && isAllPartFCAndAcv90PerPlus(maxCombo, judgeCount, achievement, playPart) ) {
            return hash;
        } else {
            return NONE;
        }
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
    private boolean isFullCombo(int maxCombo, int[] judgeCount) {
        int perfectJudge = judgeCount[JUDGE_PERFECT];
        int greatJudge = judgeCount[JUDGE_GREAT];
        int goodJudge = judgeCount[JUDGE_GOOD];
        return perfectJudge + greatJudge + goodJudge == maxCombo;
    }
    public boolean isFCAndAcv98PerPlus(int maxCombo, int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(maxCombo, judgeCount);
        return isFC && achievement >= 98.00F;
    }
    public boolean isAcv100Per(float achievement) {
        return achievement == 100.00F;
    }
    public boolean isGachaPlay(int maxCombo, int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(maxCombo, judgeCount);
        return isFC && achievement < 5.00F && judgeCount[JUDGE_OOPS] >= 999;
    }
    public boolean isJudgeLineInBrazil(int maxCombo, int[] judgeCount, float achievement) {
        boolean isFC = isFullCombo(maxCombo, judgeCount);
        return isFC && achievement < 25.00F && judgeCount[JUDGE_OOPS] == 0;
    }

    // （曲別）メロディ＆伴奏パートで達成率90%以上のフルコン
    public boolean isAllPartFCAndAcv90PerPlus(int maxCombo, int[] judgeCount, float achievement, int playPart) {
        boolean isFC = isFullCombo(maxCombo, judgeCount);
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

    private static final String NONE = "";

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

    // ------------------------------------------ //
    // トロフィーの獲得条件文とトロフィー名

    private static final Map<Integer, String> generalTrpStr = new HashMap<>();
    private static final Map<String, String> musicTrpStr = new HashMap<>();
    private static final Map<Integer, String> generalTrpTermsStr = new HashMap<>();

    static {
        generalTrpTermsStr.put(Trophies.NONE, NONE);

        generalTrpTermsStr.put(Trophies.MAX_COMBO_30PLUS, "最大コンボ30回以上を達成");
        generalTrpTermsStr.put(Trophies.MAX_COMBO_100PLUS, "最大コンボ100回以上を達成");
        generalTrpTermsStr.put(Trophies.MAX_COMBO_300PLUS, "最大コンボ300回以上を達成");
        generalTrpTermsStr.put(Trophies.MAX_COMBO_1000PLUS, "最大コンボ1000回以上を達成");

        generalTrpTermsStr.put(Trophies.PERFECT_100PLUS, "PERFECT判定100回以上を達成");
        generalTrpTermsStr.put(Trophies.PERFECT_300PLUS, "PERFECT判定300回以上を達成");
        generalTrpTermsStr.put(Trophies.PERFECT_1000PLUS, "PERFECT判定1000回以上を達成");

        generalTrpTermsStr.put(Trophies.MISS_1000PLUS_MAX_COMBO_0, "MISS判定1000回以上、かつ最大コンボが0回");

        generalTrpTermsStr.put(Trophies.FC_ACV_98PLUS, "任意の楽曲で達成率98％以上、かつフルコンボを達成");
        generalTrpTermsStr.put(Trophies.ACV_100, "任意の楽曲で達成率100％を達成");
        generalTrpTermsStr.put(Trophies.FC_ACV_LT5_OOPS_1000PLUS, "任意の楽曲で達成率5％未満、かつOOPS判定1000回以上でフルコンボを達成");
        generalTrpTermsStr.put(Trophies.FC_ACV_LT25_OOPS_0, "任意の楽曲で達成率25％未満、かつOOPS判定0回でフルコンボを達成");

        generalTrpTermsStr.put(Trophies.ALL_PLAY_COUNT_10PLUS, "演奏ゲームを10回終える");
        generalTrpTermsStr.put(Trophies.ALL_PLAY_COUNT_100PLUS, "演奏ゲームを100回終える");
        generalTrpTermsStr.put(Trophies.ALL_PLAY_COUNT_1000PLUS, "演奏ゲームを1000回終える");

        generalTrpTermsStr.put(Trophies.ACV_POINT_10000PLUS, "Achievement Pointを10000以上貯める");
        generalTrpTermsStr.put(Trophies.ACV_POINT_100000PLUS, "Achievement Pointを100000以上貯める");
        generalTrpTermsStr.put(Trophies.ACV_POINT_1000000PLUS, "Achievement Pointを1000000以上貯める");


        generalTrpStr.put(Trophies.NONE, NONE);

        generalTrpStr.put(Trophies.MAX_COMBO_30PLUS, "これならいける");
        generalTrpStr.put(Trophies.MAX_COMBO_100PLUS, "ここまでいける");
        generalTrpStr.put(Trophies.MAX_COMBO_300PLUS, "まだまだいける");
        generalTrpStr.put(Trophies.MAX_COMBO_1000PLUS, "どこまでもいける");

        generalTrpStr.put(Trophies.PERFECT_100PLUS, "積み上げる");
        generalTrpStr.put(Trophies.PERFECT_300PLUS, "築き上げる");
        generalTrpStr.put(Trophies.PERFECT_1000PLUS, "磨き上げる");

        generalTrpStr.put(Trophies.MISS_1000PLUS_MAX_COMBO_0, "芸術鑑賞");

        generalTrpStr.put(Trophies.FC_ACV_98PLUS, "得意");
        generalTrpStr.put(Trophies.ACV_100, "仰せのままに");
        generalTrpStr.put(Trophies.FC_ACV_LT5_OOPS_1000PLUS, "過剰入力遊戯");
        generalTrpStr.put(Trophies.FC_ACV_LT25_OOPS_0, "判定がブラジル");

        generalTrpStr.put(Trophies.ALL_PLAY_COUNT_10PLUS, "ハマってる？");
        generalTrpStr.put(Trophies.ALL_PLAY_COUNT_100PLUS, "ハマってる");
        generalTrpStr.put(Trophies.ALL_PLAY_COUNT_1000PLUS, "製作者冥利に尽きる");

        generalTrpStr.put(Trophies.ACV_POINT_10000PLUS, "やり込むでる？");
        generalTrpStr.put(Trophies.ACV_POINT_100000PLUS, "やり込んでる");
        generalTrpStr.put(Trophies.ACV_POINT_1000000PLUS, "やり過ぎ");


        // 民謡・古謡・童謡
        musicTrpStr.put("db88cc9ab433743771b73c1669e9141b7fe66a3a118747ad7f04d164443fa573", "あめあめふれふれ");
        musicTrpStr.put("c14685b93f8a68fdaf82edb3b33326d7f5506a544b210066c634596eb15eee52", "100年休まずに");
        musicTrpStr.put("3ea053132bd6b37044cbcdd30c8e1aa922fc1468231e8e8d2b428ca3b31d842a", "お空の星よ");
        musicTrpStr.put("2ce4abf8950045d0fe121b38be3d9fbdf0c6e9b0099bc8b0dc3c061c510131bd", "窓の雪");
        musicTrpStr.put("1e1de9f3d52f8a51b636fc4542c0395bbe624f5c43c18a7b107730b56c06b392", "屋根より高い");
        musicTrpStr.put("6742c8f970c9da4f868197fd3fba49b1a77ba8f382fa282611f1fb5e3237730d", "夕焼け小焼け");
        musicTrpStr.put("2c8a0ab5aade7f147b79a6159f84e87420b02fc0f2251ddccf878240021a47bf", "八十八夜");
        musicTrpStr.put("e5bd42687aeaf5f6718993a497a45a5ea92a113585fe08b978f601efbb988768", "まだ降りやまぬ");
        musicTrpStr.put("2049fb3966a1ac27aaa8728a4803174b86ef21c94c58167696ad8c8ca400a994", "いざや見に行かん");

        // 自作曲
        musicTrpStr.put("ed2de0633e0d4d07ea9abf1bdb11cc7847080f119ef2ac00e8d2eb59e166338a", "心のカタチ");

        // クラシック
        musicTrpStr.put("7cd8ece334d16404e177dd9c587b1f783f5bf30236077c4b2f671d5541b1c356", "海を渡って");
    }
}
