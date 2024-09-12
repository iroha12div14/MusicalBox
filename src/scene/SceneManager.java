package scene;

import data.GameDataElements;
import data.GameDataIO;
import scenes.announce.AnnounceScene;
import scenes.option.OptionScene;
import scenes.playmusic.PlayMusicScene;
import scenes.selectmusic.SelectMusicScene;
import scenes.viewtrophy.ViewTrophyScene;

import javax.swing.*;
import java.awt.*;

/**
 * 場面の切り替えを行う
 */
public class SceneManager {
    private JFrame display;

    /**
     * 場面の初期化
     * @param dataIO ゲーム内でやり取りされるデータの入出力を行う
     */
    public void activateDisplay(GameDataIO dataIO) {
        System.out.println("--------------------------------------");
        printMessage("ディスプレイの初期化中", 2);
        System.out.println("--------------------------------------");

        // ウインドウの表示
        String windowName = dataIO.get(GameDataElements.WINDOW_NAME, String.class);
        display = new JFrame(windowName);
        display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // [X]押したらプログラム終了的な
        display.setBackground(Color.BLACK); // デフォルトで黒背景にする

        // 起動時に指定されたシーンを展示
        Scene activationScene = dataIO.get(GameDataElements.SCENE, Scene.class);
        SceneBase base = makeScene(activationScene, dataIO);
        display.add(base);
        display.pack(); // ウインドウサイズをJPanelの内容に合わせて自動調整

        display.setFocusable(true); // キーリスナのフォーカスを得る
        display.addKeyListener(base.getKeyListeners()[0]);

        display.setVisible(true);

        int displayX = dataIO.get(GameDataElements.DISPLAY_X, Integer.class);
        int displayY = dataIO.get(GameDataElements.DISPLAY_Y, Integer.class);
        display.setLocation(displayX, displayY);

        System.out.println("--------------------------------------");
        printMessage("ディスプレイと場面の初期化完了", 1);
        System.out.println("--------------------------------------");
    }

    /**
     * 場面転換で呼び出されるメソッド
     * @param removePanel 削除する場面。呼び出す側は(SceneBaseの子クラスであれば)thisって書いておけばOK
     */
    public void sceneTransition(Scene nextScene, GameDataIO dataIO, SceneBase removePanel) {
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
        SceneBase addPanel = makeScene(nextScene, dataIO);
        display.add(addPanel);

        // キーリスナの登録
        display.removeKeyListener(display.getKeyListeners()[0]);
        display.addKeyListener(addPanel.getKeyListeners()[0]);

        display.setVisible(true);
    }

    /**
     * 指定したシーン名に応じてコンポーネントを生成する
     * @param scene シーン名
     * @param dataIO ゲーム内でやり取りされるデータの入出力を行う
     * @return コンポーネント（SceneBaseの子クラス）
     */
    private SceneBase makeScene(Scene scene, GameDataIO dataIO) {
        // コンポーネントの追加
        return switch (scene) {
            case SELECT_MUSIC -> new SelectMusicScene(dataIO);
            case PLAY_MUSIC   -> new PlayMusicScene(dataIO);
            case OPTION       -> new OptionScene(dataIO);
            case VIEW_TROPHY  -> new ViewTrophyScene(dataIO);
            case ANNOUNCE     -> new AnnounceScene(dataIO);
        };
    }

    /**
     * デバッグ用 インスタンスの稼働状態の確認
     * @param msg 表示するメッセージ
     * @param tab Tabインデントの数
     */
    protected void printMessage(String msg, int tab) {
        String t = "\t".repeat(tab);
        String[] fullName = this.getClass().getName().split("\\.");
        String name = fullName[fullName.length-1];
        System.out.printf("%s%s@%s\n", msg, t, name);
    }
}
