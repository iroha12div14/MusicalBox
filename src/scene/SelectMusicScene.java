package scene;

import playmusic.timeline.HeaderGetter;
import playmusic.timeline.HeaderMaker;
import selectmusic.drawer.SelectMusicDrawer;
import txt.TextFilesManager;
import wav.SongPreviewManager;
import wav.SoundEffectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SelectMusicScene extends SceneBase {
    //コンストラクタ
    public SelectMusicScene(Map<Integer, Object> data) {
        // キーアサインの初期化とデータ渡し
        init(keyAssign, data);
        printMessage("楽曲選択初期化中　", 2);

        this.data.put(elem.SCENE, Scene.SELECT_MUSIC_SCENE);

        playPart = cast.getIntData(this.data, elem.PLAY_PART);
        cursor   = cast.getIntData(this.data, elem.SELECT_MUSIC_CURSOR);

        frameRate = cast.getIntData(this.data, elem.FRAME_RATE);

        // パンチカードのデータを読み込み、ヘッダ部分をデータ化
        String directoryPunchCard = cast.getStrData(this.data, elem.DIRECTORY_PUNCH_CARD);
        TextFilesManager txtManager = new TextFilesManager(directoryPunchCard);
        Map<String, List<String>> textFiles = txtManager.loadTextFiles();
        List<String> textFileNames = txtManager.getTextFileNames();

        printMessage("ヘッダの読み込み中", 2);
        HeaderMaker hdMaker = new HeaderMaker();
        HeaderGetter hdGetter = new HeaderGetter();
        musicCount = textFileNames.size();
        musicFileNames = new String[musicCount];
        musicTitles = new String[musicCount];
        musicTempos = new int[musicCount];
        mainDifficulties = new int[musicCount];
        subDifficulties = new int[musicCount];
        int f = 0;
        for(String fileName : textFileNames) {
            musicFileNames[f] = fileName;

            List<String> textFile = textFiles.get(fileName);
            Map<String, Object> header = hdMaker.makeHeader(textFile);
            musicTitles[f]      = hdGetter.getTitle(header);
            musicTempos[f]      = hdGetter.getTempo(header);
            mainDifficulties[f] = hdGetter.getLevel(header)[0];
            subDifficulties[f]  = hdGetter.getLevel(header)[1];
            f++;
        }

        // 描画用インスタンス
        drawer = new SelectMusicDrawer(
                this.data,
                fru,
                key,
                musicTitles,
                musicTempos,
                mainDifficulties,
                subDifficulties
        );

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
        if(!drawer.isOverStripeAtPow() ) {
            drawer.drawBack(g2d); // 背景
            drawer.drawPointer(g2d, titlebarMovDir); // ポインタ
            drawer.drawCursor(g2d); // カーソル
            drawer.drawTitleBar(g2d, playPart, cursor, titlebarMovDir); // 曲名バー
            drawer.drawExplain(g2d); // 操作説明パーツ
            drawer.drawMusicDescBack(g2d); // 曲情報背景
            if(drawer.getTitlebarAnimTimer() < 2) {
                drawer.drawMusicDesc(g2d, playPart, cursor); // 曲情報
            }
            drawer.drawDirector(g2d); // ディレクタ
            drawer.drawFadeIn(g2d); // フェードイン
        }

        drawer.drawSceneTransition(g2d); // シーン転換
        if(drawer.isOverStripeAtPow() ) {
            drawer.drawTitleAfterSceneTransition(g2d, playPart, cursor); // シーン転換後の曲名表示
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
                boolean holdUpKey     = key.getKeyHold(KeyEvent.VK_UP);
                boolean holdDownKey   = key.getKeyHold(KeyEvent.VK_DOWN);
                boolean releaseAllKey = !key.isAnyKeyPress();

                // キー操作に対する動作
                // 曲名バーの移動中(正確には着地3F前)にはキー入力を受け付けない
                if (drawer.getTitlebarAnimTimer() < 3) {
                    if (pressUpKey) {
                        moveCursor(DIR_UP);         // ↑キーで選択楽曲の変更
                    } else if (pressDownKey) {
                        moveCursor(DIR_DOWN);       // ↓キーで選択楽曲の変更
                    } else if (pressSpaceKey) {
                        changePart();               // Spaceキーでパートの変更
                    } else if (pressShiftKey) {
                        optionSceneTransition();    // Shiftキーでオプション画面に遷移
                    } else if (pressEnterKey) {
                        selectMusic();              // Enterキーで楽曲を決定する
                    }
                }
                // キー継続押下による連射入力
                UpKeyHoldRapidInputTimer = holdUpKey   ? UpKeyHoldRapidInputTimer + 1 : 0;
                DownKeyHoldRapidInputTimer = holdDownKey ? DownKeyHoldRapidInputTimer + 1 : 0;
                if (UpKeyHoldRapidInputTimer >= RAPID_INPUT_TIMER_LOWER_LIMIT * frameRate / 60
                        && UpKeyHoldRapidInputTimer % (RAPID_INPUT_TIMER_LOOP * frameRate / 60) == 0) {
                    moveCursor(DIR_UP);
                }
                if (DownKeyHoldRapidInputTimer >= RAPID_INPUT_TIMER_LOWER_LIMIT * frameRate / 60
                        && DownKeyHoldRapidInputTimer % (RAPID_INPUT_TIMER_LOOP * frameRate / 60) == 0) {
                    moveCursor(DIR_DOWN);
                }

                // 何もキーを押していない間にそれ用のタイマーが加算
                keyReleaseTimer = releaseAllKey ? keyReleaseTimer + 1 : 0;

                // プレビュー再生
                if(drawer.getTitlebarAnimTimer() == 0 && prvManager.isReadyPreview() ) {
                    String previewFile = musicFileNames[getPointer(cursor)];
                    prvManager.startPreview(previewFile);
                }
            }
            // 暗転し曲名が表示されたタイミング
            else if(drawer.isOverStripeAtPow() ) {
                if( !drawer.isOverSceneTransitionAnimTimer() ) {
                    prvManager.stopPreview(); // プレビューを停止
                }
                // シーン転換の終わりに演奏ゲーム画面に遷移
                else {
                    musicGameSceneTransition();
                }
            }

            // アニメーションタイマーの経過
            drawer.decAnimTimer(
                    sceneTransition,
                    keyReleaseTimer >= KEY_RELEASE_TIMER_SET * frameRate  / 60
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
        titlebarMovDir = dir;
        drawer.startTitlebarAnimTimer();
        prvManager.stopPreview();
        seManager.startSound(SE_SWIPE);
        prvManager.readyStartPreview();
    }

    // パートの変更
    private void changePart() {
        playPart = calc.mod(playPart + 1, PART_KIND);
        seManager.startSound(SE_KNOCK);
    }

    // 楽曲を選ぶ
    private void selectMusic() {
        inputData();
        sceneTransition = true;
        seManager.startSound(SE_DECIDE);
    }

    // オプション画面・演奏ゲーム画面へのシーン転換
    private void optionSceneTransition() {
        prvManager.stopPreview(); // プレビューを停止
        data.put(elem.SELECT_MUSIC_CURSOR, cursor);
        data.put(elem.PLAY_PART, playPart);
        sceneTrans("オプション", Scene.OPTION_SCENE);
    }
    private void musicGameSceneTransition() {
        sceneTrans("演奏ゲーム", Scene.PLAY_MUSIC_SCENE);
    }
    private void sceneTrans(String sceneName, Scene scene) {
        printMessage(sceneName + "画面に移動します", 2);
        prvManager.closeClips();
        sceneTransition(scene);
    }

    // ------------------------------------------------------ //

    // ポインタを取得
    private int getPointer(int cursor) {
        return calc.mod(cursor, musicCount);
    }

    // 演奏レベルの取得
    private int getDifLevel(int playPart, int pointer) {
        int mainDifficulty = mainDifficulties[pointer];
        int subDifficulty  = subDifficulties[pointer];
        return switch (playPart) {
            case MAIN_PART -> mainDifficulty;
            case SUB_PART  -> subDifficulty;
            case ALL_PART  -> mainDifficulty + subDifficulty;
            default -> 0; // 自動演奏時
        };
    }

    // 次のシーンに引き継ぐデータを作成
    private void inputData() {
        data.put(elem.MUSIC_TITLE, musicTitles[getPointer(cursor)]);
        data.put(elem.PLAY_PART, playPart);
        data.put(elem.PLAY_LEVEL, getDifLevel(playPart, getPointer(cursor)) );
        data.put(elem.LOAD_FILE_NAME, musicFileNames[getPointer(cursor)]);
        data.put(elem.SELECT_MUSIC_CURSOR, cursor);
    }

    // -------------------------------------------------------------------- //

    // インスタンスあれこれ
    private final SelectMusicDrawer drawer;
    private final SoundEffectManager seManager;
    private final SongPreviewManager prvManager;

    // 楽曲データ
    private final int musicCount;
    private final String[] musicFileNames;
    private final String[] musicTitles;
    private final int[] musicTempos;
    private final int[] mainDifficulties;
    private final int[] subDifficulties;

    private final int frameRate;

    // シーン転換(曲決定するまで停止)
    private boolean sceneTransition = false;

    // カーソル移動もろもろ
    private static final int DIR_UP = -1;
    private static final int DIR_DOWN = 1;
    private int cursor;
    private int titlebarMovDir = 0; // DIR_UP/DIR_DOWN

    // 演奏パート
    private static final int PART_KIND = 4;
    private static final int MAIN_PART = 1;
    private static final int SUB_PART  = 2;
    private static final int ALL_PART  = 3;
    private int playPart;

    // キー操作用タイマー
    private static final int KEY_RELEASE_TIMER_SET = 300;
    private static final int RAPID_INPUT_TIMER_LOWER_LIMIT = 18; // 継続入力時間[F]
    private static final int RAPID_INPUT_TIMER_LOOP = 6; // 連射入力間隔[F]
    private int keyReleaseTimer = 0;
    private int UpKeyHoldRapidInputTimer = 0;
    private int DownKeyHoldRapidInputTimer = 0;

    // 効果音(予約語)
    private final String SE_KNOCK  = "knock_book01.wav";
    private final String SE_SWIPE  = "open_cover01.wav";
    private final String SE_DECIDE = "page_swipe02.wav";

    // -------------------------------------------------------------------- //

    // キーアサインの初期化
    private static final List<Integer> keyAssign = Arrays.asList(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, // 曲名バー移動
            KeyEvent.VK_SPACE, // パート変更
            KeyEvent.VK_SHIFT, // オプション(搭載予定)
            KeyEvent.VK_ENTER  // 曲決定
    );

}
