package scene;

import data.DataCaster;
import data.DataElements;
import scene.key.KeyController;
import scene.fps.FrameRateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SceneBase extends JPanel implements ActionListener {
    @Override
    public void paintComponent(Graphics g){
        super.paintComponents(g);
        paintField((Graphics2D) g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isActive) {
            actionField();

            repaint(); // 再描画
            fru.setDelayAndStartTimer(timer); // FPS調整
            key.avoidChattering(); // チャタリング防止
        } else {
            timer.stop();
        }
    }

    // 描画用メソッド
    protected abstract void paintField(Graphics2D g2d);
    // 処理用メソッド
    protected abstract void actionField();

    // 初期化
    protected void init(List<Integer> keyAssign, Map<Integer, Object> data) {
        // クラス内で用いるデータの移動
        this.data = new HashMap<>(data);

        int nextSceneId = cast.getIntData(this.data, elem.SCENE_ID) + 1;
        this.data.put(elem.SCENE_ID, nextSceneId);

        // FrameRateUtilを定義
        int frameRate = cast.getIntData(this.data, elem.FRAME_RATE);
        fru = new FrameRateUtil(frameRate, 0);

        // 画面サイズの指定
        int displayWidth  = cast.getIntData(this.data, elem.DISPLAY_WIDTH);
        int displayHeight = cast.getIntData(this.data, elem.DISPLAY_HEIGHT);
        setPreferredSize(new Dimension(displayWidth, displayHeight) );

        // キーリスナの登録
        key = new KeyController(keyAssign);
        key.setKeyListener(this);

        // タイマーの設定
        timer = new Timer(0, this);
        timer.start();

        isActive = true; // 稼働状態にして毎フレームの描画と処理が動くようにする
    }

    // 機能の消滅
    public void killMyself() {
        timer.stop();
        isActive = false;
    }

    // シーン転換
    protected void sceneTransition(Scene scene) {
        SceneManager sceneManager = cast.getSceneManager(data);
        sceneManager.sceneTransition(scene, data, this);
    }

    // ------------------------------------------------------ //
    // インスタンスいろいろ
    protected FrameRateUtil fru;
    protected KeyController key;
    private Timer timer;

    // データの受け渡し用
    protected Map<Integer, Object> data;
    protected final DataElements elem = new DataElements();
    protected final DataCaster cast = new DataCaster();

    private boolean isActive;

    // ------------------------------------------------------ //

    // デバッグ用 インスタンスの稼働状態の確認
    protected void printMessage(String msg, int tab) {
        int id = cast.getIntData(data, elem.SCENE_ID);
        String t = "\t".repeat(tab);
        String name = this.getClass().getName();
        System.out.printf("%s %s@%s<ID%2d>\n", msg, t, name, id);
    }
}
