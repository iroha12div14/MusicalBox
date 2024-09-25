package data;

import scene.Scene;
import scene.SceneManager;

import java.awt.Dimension;
import java.awt.Point;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// reference: ChatGPT

/**
 * ゲーム内でやり取りされるデータの入出力を行う
 */
public class GameDataIO {
    // データの値
    private final Map<GameDataElements, Object> data = new HashMap<>();

    // データの型（専ら照合用）
    private final Map<GameDataElements, Class<?>> dataType = new HashMap<>();

    // List型やMap型のデータは型別で格納
    private final Map<GameDataElements, List<Integer>> dataIntList = new HashMap<>();
    private final Map<GameDataElements, List<String >> dataStrList = new HashMap<>();
    private final Map<GameDataElements, Map<String, String>> dataHashedPlayRecords = new HashMap<>();

    /**
     * ゲーム内データを格納する
     * @param element   データの要素（GameDataElements要素）
     * @param value     データの値
     * @param type      データの型（プリミティブ型はラッパークラスで書くこと！）
     */
    public <T> void put(GameDataElements element, T value, Class<T> type) {
        data.put(element, value);
        dataType.put(element, type);
    }
    /**
     * ゲーム内データを格納する（型指定省略）
     * @param element   データの要素（GameDataElements要素）
     * @param value     データの値（nullを格納する場合は型指定する方のメソッドを使うこと！）
     */
    public <T> void put(GameDataElements element, T value) {
        data.put(element, value);
        dataType.put(element, value.getClass() );
    }

    /**
     * 型照合をした上で格納したゲーム内データを取り出す
     * @param element   データの要素（GameDataElements要素）
     * @param type      データの型（プリミティブ型はラッパークラスで書かないとnullで返されるので注意！）
     * @return データの値
     */
    @SuppressWarnings("unchecked")
    public <T> T get(GameDataElements element, Class<T> type) {
        Object value = data.get(element);
        return value == null || !dataType.get(element).equals(type)
                ? null
                : (T) value;
    }

    // ------------------------------------------------------------------------------ //
    // List型とMap型だけ警告されまくるので個別で用意することにした

    // int型List
    public void putIntList(GameDataElements element, List<Integer> value) {
        dataIntList.put(element, value);
    }
    public List<Integer> getIntList(GameDataElements element) {
        return dataIntList.get(element);
    }

    // String型List
    public void putStrList(GameDataElements element, List<String> value) {
        dataStrList.put(element, value);
    }
    public List<String> getStrList(GameDataElements element) {
        return dataStrList.get(element);
    }

    // Hash:StringキーのPlayRecord:String値のMap 要素が一意に定まるから正直不要？
    public void putHashedPlayRecords(GameDataElements elements, Map<String, String> playRecords) {
        dataHashedPlayRecords.put(elements, playRecords);
    }
    public Map<String, String> getHashedPlayRecords(GameDataElements element) {
        return dataHashedPlayRecords.get(element);
    }

    // ------------------------------------------------------------------------------ //
    // 特定の要素の出力を簡易にするメソッド

    /**
     * ウインドウ名を取得する。
     */
    public String getWindowName() {
        return get(GameDataElements.WINDOW_NAME, String.class);
    }

    /**
     * ウインドウの座標を取得する。
     * @return ウインドウの座標（Pointオブジェクト）
     */
    public Point getWindowPoint() {
        return get(GameDataElements.WINDOW_POINT, Point.class);
    }

    /**
     * ウインドウサイズを取得する。
     * @return ウインドウサイズ（Dimensionオブジェクト）
     */
    public Dimension getWindowSize() {
        return get(GameDataElements.WINDOW_SIZE, Dimension.class);
    }

    /**
     * フレームレートを取得する。
     * @return フレームレート（int型）
     */
    public int getFrameRate() {
        return get(GameDataElements.FRAME_RATE, Integer.class);
    }

    /**
     * 主音量を取得する。
     * @return 主音量（Float型）
     */
    public float getMasterVolume() {
        return get(GameDataElements.MASTER_VOLUME, Float.class);
    }

    /**
     * 場面マネージャを取得する。
     */
    public SceneManager getSceneManager() {
        return get(GameDataElements.SCENE_MANAGER, SceneManager.class);
    }

    /**
     * 現在の場面（enum）を取得する。
     */
    public Scene getScene() {
        return get(GameDataElements.SCENE, Scene.class);
    }

    /**
     * 場面IDを加算する。
     */
    public void incrementSceneID() {
        int id = get(GameDataElements.SCENE_ID, Integer.class);
        put(GameDataElements.SCENE_ID, id + 1, Integer.class);
    }

    // ------------------------------------------------------------------------------ //
    // ファイルやディレクトリのパス出力を簡易にするメソッド

    /**
     * ファイル名を取得する。
     */
    public String getFileName(GameDataElements fileElement) {
        return get(fileElement, String.class);
    }

    /**
     * ディレクトリの絶対パスの取得をする。
     * @param dirElement ディレクトリ（GameDataElements要素）
     * @return ディレクトリの絶対パス（文字型）
     */
    public String getDirectoryPathStr(GameDataElements dirElement) {
        String root = get(GameDataElements.ROOT_DIRECTORY, String.class);
        return root + "\\" + data.get(dirElement);
    }

    /**
     * ディレクトリの絶対パスの取得をする。
     * @param dirElement ディレクトリ（GameDataElements要素）
     * @return ディレクトリの絶対パス（PATH型）
     */
    public Path getDirectoryPathPath(GameDataElements dirElement) {
        return Path.of(getDirectoryPathStr(dirElement) );
    }

    /**
     * ファイルの絶対パスの取得をする。
     * <br/>
     * ファイル名に"\"を含まない場合はdirフォルダ内のものとして構成し、
     * ファイル名に"\"を含む場合は絶対アドレスと見做す。
     * @param dirElement    ディレクトリ（GameDataElements要素）
     * @param fileName      ファイル名、もしくはファイルの絶対アドレス（文字型）
     * @return ファイルの絶対パス（文字型）
     */
    public String getFilePathStr(GameDataElements dirElement, String fileName) {
        return ( !fileName.contains("\\")
                ? getDirectoryPathStr(dirElement) + "\\" : "")
                + fileName;
    }
    /**
     * ファイルの絶対パスの取得をする。
     * @param dirElement    ディレクトリ（GameDataElements要素）
     * @param fileElement   ファイル（GameDataElements要素）
     * @return ファイルの絶対パス（文字型）
     */
    public String getFilePathStr(GameDataElements dirElement, GameDataElements fileElement) {
        String fileName = get(fileElement, String.class);
        return getFilePathStr(dirElement, fileName);
    }

    /**
     * ファイルの絶対パスの取得をする。
     * @param dirElement    ディレクトリ（GameDataElements要素）
     * @param fileName      ファイル名、もしくはファイルの絶対アドレス（文字型）
     * @return ファイルの絶対パス（PATH型）
     */
    public Path getFilePathPath(GameDataElements dirElement, String fileName) {
        return Path.of(getFilePathStr(dirElement, fileName) );
    }
    /**
     * ファイルの絶対パスの取得をする。
     * @param dirElement    ディレクトリ（GameDataElements要素）
     * @param fileElement   ファイル（GameDataElements要素）
     * @return ファイルの絶対パス（PATH型）
     */
    public Path getFilePathPath(GameDataElements dirElement, GameDataElements fileElement) {
        return Path.of(getFilePathStr(dirElement, fileElement) );
    }
}
