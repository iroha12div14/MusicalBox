package scenes.header;

import scenes.playmusic.timeline.PunchCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ヘッダ情報の作成
 */
public class HeaderMaker {
    /**
     * パンチカードからヘッダを抽出する
     * @param punchCard パンチカード（文字型リスト）
     * @return ヘッダ（Map）
     */
    public Map<String, Object> makeHeader(List<String> punchCard) {
        // ヘッダ情報（タイトルやテンポ）を投げ込むためのやつ
        Map<String, Object> header = new HashMap<>();

        // 1行ずつ構文解析してデータ処理
        for(String line : punchCard) {

            // 区切りトークン"#"を含まない場合は無視
            if(line.contains(PunchCard.SPLIT_TOKEN) ) {
                String param = line.split(PunchCard.SPLIT_TOKEN)[1]; // "#"を境に右がパラメータ

                if (line.startsWith(PunchCard.TITLE) ) {
                    header.put(PunchCard.TITLE, param);
                }
                else if (line.startsWith(PunchCard.TEMPO) ) {
                    header.put(PunchCard.TEMPO, getInt(param) );
                }
                else if (line.startsWith(PunchCard.LEVEL) ) {
                    header.put(PunchCard.LEVEL, getIntArr(param) );
                }
                else if (line.startsWith(PunchCard.SEQUENCE) ) {
                    break; // SEQUENCE行に差し掛かったら終了
                }
            }
        }
        return header;
    }

    // パラメータparamの型変換
    private int getInt(String param) {
        return Integer.parseInt(param);
    }
    private String[] getStrArr(String param) {
        return param.split(PunchCard.PARAM_SPLIT_TOKEN);
    }
    private int[] getIntArr(String param) {
        String[] strArrParam = getStrArr(param);
        int[] intArrParam = new int[strArrParam.length];
        int i = 0;
        for(String str : strArrParam) {
            intArrParam[i] = Integer.parseInt(str);
            i++;
        }
        return intArrParam;
    }
}
