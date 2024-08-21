package scenes.playmusic.findstr;

// 文字検索するメソッド作ったのでせっかくなので置いてみた
public class FindStrUtil {
    private final int UNDEFINED = -1;

    // 文字検索するメソッド(一旦digStrを経由する)
    public int findStr(String str, String s){
        return digStr(str, s.charAt(0), 0);
    }

    // 再起処理で文字検索してみる
    private int digStr(String str, char c, int i){
        // 先頭の文字が目的の文字なら、処理回数(初回が0回目)を返す
        if( str.charAt(0) == c ) {
            return i;
        }
        // 先頭の文字が目的の文字でないなら、[1]文字目以降で同じ処理をする
        else if( str.length() > 1 ) {
            return digStr( str.substring(1) , c, i+1);
        }
        // 文字が見つからないならUNDEFINEDを返す
        else{
            return UNDEFINED;
        }
    }

    // 対象の文字列に検索文字が複数含まれているならこっちが使える
    public int[] findStrForSplit(String str, String c){
        String[] ss = str.split(c);     // 残った文字
        int cc = ss.length - 1;         // 見つかった文字数
        int[] findStr = new int[cc];    // 返却用

        int lenCount = UNDEFINED;
        for(int i = 0; i < cc; i++ ){
            lenCount += ss[i].length() + 1;
            findStr[i] = lenCount;
        }
        return findStr;
    }

    // 文字検索の成否
    public boolean isNotFind(int s) {
        return s == UNDEFINED;
    }

    // 検索失敗時の出力内容を返す
    public int UNDEFINED() {
        return UNDEFINED;
    }
}
