package font;

import java.awt.*;

// よく使うフォントをまとめたもの
public class FontUtil {
    public final int PLAIN  = Font.PLAIN;
    public final int BOLD   = Font.BOLD;
    public final int ITALIC = Font.ITALIC;

    public Font Arial(int size, int style) {
        return new Font("Arial", style, size);
    }
    public Font MSGothic(int size, int style) {
        return new Font("ＭＳ ゴシック", style, size);
    }
    public Font Meiryo(int size, int style) {
        return new Font("Meiryo", style, size);
    }

    public Font Arial(int size) {
        return new Font("Arial", PLAIN, size);
    }
    public Font MSGothic(int size) {
        return new Font("ＭＳ ゴシック", PLAIN, size);
    }
    public Font Meiryo(int size) {
        return new Font("Meiryo", PLAIN, size);
    }

    // コンボ表示の文字幅の算出
    public int strWidth(Graphics2D g2d, Font font, String str) {
        return g2d.getFontMetrics(font).stringWidth(str);
    }
}
