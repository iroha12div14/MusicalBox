package scene.key;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * キーの一覧をアサインし、アサインされたキーの押下状態を取得するメソッド
 */
public class KeyController implements KeyListener {
    // 操作キー定義
    private final List<Integer> keyAssign;

    // キー入力の有無と断続的なキー押下の有無
    private final Map<Integer, Boolean> keyPress = new HashMap<>();
    private final Map<Integer, Boolean> keyHold = new HashMap<>();

    private final List<Character> keyPressLog = new ArrayList<>();

    /**
     * キーアサインの一覧を貰って初期化
     * @param keyAssign "KeyEvent.VK_"系のキー一覧を格納したリストを引数として初期化する
     */
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

    /**
     * キーを押したかを取得
     * @param key キーの種類
     */
    public boolean getKeyPress(int key) {
        return keyPress.get(key);
    }
    /**
     * キーを押し続けているか取得
     * @param key キーの種類
     */
    public boolean getKeyHold(int key){
        return keyHold.get(key);
    }

    /**
     * 押したキーをすべて取得する
     * @return 押下中のキーのリスト
     */
    public List<Integer> getKeyPressList() {
        List<Integer> keyCodeList = new ArrayList<>();
        for(int key : keyAssign) {
            if(getKeyPress(key) ) {
                keyCodeList.add(key);
            }
        }
        return keyCodeList;
    }
    /**
     * 押されているキーをすべて取得する
     * @return 押下中のキーのリスト
     */
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
    /**
     * 引数で指定したキーの一覧のうち、なんらかのキーが押されているか
     * @param keys キーの一覧(配列)
     */
    public boolean isAnyKeyPress(int[] keys) {
        boolean assignKeyPress = false;
        for(int k : keys) {
            if( !isAssign(k) ) { // 万が一アサインされてないキーを指定した場合はスキップ
                continue;
            }
            if(getKeyPress(k) ) {
                assignKeyPress = true;
                break;
            }
        }
        return assignKeyPress;
    }
    public boolean isAnyKeyHold() {
        return !getKeyHoldList().isEmpty();
    }

    /**
     * キーリスナを登録するためのメソッド
     * @param panel キーリスナを登録したいコンポーネント(JPanel)
     */
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

    /**
     * 指定されたキーがアサインされているか
     * @param key キーの種類
     */
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
