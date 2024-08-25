package data;

// 各シーンで運用されるMap型変数dataのキーにあたる。
public class DataElements {
    public final int SCENE_MANAGER  = 1; // SceneManager
    public final int SCENE          = 2; // Scene
    public final int SCENE_ID       = 3; // int
    public final int WINDOW_NAME    = 4; // String

    public final int DISPLAY_WIDTH  = 11; // int
    public final int DISPLAY_HEIGHT = 12; // int
    public final int DISPLAY_X      = 13; // int
    public final int DISPLAY_Y      = 14; // int

    public final int FILE_SAVE_DATA         = 20;
    public final int DIRECTORY_SAVE_DATA    = 21;
    public final int DIRECTORY_SOUNDS       = 22; // String
    public final int DIRECTORY_PUNCH_CARD   = 23; // String
    public final int DIRECTORY_SE           = 24; // String
    public final int DIRECTORY_PREVIEW      = 25; // String

    public final int FRAME_RATE             = 31; // int
    public final int MASTER_VOLUME          = 32; // float
    public final int JUDGEMENT_SUB_DISPLAY  = 33; // int
    public final int NOTE_UNIT_MOVE         = 34; // float

    public final int KEY_CONFIG_PLAY_LEFT   = 41; // int[]
    public final int KEY_CONFIG_PLAY_RIGHT  = 42; // int[]

    public final int ACHIEVEMENT_POINT  = 51; // int
    public final int MUSIC_HASH_VALUE   = 52; // List<String> (Str:Hash)
    public final int TROPHIES           = 53; // List<Integer> (int:Trophies member)
    public final int MUSIC_TROPHIES     = 54; // List<String> (Str:Hash)

    public final int MUSIC_TITLE    = 101; // String
    public final int PLAY_PART      = 102; // int
    public final int PLAY_LEVEL     = 103; // int

    public final int SELECT_MUSIC_CURSOR    = 111; // int

    public final int LOAD_FILE_NAME         = 201; // String

    public final int NOTE_MOVE_TIME_OFFSET  = 211; // int

}
