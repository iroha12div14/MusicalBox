package scene;

import data.DataCaster;
import data.DataElements;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SceneManager {
    private JFrame display;

    // 初期化
    public void activateDisplay(Map<Integer, Object> data) {
        System.out.println("--------------------------------------");
        printMessage("ディスプレイの初期化中", 2);
        System.out.println("--------------------------------------");

        // ゲーム内で使用するデータの要素と変換インスタンス
        DataCaster cast = new DataCaster();
        DataElements elem = new DataElements();

        // ウインドウの表示
        display = new JFrame(cast.getStrData(data, elem.WINDOW_NAME));
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // [X]押したらプログラム終了的な
        display.setBackground(Color.BLACK); // デフォルトで黒背景にする

        // 起動時に指定されたシーンを展示
        Scene activationScene = cast.getScene(data);
        SceneBase base = makeScene(activationScene, data);
        display.add(base);
        display.pack(); // ウインドウサイズをJPanelの内容に合わせて自動調整

        display.setFocusable(true); // キーリスナのフォーカスを得る
        display.addKeyListener(base.getKeyListeners()[0]);

        display.setVisible(true);

        int displayX = cast.getIntData(data, elem.DISPLAY_X);
        int displayY = cast.getIntData(data, elem.DISPLAY_Y);
        display.setLocation(displayX, displayY);
        System.out.println("--------------------------------------");
        printMessage("ディスプレイと場面の初期化完了", 1);
        System.out.println("--------------------------------------");
    }

    /**
     * 場面転換で呼び出されるメソッド
     * @param removePanel
     *      呼び出す側は(SceneBaseの子クラスであれば)thisって書いておけばOK
     */
    public void sceneTransition(Scene nextScene, Map<Integer, Object> data, SceneBase removePanel) {
        // コンポーネントの削除と再描画
        removePanel.killMyself();
        display.remove(removePanel);
        display.revalidate();
        display.repaint();
        System.gc(); // リソースの破棄

        System.out.println("--------------------------------------");
        printMessage("場面の転換中", 1);
        System.out.println("--------------------------------------");

        // コンポーネントの追加
        SceneBase addPanel = makeScene(nextScene, data);
        display.add(addPanel);

        // キーリスナの登録
        display.removeKeyListener(display.getKeyListeners()[0]);
        display.addKeyListener(addPanel.getKeyListeners()[0]);

        display.setVisible(true);
    }

    /**
     * 指定したシーン名に応じて展示するコンポーネントを返す
     * @param scene
     *      シーン名
     * @return
     *      シーン名に応じたコンポーネント
     */
    private SceneBase makeScene(Scene scene, Map<Integer, Object> data) {
        // コンポーネントの追加
        return switch (scene) {
            case SELECT_MUSIC_SCENE -> new SelectMusicScene(data);
            case PLAY_MUSIC_SCENE   -> new PlayMusicScene(data);
            case OPTION_SCENE       -> new OptionScene(data);
        };
    }

    // デバッグ用 インスタンスの稼働状態の確認
    protected void printMessage(String msg, int tab) {
        String t = "\t".repeat(tab);
        String name = this.getClass().getName();
        System.out.printf("%s%s@%s\n", msg, t, name);
    }
}
