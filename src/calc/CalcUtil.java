package calc;

// ちょっとした煩わしい計算処理するときに便利なやつ
public class CalcUtil {
    private int digit(int fraction){
        // (int)10^nにすると桁処理が上手く行かないのでこの処理にしている
        return (int) Math.pow(10, fraction);
    }

    // 余り
    public int mod(int val, int mod) {
        int m = (mod + val) % mod;
        return (m < 0) ? mod(m, mod) : m;
    }
    // 割って指定桁数の小数で出す
    public float div(int val, int div, int fraction) {
        int digit = digit(fraction);
        return (float) Math.round( (float) digit * val / div) / digit;
    }
    // 割って四捨五入
    public int div(int val, int div) {
        return Math.round( (float) val / div);
    }
    // 2乗
    public double pow2(int val) {
        return Math.pow(val, 2);
    }
    // 小数点以下をN桁取得
    public int getDotUnder(float val, int digit) {
        float dotUnder = (val - (int) val) * digit(digit);
        return (int) Math.round(dotUnder);
    }

    // 文字列操作？ 指定した桁になるように整数に0を埋める
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
}
