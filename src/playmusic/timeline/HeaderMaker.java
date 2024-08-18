package playmusic.timeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HeaderMaker {
    // ヘッダの分解と格納
    public Map<String, Object> makeHeader(List<String> punchCard) {
        // 読み取りモード falseでヘッダ部分、trueでパンチカード本体読み取り
        boolean readSequenceMode = false;

        // ヘッダ情報（タイトルやテンポ）を投げ込むためのやつ
        Map<String, Object> header = new HashMap<>();

        // 1行ずつ構文解析してデータ処理
        for( String line : punchCard ){
            // "#"を境に左が変数名、右がパラメータ
            String hd    = line.split(PunchCard.SPLIT_TOKEN)[0];
            String param = line.split(PunchCard.SPLIT_TOKEN)[1];

            // ヘッダ部分の読み取り 曲名とかテンポとか
            if( !readSequenceMode ){
                if( Objects.equals(hd, PunchCard.TITLE) ){
                    header.put(hd, param);
                }
                else if( Objects.equals(hd, PunchCard.TEMPO) ){
                    int intParam = Integer.parseInt(param);
                    header.put(hd, intParam);
                }
                else if( Objects.equals(hd, PunchCard.LEVEL) ){

                    String[] p = param.split(PunchCard.PARAM_SPLIT_TOKEN);
                    int[] intParam = {Integer.parseInt(p[0]), Integer.parseInt(p[1])};
                    header.put(hd, intParam);
                }
                else if( Objects.equals(hd, PunchCard.SEQUENCE) ){
                    readSequenceMode = true;
                }
            }
            else {
                break;
            }
        }
        return header;
    }
}
