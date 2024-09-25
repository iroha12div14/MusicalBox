package scenes.se;

import logger.MessageLogger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * 効果音の再生に用いるクラス
 */
public class SoundEffectManager {
    private Clip[] clips;
    private final String[] fileNames; // ファイル名の一覧
    private final String dirPath; // ディレクトリの絶対パス

    private float masterVolume = 1.0F;

    /**
     * インスタンス. 読み込み先のディレクトリ名と使うファイル名をここで格納
     * @param dirPath ディレクトリの絶対パス（文字列）
     * @param fileNames ファイル名の一覧（文字列配列）
     */
    public SoundEffectManager(String dirPath, String[] fileNames) {
        this.dirPath = dirPath;
        this.fileNames = fileNames;
    }

    // ファイルの読み込み
    public void loadWaveFile() {
        clips = new Clip[fileNames.length];
        int fileCount = fileNames.length;

        for(int f = 0; f < fileCount; f++) {
            String address = dirPath + "\\" + fileNames[f];
            File file = new File(address);
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = stream.getFormat();

                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clips[f] = (Clip) AudioSystem.getLine(info);
                clips[f].open(stream);
            }
            catch (FileNotFoundException e) {
                // プレビューファイルが無い場合はここを通る
                // clip[f]はnullになるのでぬるぽが出ないよう各メソッド側で弾いておく
                MessageLogger.printMessage(this, "<Error> " + address + "が見つかりません。");
            }
            catch ( // エラーをまとめてポイ
                    UnsupportedAudioFileException |
                    LineUnavailableException |
                    IOException e
            ) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 音声の再生
     * @param s 音源名（文字列）
     */
    public void startSound(String s) {
        int i = findFileName(s);
        if(i != -1) {
            startSound(i);
        }
    }
    private void startSound(int i) {
        if(clips[i] != null) {
            clips[i].stop();
            clips[i].flush();
            clips[i].setFramePosition(0);
            setVolume(clips[i]);
            clips[i].start();
        }
    }
    private int findFileName(String s) {
        for(int f = 0; f < fileNames.length; f++) {
            if(Objects.equals(fileNames[f], s) ) {
                return f;
            }
        }
        return -1;
    }

    // 音量の調整
    private void setVolume(Clip clip) {
        float volume = masterVolume;
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float maxVolume = volumeControl.getMaximum();
        volumeControl.setValue(Math.min( (float) Math.log10(volume) * 20, maxVolume) ); // 音量制限
    }

    /**
     * 主音量を調整する
     * @param v ボリューム（浮動点小数）
     */
    public void setMasterVolume(float v) {
        masterVolume = v;
    }
}
