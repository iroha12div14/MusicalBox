package scenes.viewtrophy;

import hash.HashGenerator;
import scene.Scene;
import scene.SceneBase;
import scenes.header.HeaderGetter;
import scenes.header.HeaderMaker;
import text.TextFilesManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTrophyScene extends SceneBase {
    // 描画用インスタンス
    ViewTrophyDrawer drawer = new ViewTrophyDrawer();

    // 試用キー一覧
    private static final List<Integer> keyAssign = Arrays.asList(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_SPACE,
            KeyEvent.VK_ENTER
    );

    // 定数
    private static final int UP   = -1;
    private static final int DOWN = 1;
    private static final int MOVE_Y = 10;

    private int stdY; // トロフィー一覧のY座標
    private int limitY; // トロフィー一覧の長さ
    private int viewMode = 0;

    List<Integer> generalTrophies;
    List<String> musicTrophies;
    Map<String, String> musicTitles;

    // コン
    public ViewTrophyScene(Map<Integer, Object> data) {
        init(keyAssign, data);

        int displayWidth  = cast.getDisplayWidth(this.data);
        int displayHeight = cast.getDisplayHeight(this.data);
        drawer.setDisplaySize(displayWidth, displayHeight);
        drawer.setBlueprint();

        int frameRate = cast.getIntData(this.data, elem.FRAME_RATE);
        drawer.setAnimationTimer(frameRate);

        generalTrophies = cast.getIntListData(this.data, elem.TROPHY);
        musicTrophies = cast.getStrListData(this.data, elem.MUSIC_TROPHY);

        // ハッシュ値と曲名の結び付け
        musicTitles = new HashMap<>();
        TextFilesManager txtManager = new TextFilesManager();
        String directory = cast.getStrData(this.data, elem.DIRECTORY_PUNCH_CARD);
        List<String> fileNames = txtManager.getTextFileNames(directory);
        for(String fileName : fileNames) {
            List<String> lines = txtManager.loadTextFile(directory, fileName);
            Map<String, Object> header = new HeaderMaker().makeHeader(lines);
            String musicTitle = HeaderGetter.getTitle(header);

            String sha256 = HashGenerator.getSha256(directory, fileName);
            musicTitles.put(sha256, musicTitle);
        }

        stdY = 0;
        limitY = drawer.getTrophyListHeight(viewMode);
    }

    // 一覧を上下に動かす
    private void moveView(int dir) {
        if(stdY > 0 && dir == UP || stdY < limitY && dir == DOWN) {
            stdY += dir * MOVE_Y;
        }
    }

    // 表示内容の切り替え
    private void changeMode() {
        viewMode = 1 - viewMode;
        stdY = 0;
        limitY = drawer.getTrophyListHeight(viewMode);
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {
        if(drawer != null) {
            drawer.drawBackground(g2d); // 背景
            if(viewMode == 0) {
                drawer.drawGeneralTrophyView(g2d, generalTrophies, stdY); // 一般トロフィーの一覧
            } else if (viewMode == 1) {
                List<String> hashes = cast.getStrListData(data, elem.MUSIC_HASH_VALUE);
                drawer.drawMusicTrophyView(g2d, musicTrophies, stdY, hashes, musicTitles); // 楽曲別トロフィーの一覧
            }
            drawer.drawTitle(g2d); // タイトル
            drawer.drawScrollBar(g2d, stdY, viewMode); // スクロールバー
        }
    }

    // 毎フレーム処理したい内容はここ
    @Override
    protected void actionField() {
        boolean keyPressEnter = key.getKeyPress(KeyEvent.VK_ENTER);
        boolean keyPressSpace = key.getKeyPress(KeyEvent.VK_SPACE);
        boolean keyHoldUp     = key.getKeyHold(KeyEvent.VK_UP);
        boolean keyHoldDown   = key.getKeyHold(KeyEvent.VK_DOWN);

        if(keyPressEnter) { // Enterキーで楽曲選択に戻る
            printMessage("楽曲選択に戻ります", 2);
            sceneTransition(Scene.SELECT_MUSIC);
        }
        else if(keyPressSpace) { // Spaceキーでトロフィー表示の切り替え
            changeMode();
        }
        // 上下キーで
        if(keyHoldUp) {
            moveView(UP);
        }
        else if (keyHoldDown) {
            moveView(DOWN);
        }
    }
}
