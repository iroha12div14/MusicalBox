package data;

import scene.SceneManager;
import scene.Scene;

import java.util.List;
import java.util.Map;

// 各シーンで運用されるMap型変数dataの出力時のキャスト用(翻訳)メソッド
// 単に data.get(scene.key) で取り出すとObject型で出てきちゃうので
// それを型変換(翻訳)して取り出す役割をもつ
public class DataCaster {
    // ゲーム内で運用されるデータの「要素」
    DataElements elements = new DataElements();

    // プリミティブなもの
    public boolean getBoolData (Map<Integer, Object> data, int key) {
        return (boolean) data.get(key);
    }
    public int getIntData (Map<Integer, Object> data, int key) {
        return (int) data.get(key);
    }
    public float getFloatData (Map<Integer, Object> data, int key) {
        return (float) data.get(key);
    }

    // 型に対する要素が一意に定まるもの
    public SceneManager getSceneManager(Map<Integer, Object> data) {
        int key = elements.SCENE_MANAGER;
        return (SceneManager) data.get(key);
    }
    public Scene getScene(Map<Integer, Object> data) {
        int key = elements.SCENE;
        return (Scene) data.get(key);
    }

    // 配列や参照型のもの
    public int[] getIntArrData (Map<Integer, Object> data, int key) {
        return (int[]) data.get(key);
    }
    public String getStrData (Map<Integer, Object> data, int key) {
        return (String) data.get(key);
    }

    public List<Integer> getIntListData(Map<Integer, Object> data, int key) {
        return (List<Integer>) data.get(key);
    }
    public List<String> getStrListData(Map<Integer, Object> data, int key) {
        return (List<String>) data.get(key);
    }

    // 頻繁に使うもの
    public int getDisplayFrameRate(Map<Integer, Object> data) {
        int key = elements.FRAME_RATE;
        return (int) data.get(key);
    }
    public int getDisplayWidth(Map<Integer, Object> data) {
        int key = elements.DISPLAY_WIDTH;
        return (int) data.get(key);
    }
    public int getDisplayHeight(Map<Integer, Object> data) {
        int key = elements.DISPLAY_HEIGHT;
        return (int) data.get(key);
    }

    // 楽曲別データ用（キーにハッシュ値をとるMap型変数）
    public Map<String, String> getHashedStringData(Map<Integer, Object> data, int key) {
        return (Map<String, String>) data.get(key);
    }

}
