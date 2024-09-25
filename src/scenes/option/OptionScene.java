package scenes.option;

import data.GameDataElements;
import data.GameDataIO;
import logger.MessageLogger;
import save.SaveDataManager;
import scenes.draw.selector.DrawSelector;
import scenes.draw.slider.DrawSlider;
import scene.Scene;
import scene.SceneBase;
import scenes.se.SoundEffectManager;
import scenes.font.FontUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionScene extends SceneBase {

    //コンストラクタ
    public OptionScene(GameDataIO dataIO) {
        // 画面サイズ、FPS、キーアサインの初期化
        dataIO.put(GameDataElements.SCENE, Scene.OPTION);
        init(KEY_ASSIGN, dataIO);

        drawer.setWindowSize(data.getWindowSize() );
        drawer.setBlueprint();

        putFrameRate();
        putMasterVolume();
        putJudgementSubDisplay();

        setValue();

        // SEの読み込み
        seChangeState  = data.getFileName(GameDataElements.FILE_SOUND_KNOCK_BOOK);
        seChangeCursor = data.getFileName(GameDataElements.FILE_SOUND_OPEN_COVER);
        String dirPathSoundEffect = data.getDirectoryPathStr(GameDataElements.DIR_SE);
        String[] seFileNames = {seChangeState, seChangeCursor};
        seManager = new SoundEffectManager(dirPathSoundEffect, seFileNames);
        seManager.loadWaveFile();
        seManager.setMasterVolume(data.getMasterVolume() ); // 主音量を設定
    }

    // 描画したい内容はここ
    @Override
    protected void paintField(Graphics2D g2d) {
        drawer.drawBack(g2d);

        // 題字と説明
        drawer.drawBoxTitle(g2d);
        // メニュー
        drawer.drawBoxMenu(g2d);

        // 選択項目
        selector.drawVerticalSelector(g2d, strProperty, cursor, 22, 160, font.MSGothic(20), 70);

        // フレームレート（セレクタ）
        int pfr = state.get(FRAME_RATE);
        selector.drawSelector(g2d, strFrameRate, pfr, 80, 180, font.MSGothic(12));

        // 主音量（スライダ）
        int pmv = state.get(MASTER_VOLUME);
        font.setStr(g2d, font.Arial(12), Color.WHITE);
        font.drawStr(g2d, strMasterVolume[pmv], 80, 250);
        slider.drawSlider(g2d, 120, 236, 200, 15, pmv);

        // 判定のサブ表示（セレクタ）
        int pjsd = state.get(JUDGE_SUB_DISPLAY);
        selector.drawSelector(g2d, strJudgementSubDisplay, pjsd, 80, 320, font.MSGothic(12));
    }

    // 毎フレーム処理したい内容はここ
    @Override
    protected void actionField() {
        boolean isPressUpKey    = key.getKeyPress(KeyEvent.VK_UP);
        boolean isPressDownKey  = key.getKeyPress(KeyEvent.VK_DOWN);
        boolean isPressLeftKey  = key.getKeyPress(KeyEvent.VK_LEFT);
        boolean isPressRightKey = key.getKeyPress(KeyEvent.VK_RIGHT);
        boolean isPressEnterKey = key.getKeyPress(KeyEvent.VK_ENTER);
        boolean isPressSpaceKey = key.getKeyPress(KeyEvent.VK_SPACE);

        // 項目の移動
        if(isPressUpKey) {
            moveCursor(DIR_UP);
        }
        else if(isPressDownKey) {
            moveCursor(DIR_DOWN);
        }
        else if(isPressLeftKey) {
            changeState(DIR_LEFT);
        }
        else if(isPressRightKey) {
            changeState(DIR_RIGHT);
        }
        // 変更を（適用して・適用せず）選曲画面に移動
        else if(isPressEnterKey) {
            commitChange();
            Path filePath = data.getFilePathPath(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);
            sdManager.makeSaveData(data, filePath);
            MessageLogger.printMessage(this, "設定した内容を適用して終了", 1);
            sceneTransition(Scene.SELECT_MUSIC);
        }
        else if(isPressSpaceKey) {
            MessageLogger.printMessage(this, "設定を適用せず終了", 2);
            sceneTransition(Scene.SELECT_MUSIC);
        }
    }

    // 変更内容の確定
    private void commitChange() {
        int pfr = state.get(FRAME_RATE);
        data.put(FRAME_RATE, valueFrameRate[pfr]);

        int pmv = state.get(MASTER_VOLUME);
        data.put(MASTER_VOLUME, valueMasterVolume[pmv]);

        int pjsd = state.get(JUDGE_SUB_DISPLAY);
        data.put(JUDGE_SUB_DISPLAY, pjsd);
    }

    // カーソル移動
    private void moveCursor(int dir) {
        if(dir == DIR_UP && cursor > 0) {
            cursor--;
        } else if(dir == DIR_DOWN && cursor < property.length - 1) {
            cursor++;
        }
        seManager.startSound(seChangeCursor);
    }
    private void changeState(int dir) {
        GameDataElements key = property[cursor];    // 照会されている項目キー(element)
        int val = state.get(key);                   // 項目内の設定番号
        int max = values.get(key).length;           // 項目内の要素数上限

        if(dir == DIR_LEFT && val > 0) {
            state.put(key, val - 1);
        } else if(dir == DIR_RIGHT && val < max - 1) {
            state.put(key, val + 1);
        }
        seManager.startSound(seChangeState);
    }

    // （項目数数えるためにしか使ってない）
    private void setValue() {
        values.put(FRAME_RATE, strFrameRate);
        values.put(MASTER_VOLUME, strMasterVolume);
        values.put(JUDGE_SUB_DISPLAY, strJudgementSubDisplay);
    }

    // stateにいろいろputする
    private void putFrameRate() {
        int fps = data.get(FRAME_RATE, Integer.class);
        int p = 0;
        for(int i = 0; i < valueFrameRate.length; i++) {
            if(fps == valueFrameRate[i]) {
                p = i;
            }
        }
        state.put(FRAME_RATE, p);
    }
    private void putMasterVolume() {
        float masterVolume = data.get(MASTER_VOLUME, Float.class);
        int p = 0;
        for(int i = 0; i < valueMasterVolume.length; i++) {
            if(masterVolume == valueMasterVolume[i]) {
                p = i;
            }
        }
        state.put(MASTER_VOLUME, p);
    }
    private void putJudgementSubDisplay() {
        int pjsd = data.get(JUDGE_SUB_DISPLAY, Integer.class);
        int p = 0;
        for(int i = 0; i < valueJudgementSubDisplay.length; i++) {
            if(pjsd == valueJudgementSubDisplay[i]) {
                p = i;
            }
        }
        state.put(JUDGE_SUB_DISPLAY, p);
    }

    // ------------------------------------------------------------- //
    // インスタンス
    private final OptionDrawer drawer = new OptionDrawer();
    private final DrawSlider slider = new DrawSlider();
    private final DrawSelector selector = new DrawSelector();
    private final SoundEffectManager seManager;
    private final SaveDataManager sdManager = new SaveDataManager();

    private final FontUtil font = new FontUtil();

    // カーソル
    private static final int DIR_UP    = -1;
    private static final int DIR_DOWN  = 1;
    private static final int DIR_LEFT  = -1;
    private static final int DIR_RIGHT = 1;
    private int cursor = 0;
    private final Map<GameDataElements, Integer> state = new HashMap<>();

    // 項目数と項目名定義
    private final Map<GameDataElements, String[]> values = new HashMap<>();
    private final GameDataElements FRAME_RATE = GameDataElements.FRAME_RATE;
    private final GameDataElements MASTER_VOLUME = GameDataElements.MASTER_VOLUME;
    private final GameDataElements JUDGE_SUB_DISPLAY = GameDataElements.JUDGE_SUB_DISPLAY;
    private final GameDataElements[] property = {
            FRAME_RATE, MASTER_VOLUME, JUDGE_SUB_DISPLAY
    };

    // 項目内容
    private final int[] valueFrameRate = {30, 50, 60, 120, 144, 240, 288, 999};
    private final float[] valueMasterVolume = {
            0.0F, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F,
            1.0F, 1.1F, 1.2F, 1.3F, 1.4F, 1.5F
    };
    private final int[] valueJudgementSubDisplay = {0, 1, 2, 3};
    // 表示文字
    private final String[] strProperty = {"フレームレート", "主音量", "判定サブ表示"};
    private final String[] strFrameRate = {"30", "50", "60", "120", "144", "240", "288", "上限"};
    private final String[] strMasterVolume = {
            "0%", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%",
            "100%", "110%", "120%", "130%", "140%", "150%"
    };
    private final String[] strJudgementSubDisplay = {
            "非表示", "コンボ", "達成率", "コンボと達成率両方"
    };

    // 効果音(予約語)
    private final String seChangeState;
    private final String seChangeCursor;

    // キーアサインの初期化
    private static final List<Integer> KEY_ASSIGN = Arrays.asList(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,       // 項目移動
            KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT,    // 項目変更
            KeyEvent.VK_ENTER,  // 変更を適用して終了
            KeyEvent.VK_SPACE   // 変更を適用せず終了
    );
}
