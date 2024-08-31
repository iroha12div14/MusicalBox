package trophy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrophyList {
    public final static int NONE    = 0;

    // [1001 - 1199] 判定数
    public final static int MAX_COMBO_30PLUS    = 1001;
    public final static int MAX_COMBO_100PLUS   = 1002;
    public final static int MAX_COMBO_300PLUS   = 1003;
    public final static int MAX_COMBO_1000PLUS  = 1004;

    public final static int PERFECT_30PLUS      = 1011;
    public final static int PERFECT_100PLUS     = 1012;
    public final static int PERFECT_300PLUS     = 1013;
    public final static int PERFECT_1000PLUS    = 1014;

    public final static int MISS_1000PLUS_MAX_COMBO_0 = 1101;

    // [1201 - 1399] フルコンボ + 達成率や判定数
    public final static int FC_ACV_98PLUS   = 1201;
    public final static int ACV_100         = 1202;

    public final static int FC_ACV_LT5_OOPS_1000PLUS    = 1311;
    public final static int FC_ACV_LT25_OOPS_0          = 1312;

    // [2001 - 2099] 累積ポイント、累積回数
    public final static int ALL_PLAY_COUNT_10PLUS   = 2001;
    public final static int ALL_PLAY_COUNT_100PLUS  = 2002;
    public final static int ALL_PLAY_COUNT_1000PLUS = 2003;

    public final static int ACV_POINT_10000PLUS     = 2011;
    public final static int ACV_POINT_100000PLUS    = 2012;
    public final static int ACV_POINT_1000000PLUS   = 2013;

    public final static List<Integer> member = Arrays.asList(
            MAX_COMBO_30PLUS, MAX_COMBO_100PLUS, MAX_COMBO_300PLUS, MAX_COMBO_1000PLUS,
            PERFECT_30PLUS, PERFECT_100PLUS, PERFECT_300PLUS, PERFECT_1000PLUS,
            MISS_1000PLUS_MAX_COMBO_0,
            FC_ACV_98PLUS, ACV_100,
            FC_ACV_LT5_OOPS_1000PLUS, FC_ACV_LT25_OOPS_0,
            ALL_PLAY_COUNT_10PLUS, ALL_PLAY_COUNT_100PLUS, ALL_PLAY_COUNT_1000PLUS,
            ACV_POINT_10000PLUS, ACV_POINT_100000PLUS, ACV_POINT_1000000PLUS
    );

    // ---------------------------------------------------- //
    // トロフィーの獲得条件文とトロフィー名

    private static final Map<Integer, String> generalTrophyStr = new HashMap<>();
    private static final Map<String, String> musicTrophyStr = new HashMap<>();
    private static final Map<Integer, String> generalTrophyTermsStr = new HashMap<>();
    private static final Map<Integer, String> generalTrophyMaskedTermsStr = new HashMap<>();
    private static final String musicTrophyTermsStr = "（楽曲別）メロディ＆伴奏パートで、達成率90%以上のフルコンボを達成";

    static {
        generalTrophyTermsStr.put(NONE, "");

        generalTrophyTermsStr.put(MAX_COMBO_30PLUS, "最大コンボ30回以上を達成");
        generalTrophyTermsStr.put(MAX_COMBO_100PLUS, "最大コンボ100回以上を達成");
        generalTrophyTermsStr.put(MAX_COMBO_300PLUS, "最大コンボ300回以上を達成");
        generalTrophyTermsStr.put(MAX_COMBO_1000PLUS, "最大コンボ1000回以上を達成");

        generalTrophyTermsStr.put(PERFECT_30PLUS, "PERFECT判定30回以上を達成");
        generalTrophyTermsStr.put(PERFECT_100PLUS, "PERFECT判定100回以上を達成");
        generalTrophyTermsStr.put(PERFECT_300PLUS, "PERFECT判定300回以上を達成");
        generalTrophyTermsStr.put(PERFECT_1000PLUS, "PERFECT判定1000回以上を達成");

        generalTrophyTermsStr.put(MISS_1000PLUS_MAX_COMBO_0, "MISS判定1000回以上、かつ最大コンボが0回");

        generalTrophyTermsStr.put(FC_ACV_98PLUS, "任意の楽曲で達成率98％以上、かつフルコンボを達成");
        generalTrophyTermsStr.put(ACV_100, "任意の楽曲で達成率100％を達成");
        generalTrophyTermsStr.put(FC_ACV_LT5_OOPS_1000PLUS, "任意の楽曲で達成率5％未満、かつOOPS判定1000回以上でフルコンボを達成");
        generalTrophyTermsStr.put(FC_ACV_LT25_OOPS_0, "任意の楽曲で達成率25％未満、かつOOPS判定0回でフルコンボを達成");

        generalTrophyTermsStr.put(ALL_PLAY_COUNT_10PLUS, "演奏ゲームを10回終える");
        generalTrophyTermsStr.put(ALL_PLAY_COUNT_100PLUS, "演奏ゲームを100回終える");
        generalTrophyTermsStr.put(ALL_PLAY_COUNT_1000PLUS, "演奏ゲームを1000回終える");

        generalTrophyTermsStr.put(ACV_POINT_10000PLUS, "Achievement Pointを10000以上貯める");
        generalTrophyTermsStr.put(ACV_POINT_100000PLUS, "Achievement Pointを100000以上貯める");
        generalTrophyTermsStr.put(ACV_POINT_1000000PLUS, "Achievement Pointを1000000以上貯める");


        generalTrophyMaskedTermsStr.put(MAX_COMBO_300PLUS, "最大コンボ[？]回以上を達成");
        generalTrophyMaskedTermsStr.put(MAX_COMBO_1000PLUS, "最大コンボ[？]回以上を達成");
        generalTrophyMaskedTermsStr.put(PERFECT_300PLUS, "PERFECT判定[？]回以上を達成");
        generalTrophyMaskedTermsStr.put(PERFECT_1000PLUS, "PERFECT判定[？]回以上を達成");
        generalTrophyMaskedTermsStr.put(MISS_1000PLUS_MAX_COMBO_0, "MISS判定[？？？]、かつ最大コンボ[？？？]");
        generalTrophyMaskedTermsStr.put(FC_ACV_LT5_OOPS_1000PLUS, "任意の楽曲で達成率[？？？]、かつOOPS判定[？？？]でフルコンボを達成");
        generalTrophyMaskedTermsStr.put(FC_ACV_LT25_OOPS_0, "任意の楽曲で達成率[？？？]、かつOOPS判定[？？？]でフルコンボを達成");
        generalTrophyMaskedTermsStr.put(ALL_PLAY_COUNT_10PLUS, "[？？？？？]");
        generalTrophyMaskedTermsStr.put(ALL_PLAY_COUNT_100PLUS, "[？？？？？]");
        generalTrophyMaskedTermsStr.put(ALL_PLAY_COUNT_1000PLUS, "[？？？？？]");
        generalTrophyMaskedTermsStr.put(ACV_POINT_10000PLUS, "[？？？？？]");
        generalTrophyMaskedTermsStr.put(ACV_POINT_100000PLUS, "[？？？？？]");
        generalTrophyMaskedTermsStr.put(ACV_POINT_1000000PLUS, "[？？？？？]");


        generalTrophyStr.put(NONE, "");

        generalTrophyStr.put(MAX_COMBO_30PLUS, "これならいける");
        generalTrophyStr.put(MAX_COMBO_100PLUS, "まだまだいける");
        generalTrophyStr.put(MAX_COMBO_300PLUS, "ここまでいける");
        generalTrophyStr.put(MAX_COMBO_1000PLUS, "どこまでもいける");

        generalTrophyStr.put(PERFECT_30PLUS, "立ち上げる");
        generalTrophyStr.put(PERFECT_100PLUS, "積み上げる");
        generalTrophyStr.put(PERFECT_300PLUS, "築き上げる");
        generalTrophyStr.put(PERFECT_1000PLUS, "磨き上げる");

        generalTrophyStr.put(MISS_1000PLUS_MAX_COMBO_0, "芸術鑑賞");

        generalTrophyStr.put(FC_ACV_98PLUS, "得意");
        generalTrophyStr.put(ACV_100, "仰せのままに");
        generalTrophyStr.put(FC_ACV_LT5_OOPS_1000PLUS, "過剰入力遊戯");
        generalTrophyStr.put(FC_ACV_LT25_OOPS_0, "判定がブラジル");

        generalTrophyStr.put(ALL_PLAY_COUNT_10PLUS, "ハマってる？");
        generalTrophyStr.put(ALL_PLAY_COUNT_100PLUS, "ハマってる");
        generalTrophyStr.put(ALL_PLAY_COUNT_1000PLUS, "製作者冥利に尽きる");

        generalTrophyStr.put(ACV_POINT_10000PLUS, "やり込んでる？");
        generalTrophyStr.put(ACV_POINT_100000PLUS, "やり込んでる");
        generalTrophyStr.put(ACV_POINT_1000000PLUS, "やり過ぎ");


        // 民謡・古謡・童謡
        musicTrophyStr.put("db88cc9ab433743771b73c1669e9141b7fe66a3a118747ad7f04d164443fa573", "あめあめふれふれ");
        musicTrophyStr.put("c14685b93f8a68fdaf82edb3b33326d7f5506a544b210066c634596eb15eee52", "100年休まずに");
        musicTrophyStr.put("3ea053132bd6b37044cbcdd30c8e1aa922fc1468231e8e8d2b428ca3b31d842a", "お空の星よ");
        musicTrophyStr.put("2ce4abf8950045d0fe121b38be3d9fbdf0c6e9b0099bc8b0dc3c061c510131bd", "窓の雪");
        musicTrophyStr.put("1e1de9f3d52f8a51b636fc4542c0395bbe624f5c43c18a7b107730b56c06b392", "屋根より高い");
        musicTrophyStr.put("6742c8f970c9da4f868197fd3fba49b1a77ba8f382fa282611f1fb5e3237730d", "夕焼け小焼け");
        musicTrophyStr.put("2c8a0ab5aade7f147b79a6159f84e87420b02fc0f2251ddccf878240021a47bf", "八十八夜");
        musicTrophyStr.put("e5bd42687aeaf5f6718993a497a45a5ea92a113585fe08b978f601efbb988768", "まだ降りやまぬ");
        musicTrophyStr.put("2049fb3966a1ac27aaa8728a4803174b86ef21c94c58167696ad8c8ca400a994", "いざや見に行かん");

        // 自作曲
        musicTrophyStr.put("ed2de0633e0d4d07ea9abf1bdb11cc7847080f119ef2ac00e8d2eb59e166338a", "心のカタチ");

        // クラシック
        musicTrophyStr.put("7cd8ece334d16404e177dd9c587b1f783f5bf30236077c4b2f671d5541b1c356", "海を渡って");

    }

    // ---------------------------------------------------- //
    public static String getGenTrophy(int key) {
        return generalTrophyStr.get(key);
    }
    public static String getMusicTrophy(String key) {
        return musicTrophyStr.get(key);
    }
    public static Map<String, String> getMusicTrophy() {
        return musicTrophyStr;
    }
    public static String getGenTrophyTerms(int key) {
        return generalTrophyTermsStr.get(key);
    }
    public static String getGenTrophyMaskedTerms(int key) {
        return generalTrophyMaskedTermsStr.get(key);
    }
    public static String getMusicTrophyTermsStr() {
        return musicTrophyTermsStr;
    }
    public static int getMusicTrophyCount() {
        return musicTrophyStr.size();
    }

}
