package calc;

/**
 * ちょっとした煩わしい計算処理するときに便利なやつ
 */
public class CalcUtil {
    private int digit(int fraction){
        // 再帰で出してみる
        return fraction != 0 ? 10 * digit(fraction - 1) : 1;
    }

    /**
     *
     * @param val 被除数(整数、負数可)
     * @param mod 除数
     * @return 余り
     */
    public int mod(int val, int mod) {
        int m = (mod + val) % mod;
        return (m < 0) ? mod(m, mod) : m;
    }

    /**
     * 割って指定桁数の小数で商を出す
     * @param val 被除数
     * @param div 除数
     * @param fraction 小数点以下の桁数
     * @return 商
     */
    public float div(int val, int div, int fraction) {
        int digit = digit(fraction);
        return (float) Math.round( (float) digit * val / div) / digit;
    }
    /**
     * 割って四捨五入
     * @param val 被除数
     * @param div 除数
     * @return 商（整数）
     */
    public int div(int val, int div) {
        return Math.round( (float) val / div);
    }

    /**
     * 2乗
     * @param val 被乗数（整数）
     * @return 積
     */
    public double pow2(int val) {
        return val * val;
    }
    /**
     * 2乗
     * @param val 被乗数（小数）
     * @return 積
     */
    public double pow2(float val) {
        return val * val;
    }

    /**
     * 小数点以下だけをN桁取得
     * @param val 対象の値
     * @param digit 小数点以下の桁数
     * @return 桁数分の小数点以下の数の並び（正の整数）
     */
    public int getDotUnder(float val, int digit) {
        float dotUnder = (val - (int) (val) ) * digit(digit);
        return Math.round(dotUnder);
    }

    /**
     * 小数を含む値を小数点以下N桁で返す
     * @param val 対象の値
     * @param digit 桁数
     * @return 小数点以下を指定桁数で切り揃えた値
     */
    public float getFloatDotUnder(float val, int digit) {
        int v = (int) val;
        return v + (float) getDotUnder(val, digit) / digit(digit);
    }

    /**
     * 文字列操作 指定した桁になるように整数の先頭に0を付け加える
     * @param val 対象の整数値
     * @param digit 桁数
     * @return 先頭に0を必要分だけ付け加えた文字列
     */
    public String paddingZero(int val, int digit) {
        int d = 1;
        StringBuilder str = new StringBuilder(String.valueOf(val));
        while(d < digit) {
            if(val < digit(d) ) {
                str.insert(0, "0");
            }
            d++;
        }
        return str.toString();
    }

    /**
     * 文字列操作 小数値を指定桁数の小数点以下で文字列で出す
     * @param val 対象の値
     * @param digit 桁数
     * @return 指定桁数で小数を切り揃えた文字列
     */
    public String getStrFloatDotUnder(float val, int digit) {
        String valStr = String.valueOf(val);
        String[] valSplit = valStr.split("\\.");
        char[] dotUnder = valSplit[1].toCharArray();
        int dotUnderLen = dotUnder.length;
        valSplit[0] += ".";
        for(int d = 0; d < digit; d++) {
            valSplit[0] += d < dotUnderLen ? String.valueOf(dotUnder[d]) : "0";
        }
        return valSplit[0];
    }

}
