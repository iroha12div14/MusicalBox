package scenes.playmusic.keysound;

import javax.sound.sampled.Clip;

/*
 * clipsの構造
 *     [0] [0 ~ clipPlayLimit-1] [clipPlayLimit ~ …]
 *     [1] [0 ~ clipPlayLimit-1] [clipPlayLimit ~ …]
 *     [2] [0 ~ clipPlayLimit-1] [clipPlayLimit ~ …]
 *     ……
 * みたいな感じの2次元配列
 *
 * manualPlayCounterCycle(以下、"mpcc")
 *     手動演奏用カウンタがこの値に達する前に0に循環させる
 *
 * clips[n] の長さは (mpcc + 曲に含まれる音程nの鳴動回数)
 * 枠の用途
 *     clips[n][0 ~ mpcc-1]
 *         手動演奏用で何度も使いまわす
 *     clips[n][mpcc ~ …]
 *         自動再生用で確実に鳴らせるように1度きり
 *
 * manualPlayCounter
 *     手動演奏用の再生音源の指定カウンタ
 *     manualPlayCounter[n]の初期値は0
 *     0 ~ mpcc-1で循環する
 *
 * autoPlayCounter
 *     は自動再生用の再生音源の指定カウンタ
 *     autoPlayCounter[n]の初期値はmpcc
 *     加算され続ける
 *
 */

// 読み込んだ音源データを格納するクラス

public class KeySoundContainer {
    // 音声データの音程名
    private final String[] pitchesName = {
            "_4An", "_4Bf", "_4Bn",
            "_5Cn", "_5Cs", "_5Dn", "_5Ef", "_5En",
            "_5Fn", "_5Fs", "_5Gn", "_5Gs", "_5An", "_5Bf", "_5Bn",
            "_6Cn", "_6Cs", "_6Dn", "_6Ef", "_6En",
            "_6Fn", "_6Fs", "_6Gn", "_6Gs", "_6An", "_6Bf", "_6Bn",
            "_7Cn", "_7Cs", "_7Dn", "_7Ef", "_7En"
    };
    // 手動演奏用のカウンタの上限
    private final int manualPlayCounterCycle = 12;

    // 音源の格納先
    private final Clip[][] clips;
    // 手動演奏時に再生する音源の番号
    private final int[] manualPlayCounter;
    // 自動再生時に再生する音源の番号
    private final int[] autoPlayCounter;

    // 無音（動作安定用）
    private Clip noSoundClip;

    // コンストラクタ
    public KeySoundContainer() {
        int pitchCount = getPitchCount();

        // クリップ
        clips = new Clip[pitchCount][];

        // 手動演奏用カウンタ、自動再生用カウンタ
        manualPlayCounter = new int[pitchCount]; // 自動で0が補完される
        autoPlayCounter = new int[pitchCount];
        for(int p = 0; p <pitchCount; p++) {
            autoPlayCounter[p] = manualPlayCounterCycle;
        }
    }

    // クリップの格納
    public void setClip(int p, Clip[] clip) {
        clips[p] = clip;
    }
    // 無音クリップの格納
    public void setNoSoundClip(Clip clip) {
        noSoundClip = clip;
    }

    // 音源の種類の数
    public int getPitchCount() {
        return pitchesName.length;
    }
    // 音源名の取得
    public String getPitchesName(int p) {
        return pitchesName[p];
    }
    // クリップの各音程の枠数
    public int[] getClipLength() {
        int[] len = new int[getPitchCount()];
        for(int p = 0; p < getPitchCount(); p++) {
            len[p] = clips[p].length - manualPlayCounterCycle;
        }
        return len;
    }

    // 手動演奏用のクリップの枠数
    public int getManualPlayCounterCycle() {
        return manualPlayCounterCycle;
    }

    // 手動演奏用のクリップを取得
    public Clip getManualPlayClip(int p) {
        int manualPlayCounter = getManualPlayCounter(p);
        updateManualPlayCounter(p);
        return getClip(p, manualPlayCounter);
    }
    // 自動再生用のクリップを取得
    public Clip getAutoPlayClip(int p) {
        int autoPlayCounter = getAutoPlayCounter(p);
        updateAutoPlayCounter(p);
        return getClip(p, autoPlayCounter);
    }
    // クリップを取得
    private Clip getClip(int p, int counter) {
        Clip[] clip = clips[p];
        return clip[counter];
    }
    public Clip getNoSoundClip() {
        return noSoundClip;
    }

    // 手動演奏用カウンタ
    private int getManualPlayCounter(int p) {
        return manualPlayCounter[p];
    }
    // 自動再生用カウンタ
    private int getAutoPlayCounter(int p) {
        return autoPlayCounter[p];
    }
    // カウンタのアップデート
    private void updateManualPlayCounter(int p) {
        int cnt = manualPlayCounter[p];
        manualPlayCounter[p] = (cnt == manualPlayCounterCycle - 1) ? 0 : cnt + 1;
    }
    private void updateAutoPlayCounter(int p) {
        autoPlayCounter[p]++;
    }

    // クリップが抱えているリソースを開放
    public void closeClips(){
        for(Clip[] clip : clips) {
            for(Clip c : clip) {
                if(c != null) {
                    c.stop();
                    c.flush();
                    c.close();
                }
            }
        }
        if(noSoundClip != null) {
            noSoundClip.stop();
            noSoundClip.flush();
            noSoundClip.close();
        }
    }
}
