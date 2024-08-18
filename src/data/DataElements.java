package data;

// 各シーンで運用されるMap型変数dataのキーにあたる。
public class DataElements {
    public final int SCENE_MANAGER  = 1; // SceneManager
    public final int SCENE          = 2; // Scene
    public final int SCENE_ID       = 3; // int

    public final int WINDOW_NAME    = 11; // String
    public final int DISPLAY_WIDTH  = 12; // int
    public final int DISPLAY_HEIGHT = 13; // int
    public final int DISPLAY_X      = 14; // int
    public final int DISPLAY_Y      = 15; // int
    public final int FRAME_RATE     = 16; // int

    public final int DIRECTORY_SOUNDS     = 21; // String
    public final int DIRECTORY_PUNCH_CARD = 22; // String
    public final int DIRECTORY_SE         = 23; // String
    public final int DIRECTORY_PREVIEW    = 24; // String

    public final int MUSIC_TITLE           = 101; // String
    public final int PLAY_PART             = 102; // int
    public final int PLAY_LEVEL            = 103; // int

    public final int SELECT_MUSIC_CURSOR   = 111; // int

    public final int LOAD_FILE_NAME        = 201; // String

    public final int NOTE_MOVE_TIME_OFFSET = 211; // int
    public final int NOTE_UNIT_MOVE        = 212; // float

    public final int JUDGEMENT_SUB_DISPLAY = 221; // int

    public final int KEY_CONFIG_PLAY_LEFT  = 231; // int[]
    public final int KEY_CONFIG_PLAY_RIGHT = 232; // int[]
}
