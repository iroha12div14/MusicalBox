package scenes.playmusic.keysound;

import scenes.playmusic.keysound.KeySoundContainer;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.Random;

/*
 * 音量調整 参考: https://nat-programming.hatenadiary.org/entry/20090923/1253697562
 *
 * 音量調整できるならピッチも変えられないかなって思ったけど
 *     clip.isControlSupported(FloatControl.Type.SAMPLE_RATE)
 * でfalseが帰って来るんで駄目みたいです。残念
 */

/**
 * 格納した音声データを再生するクラス
  */
public class KeySoundPlayer {
    private final KeySoundContainer container;
    private final Random rand = new Random();

    private float masterVolume = 1.0F;
    // 音量バランス
    private final float manualAudioVolume = 1.0F;           // 手動演奏の音量
    private final float autoAudioVolume = 0.7F;             // 自動再生の音量
    private final float[] scoreKindVolume = {1.2F, 0.9F};   // scoreKind: 0, 1

    /**
     * コンストラクタ
     * @param container 音源コンテナ
      */
    public KeySoundPlayer(KeySoundContainer container) {
        this.container = container;
    }

    // オーディオの再生 startAudioManualが手動演奏用、startAudioAutoが自動演奏用
    public void startManualAudio(int scoreKind, int pitch){
        Clip c = container.getManualPlayClip(pitch);
        if(c != null) {
            setVolume(c, scoreKind, false);

            // 使いまわすので再生位置を指定し直す
            c.stop();
            c.flush();
            c.setFramePosition(170);

            c.start();
        }
    }
    public void startAutoAudio(int scoreKind, int pitch){
        Clip c = container.getAutoPlayClip(pitch);
        if(c != null) {
            setVolume(c, scoreKind, true);
            c.start();
        }
    }
    // 無音を再生
    public void startNoSound() {
        Clip c = container.getNoSoundClip();
        if(c != null) {
            c.start();
        }
    }

    // ランダムな音程の取得
    public int randomizePitch(int p) {
        int pitchCount = container.getPitchCount();
        int r = rand.nextInt(5) - 2; // -2 ≦ r ≦ 2
        int randomized = Math.min( Math.max(p+r, 0), pitchCount - 1);
        return (randomized != p) ? randomized : randomizePitch(p); // 被ったら再帰で再抽選
    }

    // 音量の調整
    public void setVolume(Clip clip, int scoreKind, boolean auto) {
        float mul = !auto ? manualAudioVolume : autoAudioVolume;
        float volume = masterVolume * mul * scoreKindVolume[scoreKind];
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float maxVolume = volumeControl.getMaximum();
        volumeControl.setValue(Math.min( (float) Math.log10(volume) * 20, maxVolume) ); // 音量制限
    }

    // 主音量の調整
    public void setMasterVolume(float v) {
        masterVolume = v;
    }
}
