package scenes.announce;

import data.GameDataElements;
import data.GameDataIO;
import scene.Scene;
import scene.SceneBase;
import trophy.TrophyList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class AnnounceScene extends SceneBase {
    // 使用キー
    private static final List<Integer> KEY_ASSIGN = List.of(KeyEvent.VK_ENTER);

    // 描画用インスタンス
    private final AnnounceDrawer drawer = new AnnounceDrawer();

    // 新規実績の一覧
    private final List<String> newTrophiesStr = new ArrayList<>();

    public AnnounceScene(GameDataIO dataIO) {
        init(KEY_ASSIGN, dataIO);
        data.put(GameDataElements.SCENE, Scene.ANNOUNCE);

        // 新規トロフィー
        List<Integer> newGeneralTrophy = data.getIntList(GameDataElements.NEW_GENERAL_TROPHY);
        String newMusicTrophy = data.get(GameDataElements.NEW_MUSIC_TROPHY, String.class);

        int displayWidth  = data.get(GameDataElements.DISPLAY_WIDTH, Integer.class);
        int displayHeight = data.get(GameDataElements.DISPLAY_HEIGHT, Integer.class);
        drawer.setDisplaySize(displayWidth, displayHeight);
        drawer.setBlueprint();

        int frameRate = data.get(GameDataElements.FRAME_RATE, Integer.class);
        drawer.setAnimationTimer(frameRate);

        // 新規実績解除が無いなら選曲画面にリダイレクト
        if(newGeneralTrophy.isEmpty() && newMusicTrophy == null) {
            printMessage("新規トロフィー無し", 2);
            drawer.endState();
        }
        else {
            for(int trophy : newGeneralTrophy) {
                newTrophiesStr.add(TrophyList.getGenTrophy(trophy) );
            }
            if(newMusicTrophy != null) {
                newTrophiesStr.add(TrophyList.getMusicTrophy(newMusicTrophy) );
            }

            // 表示済のデータを消去
            data.putIntList(GameDataElements.NEW_GENERAL_TROPHY, new ArrayList<>() );
            data.put(GameDataElements.NEW_MUSIC_TROPHY, null, String.class);
        }
    }

    @Override
    protected void paintField(Graphics2D g2d) {
        drawer.drawBackground(g2d);
        drawer.drawBox(g2d);
        drawer.drawTrophy(g2d, newTrophiesStr);
    }

    @Override
    protected void actionField() {
        boolean keyPress = key.getKeyPress(KeyEvent.VK_ENTER);
        int state = drawer.getState();

        // Enterキーを押して画面を閉じる
        if(keyPress && state == AnnounceDrawer.VIEWING
                || drawer.isOpenBoxZero()  && state == AnnounceDrawer.OPENING
                || drawer.isCloseBoxZero() && state == AnnounceDrawer.CLOSING
        ) {
            drawer.proceedState();
        }
        // 表示を閉じた後の処理
        else if(state == AnnounceDrawer.CLOSED) {
            printMessage("楽曲選択画面に移動します", 1);
            sceneTransition(Scene.SELECT_MUSIC);
        }

        // アニメーションタイマーの経過
        drawer.pastAnimationTimer();
    }
}
