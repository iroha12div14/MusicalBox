package scenes.playmusic.timeline;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * パンチカードの使用文字をまとめたもの
 */
public class PunchCard {
    public static final String SPLIT_TOKEN = "#";
    public static final String PARAM_SPLIT_TOKEN = ",";

    public static final String TITLE = "TITLE";
    public static final String TEMPO = "TEMPO";
    public static final String LEVEL = "LEVEL";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String FILE_NAME = "FILE_NAME";

    public static final String MO = "O";
    public static final String MA = "A";
    public static final String MP = "P";
    public static final String MN = "N";
    public static final String SO = "o";
    public static final String SA = "a";
    public static final String SP = "p";
    public static final String SN = "n";

    public static final String TIME = "T";

    private static final int UNDEFINED = -1;

    /*
     * collection: 収集・収蔵 ←こっちのスペリング
     * correction: 修正・補正
     *
     * 読み取った順にListに挿入するので P -> O,A -> N の順で並べている
     */
    public List<String> collection() {
        return Arrays.asList(MP, MO, MA, MN, SP, SO, SA, SN);
    }
    public List<String> collectionMain() {
        return Arrays.asList(MP, MO, MA, MN);
    }
    public List<String> collectionSub() {
        return Arrays.asList(SP, SO, SA, SN);
    }

    /**
     * 文字情報からアルペジオ用の時刻補正に変換
     * @param str ノートの種類
     * @return 時刻補正の向き（負なら早い、正なら遅い）
     */
    public int StringToArpeggio(String str) {
        return switch (str) {
            case MP, SP -> -1;
            case MN, SN -> 1;
            default -> 0;
        };
    }

    /**
     * アルペジオノーツの結線先の音階
     * @param str   結線元ノートの種類（PかA）
     * @param note  その時刻におけるノーツ
     * @return 結線先ノートの音階
     */
    public int arpTo(String str, Map<String, Integer> note){
        return switch (str) {
            case MP -> note.get(MA);
            case MA -> note.get(MN);
            case SP -> note.get(SA);
            case SA -> note.get(SN);
            default -> UNDEFINED;
        };
    }
}
