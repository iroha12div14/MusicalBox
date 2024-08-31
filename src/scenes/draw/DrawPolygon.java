package scenes.draw;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// 辺で囲まれた図形
public abstract class DrawPolygon implements Draw {
    // 上下・左右・中央寄せによる座標補正
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> side) {
        Map<Param, Integer> p = sideFixParam(param, side);
        draw(g2d, c, p);
    }
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
    public void drawRegular(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> s) {
        Map<Param, Integer> p = R2WH(param);
        draw(g2d, c, p, s);
    }
    public void fillRegular(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> s) {
        Map<Param, Integer> p = R2WH(param);
        fill(g2d, c, p, s);
    }
    public void drawRegular(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = R2WH(param);
        draw(g2d, c, p);
    }
    public void fillRegular(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = R2WH(param);
        fill(g2d, c, p);
    }
    private Map<Param, Integer> R2WH(Map<Param, Integer> param){
        Map<Param, Integer> p = new HashMap<>(param);
        p.put(W, param.get(R) * 2);
        p.put(H, param.get(R) * 2);
        p.remove(R);
        return p;
    }

    // 描画用パラメータの作成の補助メソッド
    public Map<Param, Integer> makeParam(int x, int y, int w, int h) {
        Map<Param, Integer> param = new HashMap<>();
        param.put(X, x);
        param.put(Y, y);
        param.put(W, w);
        param.put(H, h);
        return param;
    }
    public Map<Param, Integer> makeParamRegular(int x, int y, int r) {
        Map<Param, Integer> param = new HashMap<>();
        param.put(X, x);
        param.put(Y, y);
        param.put(R, r);
        return param;
    }
    public Map<Side, Integer> makeSide(int sideX, int sideY) {
        Map<Side, Integer> side = new HashMap<>();
        side.put(Side.X, sideX);
        side.put(Side.Y, sideY);
        return side;
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
