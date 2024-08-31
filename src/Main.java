import hash.HashGenerator;
import save.SaveFileManager;
import scene.Scene;
import scene.SceneManager;
import data.DataElements;
import data.DataCaster;
import text.TextFilesManager;

import java.awt.event.KeyEvent;
import java.util.*;

class Main {

    // メインでやる事
    //      1. ゲーム内で運用するデータの初期化
    //      2. ウインドウを立ち上げて最初の場面を表示
    // 以上
    public static void main(String[] args){
        printMessage("データの初期化中　");

        // ゲーム内で使用するデータの要素と翻訳インスタンス
        DataCaster cast = new DataCaster();
        DataElements elem = new DataElements();
        SaveFileManager svManager = new SaveFileManager();

        // dataの初期化
        Map<Integer, Object> data = dataInit(args);

        // ここにセーブデータによる入出力を挟む
        svManager.makeSaveFile(data);
        data = svManager.applySaveFile(data);

        // ウインドウを立ち上げ、最初の場面を表示する
        SceneManager sceneManager = cast.getSceneManager(data);
        sceneManager.activateDisplay(data);

        // 開始シーンの指定で分岐
        if(cast.getScene(data) == Scene.PLAY_MUSIC) {
            String fileName = cast.getStrData(data, elem.LOAD_FILE_NAME);
            printMessage(fileName + " で演奏ゲームを開始します.");
        }

        printMessage("データの初期化完了");
    }

    // ゲーム内で運用するデータの初期化
    private static Map<Integer, Object> dataInit(String[] args) {
        Map<Integer, Object> data = new HashMap<>();
        DataElements elem = new DataElements();
        DataCaster cast = new DataCaster();
        SceneManager sceneManager = new SceneManager();  // 後に立ち上げるウインドウのマネージャ
        int len = args.length;

        // シーン切り替え時に稼働するインスタンスの場所を埋め込んでおく
        data.put(elem.SCENE_MANAGER, sceneManager);

        // ウインドウの設定
        data.put(elem.WINDOW_NAME,    "オルゴールプレーヤ v0.0.5");
        data.put(elem.DISPLAY_WIDTH,  400);
        data.put(elem.DISPLAY_HEIGHT, 500);
        data.put(elem.DISPLAY_X,      500);
        data.put(elem.DISPLAY_Y,      300);

        // 使用ディレクトリ
        data.put(elem.FILE_SAVE_DATA,       "saveData.txt");
        data.put(elem.DIRECTORY_SAVE_DATA,  "_save");
        data.put(elem.DIRECTORY_PUNCH_CARD, "_punchCard");
        data.put(elem.DIRECTORY_SOUNDS,     "_sounds");
        data.put(elem.DIRECTORY_SE,         "_se");
        data.put(elem.DIRECTORY_PREVIEW,    "_preview");

        data.put(elem.SCENE_ID, 1);

        data.put(elem.SELECT_MUSIC_CURSOR, 0);

        data.put(elem.NOTE_MOVE_TIME_OFFSET, 4000);
        data.put(elem.NOTE_UNIT_MOVE, 0.10F);

        data.put(elem.FRAME_RATE, 60); // フレームレート
        data.put(elem.MASTER_VOLUME, 1.0F); //主音量
        data.put(elem.JUDGEMENT_SUB_DISPLAY, 1); // 判定のサブ表示

        // 演奏ゲームで使用するキー
        data.put(elem.KEY_CONFIG_PLAY_RIGHT, new int[]{
                KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L
        });
        data.put(elem.KEY_CONFIG_PLAY_LEFT,  new int[]{
                KeyEvent.VK_F, KeyEvent.VK_D, KeyEvent.VK_S
        });

        data.put(elem.ACHIEVEMENT_POINT, 0); // なんかやり込みポイント的なやつ
        data.put(elem.PLAY_COUNT, 0); // プレー回数

        // TODO: MUSIC_HASH_VALUEを読み込み、以下のデータを作成する
        data.put(elem.TROPHY, new ArrayList<Integer>() ); // トロフィー
        data.put(elem.MUSIC_TROPHY, new ArrayList<String>() ); // 楽曲別トロフィー

        String directoryPunchCard = cast.getStrData(data, elem.DIRECTORY_PUNCH_CARD);
        List<String> fileNames = new TextFilesManager().getTextFileNames(directoryPunchCard);
        List<String> hashes = new ArrayList<>();
        for(String fileName : fileNames) {
            hashes.add(HashGenerator.getSha256(directoryPunchCard, fileName));
        }
        data.put(elem.MUSIC_HASH_VALUE, hashes); // SHA-256のリスト

        Map<String, String> playRecords = new HashMap<>();
         for(String hash : hashes) {
            playRecords.put(hash, "0,0.00/0,0.00/0,0.00"); // 空のプレー記録
        }
        data.put(elem.PLAY_RECORD, playRecords); // プレー記録

        data.put(elem.NEW_GENERAL_TROPHY, new ArrayList<>() );
        data.put(elem.NEW_MUSIC_TROPHY, null);

        // コマンドライン引数の有無で最初に表示する場面を変える
        if(len == 1 || len == 2) {
            String loadText = args[0];
            int playPart;
            if (len == 2) {
                int p = Integer.parseInt(args[1]);
                playPart = (p >= 0 && p <= 3) ? p : 0;
            } else {
                playPart = 0;
            }
            data.put(elem.SCENE, Scene.PLAY_MUSIC);
            data.put(elem.LOAD_FILE_NAME, loadText);
            data.put(elem.PLAY_PART, playPart);
        } else {
            data.put(elem.SCENE, Scene.SELECT_MUSIC);
            data.put(elem.PLAY_PART, 1);
        }

        return data;
    }

    // ログ出力用
    private static void printMessage(String msg) {
        System.out.println(msg + "\t@Main");
    }
}


