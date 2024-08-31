package scenes.header;

import scenes.playmusic.timeline.PunchCard;

import java.util.Map;

public class HeaderGetter {
    // ヘッダ情報（タイトル・テンポ・レベル）の取得
    public static String getTitle(Map<String, Object> header){
        return (String) header.get(PunchCard.TITLE);
    }
    public static int getTempo(Map<String, Object> header){
        return (int) header.get(PunchCard.TEMPO);
    }
    public static int[] getLevel(Map<String, Object> header) {
        return (int[]) header.get(PunchCard.LEVEL);
    }
    public static String getFileName(Map<String, Object> header) {
        return (String) header.get(PunchCard.FILE_NAME);
    }
}
