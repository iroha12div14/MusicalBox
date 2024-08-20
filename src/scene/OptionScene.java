package scene;

import draw.selector.DrawSelector;
import draw.slider.DrawSlider;
import option.drawer.OptionDrawer;
import wav.SoundEffectManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionScene extends SceneBase {

    //コンストラクタ
    public OptionScene(Map<Integer, Object> data) {
        // 画面サイズ、FPS、キーアサインの初期化
        init(keyAssign, data);

        drawer = new OptionDrawer(
                cast.getIntData(this.data, elem.DISPLAY_WIDTH),
                cast.getIntData(this.data, elem.DISPLAY_HEIGHT)
        );

        putFrameRate();
        putMasterVolume();
        putJudgementSubDisplay();

        setValue();

        // SEの読み込み
        String directorySE = cast.getStrData(this.data, elem.DIRECTORY_SE);
        String[] seFileName = {SE_KNOCK, SE_SWIPE};
        seManager = new SoundEffectManager(directorySE, seFileName);
        seManager.loadWaveFile();
        seManager.setMasterVolume(cast.getFloatData(this.data, elem.MASTER_VOLUME) ); // 主音量を設定
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
        int pjsd = state.get(JUDGEMENT_SUB_DISPLAY);
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
            sceneTransition(Scene.SELECT_MUSIC_SCENE);
        }
        else if(isPressSpaceKey) {
            sceneTransition(Scene.SELECT_MUSIC_SCENE);
        }
    }

    // 変更内容の確定
    private void commitChange() {
        int pfr = state.get(FRAME_RATE);
        data.put(FRAME_RATE, valueFrameRate[pfr]);

        int pmv = state.get(MASTER_VOLUME);
        data.put(MASTER_VOLUME, valueMasterVolume[pmv]);

        int pjsd = state.get(JUDGEMENT_SUB_DISPLAY);
        data.put(JUDGEMENT_SUB_DISPLAY, pjsd);
    }

    // カーソル移動
    private void moveCursor(int dir) {
        if(dir == DIR_UP && cursor > 0) {
            cursor--;
        } else if(dir == DIR_DOWN && cursor < property.length - 1) {
            cursor++;
        }
        seManager.startSound(SE_SWIPE);
    }
    private void changeState(int dir) {
        int key = property[cursor];         // 照会されている項目キー(elem)
        int val = state.get(key);           // 項目内の設定番号
        int max = values.get(key).length;   // 項目内の要素数上限

        if(dir == DIR_LEFT && val > 0) {
            state.put(key, val - 1);
        } else if(dir == DIR_RIGHT && val < max - 1) {
            state.put(key, val + 1);
        }
        seManager.startSound(SE_KNOCK);
    }

    // （項目数数えるためにしか使ってない）
    private void setValue() {
        values.put(FRAME_RATE, strFrameRate);
        values.put(MASTER_VOLUME, strMasterVolume);
        values.put(JUDGEMENT_SUB_DISPLAY, strJudgementSubDisplay);
    }

    // stateにいろいろputする
    private void putFrameRate() {
        int fps = cast.getIntData(data, FRAME_RATE);
        int p = 0;
        for(int i = 0; i < valueFrameRate.length; i++) {
            if(fps == valueFrameRate[i]) {
                p = i;
            }
        }
        state.put(FRAME_RATE, p);
    }
    private void putMasterVolume() {
        float masterVolume = cast.getFloatData(data, MASTER_VOLUME);
        int p = 0;
        for(int i = 0; i < valueMasterVolume.length; i++) {
            if(masterVolume == valueMasterVolume[i]) {
                p = i;
            }
        }
        state.put(MASTER_VOLUME, p);
    }
    private void putJudgementSubDisplay() {
        int pjsd = cast.getIntData(data, JUDGEMENT_SUB_DISPLAY);
        int p = 0;
        for(int i = 0; i < valueJudgementSubDisplay.length; i++) {
            if(pjsd == valueJudgementSubDisplay[i]) {
                p = i;
            }
        }
        state.put(JUDGEMENT_SUB_DISPLAY, p);
    }

    // ------------------------------------------------------------- //
    // インスタンス
    private final OptionDrawer drawer;
    private final DrawSlider slider = new DrawSlider();
    private final DrawSelector selector = new DrawSelector();
    private final SoundEffectManager seManager;

    // カーソル
    private final int DIR_UP    = -1;
    private final int DIR_DOWN  = 1;
    private final int DIR_LEFT  = -1;
    private final int DIR_RIGHT = 1;
    private int cursor = 0;
    private final Map<Integer, Integer> state = new HashMap<>();

    // 項目数と項目名定義
    private final Map<Integer, String[]> values = new HashMap<>();
    private final int FRAME_RATE = elem.FRAME_RATE;
    private final int MASTER_VOLUME = elem.MASTER_VOLUME;
    private final int JUDGEMENT_SUB_DISPLAY = elem.JUDGEMENT_SUB_DISPLAY;
    private final int[] property = {
            FRAME_RATE, MASTER_VOLUME, JUDGEMENT_SUB_DISPLAY
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
    private final String SE_KNOCK  = "knock_book01.wav";
    private final String SE_SWIPE  = "open_cover01.wav";

    // キーアサインの初期化
    private static final List<Integer> keyAssign = Arrays.asList(
            KeyEvent.VK_UP, KeyEvent.VK_DOWN,       // 項目移動
            KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT,    // 項目変更
            KeyEvent.VK_ENTER,  // 変更を適用して終了
            KeyEvent.VK_SPACE   // 変更を適用せず終了
    );
}
