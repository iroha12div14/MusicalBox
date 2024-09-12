package scenes.viewtrophy;

import data.GameDataElements;
import data.GameDataIO;
import hash.HashGenerator;
import scene.Scene;
import scene.SceneBase;
import scenes.header.HeaderGetter;
import scenes.header.HeaderMaker;
import text.TextFilesManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTrophyScene extends SceneBase {
    // 描画用インスタンス
    private final ViewTrophyDrawer drawer = new ViewTrophyDrawer();

    // 試用キー一覧
    private static final List<Integer> KEY_ASSIGN = Arrays.asList(
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

    private final List<Integer> generalTrophies;
    private final List<String> musicTrophies;
    private final Map<String, String> musicTitles;

    // コン
    public ViewTrophyScene(GameDataIO dataIO) {
        init(KEY_ASSIGN, dataIO);
        data.put(GameDataElements.SCENE, Scene.VIEW_TROPHY);

        int displayWidth  = data.get(GameDataElements.DISPLAY_WIDTH,  Integer.class);
        int displayHeight = data.get(GameDataElements.DISPLAY_HEIGHT, Integer.class);
        drawer.setDisplaySize(displayWidth, displayHeight);
        drawer.setBlueprint();

        int frameRate = data.get(GameDataElements.FRAME_RATE, Integer.class);
        drawer.setAnimationTimer(frameRate);

        generalTrophies = data.getIntList(GameDataElements.TROPHY);
        musicTrophies   = data.getStrList(GameDataElements.MUSIC_TROPHY);

        // ハッシュ値と曲名の結び付け
        musicTitles = new HashMap<>();
        TextFilesManager txtManager = new TextFilesManager();
        Path dirPath = data.getDirectoryPathPath(GameDataElements.DIR_PUNCH_CARD);
        List<String> fileNames = txtManager.getTextFileNames(dirPath);

        for(String fileName : fileNames) {
            String filePathStr = data.getFilePathStr(GameDataElements.DIR_PUNCH_CARD, fileName);
            List<String> lines = txtManager.loadTextFile(filePathStr);
            Map<String, Object> header = new HeaderMaker().makeHeader(lines);
            String musicTitle = HeaderGetter.getTitle(header);

            Path filePathPath = data.getFilePathPath(GameDataElements.DIR_PUNCH_CARD, fileName);
            String sha256 = HashGenerator.getSha256(filePathPath);
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
        drawer.drawBackground(g2d); // 背景
        if(viewMode == 0) {
            drawer.drawGeneralTrophyView(g2d, generalTrophies, stdY); // 一般トロフィーの一覧
        }
        else if (viewMode == 1) {
            List<String> hashes = data.getStrList(GameDataElements.MUSIC_HASH_VALUE);
            drawer.drawMusicTrophyView(g2d, musicTrophies, stdY, hashes, musicTitles); // 楽曲別トロフィーの一覧
        }
        drawer.drawTitle(g2d); // タイトル
        drawer.drawScrollBar(g2d, stdY, viewMode); // スクロールバー
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
