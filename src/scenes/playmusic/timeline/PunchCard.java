package scenes.playmusic.timeline;

import scenes.playmusic.findstr.FindStrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// パンチカードの使用文字
public class PunchCard {
    public static final String SPLIT_TOKEN = "#";
    public static final String PARAM_SPLIT_TOKEN = ",";

    public static final String TITLE = "TITLE";
    public static final String TEMPO = "TEMPO";
    public static final String LEVEL = "LEVEL";
    public static final String SEQUENCE = "SEQUENCE";

    public static final String MO = "O";
    public static final String MA = "A";
    public static final String MP = "P";
    public static final String MN = "N";
    public static final String SO = "o";
    public static final String SA = "a";
    public static final String SP = "p";
    public static final String SN = "n";

    public static final String TIME = "T";

    private final FindStrUtil fsu = new FindStrUtil();

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

    // 文字情報からアルペジオ用の時刻補正に変換
    public int StringToArpeggio(String str) {
        return switch (str) {
            case MP, SP -> -1;
            case MN, SN -> 1;
            default -> 0;
        };
    }
    public int arpTo(String str, Map<String, Integer> note){
        return switch (str) {
            case MP -> note.get(MA);
            case MA -> note.get(MN);
            case SP -> note.get(SA);
            case SA -> note.get(SN);
            default -> fsu.UNDEFINED();
        };
    }
}
