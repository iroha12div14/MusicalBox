package scenes.selectmusic.preview;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/*
 * 使い方はこんなSoundEffectManagerと大体一緒
 * ファイル名の一覧は拡張子無し
 * 読み込むときは
 *     fileName = filenames[i] + ".wav";
 *     String address = directory + "/" + dirSE + "/" + fileName;
 *     ...
 *
 * テキストファイル名から探すときは
 *     fileName = scoreTextName.replace(".txt", "");
 *     if( Object.equals(fileNames[f], fileName) ) {
 *         ...
 *
 * プレビューを再生するときは
 *     String previewFile = musicFileNames[getPointer(cursor)];
 *     startPreview(previewFile);
 *
 * プレビューを停止するときは
 *     stopPreview();
 *     // 再生中の楽曲ファイルは中で保持しているので指定しなくてもOK
 */

// 効果音の再生に用いるクラス
public class SongPreviewManager {
    private Clip[] clips;
    private final String[] fileNames; // 拡張子を含まないファイル名の一覧
    private final String dirSoundEffect;

    private boolean readyPreview = false;   // 再生準備の有無(多重再生の防止)
    private String playedPreviewFile = "";  // 再生中のファイル名(stopに用いる情報の保持)

    private float masterVolume = 1.0F;
    private static final float previewVolume = 0.5F; // プレビューの再生音量

    // インスタンス
    // 読み込み先のディレクトリ名と使うファイル名をここで格納
    public SongPreviewManager(String directory, String[] fileNames) {
        dirSoundEffect = directory;
        this.fileNames = new String[fileNames.length];
        for(int f = 0; f < fileNames.length; f++) {
            // 単純な置き換えだけどファイル名のド真ん中に".txt"が含まれてるのは多分無いでしょう 多分
            this.fileNames[f] = fileNames[f].replace(".txt", "");
        }
    }

    // ファイルの読み込み
    public void loadWaveFile() {
        clips = new Clip[fileNames.length];
        int fileCount = fileNames.length;

        for(int f = 0; f < fileCount; f++) {
            String fileName = fileNames[f] + ".wav";

            String address = "./" + dirSoundEffect + "/" + fileName;
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
                System.out.println("<Error> " + address + "が見つかりません。 @SongPreviewManager");
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

    // 楽曲プレビューの再生
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
    // 楽曲プレビューの停止
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

    // プレビューの再生準備
    public boolean isReadyPreview() {
        return readyPreview;
    }
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

    // 主音量の調整
    public void setMasterVolume(float v) {
        masterVolume = v;
    }

    // クリップをクローズしてなんとやら
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
