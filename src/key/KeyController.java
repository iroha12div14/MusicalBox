package key;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * <P>
 * キー入力用のクラス
 * </P>
 * <P>
 * コンストラクタ(key.KeyController(List&lt;Integer&gt; keyAssign)) <br />
 * 　　"KeyEvent.VK_"系のキー一覧を格納したリストを引数として初期化する
 * </P>
 * <P>
 * getKeyPress(int key), getKeyHold(int key) <br />
 * 　　前者はキーを押したか、後者はキーを押されているかを取得
 * </P>
 * <P>
 * getKeyPressList(), getKeyHoldList() <br />
 * 　　アサインされた押下中のキーをすべて取得
 * </P>
 * <P>
 * isAnyKeyPress(), isAnyKeyHold() <br />
 * 　　アサインされたなんらかのキーが押されているかを取得
 * </P>
 * <P>
 * avoidChattering() <br />
 * 　　getKeyPressでキー多重入力されるのを防止するメソッド <br />
 * 　　getKeyHoldのみ使用するなら不要 <br />
 * 　　actionListenerを実装したクラスのactionPerformedメソッドで稼働させて <br />
 * 　　キーの多重入力を遮断する
 * </P>
 * <P>
 * setKeyListener(JPanel panel) <br />
 * 　　キーリスナをコンポーネント？に登録するメソッド <br />
 * 　　フォーカスしたいJPanelを引数にするけど、とりあえずthisを入れとけばOK
 * </P>
 */

public class KeyController implements KeyListener {
    // 操作キー定義
    private final List<Integer> keyAssign;

    // キー入力の有無と断続的なキー押下の有無
    private final Map<Integer, Boolean> keyPress = new HashMap<>();
    private final Map<Integer, Boolean> keyHold = new HashMap<>();

    private final List<Character> keyPressLog = new ArrayList<>();

    // キーアサインの一覧を貰って初期化
    public KeyController(List<Integer> keyAssign){
        this.keyAssign = keyAssign;
        for(int key : keyAssign) {
            setKeyPress(key, false);
            setKeyHold(key, false);
        }
    }

    // keyListenerメソッド三銃士をオーバーライド
    @Override
    public void keyTyped(KeyEvent e) { } // 中身なし
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        char keyChar = e.getKeyChar();
        if( isAssign(key) ) {
            if( !getKeyHold(key) ) {
                setKeyPress(key, true);
                setKeyHold(key, true);

                keyPressLog.add(keyChar);
                if(keyPressLog.size() > 16) {
                    keyPressLog.remove(0);
                }
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if( isAssign(key) ) {
            if( getKeyHold(key) ) {
                setKeyHold(key, false);
            }
        }
    }
    // actionPerformメソッドで稼働させて多重反応しないようにする
    public void avoidChattering() {
        for(int key : keyAssign){
            setKeyPress(key, false);
        }
    }

    // キー押下状態の取得
    public boolean getKeyPress(int key) {
        return keyPress.get(key);
    }
    public boolean getKeyHold(int key){
        return keyHold.get(key);
    }

    // アサインされた押下中のキーをすべて取得
    public List<Integer> getKeyPressList() {
        List<Integer> keyCodeList = new ArrayList<>();
        for(int key : keyAssign) {
            if(getKeyPress(key) ) {
                keyCodeList.add(key);
            }
        }
        return keyCodeList;
    }
    public List<Integer> getKeyHoldList() {
        List<Integer> keyCodeList = new ArrayList<>();
        for(int key : keyAssign) {
            if(getKeyHold(key) ) {
                keyCodeList.add(key);
            }
        }
        return keyCodeList;
    }

    // アサインされたなんらかのキーが押されているか
    public boolean isAnyKeyPress() {
        return !getKeyPressList().isEmpty();
    }
    public boolean isAnyKeyHold() {
        return !getKeyHoldList().isEmpty();
    }

    // キーリスナを登録
    public void setKeyListener(JPanel panel) {
        panel.setFocusable(true);
        panel.addKeyListener(this);
    }

    // セッタ
    private void setKeyPress(int key, boolean set) {
        keyPress.put(key, set);
    }
    private void setKeyHold(int key, boolean set) {
        keyHold.put(key, set);
    }

    // 指定されたキーのアサインの有無
    private boolean isAssign(int key) {
        return keyAssign.contains(key);
    }

    // ------------------------------------------------------ //

    // デバッグ用
    // アサインされた押下中のキーをメッセージ形式で出力
    public String msgKeyPressLog() {
        StringBuilder str = new StringBuilder("keyPressLog: ");
        for(char keyChar : keyPressLog) {
            str.append(keyChar).append(", ");
        }
        return str.toString();
    }
}
