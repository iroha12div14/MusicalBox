package scenes.font;

import java.awt.*;

/**
 * よく使うフォントをまとめたもの
 */
public class FontUtil {
    // 字体
    public final int PLAIN  = Font.PLAIN;
    public final int BOLD   = Font.BOLD;
    public final int ITALIC = Font.ITALIC;

    // フォントインスタンスの作成
    public Font Arial(int size, int style) {
        return new Font("Arial", style, size);
    }
    public Font Arial(int size) {
        return new Font("Arial", PLAIN, size);
    }

    public Font MSGothic(int size, int style) {
        return new Font("ＭＳ ゴシック", style, size);
    }
    public Font MSGothic(int size) {
        return new Font("ＭＳ ゴシック", PLAIN, size);
    }

    public Font Meiryo(int size, int style) {
        return new Font("Meiryo", style, size);
    }
    public Font Meiryo(int size) {
        return new Font("Meiryo", PLAIN, size);
    }

    // 描画支援メソッド
    /**
     * フォントと色を設定する
     * @param font  フォント
     * @param color 色
     */
    public void setStr(Graphics2D g2d, Font font, Color color) {
        g2d.setColor(color);
        g2d.setFont(font);
    }

    /**
     * 位置を指定して文字列を描画する
     * @param str   描画する文字列
     * @param x     X座標
     * @param y     Y座標
     */
    public void drawStr(Graphics2D g2d, String str, int x, int y) {
        g2d.drawString(str, x, y);
    }

    /**
     * 文字列の幅を算出する
     * @param font  フォント
     * @param str   文字列
     */
    public int strWidth(Graphics2D g2d, Font font, String str) {
        return g2d.getFontMetrics(font).stringWidth(str);
    }

    /**
     * 文字の高さを算出する
     * @param font  フォント
     */
    public int strHeight(Graphics2D g2d, Font font) {
        return g2d.getFontMetrics(font).getHeight();
    }
}
