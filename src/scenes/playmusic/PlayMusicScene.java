package scenes.playmusic;

import data.GameDataElements;
import data.GameDataIO;
import hash.HashGenerator;
import save.SaveDataManager;
import scenes.header.HeaderGetter;
import scenes.header.HeaderMaker;
import scenes.playmusic.judge.JudgeUtil;
import scenes.playmusic.keyobserve.KeyPressObserver;
import scenes.playmusic.keysound.KeySoundContainer;
import scenes.playmusic.keysound.KeySoundLoader;
import scenes.playmusic.note.NoteObject;
import scenes.playmusic.note.NotesManager;
import scenes.playmusic.parts.PartsKeyboard;
import scenes.playmusic.timeline.*;
import scene.Scene;
import scene.SceneBase;
import calc.CalcUtil;
import text.TextFilesManager;
import trophy.TrophyGenerator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class PlayMusicScene extends SceneBase {
    //コンストラクタ
    public PlayMusicScene(GameDataIO dataIO) {
        // キーアサインの初期化とデータ渡し
        init(KEY_ASSIGN, dataIO);
        printMessage("演奏ゲーム初期化中", 3);
        data.put(GameDataElements.SCENE, Scene.PLAY_MUSIC);

        playPart = data.get(GameDataElements.PLAY_PART, Integer.class);

        noteMovTimeOffset = data.get(GameDataElements.NOTE_MOVE_TIME_OFFSET, Integer.class);
        noteUnitMov       = data.get(GameDataElements.NOTE_UNIT_MOVE,        Float.class);
        keyAssignMainPart = data.get(GameDataElements.KEY_CONFIG_PLAY_RIGHT, int[].class);
        keyAssignSubPart  = data.get(GameDataElements.KEY_CONFIG_PLAY_LEFT,  int[].class);

        // TODO: 読み込みに時間が掛かるので別スレッドで進行させ、その間に読み込みの進度を表示しておく。

        printMessage("パンチカード読み込みと時系列化", 2);
        // パンチカードの読み込み
        String filePathPunchCard = data.getFilePathStr(GameDataElements.DIR_PUNCH_CARD, GameDataElements.LOAD_FILE_ADDRESS);
        punchCard = new TextFilesManager().loadTextFile(filePathPunchCard);

        // ヘッダ情報の出力とシーケンサの作成
        header = new HeaderMaker().makeHeader(punchCard);
        musicTitle = HeaderGetter.getTitle(header);
        musicTempo = HeaderGetter.getTempo(header);
        sequenceUnitTime = (float) 15000 / musicTempo; // 16分音符のミリ秒時間
        arpDistanceTime = 1000 * 2 / 60; // アルペジオノート同士の間隔(60分の2秒)
        sequence = new SequenceMaker().makeSequence(punchCard, 0);

        // シーケンスの解析
        SequenceAnalyzer analyzer = new SequenceAnalyzer();
        int notesCount  = analyzer.getNotesCount(sequence, playPart);
        float[] analyze = analyzer.analyzeSequence(sequence, sequenceUnitTime, arpDistanceTime, playPart);
        int playTime    = (int) analyzer.getPlayTime(sequence, sequenceUnitTime);

        // 終了時刻 最後の音が最終時刻 + (スクロール時間) + 3000(オルゴール音源の長さ)
        musicEndTime = playTime + data.get(GameDataElements.NOTE_MOVE_TIME_OFFSET, Integer.class) + 3000;

        // 音源コンテナのパラメータを準備
        SequenceGetter sqGetter = new SequenceGetter();
        PunchCard pc = new PunchCard();
        int[] mainScoreAutoPlayCount = (playPart == SUB_PART || playPart == NONE)
                ? sqGetter.getScorePlayCount(sequence, pc.collectionMain() )
                : sqGetter.getScorePlayCountEmpty();
        int[] subScoreAutoPlayCount = (playPart == MAIN_PART || playPart == NONE)
                ? sqGetter.getScorePlayCount(sequence, pc.collectionSub() )
                : sqGetter.getScorePlayCountEmpty();
        // 音源データの格納
        printMessage("音源ファイル読み込みと格納", 2);
        String dirPathSounds = data.getDirectoryPathStr(GameDataElements.DIR_SOUNDS);
        KeySoundLoader keySoundLoader = new KeySoundLoader(dirPathSounds);
        container = keySoundLoader.createContainer(mainScoreAutoPlayCount, subScoreAutoPlayCount);
        keySoundLoader.containNoSound();

        // 描画インスタンス
        int displayWidth  = data.get(GameDataElements.DISPLAY_WIDTH , Integer.class);
        int displayHeight = data.get(GameDataElements.DISPLAY_HEIGHT, Integer.class);
        drawer.setDisplaySize(displayWidth, displayHeight);
        drawer.setBlueprint();

        int frameRate = data.get(GameDataElements.FRAME_RATE, Integer.class);
        drawer.setAnimationTimer(frameRate);

        // 判定
        judgeUtil = new JudgeUtil(
                container,  // 音源コンテナ
                drawer,     // 描画インスタンス
                keyboard    // キーボード部品インスタンス
        );
        // キー音量の設定
        float volume = data.get(GameDataElements.MASTER_VOLUME, Float.class);
        judgeUtil.setKeySoundMasterVolume(volume);
        judgeAuto = (playPart == NONE);             // 自動再生の有無
        observer = new KeyPressObserver(judgeUtil); // キー押下の監視インスタンス

        fru.setPause(true);             // 最初は一時停止する

        Path filePath = data.getFilePathPath(GameDataElements.DIR_PUNCH_CARD, GameDataElements.LOAD_FILE_ADDRESS);
        hash = HashGenerator.getSha256(filePath); // ハッシュ値

        playCount = data.get(GameDataElements.PLAY_COUNT, Integer.class); // プレー回数

        printSequenceAnalyze(notesCount, playTime, analyze);

        printMessage("演奏ゲーム開始", 4);
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {
        int pastTime = fru.getPastTime();
        int judgeState = judgeUtil.getJudgeState();
        int combo = judgeUtil.getCombo();
        String acvStrEn = judgeUtil.getAchievementStr("Acv", "%");
        int judgeSubDsp = data.get(GameDataElements.JUDGE_SUB_DISPLAY, Integer.class);
        String msgFPS = fru.msgFPS(false);
        String msgLatency = fru.msgLatency(500);

        drawer.drawBack(g2d); // 背景
        drawer.drawJudgeLine(g2d); // 判定線
        drawer.drawNotes(g2d, mainScore, MAIN_SCORE, pastTime, noteUnitMov, playPart); // ノーツ(メロディ側)
        drawer.drawNotes(g2d, subScore,  SUB_SCORE,  pastTime, noteUnitMov, playPart); // ノーツ(伴奏側)
        drawer.drawKeyBoard(g2d); // キーボード
        drawer.drawJudgement(g2d, judgeState, combo, acvStrEn, judgeSubDsp); // 判定
        drawer.drawScrollSpeed(g2d, noteUnitMov); // スクロール速度
        drawer.drawMusicProgress(g2d, pastTime, musicEndTime); // 曲進行バー
        drawer.drawFrame(g2d, musicTitle, musicTempo, playPart); // 枠とその他
        drawer.drawFrameRate(g2d, msgFPS, msgLatency); // フレームレート表示

        if( !isMusicStart ) {
            drawer.drawHowToPlay(g2d, playPart); // あそびかた
            drawer.drawFadeIn(g2d); // フェードイン
        }
        if(pastTime >= musicEndTime && playPart != NONE) {
            // 結果画面
            int[] judgeCount = judgeUtil.getJudgeCount();
            int maxCombo = judgeUtil.getMaxCombo();
            String acvStrJp = judgeUtil.getAchievementStr("達成率", "％");
            drawer.drawResult(g2d, judgeCount, maxCombo, acvStrJp);
        }
        if(isMusicEnd) {
            drawer.drawFadeOut(g2d); // フェードアウト
        }
    }

    // 毎フレーム処理したい内容はここ(主にキー入力とタイマーの処理関連)
    @Override
    protected void actionField() {
        boolean isPressSpaceKey    = key.getKeyPress(KeyEvent.VK_SPACE);
        boolean isPressUpKey       = key.getKeyPress(KeyEvent.VK_UP);
        boolean isPressDownKey     = key.getKeyPress(KeyEvent.VK_DOWN);
        boolean isPressEnterKey    = key.getKeyPress(KeyEvent.VK_ENTER);
        boolean isPressMainPartKey = key.isAnyKeyPress(keyAssignMainPart);
        boolean isPressSubPartKey  = key.isAnyKeyPress(keyAssignSubPart);

        // スペースを押して楽曲を開始する もしくは自動再生なら操作いらずで開始
        if ( !isMusicStart && (isPressSpaceKey || drawer.isEndFadeIn() && playPart == NONE) ) {
            isMusicStart = true;
            fru.setPause(false); // FrameRateUtilのポーズ状態を解除
            judgeUtil.startNoSound();
        }

        // 演奏ゲーム中の動作
        if (nowTime < musicEndTime) {
            // 現在時刻を更新
            nowTime = fru.getPastTime();

            // 時刻を監視して、ノーツを楽譜に書き込むか決める
            manager.notesManage(
                    sequence, nowTime, sequenceUnitTime, noteMovTimeOffset, mainScore, subScore, arpDistanceTime
            );

            // 演奏に割り当てたキーの押下の有無を監視し、条件を満たせば判定を行う
            if (isMusicStart) { // 演奏開始していない間は入力を受け付けない(開始前のOOPS判定防止)
                // メロディ側
                observer.observeKeyPress(
                        isPressMainPartKey, playPart, mainScore, MAIN_SCORE, nowTime, judgeAuto
                );
                // 伴奏側
                observer.observeKeyPress(
                        isPressSubPartKey, playPart, subScore, SUB_SCORE, nowTime, judgeAuto
                );
            }

            // スクロール速度調整 ↑↓キー
            if (isPressUpKey) {
                if (noteUnitMov < 0.99F) {
                    noteUnitMov += 0.01F;
                    drawer.startScrollSpeedAnimTimer();
                }
            } else if (isPressDownKey) {
                if (noteUnitMov > 0.051F) { // 0.05Fだと何故か0.05のときに通らない 整数の方が良かったかも
                    noteUnitMov -= 0.01F;
                    drawer.startScrollSpeedAnimTimer();
                }
            }
            // 強制終了 Enterキー
            else if (isPressEnterKey) {
                nowTime = musicEndTime;
                isMusicEnd = true;

                // スクロール速度だけ保持して保存
                data.put(GameDataElements.NOTE_UNIT_MOVE, noteUnitMov);
                Path filePath = data.getFilePathPath(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);
                sdManager.makeSaveData(data, filePath);

                printMessage("強制終了します", 4);
            }

        }
        // リザルト画面で Enterを押して終了 もしくは自動再生ならそのまま終了
        else if ( !isMusicEnd && (isPressEnterKey || playPart == NONE) ) {
            isMusicEnd = true;

            // 自動再生でない場合はプレー記録をインプットする
            if(playPart != NONE) {
                playCount++;
                printAchievementAndTiming();
                playDataInput();
            }

            // スクロール速度や記録全般のセーブ
            data.put(GameDataElements.NOTE_UNIT_MOVE, noteUnitMov);
            Path filePath = data.getFilePathPath(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);
            sdManager.makeSaveData(data, filePath);
        }
        // フェードアウト直後の処理（dataの持ち越しとシーン遷移）
        else if (isMusicEnd && drawer.isEndFadeOut() ) {
            container.closeClips(); // クリップが抱えているリソースを開放

            printMessage("楽曲の選択に移動します", 3);
            sceneTransition(Scene.ANNOUNCE); // アナウンス画面に移動(新規トロフィーが無ければ選曲画面にリダイレクト)
        }

        // アニメーションタイマーの経過
        drawer.passAnimTimer(isMusicEnd);
    }

    // なんかコンソールに出力するやつ
    private void printSequenceAnalyze(int notesCount, int playTime, float[] analyze) {
        printMessage("[Analyze Sequence]", 3);
        System.out.printf("  Title (Part): %s (%s)\n", musicTitle, drawer.getPlayPartStr(playPart));
        System.out.printf("  Notes Count:\t%d[notes]\n", notesCount);
        System.out.printf("  Play Time:\t%.3f[sec]\n", (float) playTime / 1000);
        System.out.printf("  Density:\t%.3f[notes/sec]\n", analyze[0]);
        System.out.printf("  Peak:\t\t%.3f[notes]\n", analyze[1]);
    }
    private void printAchievementAndTiming() {
        int acvPoint = judgeUtil.getAchievementPoint() / 20;
        int sumAcvPoint = data.get(GameDataElements.ACHIEVEMENT_POINT, Integer.class);
        int totalAcvPoint = sumAcvPoint + acvPoint;
        printMessage("  Acv Point:\t" + acvPoint + "[pt]", 2);
        printMessage("  Total Acv:\t" + totalAcvPoint + "[pt]", 2);
        printMessage("  Timing Ave:\t" + judgeUtil.getTimingAverage() + "[ms]", 2);
        printMessage("  Timing STDEV:\t" + calc.getFloatDotUnder(judgeUtil.getTimingSTDEV(), 2) + "[ms]", 2);
    }

    // プレー記録の挿入
    private void insertPlayRecord() {
        float achievement = judgeUtil.getAchievement();
        int judgeCountLost = judgeUtil.getJudgeCount()[judgeUtil.JUDGE_LOST];
        int playState
                = achievement == 100 ? 3    // All Perfect
                : judgeCountLost == 0 ? 2   // Full Combo
                : 1;                        // Played

        // 文字列の記録を構文解析
        Map<String, String> playRecords = data.getHashedPlayRecords(GameDataElements.PLAY_RECORD);
        String playRecordStr = playRecords.getOrDefault(hash, SaveDataManager.playRecordDefault() );
        String newPlayRecordStr = new SaveDataManager().makePlayRecord(playState, achievement, playPart, playRecordStr);
        playRecords.put(hash, newPlayRecordStr);

        // 格納したものをdataに仕舞う
        data.putHashedPlayRecords(GameDataElements.PLAY_RECORD, playRecords);
    }

    // トロフィーの獲得
    private void getTrophy() {
        TrophyGenerator generator = new TrophyGenerator();

        // 新規で取得したトロフィー
        List<Integer> newTrophies = generator.getTrophyByResult(
                judgeUtil.getMaxCombo(),
                judgeUtil.getJudgeCount(),
                judgeUtil.getAchievement(),
                playCount,
                data.get(GameDataElements.ACHIEVEMENT_POINT, Integer.class),
                data
        );
        // 既に取得したものと比較し、新規取得分を追加する
        List<Integer> ownTrophies = data.getIntList(GameDataElements.TROPHY);
        ownTrophies.addAll(newTrophies);
        data.putIntList(GameDataElements.TROPHY, ownTrophies);
        int getTrp = newTrophies.size();
        if(getTrp > 0) {
            data.putIntList(GameDataElements.NEW_GENERAL_TROPHY, newTrophies); // アナウンス用
            printMessage(getTrp + "件の新規トロフィーを獲得", 2);
        }

        // 楽曲別トロフィー
        if( !generator.isNONE(
                generator.getTrophyOfMusic(
                        data,
                        hash,
                        judgeUtil.getJudgeCount(),
                        judgeUtil.getAchievement(),
                        playPart
                )
        ) ) {
            List<String> ownMusicTrophies = data.getStrList(GameDataElements.MUSIC_TROPHY);
            if( !ownMusicTrophies.contains(hash) ) {
                ownMusicTrophies.add(hash);
                data.putStrList(GameDataElements.MUSIC_TROPHY, ownMusicTrophies);
                data.put(GameDataElements.NEW_MUSIC_TROPHY, hash); // アナウンス用
                printMessage("楽曲別固有トロフィーを獲得", 2);
            }
        }
    }

    // プレー記録の保存
    private void playDataInput() {
        int acvPoint = judgeUtil.getAchievementPoint() / 20;
        int sumAcvPoint = data.get(GameDataElements.ACHIEVEMENT_POINT, Integer.class);
        int totalAcvPoint = sumAcvPoint + acvPoint;
        data.put(GameDataElements.ACHIEVEMENT_POINT, totalAcvPoint);
        data.put(GameDataElements.PLAY_COUNT, playCount);

        insertPlayRecord(); // プレー記録の挿入
        getTrophy(); // トロフィーの獲得
    }

    // ------------------------------------------------------ //

    // インスタンス諸々
    private final PartsKeyboard keyboard = new PartsKeyboard();
    private final PlayMusicDrawer drawer = new PlayMusicDrawer(keyboard);
    private final KeySoundContainer container;
    private final JudgeUtil judgeUtil;
    private final KeyPressObserver observer;
    private final NotesManager manager = new NotesManager();
    private final SaveDataManager sdManager = new SaveDataManager();

    private final CalcUtil calc = new CalcUtil();

    // ヘッダ・シーケンサ・楽譜諸々
    private final List<String> punchCard;
    private final Map<String, Object> header;
    private final List<Map<String, Integer>> sequence;
    private final List<NoteObject> mainScore = new ArrayList<>();
    private final List<NoteObject> subScore  = new ArrayList<>();
    private final static int MAIN_SCORE = 0;
    private final static int SUB_SCORE  = 1;

    // 演奏パート
    private final int playPart;
    private final static int MAIN_PART = 1;
    private final static int SUB_PART  = 2;
    private final static int BOTH_PART = 3;
    private final static int NONE = 0;
    private final boolean judgeAuto;

    // 楽曲のタイトルとテンポ
    private final String musicTitle;
    private final int musicTempo;

    // 時刻と時間にまつわるもの
    private int nowTime;                    // 現時刻
    private final int noteMovTimeOffset;    // ノートの移動時間
    private float noteUnitMov;              // 1ミリ秒当たりの移動量
    private final float sequenceUnitTime;   // シーケンス単位時間あたりの実際のミリ秒時間
    private final int arpDistanceTime;      // アルペジオノート同士の間隔時間

    // 使用キー定義
    private final int[] keyAssignMainPart;
    private final int[] keyAssignSubPart;

    // 楽曲のはじまりと終わり
    private boolean isMusicStart = false;
    private boolean isMusicEnd = false;
    private final int musicEndTime;

    // ハッシュ値
    private final String hash;

    // プレー回数
    private int playCount;

    // キーアサインの初期化
    private static final List<Integer> KEY_ASSIGN = Arrays.asList(
            KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L,
            KeyEvent.VK_F, KeyEvent.VK_D, KeyEvent.VK_S,
            KeyEvent.VK_SPACE,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,
            KeyEvent.VK_ENTER
    );
}
