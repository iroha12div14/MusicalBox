package scenes.drawer;

import scenes.draw.DrawLine;
import scenes.draw.DrawOval;
import scenes.draw.DrawPolygon;
import scenes.draw.DrawRect;
import scenes.font.FontUtil;

public abstract class SceneDrawer {
    // 描画用インスタンス
    protected final DrawLine drawLine = new DrawLine();
    protected final DrawPolygon drawRect = new DrawRect();
    protected final DrawPolygon drawCircle = new DrawOval();
    protected final FontUtil font = new FontUtil();

    // 画面サイズ
    protected int displayWidth;
    protected int displayHeight;

    // 画面サイズの設定
    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    };

    // 設計図の設定
    protected abstract void setBlueprint();

    // アニメーションタイマーの設定
    protected abstract void setAnimationTimer(int frameRate);

    // アニメーションタイマーの経過
    protected abstract void pastAnimationTimer();
}
