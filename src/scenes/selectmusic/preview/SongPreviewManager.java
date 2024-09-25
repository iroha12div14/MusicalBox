package scenes.selectmusic.preview;

import logger.MessageLogger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * 効果音の再生に用いるクラス
 */
public class SongPreviewManager {
    private Clip[] clips;
    private final String dirPath; // ディレクトリの絶対パス
    private final String[] fileNames; // 拡張子を含まないファイル名の一覧

    private boolean readyPreview = false;   // 再生準備の有無(多重再生の防止)
    private String playedPreviewFile = "";  // 再生中のファイル名(stopに用いる情報の保持)

    private float masterVolume = 1.0F;
    private static final float previewVolume = 0.5F; // プレビューの再生音量

    /**
     * インスタンス. 読み込み先のディレクトリ名と使うファイル名をここで格納
     * @param dirPath 読み込み先ディレクトリの絶対パス（文字型）
     * @param fileNames ファイル名の一覧（文字列配列）
     */
    public SongPreviewManager(String dirPath, String[] fileNames) {
        this.dirPath = dirPath;
        this.fileNames = new String[fileNames.length];
        for(int f = 0; f < fileNames.length; f++) {
            // 単純な置き換えだけどファイル名のド真ん中に".txt"が含まれてる音声ファイルは多分無いでしょう 多分
            this.fileNames[f] = fileNames[f].replace(".txt", "");
        }
    }

    // ファイルの読み込み
    public void loadWaveFile() {
        clips = new Clip[fileNames.length];
        int fileCount = fileNames.length;

        for(int f = 0; f < fileCount; f++) {
            String fileName = fileNames[f] + ".wav";

            String address = dirPath + "\\" + fileName;
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
     * 楽曲プレビューの再生
     * @param s 音源名（文字列）
     */
    public void startPreview(String s) {
        int i = findFileName(s);
        if(i != -1) {
            startPreview(i);
        }
    }
    private void startPreview(int i) {
        if(clips[i] != null) {
            setVolume(clips[i]);
            clips[i].start();
        }
        readyPreview = false;
    }

    /**
     * 楽曲プレビューの停止
     */
    public void stopPreview() {
        int i = findFileName(playedPreviewFile);
        if(i != -1) {
            stopPreview(i);
        }
    }
    private void stopPreview(int i) {
        if(clips[i] != null) {
            clips[i].stop();
            clips[i].flush();
            clips[i].setFramePosition(0);
        }
    }
    // ファイル名探索(start, stopどちらでも使う)
    private int findFileName(String scoreTextName) {
        String fileName = scoreTextName.replace(".txt", "");
        playedPreviewFile = fileName;
        for(int f = 0; f < fileNames.length; f++) {
            if(Objects.equals(fileNames[f], fileName) ) {
                return f;
            }
        }
        return -1;
    }

    /**
     * プレビューの再生準備ができているか
     */
    public boolean isReadyPreview() {
        return readyPreview;
    }
    /**
     * 多重再生防止トリガーを外す
     */
    public void readyStartPreview() {
        readyPreview = true;
    }

    // 音量の調整
    private void setVolume(Clip c) {
        float volume = masterVolume * previewVolume;
        FloatControl volumeControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
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

    /**
     * クリップをクローズする
     */
    public void closeClips() {
        for(Clip clip : clips) {
            if(clip != null) {
                clip.stop();
                clip.flush();
                clip.close();
            }
        }
    }
}
