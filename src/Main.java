import data.GameDataElements;
import data.GameDataIO;
import hash.HashGenerator;
import save.SaveDataManager;
import save.SaveFileManager;
import scene.Scene;
import scene.SceneManager;
import text.TextFilesManager;

import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Main {

    // メインでやる事
    //      1. ゲーム内で運用するデータの初期化
    //      2. ウインドウを立ち上げて最初の場面を表示
    // 以上
    public static void main(String[] args){
        printMessage("データの初期化中　");

        // ゲーム内で使用するデータの要素と翻訳インスタンス
        SaveFileManager svManager = new SaveFileManager();

        // dataの初期化
        GameDataIO dataIO = gameDataInit(args);

        // ここにセーブデータによる入出力を挟む
        svManager.makeSaveFile(dataIO);
        dataIO = svManager.applySaveFile(dataIO);

        // ウインドウを立ち上げ、最初の場面を表示する
        SceneManager sceneManager = dataIO.get(GameDataElements.SCENE_MANAGER, SceneManager.class);
        sceneManager.activateDisplay(dataIO);

        // 演奏ゲームから開始する場合はコンソールにそれ用の表示
        if(dataIO.get(GameDataElements.SCENE, Scene.class) == Scene.PLAY_MUSIC) {
            String fileAddress = dataIO.get(GameDataElements.LOAD_FILE_ADDRESS, String.class);
            String fileName;
            if(fileAddress.contains("\\") ) {
                String[] fileAddressArr = fileAddress.split("\\\\");
                fileName = fileAddressArr[fileAddressArr.length - 1];
            } else {
                fileName = fileAddress;
            }
            printMessage(fileName + " で演奏ゲームを開始します.");
        }

        printMessage("データの初期化完了");
    }

    // ゲーム内で運用するデータの初期化
    private static GameDataIO gameDataInit(String[] args) {
        GameDataIO dataIO = new GameDataIO();
        SceneManager sceneManager = new SceneManager();  // 後に立ち上げるウインドウのマネージャ
        int len = args.length;

        // シーン切り替え時に稼働するインスタンスの場所を埋め込んでおく
        dataIO.put(GameDataElements.SCENE_MANAGER, sceneManager, SceneManager.class);

        // ウインドウの設定
        dataIO.put(GameDataElements.WINDOW_NAME,    "オルゴールプレーヤ v0.1.0");
        dataIO.put(GameDataElements.DISPLAY_WIDTH,  400);
        dataIO.put(GameDataElements.DISPLAY_HEIGHT, 500);
        dataIO.put(GameDataElements.DISPLAY_X,      500);
        dataIO.put(GameDataElements.DISPLAY_Y,      300);

        // JARファイルのディレクトリを取得（文字型）
        Path exePath;
        try {
            exePath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String exeDir = exePath.getParent().toString();
        String assetsDir = "assets\\";
        String scoresDir = "musics\\";

        dataIO.put(GameDataElements.ROOT_DIRECTORY, exeDir);
        dataIO.put(GameDataElements.DIR_SAVE_DATA,  assetsDir + "save");
        dataIO.put(GameDataElements.DIR_SOUNDS,     assetsDir + "sounds");
        dataIO.put(GameDataElements.DIR_SE,         assetsDir + "se");
        dataIO.put(GameDataElements.DIR_PUNCH_CARD, scoresDir + "punchCard");
        dataIO.put(GameDataElements.DIR_PREVIEW,    scoresDir + "preview");

        dataIO.put(GameDataElements.FILE_SAVE_DATA, "saveData.txt");

        dataIO.put(GameDataElements.FILE_SOUND_OPEN_COVER, "open_cover01.wav");
        dataIO.put(GameDataElements.FILE_SOUND_KNOCK_BOOK, "knock_book01.wav");
        dataIO.put(GameDataElements.FILE_SOUND_SWIPE_PAGE, "page_swipe02.wav");

        dataIO.put(GameDataElements.SCENE_ID, 1);

        dataIO.put(GameDataElements.SELECT_MUSIC_CURSOR, 0);

        dataIO.put(GameDataElements.NOTE_MOVE_TIME_OFFSET, 4000);
        dataIO.put(GameDataElements.NOTE_UNIT_MOVE, 0.10F);

        dataIO.put(GameDataElements.FRAME_RATE, 60); // フレームレート
        dataIO.put(GameDataElements.MASTER_VOLUME, 1.0F); //主音量
        dataIO.put(GameDataElements.JUDGE_SUB_DISPLAY, 1); // 判定のサブ表示

        // 演奏ゲームで使用するキー
        dataIO.put(GameDataElements.KEY_CONFIG_PLAY_RIGHT, new int[]{
                KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L
        });
        dataIO.put(GameDataElements.KEY_CONFIG_PLAY_LEFT,  new int[]{
                KeyEvent.VK_F, KeyEvent.VK_D, KeyEvent.VK_S
        });

        dataIO.put(GameDataElements.ACHIEVEMENT_POINT, 0); // なんかやり込みポイント的なやつ
        dataIO.put(GameDataElements.PLAY_COUNT, 0); // プレー回数

        // TODO: MUSIC_HASH_VALUEを読み込み、以下のデータを作成する
        dataIO.putIntList(GameDataElements.TROPHY,       new ArrayList<>() ); // トロフィー
        dataIO.putStrList(GameDataElements.MUSIC_TROPHY, new ArrayList<>() ); // 楽曲別トロフィー

        Path dirPath = dataIO.getDirectoryPathPath(GameDataElements.DIR_PUNCH_CARD);
        List<String> fileNames = new TextFilesManager().getTextFileNames(dirPath);
        List<String> hashes = new ArrayList<>();
        for(String fileName : fileNames) {
            Path filePath = dataIO.getFilePathPath(GameDataElements.DIR_PUNCH_CARD, fileName);
            hashes.add(HashGenerator.getSha256(filePath) );
        }
        dataIO.putStrList(GameDataElements.MUSIC_HASH_VALUE, hashes); // SHA-256のリスト

        Map<String, String> playRecords = new HashMap<>();
         for(String hash : hashes) {
            playRecords.put(hash, SaveDataManager.playRecordDefault() ); // 空のプレー記録
        }
        dataIO.putHashedPlayRecords(GameDataElements.PLAY_RECORD, playRecords); // プレー記録

        dataIO.putIntList(GameDataElements.NEW_GENERAL_TROPHY, new ArrayList<>() );
        dataIO.put(GameDataElements.NEW_MUSIC_TROPHY, null, String.class);

        // コマンドライン引数の有無で最初に表示する場面を変える
        if(len == 1 || len == 2) {
            String fileAddress = args[0];
            int playPart;
            if (len == 2) {
                int p = Integer.parseInt(args[1]);
                playPart = (p >= 0 && p <= 3) ? p : 0;
            } else {
                playPart = 0;
            }
            dataIO.put(GameDataElements.SCENE, Scene.PLAY_MUSIC);
            dataIO.put(GameDataElements.LOAD_FILE_ADDRESS, fileAddress);
            dataIO.put(GameDataElements.PLAY_PART, playPart);
        } else {
            dataIO.put(GameDataElements.SCENE, Scene.SELECT_MUSIC);
            dataIO.put(GameDataElements.PLAY_PART, 1);
        }

        return dataIO;
    }

    // ログ出力用
    private static void printMessage(String msg) {
        System.out.println(msg + "\t@Main");
    }
}


