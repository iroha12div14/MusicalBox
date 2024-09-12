package scenes.draw;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 辺で囲まれた図形の描画
 * <br/>
 * 長方形・楕円・弧、及びその正則図形を描画する。
 */
public abstract class DrawPolygon implements Draw {
    /**
     * 図形の描画（辺のみ）
     * @param c     色
     * @param param パラメータ（位置・大きさ）
     * @param side  パラメータ（上下左右寄せ）
     */
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> side) {
        Map<Param, Integer> p = sideFixParam(param, side);
        draw(g2d, c, p);
    }
    /**
     * 図形の描画（中塗りあり）
     * @param c     色
     * @param param パラメータ（位置・大きさ）
     * @param side  パラメータ（上下左右寄せ）
     */
    public void fill(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> side) {
        Map<Param, Integer> p = sideFixParam(param, side);
        fill(g2d, c, p);
    }

    // 座標補正
    private Map<Param, Integer> sideFixParam(Map<Param, Integer> param, Map<Side, Integer> side){
        int px = param.get(X) - param.get(W) * (1 + side.get(SIDE_X)) / 2;
        int py = param.get(Y) - param.get(H) * (1 + side.get(SIDE_Y)) / 2;

        // sideによる部分的な書き換え
        Map<Param, Integer> p = new HashMap<>(param);
        p.put(X, px);
        p.put(Y, py);
        return p;
    }

    // 正多角形

    /**
     * 正則図形の描画（線のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param s     描画パラメータ（上下左右寄せ）
     */
    public void drawRegular(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> s) {
        Map<Param, Integer> p = R2WH(param);
        draw(g2d, c, p, s);
    }
    /**
     * 正則図形の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param s     描画パラメータ（上下左右寄せ）
     */
    public void fillRegular(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> s) {
        Map<Param, Integer> p = R2WH(param);
        fill(g2d, c, p, s);
    }
    /**
     * 正則図形の描画（線のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    public void drawRegular(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = R2WH(param);
        draw(g2d, c, p);
    }
    /**
     * 正則図形の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    public void fillRegular(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = R2WH(param);
        fill(g2d, c, p);
    }

    // 半径のみの表記から幅・高さの変換
    private Map<Param, Integer> R2WH(Map<Param, Integer> param){
        Map<Param, Integer> p = new HashMap<>(param);
        p.put(W, param.get(R) * 2);
        p.put(H, param.get(R) * 2);
        p.remove(R);
        return p;
    }

    // パラメータの簡略表記
    // Rは四角形においては辺の半分の扱い
    public final Param X = Param.X;
    public final Param Y = Param.Y;
    public final Param W = Param.WIDTH;
    public final Param H = Param.HEIGHT;
    public final Param R = Param.RADIUS;
    public final Side SIDE_X = Side.X;
    public final Side SIDE_Y = Side.Y;
    public final int LEFT   = -1;
    public final int RIGHT  = 1;
    public final int TOP    = -1;
    public final int BOTTOM = 1;
    public final int CENTER = 0;
}
