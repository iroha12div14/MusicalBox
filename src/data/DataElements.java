package data;

// 各シーンで運用されるMap型変数dataのキーにあたる。
public class DataElements {
    // [1 - 9] 場面の管理
    public final int SCENE_MANAGER  = 1; // SceneManager
    public final int SCENE          = 2; // Scene
    public final int SCENE_ID       = 3; // int

    // [11 - 19] ウインドウの名前やサイズ
    public final int WINDOW_NAME    = 10; // String
    public final int DISPLAY_WIDTH  = 11; // int
    public final int DISPLAY_HEIGHT = 12; // int
    public final int DISPLAY_X      = 13; // int
    public final int DISPLAY_Y      = 14; // int

    // [21 - 29] 使用ファイル名、ディレクトリ名
    public final int FILE_SAVE_DATA         = 20;
    public final int DIRECTORY_SAVE_DATA    = 21;
    public final int DIRECTORY_SOUNDS       = 22; // String
    public final int DIRECTORY_PUNCH_CARD   = 23; // String
    public final int DIRECTORY_SE           = 24; // String
    public final int DIRECTORY_PREVIEW      = 25; // String

    // [31 - 39] 保存するデータ
    public final int FRAME_RATE             = 31; // int
    public final int MASTER_VOLUME          = 32; // float
    public final int JUDGEMENT_SUB_DISPLAY  = 33; // int
    public final int NOTE_UNIT_MOVE         = 34; // float

    // [41 - 49] キーコンフィグ
    public final int KEY_CONFIG_PLAY_LEFT   = 41; // int[]
    public final int KEY_CONFIG_PLAY_RIGHT  = 42; // int[]

    // [51 - 59] 実績関係
    public final int ACHIEVEMENT_POINT  = 51; // int
    public final int PLAY_COUNT         = 52; // int
    public final int TROPHY             = 54; // List<Integer> (int:TrophyList)
    public final int MUSIC_TROPHY       = 55; // List<String> (Str:Hash)

    // [61 - 69] 曲別記録
    public final int MUSIC_HASH_VALUE   = 60; // List<String> (Str:Hash)
    public final int PLAY_RECORD        = 61; // Map<String, String> (Str:Hash, Str:プレー値、達成率)

    // [101 - 109] 楽曲選択画面で用いるデータ

    // [111 - 119] 楽曲選択画面から一時的に離れる時に記録するデータ
    public final int SELECT_MUSIC_CURSOR    = 111; // int

    // [201 - 209] 楽曲選択画面から演奏ゲーム画面に引き継ぐデータ
    public final int LOAD_FILE_NAME = 201; // String
    public final int PLAY_PART      = 202; // int

    // [211 - 219] 演奏ゲーム画面で用いるデータ
    public final int NOTE_MOVE_TIME_OFFSET  = 211; // int

    // [301 - 309] 演奏ゲーム画面から新規獲得実績表示画面に引き継ぐデータ
    public final int NEW_GENERAL_TROPHY = 301; // List<Integer> (int:TrophyList)
    public final int NEW_MUSIC_TROPHY   = 302; // String (Str:Hash)
}
