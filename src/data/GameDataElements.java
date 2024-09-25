package data;

/**
 * ゲーム内でやり取りされるデータの要素
 */
public enum GameDataElements {
    // 使用ディレクトリ名
    ROOT_DIRECTORY,     // String
    DIR_SOUNDS,         // String
    DIR_SE,             // String
    DIR_SAVE_DATA,      // String
    DIR_PUNCH_CARD,     // String
    DIR_PREVIEW,        // String

    // 使用ファイル名、もしくはそのアドレス
    LOAD_FILE_ADDRESS,      // String
    FILE_SAVE_DATA,         // String
    FILE_SOUND_OPEN_COVER,  // String
    FILE_SOUND_KNOCK_BOOK,  // String
    FILE_SOUND_SWIPE_PAGE,  // String

    // 保存するデータ
    FRAME_RATE,         // int
    MASTER_VOLUME,      // float
    JUDGE_SUB_DISPLAY,  // int
    NOTE_UNIT_MOVE,     // float

    // キーコンフィグ
    KEY_CONFIG_PLAY_LEFT,   // int[]
    KEY_CONFIG_PLAY_RIGHT,  // int[]

    // 実績関係
    ACHIEVEMENT_POINT,  // int
    PLAY_COUNT,         // int
    TROPHY,             // List<Integer> (int:TrophyList)
    MUSIC_TROPHY,       // List<String> (Str:Hash)

    // 曲別記録
    MUSIC_HASH_VALUE,   // List<String> (Str:Hash)
    PLAY_RECORD,        // Map<String, String> (Str:Hash, Str:プレー値、達成率)

    // 場面の管理
    SCENE_MANAGER,      // SceneManager
    SCENE,              // Scene
    SCENE_ID,           // int

    // ウインドウの名前やサイズ
    WINDOW_NAME,        // String
    WINDOW_POINT,       // java.awt.Point
    WINDOW_SIZE,        // java.awt.Dimension

    // 楽曲選択画面で用いるデータ

    // 楽曲選択画面から一時的に離れる時に記録するデータ
    SELECT_MUSIC_CURSOR,    // int

    // 楽曲選択画面から演奏ゲーム画面に引き継ぐデータ
    PLAY_PART,              // int

    // 演奏ゲーム画面で用いるデータ
    NOTE_MOVE_TIME_OFFSET,  // int

    // 演奏ゲーム画面から新規獲得実績表示画面に引き継ぐデータ
    NEW_GENERAL_TROPHY,     // List<Integer> (int:TrophyList)
    NEW_MUSIC_TROPHY        // String (Str:Hash)
}
