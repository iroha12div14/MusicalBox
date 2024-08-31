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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // アンチエイリアスの適用
        paintField(g2d);
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
        // 同フレーム内で誤って2回呼ぶとバグることが判明したのでセーフティネット敷いてる
        if( !isCalledScene ) {
            isCalledScene = true;
            SceneManager sceneManager = cast.getSceneManager(data);
            sceneManager.sceneTransition(scene, data, this);
        }
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
    private boolean isCalledScene = false; // シーンを誤って2回呼ばない為のセーフティネット

    // ------------------------------------------------------ //

    // デバッグ用 インスタンスの稼働状態の確認
    protected void printMessage(String msg, int tab) {
        int id = cast.getIntData(data, elem.SCENE_ID);
        String t = "\t".repeat(tab);
        String[] fullName = this.getClass().getName().split("\\.");
        String name = fullName[fullName.length-1];
        System.out.printf("%s %s@%s<ID%2d>\n", msg, t, name, id);
    }
}
