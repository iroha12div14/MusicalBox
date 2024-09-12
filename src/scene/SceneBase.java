package scene;

import data.GameDataElements;
import data.GameDataIO;
import scene.key.KeyController;
import scene.fps.FrameRateUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 場面表示のベース機能となるクラス。
 * <br/>
 * paintFieldメソッドは描画を、actionFieldメソッドは定期的に実行する処理を記述する。
 */
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

    /**
     * 描画用メソッド
     */
    protected abstract void paintField(Graphics2D g2d);

    /**
     * 処理用メソッド
     */
    protected abstract void actionField();

    /**
     * 初期化
     * @param keyAssign その場面で使用するキーの一覧（リスト型）
     * @param dataIO    ゲーム内でやり取りされるデータの入出力を行う
     */
    protected void init(List<Integer> keyAssign, GameDataIO dataIO) {
        // クラス内で用いるデータの移動
        data = dataIO;

        int nextSceneId = data.get(GameDataElements.SCENE_ID, Integer.class) + 1;
        data.put(GameDataElements.SCENE_ID, nextSceneId);

        // FrameRateUtilを定義
        int frameRate = data.get(GameDataElements.FRAME_RATE, Integer.class);
        fru = new FrameRateUtil(frameRate);

        // 画面サイズの指定
        int displayWidth  = data.get(GameDataElements.DISPLAY_WIDTH, Integer.class);
        int displayHeight = data.get(GameDataElements.DISPLAY_HEIGHT, Integer.class);
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

    /**
     * シーン転換
     * @param scene 転換先の場面
     */
    protected void sceneTransition(Scene scene) {
        // 同フレーム内で誤って2回呼ぶとバグることが判明したのでセーフティネット敷いてる
        if( !isCalledScene ) {
            isCalledScene = true;
            SceneManager sceneManager = data.get(GameDataElements.SCENE_MANAGER, SceneManager.class);
            sceneManager.sceneTransition(scene, data, this);
        }
    }

    // ------------------------------------------------------ //
    // インスタンスいろいろ
    protected FrameRateUtil fru;
    protected KeyController key;
    private Timer timer;

    // データの受け渡し用
    protected GameDataIO data;

    private boolean isActive;
    private boolean isCalledScene = false; // シーンを誤って2回呼ばない為のセーフティネット

    // ------------------------------------------------------ //

    /**
     * デバッグ用 インスタンスの稼働状態の確認
     * @param msg コンソールに表示する文字列
     * @param tab Tabインデントの数
     */
    protected void printMessage(String msg, int tab) {
        int id = data.get(GameDataElements.SCENE_ID, Integer.class);
        String t = "\t".repeat(tab);
        String[] fullName = this.getClass().getName().split("\\.");
        String name = fullName[fullName.length-1];
        System.out.printf("%s %s@%s<ID%2d>\n", msg, t, name, id);
    }
}
