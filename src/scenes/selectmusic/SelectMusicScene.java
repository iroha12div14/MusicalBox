package scenes.selectmusic;

import hash.HashGenerator;
import scenes.header.HeaderMaker;
import scene.Scene;
import scene.SceneBase;
import scenes.animtimer.AnimationTimer;
import scenes.selectmusic.preview.SongPreviewManager;
import scenes.se.SoundEffectManager;
import calc.CalcUtil;
import text.TextFilesManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMusicScene extends SceneBase {
    //コンストラクタ
    public SelectMusicScene(Map<Integer, Object> data) {
        // キーアサインの初期化とデータ渡し
        init(keyAssign, data);
        printMessage("楽曲選択初期化中　", 2);

        this.data.put(elem.SCENE, Scene.SELECT_MUSIC);

        playPart = cast.getIntData(this.data, elem.PLAY_PART);
        cursor   = cast.getIntData(this.data, elem.SELECT_MUSIC_CURSOR);

        frameRate = cast.getIntData(this.data, elem.FRAME_RATE);

        // アニメーションじゃないけどタイマー
        keyReleaseTimer             = new AnimationTimer(frameRate, 300, false);
        holdUpKeyRapidInputTimer    = new AnimationTimer(frameRate, 18, false);
        holdDownKeyRapidInputTimer  = new AnimationTimer(frameRate, 18, false);
        rapidInputLoopTimer         = new AnimationTimer(frameRate, 6, true);

        // パンチカードのデータを読み込み、ヘッダ部分をデータ化
        printMessage("ヘッダの読み込み中", 2);
        String directoryPunchCard = cast.getStrData(this.data, elem.DIRECTORY_PUNCH_CARD);
        TextFilesManager txtManager = new TextFilesManager();
        Map<String, List<String>> textFiles = txtManager.loadTextFiles(directoryPunchCard); // <FileName, <TextFile>>
        List<String> textFileNames = txtManager.getTextFileNames(directoryPunchCard);

        // ヘッダ（楽曲データ）を束ねる
        int musicCount = textFileNames.size();
        Map<String, Map<String, Object> > musicHeaders = new HashMap<>(); // <Hash, <FileName, Data>>
        String[] hashes = new String[musicCount];
        musicFileNames = new String[musicCount];
        int f = 0;
        for(String fileName : textFileNames) {
            musicFileNames[f] = fileName;
            hashes[f] = HashGenerator.getSha256(directoryPunchCard, fileName);
            List<String> textFile = textFiles.get(fileName);
            Map<String, Object> header = new HeaderMaker().makeHeader(textFile);

            header.put("FILE_NAME", fileName);
            musicHeaders.put(hashes[f], header);
            f++;
        }

        // プレー記録を抽出して格納
        Map<String, String> playRecordAll = cast.getHashedStringData(this.data, elem.PLAY_RECORD);
        int[][] playStates = new int[musicCount][3];
        float[][] achievements = new float[musicCount][3];
        int h = 0;
        for(String hash : hashes) {
            String playRecordStr = playRecordAll != null
                    ? playRecordAll.get(hash)
                    : "0,0.00/0,0.00/0,0.00"; // こんな感じの文法の文字型データをバラして格納する
            String[] playRecordSplit = playRecordStr.split("/");
            int p = 0;
            int[] ps = new int[3];
            float[] acv = new float[3];
            for(String record : playRecordSplit) {
                String[] rec = record.split(",");
                ps[p] = Integer.parseInt(rec[0]);
                acv[p] = Float.parseFloat(rec[1]);
                p++;
            }
            playStates[h] = ps;
            achievements[h] = acv;
            h++;
        }

        // 描画用インスタンスの生成と設定
        int frameRate = cast.getDisplayFrameRate(this.data);
        int displayWidth = cast.getDisplayWidth(this.data);
        int displayHeight = cast.getDisplayHeight(this.data);
        drawer = new SelectMusicDrawer();                   // 必要なデータをコンの引数じゃなくて雪駄で渡すことにした
        drawer.setAnimationTimer(frameRate);                // アニメーションタイマー
        drawer.setDisplaySize(displayWidth, displayHeight); // 画面サイズ
        drawer.setBlueprint();                              // 設計図
        drawer.setMusicHeaders(musicHeaders, hashes);       // ヘッダ（楽曲データ）
        drawer.setPlayRecord(playStates, achievements);     // プレー記録

        // SEの読み込み
        String directorySE = cast.getStrData(this.data, elem.DIRECTORY_SE);
        String[] seFileName = {SE_KNOCK, SE_SWIPE, SE_DECIDE};
        seManager = new SoundEffectManager(directorySE, seFileName);
        seManager.loadWaveFile();
        seManager.setMasterVolume(cast.getFloatData(this.data, elem.MASTER_VOLUME) ); // 主音量を設定

        // プレビューの読み込み
        String directoryPreview = cast.getStrData(this.data, elem.DIRECTORY_PREVIEW);
        prvManager = new SongPreviewManager(directoryPreview, musicFileNames);
        prvManager.loadWaveFile();
        prvManager.setMasterVolume(cast.getFloatData(this.data, elem.MASTER_VOLUME) ); // 主音量を設定
        prvManager.readyStartPreview(); // 初期カーソル曲を再生

        printMessage("設定の受け渡し完了", 2);
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {
        if(!drawer.isEndFadeOut() ) {
            drawer.drawBack(g2d); // 背景
            drawer.drawPointer(g2d, titleBarMoveDirection); // ポインタ
            drawer.drawCursor(g2d); // カーソル
            drawer.drawTitleBar(g2d, playPart, cursor, titleBarMoveDirection); // 曲名バー
            drawer.drawExplain(g2d); // 操作説明パーツ
            drawer.drawFrameRate(g2d, fru.msgFPS(false), fru.msgLatency(500) ); // フレームレート
            drawer.drawMusicDescBack(g2d); // 曲情報背景
            if(drawer.getTitleBarAnimTimer() < 2) {
                drawer.drawMusicDesc(g2d, playPart, cursor); // 曲情報
            }
            drawer.drawDirector(g2d); // ディレクタ
            drawer.drawFadeIn(g2d); // フェードイン
        }

        drawer.drawFadeOut(g2d); // シーン転換
        if(drawer.isEndFadeOut() ) {
            drawer.drawTitleAfterFadeOut(g2d, playPart, cursor); // シーン転換後の曲名表示
        }
    }

    // 毎フレーム処理したい内容はここ(主にキー入力とタイマーの処理関連)
    @Override
    protected void actionField() {
        // 何故かdrawer・prvManagerコンストラクタの生成より先にactionFieldが走ってるっぽいので
        // とりあえずdrawer == null, prvManager == null の場合を誤魔化してる
        if(drawer != null && prvManager != null) {
            // シーン転換中はキー操作を受け付けない
            if (!sceneTransition) {
                // キー入力構文の表記省略
                boolean pressUpKey    = key.getKeyPress(KeyEvent.VK_UP);
                boolean pressDownKey  = key.getKeyPress(KeyEvent.VK_DOWN);
                boolean pressSpaceKey = key.getKeyPress(KeyEvent.VK_SPACE);
                boolean pressShiftKey = key.getKeyPress(KeyEvent.VK_SHIFT);
                boolean pressEnterKey = key.getKeyPress(KeyEvent.VK_ENTER);
                boolean pressT        = key.getKeyPress(KeyEvent.VK_T);
                boolean holdUpKey     = key.getKeyHold(KeyEvent.VK_UP);
                boolean holdDownKey   = key.getKeyHold(KeyEvent.VK_DOWN);
                boolean releaseAllKey = !key.isAnyKeyPress();

                // キー操作に対する動作
                // 曲名バーの移動中(正確には着地3F前)にはキー入力を受け付けない
                if (drawer.getTitleBarAnimTimer() < 3) {
                    if (pressDownKey) {
                        moveCursor(DIR_DOWN);           // ↓キーで選択楽曲の変更
                    } else if (pressUpKey) {
                        moveCursor(DIR_UP);             // ↑キーで選択楽曲の変更
                    } else if (pressSpaceKey) {
                        changePart();                   // Spaceキーでパートの変更
                    } else if (pressShiftKey) {
                        optionSceneTransition();        // Shiftキーでオプション画面に遷移
                    } else if (pressT) {
                        viewTrophySceneTransition();    // Tキーでトロフィー画面に遷移
                    } else if (pressEnterKey) {
                        selectMusic();                  // Enterキーで楽曲を決定する
                    }
                }
                // キー継続押下による連射入力
                if(holdDownKey) {
                    holdDownKeyRapidInputTimer.pass();
                } else {
                    holdDownKeyRapidInputTimer.reset();
                }
                if(holdUpKey) {
                    holdUpKeyRapidInputTimer.pass();
                } else {
                    holdUpKeyRapidInputTimer.reset();
                }
                if(holdDownKey || holdUpKey) {
                    rapidInputLoopTimer.pass();
                } else {
                    rapidInputLoopTimer.reset();
                }
                if(rapidInputLoopTimer.isZero() ) {
                    if(holdDownKeyRapidInputTimer.isZero() ) {
                        moveCursor(DIR_DOWN);
                    }
                    else if(holdUpKeyRapidInputTimer.isZero() ) {
                        moveCursor(DIR_UP);
                    }
                }

                // 何もキーを押していない間にそれ用のタイマーが加算
                if(releaseAllKey) {
                    keyReleaseTimer.pass();
                } else {
                    keyReleaseTimer.reset();
                }

                // プレビュー再生
                if(drawer.getTitleBarAnimTimer() == 0 && prvManager.isReadyPreview() ) {
                    int pointer = getPointer(cursor);
                    String previewFile = musicFileNames[pointer];
                    prvManager.startPreview(previewFile);
                }
            }
            // 暗転し曲名が表示されたタイミング
            else if(drawer.isEndFadeOut() ) {
                if( !drawer.isEndSceneTransition() ) {
                    prvManager.stopPreview(); // プレビューを停止
                }
                // シーン転換の終わりに演奏ゲーム画面に遷移
                else {
                    musicGameSceneTransition();
                }
            }

            // アニメーションタイマーの経過
            drawer.pastAnimationTimer(
                    sceneTransition,
                    keyReleaseTimer.isZero()
            );

        }
        // drawerとprvManagerが初期化されるまではここを通る
        else {
            int f = frameRate == 0 ? 60 : frameRate;
            if(fru.getPastFrame() % f == 0) {
                printMessage("    初期化中...", 2);
            }
        }
    }

    // ------------------------------------------------------ //

    // 曲名バーを動かす
    private void moveCursor(int dir) {
        cursor += dir;
        titleBarMoveDirection = dir;
        drawer.startTitleBarAnimTimer();
        prvManager.stopPreview();
        seManager.startSound(SE_SWIPE);
        prvManager.readyStartPreview();
    }

    // パートの変更
    private void changePart() {
        playPart = calc.mod(playPart + 1, PART_KIND);
        seManager.startSound(SE_KNOCK);
    }

    // ポインタを取得
    private int getPointer(int cursor) {
        return calc.mod(cursor, musicFileNames.length);
    }

    // ------------------------------------------------------ //

    // 楽曲を選ぶ
    private void selectMusic() {
        setPlayMusicData();
        sceneTransition = true;
        seManager.startSound(SE_DECIDE);
    }

    // オプション画面・演奏ゲーム画面へのシーン転換
    private void optionSceneTransition() {
        prvManager.stopPreview(); // プレビューを停止
        setSelectMusicData();
        sceneTrans("オプション", Scene.OPTION);
    }
    // 実績一覧画面へのシーン転換
    private void viewTrophySceneTransition() {
        prvManager.stopPreview(); // プレビューを停止
        setSelectMusicData();
        sceneTrans("トロフィー", Scene.VIEW_TROPHY);
    }
    // 演奏ゲーム画面へのシーン転換
    private void musicGameSceneTransition() {
        sceneTrans("演奏ゲーム", Scene.PLAY_MUSIC);
    }
    // 各画面へのシーン転換の共通内容
    private void sceneTrans(String sceneName, Scene scene) {
        printMessage(sceneName + "画面に移動します", 1);
        prvManager.closeClips();
        sceneTransition(scene);
    }

    // 次のシーンに引き継ぐデータを作成
    private void setPlayMusicData() {
        int pointer = getPointer(cursor);
        data.put(elem.LOAD_FILE_NAME, musicFileNames[pointer]);
        setSelectMusicData();
    }
    private void setSelectMusicData() {
        data.put(elem.SELECT_MUSIC_CURSOR, cursor);
        data.put(elem.PLAY_PART, playPart);
    }


    // -------------------------------------------------------------------- //

    // インスタンスあれこれ
    private final SelectMusicDrawer drawer;
    private final SoundEffectManager seManager;
    private final SongPreviewManager prvManager;

    protected final CalcUtil calc = new CalcUtil();

    // フレームレートとキー操作用タイマー
    private final int frameRate;
    private final AnimationTimer keyReleaseTimer;
    private final AnimationTimer holdUpKeyRapidInputTimer;
    private final AnimationTimer holdDownKeyRapidInputTimer;
    private final AnimationTimer rapidInputLoopTimer;

    // 楽曲ファイル名
    private final String[] musicFileNames;

    // シーン転換(曲決定するまで停止)
    private boolean sceneTransition = false;

    // カーソル移動もろもろ
    private static final int DIR_UP = -1;
    private static final int DIR_DOWN = 1;
    private int cursor;
    private int titleBarMoveDirection = 0; // DIR_UP/DIR_DOWN

    // 演奏パート
    private static final int PART_KIND = 4;
    private int playPart;

    // 効果音(予約語)
    private static final String SE_KNOCK  = "knock_book01.wav";
    private static final String SE_SWIPE  = "open_cover01.wav";
    private static final String SE_DECIDE = "page_swipe02.wav";

    // -------------------------------------------------------------------- //

    // キーアサインの初期化
    private static final List<Integer> keyAssign = Arrays.asList(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, // 曲名バー移動
            KeyEvent.VK_SPACE,  // パート変更
            KeyEvent.VK_SHIFT,  // オプション
            KeyEvent.VK_T,      // トロフィー
            KeyEvent.VK_ENTER   // 曲決定
    );

}
