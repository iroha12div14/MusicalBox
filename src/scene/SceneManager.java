package scene;

import data.GameDataIO;
import logger.MessageLogger;
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
    private JFrame window;

    /**
     * 場面の初期化
     * @param dataIO ゲーム内でやり取りされるデータの入出力を行う
     */
    public void activateDisplay(GameDataIO dataIO) {
        System.out.println("--------------------------------------");
        MessageLogger.printMessage(this, "ディスプレイの初期化中", 2);
        System.out.println("--------------------------------------");

        // ウインドウの表示
        window = new JFrame(dataIO.getWindowName() );
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // [X]押したらプログラム終了的な
        window.setBackground(Color.BLACK); // デフォルトで黒背景にする

        // 起動時に指定されたシーンを展示
        SceneBase base = makeScene(dataIO.getScene(), dataIO);
        window.add(base);
        window.pack(); // ウインドウサイズをJPanelの内容に合わせて自動調整

        window.setFocusable(true); // キーリスナのフォーカスを得る
        window.addKeyListener(base.getKeyListeners()[0]);

        window.setVisible(true);

        window.setLocation(dataIO.getWindowPoint() );
        System.out.println("--------------------------------------");
        MessageLogger.printMessage(this, "ディスプレイと場面の初期化完了", 1);
        System.out.println("--------------------------------------");
    }

    /**
     * 場面転換で呼び出されるメソッド
     * @param removePanel 削除する場面。呼び出す側は(SceneBaseの子クラスであれば)thisって書いておけばOK
     */
    public void sceneTransition(Scene nextScene, GameDataIO dataIO, SceneBase removePanel) {
        // コンポーネントの削除と再描画
        removePanel.killMyself();
        window.remove(removePanel);
        window.revalidate();
        window.repaint();
        System.gc(); // リソースの破棄

        System.out.println("--------------------------------------");
        MessageLogger.printMessage(this, "場面の転換中", 1);
        System.out.println("--------------------------------------");

        // コンポーネントの追加
        SceneBase addPanel = makeScene(nextScene, dataIO);
        window.add(addPanel);

        // キーリスナの登録
        window.removeKeyListener(window.getKeyListeners()[0]);
        window.addKeyListener(addPanel.getKeyListeners()[0]);

        window.setVisible(true);
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
}
