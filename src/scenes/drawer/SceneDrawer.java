package scenes.drawer;

import scenes.draw.DrawLine;
import scenes.draw.DrawOval;
import scenes.draw.DrawPolygon;
import scenes.draw.DrawRect;
import scenes.font.FontUtil;

/**
 * 場面の描画内容を設定する。
 */
public abstract class SceneDrawer {
    // 描画用インスタンス
    protected final DrawLine drawLine = new DrawLine();
    protected final DrawPolygon drawRect = new DrawRect();
    protected final DrawPolygon drawCircle = new DrawOval();

    protected final FontUtil font = new FontUtil();

    // 画面サイズ
    protected int displayWidth;
    protected int displayHeight;

    /**
     * 画面サイズの設定
     * @param width     幅
     * @param height    高さ
     */
    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    };

    /**
     * 設計図を設定する
     */
    protected abstract void setBlueprint();

    /**
     * アニメーションタイマーを設定する
     * @param frameRate フレームレート
     */
    protected abstract void setAnimationTimer(int frameRate);

    /**
     * 経過させるアニメーションタイマーのインスタンスをここにまとめて書く。
     */
    protected abstract void pastAnimationTimer();
}
