package scenes.draw;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// 台形 上辺・下辺・高さを指定して描画
public class DrawTrapezoid implements Draw {
    // 上下・左右・中央寄せによる座標補正
    // Sideパラメータがここのメソッドでロストするので、Side.DIRキーの値だけ退避している(あまりスマートじゃない処理)
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> side) {
        Map<Param, Integer> p = sideFixParam(param, side);
        setDIR(side.get(DIR));
        draw(g2d, c, p);
    }
    public void fill(Graphics2D g2d, Color c, Map<Param, Integer> param, Map<Side, Integer> side) {
        Map<Param, Integer> p = sideFixParam(param, side);
        setDIR(side.get(DIR));
        fill(g2d, c, p);
    }
    // 座標補正
    private Map<Param, Integer> sideFixParam(Map<Param, Integer> param, Map<Side, Integer> side){
        // Side_X,_Y未記入の場合は左上寄せ
        int sa = switch(side.get(DIR)){
            case HORIZONTAL -> side.get(SIDE_X);
            case VERTICAL -> side.get(SIDE_Y);
            default -> LEFT;
        };
        int sb = switch(side.get(DIR)){
            case HORIZONTAL -> side.get(SIDE_Y);
            case VERTICAL -> side.get(SIDE_X);
            default -> TOP;
        };

        // より長い辺を採用して補正する
        int w = Math.max(param.get(WT), param.get(WB));
        int qa = w * (sa+1) / 2;
        int qb = param.get(H) * (sb+1) / 2;

        // Side.DIRによる部分的な書き換え
        // ここだけ説明不可能な構造してる(良くない)
        Map<Param, Integer> p = new HashMap<>(param);
        if(side.get(DIR) == HORIZONTAL) {
            p.put(X, param.get(X) - qa);
            p.put(Y, param.get(Y) - qb);
        }
        else if(side.get(DIR) == VERTICAL) {
            p.put(X, param.get(Y) - qa);
            p.put(Y, param.get(X) - qb);
        }
        // else -> 無補正(HORIZONTAL)
        return p;
    }

    // 台形の描画
    @Override
    public void draw(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        int[] pArrA = pointArrayA(param);
        int[] pArrB = pointArrayB(param);

        g2d.setColor(c);
        if( getDIR() == VERTICAL ) {
            g2d.drawPolygon(pArrB, pArrA, 4);
        }
        else { // HORIZONTAL, else
            g2d.drawPolygon(pArrA, pArrB, 4);
        }
    }
    @Override
    public void fill(Graphics2D g2d, Color c, Map<Param, Integer> param) {
        int[] pArrA = pointArrayA(param);
        int[] pArrB = pointArrayB(param);

        g2d.setColor(c);
        if( getDIR() == VERTICAL ) {
            g2d.fillPolygon(pArrB, pArrA, 4);
        }
        else { // HORIZONTAL, else
            g2d.fillPolygon(pArrA, pArrB, 4);
        }
    }
    // 座標要素の構成
    private int[] pointArrayA(Map<Draw.Param, Integer> param) {
        int wt = param.get(WT);
        int wb = param.get(WB);

        int wah = (wt > wb) ? wt / 2 : wb / 2;
        int wbh = (wt + wb) / 2 - wah;
        int p = param.get(X);
        return new int[] {p, p+wah*2, p+wah+wbh, p+wah-wbh};
    }
    private int[] pointArrayB(Map<Draw.Param, Integer> param) {
        int wt = param.get(WT);
        int wb = param.get(WB);
        int h = param.get(H);

        int ha = (wt > wb) ? 0 : h;
        int hb = h - ha;
        int p = param.get(Y);
        return new int[] {p+ha, p+ha, p+hb, p+hb};
    }

    //Side.DIRキーの値が取得できなくなるの面倒なので、それ用のセッタとゲッタと変数を用意した
    private void setDIR(int d){
        dir = d;
    }
    private int getDIR(){
        return dir;
    }
    private int dir;

    // 描画用パラメータの作成の補助メソッド
    public Map<Param, Integer> makeParam(int x, int y, int wt, int wb, int h) {
        Map<Param, Integer> param = new HashMap<>();
        param.put(X, x);
        param.put(Y, y);
        param.put(WT, wt);
        param.put(WB, wb);
        param.put(H, h);
        return param;
    }
    public Map<Side, Integer> makeSide(int sideX, int sideY, int sideDir) {
        Map<Side, Integer> side = new HashMap<>();
        side.put(Side.X, sideX);
        side.put(Side.Y, sideY);
        side.put(Side.DIR, sideDir);
        return side;
    }

    // パラメータの簡略表記
    public final Param X = Param.X;
    public final Param Y = Param.Y;
    public final Param WT = Param.WIDTH_TOP;
    public final Param WB = Param.WIDTH_BOTTOM;
    public final Param H = Param.HEIGHT;
    public final Side SIDE_X = Side.X;
    public final Side SIDE_Y = Side.Y;
    public final Side DIR = Side.DIR;
    public final int LEFT   = -1;
    public final int RIGHT  = 1;
    public final int TOP    = -1;
    public final int BOTTOM = 1;
    public final int CENTER = 0;
    public final int HORIZONTAL = 0;
    public final int VERTICAL = 1;
}
