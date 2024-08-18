package playmusic.timeline;

import java.util.Map;

public class HeaderGetter {
    // ヘッダ情報（タイトル・テンポ・レベル）の取得
    public String getTitle(Map<String, Object> header){
        return (String) header.get(PunchCard.TITLE);
    }
    public int getTempo(Map<String, Object> header){
        return (int) header.get(PunchCard.TEMPO);
    }
    public int[] getLevel(Map<String, Object> header) {
        return (int[]) header.get(PunchCard.LEVEL);
    }
}
