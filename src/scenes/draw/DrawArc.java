package scenes.draw;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 弧と扇の描画。
 * <br/>
 * sin,cosによる曲座標変換(角度が時計回り)と、
 * drawArcの描画(角度が反時計回り)が上下逆(？？？？？)なので、
 * 角度の扱いを描画座標を基準として統一した……
 * が、
 * Graphics -> Graphics2Dにキャストすると、
 * ちゃんと時計回りの挙動になるらしい。
 * まさかの差し戻し
  */
public class DrawArc extends DrawPolygon {
    /**
     * 弧の描画（中抜きの扇ではない）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void draw(Graphics2D g2D, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = convertAngle(param);
        g2D.setColor(c);
        g2D.drawArc(p.get(X), p.get(Y), p.get(W), p.get(H), p.get(A1), p.get(A2));
    }

    /**
     * 扇の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void fill(Graphics2D g2D, Color c, Map<Param, Integer> param) {
        Map<Param, Integer> p = convertAngle(param);
        g2D.setColor(c);
        g2D.fillArc(p.get(X), p.get(Y), p.get(W), p.get(H), p.get(A1), p.get(A2));
    }

    private Map<Param, Integer> convertAngle(Map<Param, Integer> param) {
        int startAngle = param.get(A1);
        int arcAngle = param.get(A2) - param.get(A1);

        // 部分的な書き換え
        Map<Param, Integer> p = new HashMap<>(param);
        p.put(A1, startAngle);
        p.put(A2, arcAngle);
        return p;
    }

    // パラメータの簡略表記
    public final Param A1 = Param.ANGLE;
    public final Param A2 = Param.ANGLE2;
}
