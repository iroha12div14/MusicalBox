package scenes.draw.blueprint;

import scenes.draw.Draw;
import scenes.draw.DrawArc;
import scenes.draw.DrawPolygon;
import scenes.draw.DrawTrapezoid;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 図形を描画するためのパラメータ（設計図）クラス。
 */
public class Blueprint {
    // 定数
    public static final int CENTER = 0;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public static final int TOP = -1;
    public static final int BOTTOM = 1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    // フィールド
    private int sideX = -1;
    private int sideY = -1;
    private int anchorX;
    private int anchorY;
    private int width;
    private int height;
    private int topWidth; // 台形の上辺
    private int bottomWidth; // 台形の下辺
    private int angle;
    private int angle2;
    private int dir = 0; // 台形の横向き・縦向き

    /**
     * 長方形や楕円のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param width     幅
     * @param height    高さ
     */
    public Blueprint(int anchorX, int anchorY, int width, int height) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.width = width;
        this.height = height;
    }

    /**
     * 正方形や正円のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param radius    半径
     */
    public Blueprint(int anchorX, int anchorY, int radius) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.height = radius;
    }
    /**
     * 台形や三角形のパラメータ設定
     * @param anchorX       アンカーのX座標
     * @param anchorY       アンカーのY座標
     * @param topWidth      上辺
     * @param bottomWidth   下辺
     * @param height        高さ
     */
    public Blueprint(int anchorX, int anchorY, int topWidth, int bottomWidth, int height) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.topWidth = topWidth;
        this.bottomWidth = bottomWidth;
        this.height = height;
    }
    /**
     * 弧や扇形のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param width     幅
     * @param height    高さ
     * @param angle     角度①
     * @param angle2    角度②(角度①からの増分ではない)
     */
    public Blueprint(int anchorX, int anchorY, int width, int height, int angle, int angle2) {
        this(anchorX, anchorY, width, height);
        this.angle = angle;
        this.angle2 = angle2;
    }

    // 上下左右寄せと台形であれば向きの設定
    /**
     * 上下左右寄せの設定
     * @param sideX 左右寄せ LEFTなら左寄せ、RIGHTなら右寄せ
     * @param sideY 上下寄せ TOPなら上寄せ、BOTTOMなら下寄せ
     */
    public void setSide(int sideX, int sideY) {
        this.sideX = sideX;
        this.sideY = sideY;
    }
    /**
     * 台形の上下左右寄せと向きの設定
     * @param sideX 左右寄せ LEFTなら左寄せ、RIGHTなら右寄せ
     * @param sideY 上下寄せ TOPなら上寄せ、BOTTOMなら下寄せ
     * @param dir   向き HORIZONTALなら上下辺が水平、VERTICALなら垂直
     */
    public void setSide(int sideX, int sideY, int dir) {
       setSide(sideX, sideY);
       this.dir = dir;
    }

    // パラメータ変更用
    public void setAnchorX (int anchorX) {
        this.anchorX = anchorX;
    }
    public void setAnchorY (int anchorY) {
        this.anchorY = anchorY;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
    public void setAngle2(int angle2) {
        this.angle2 = angle2;
    }

    // 座標の取得
    public int X() {
        return anchorX - width * (sideX + 1) / 2;
    }
    public int Y() {
        return anchorY - height * (sideY + 1) / 2;
    }
    public int anchorX() {
        return anchorX;
    }
    public int anchorY() {
        return anchorY;
    }

    // 描画パラメータの作成
    private Map<Draw.Param, Integer> param() {
        Map<Draw.Param, Integer> param = new HashMap<>();
        param.put(Draw.Param.X, anchorX);
        param.put(Draw.Param.Y, anchorY);
        param.put(Draw.Param.WIDTH, width);
        param.put(Draw.Param.HEIGHT, height);
        return param;
    }
    private Map<Draw.Param, Integer> paramReg() {
        Map<Draw.Param, Integer> param = new HashMap<>();
        param.put(Draw.Param.X, anchorY);
        param.put(Draw.Param.Y, anchorY);
        param.put(Draw.Param.RADIUS, width);
        param.put(Draw.Param.WIDTH, width);
        param.put(Draw.Param.HEIGHT, width); // 微妙に正しくないけど一応これで
        return param;
    }
    private Map<Draw.Param, Integer> paramTz() {
        Map<Draw.Param, Integer> param = new HashMap<>();
        param.put(Draw.Param.X, anchorX);
        param.put(Draw.Param.Y, anchorY);
        param.put(Draw.Param.WIDTH_TOP, topWidth);
        param.put(Draw.Param.WIDTH_BOTTOM, bottomWidth);
        param.put(Draw.Param.HEIGHT, height);
        return param;
    }
    private Map<Draw.Param, Integer> paramArc() {
        Map<Draw.Param, Integer> param = new HashMap<>(param() );
        param.put(Draw.Param.ANGLE, angle);
        param.put(Draw.Param.ANGLE2, angle2);
        return param;
    }

    // 描画サイドの作成
    private Map<Draw.Side, Integer> side() {
        Map<Draw.Side, Integer> side = new HashMap<>();
        side.put(Draw.Side.X, sideX);
        side.put(Draw.Side.Y, sideY);
        return side;
    }
    private Map<Draw.Side, Integer> sideTz() {
        Map<Draw.Side, Integer> side = new HashMap<>();
        side.put(Draw.Side.X, sideX);
        side.put(Draw.Side.Y, sideY);
        side.put(Draw.Side.DIR, dir);
        return side;
    }

    // 設計図の描画
    public void drawPolygon(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        drawStyle.draw(g2d, color, param(), side() );
    }
    public void fillPolygon(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        drawStyle.fill(g2d, color, param(), side() );
    }

    public void drawRegular(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        drawStyle.drawRegular(g2d, color, param(), side() );
    }
    public void fillRegular(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        drawStyle.fillRegular(g2d, color, paramReg(), side() );
    }

    public void drawTrapezoid(Graphics2D g2d, Color color) {
        DrawTrapezoid drawStyle = new DrawTrapezoid();
        drawStyle.draw(g2d, color, paramTz(), sideTz() );
    }
    public void fillTrapezoid(Graphics2D g2d, Color color) {
        DrawTrapezoid drawStyle = new DrawTrapezoid();
        drawStyle.fill(g2d, color, paramTz(), sideTz() );
    }

    public void drawArc(Graphics2D g2d, Color color) {
        DrawArc drawStyle = new DrawArc();
        drawStyle.draw(g2d, color, paramArc(), side() );
    }
    public void fillArc(Graphics2D g2d, Color color) {
        DrawArc drawStyle = new DrawArc();
        drawStyle.fill(g2d, color, paramArc(), side() );
    }
}
