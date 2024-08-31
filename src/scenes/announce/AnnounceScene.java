package scenes.announce;

import scene.Scene;
import scene.SceneBase;
import scenes.announce.AnnounceDrawer;
import trophy.TrophyList;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnounceScene extends SceneBase {
    // 使用キー
    private final List<Integer> keyAssign = Arrays.asList(KeyEvent.VK_ENTER);

    // 描画用インスタンス
    private final AnnounceDrawer drawer = new AnnounceDrawer();

    // 新規実績の一覧
    private final List<String> newTrophiesStr = new ArrayList<>();

    public AnnounceScene(Map<Integer, Object> data) {
        init(keyAssign, data);

        // 新規トロフィー
        List<Integer> newGeneralTrophy = cast.getIntListData(this.data, elem.NEW_GENERAL_TROPHY);
        String newMusicTrophy = cast.getStrData(this.data, elem.NEW_MUSIC_TROPHY);

        int displayWidth  = cast.getDisplayWidth(this.data);
        int displayHeight = cast.getDisplayHeight(this.data);
        drawer.setDisplaySize(displayWidth, displayHeight);
        drawer.setBlueprint();
        int frameRate = cast.getDisplayFrameRate(this.data);
        drawer.setAnimationTimer(frameRate);

        // 新規実績解除が無いなら選曲画面にリダイレクト
        if(newGeneralTrophy.isEmpty() && newMusicTrophy == null) {
            printMessage("新規トロフィー無し", 2);
            drawer.endState();
        }
        else {
            for(int trophy : newGeneralTrophy) {
                newTrophiesStr.add(TrophyList.getGenTrophy(trophy));
            }
            if(newMusicTrophy != null) {
                newTrophiesStr.add(TrophyList.getMusicTrophy(newMusicTrophy));
            }

            // 表示済のデータを消去
            this.data.put(elem.NEW_GENERAL_TROPHY, new ArrayList<Integer>() );
            this.data.put(elem.NEW_MUSIC_TROPHY, null);
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
