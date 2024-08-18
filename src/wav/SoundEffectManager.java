package wav;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/*
 * // 使い方はこんな感じ
 * private final String[] seSoundFiles = {"knock_book01.wav", "page_swipe01.wav"};
 * private final String SE_KNOCK = "knock_book01.wav";
 * private final String SE_SWIPE = "page_swipe01.wav";
 *
 * private final WaveFileManager wfm = new WaveFileManager(seSoundFiles);
 *
 * wfm.startSound(SE_KNOCK);
 */

// 効果音の再生に用いるクラス
public class SoundEffectManager {
    private Clip[] clips;
    private final String[] fileNames;
    private final String dirSoundEffect;

    // インスタンス
    // 読み込み先のディレクトリ名と使うファイル名をここで格納
    public SoundEffectManager(String directory, String[] fileNames) {
        dirSoundEffect = directory;
        this.fileNames = fileNames;
    }

    // ファイルの読み込み
    public void loadWaveFile() {
        clips = new Clip[fileNames.length];
        int fileCount = fileNames.length;

        for(int f = 0; f < fileCount; f++) {
            String address = "./" + dirSoundEffect + "/" + fileNames[f];
            File file = new File(address);
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = stream.getFormat();

                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clips[f] = (Clip) AudioSystem.getLine(info);
                clips[f].open(stream);

            } catch ( // エラーをまとめてポイ
                    UnsupportedAudioFileException |
                    LineUnavailableException |
                    IOException e
            ) {
                throw new RuntimeException(e);
            }
        }
    }

    // 音声の再生
    public void startSound(int i) {
        clips[i].stop();
        clips[i].flush();
        clips[i].setFramePosition(10);
        clips[i].start();
    }
    public void startSound(String s) {
        int i = findFileName(s);
        if(i != -1) {
            startSound(i);
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
}
