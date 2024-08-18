package wav;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/* ----------------------------------------------------------------
 * JavaサウンドAPIにはjavax.sound.sampled、javax.sound.midi の2種類があって
 * javax.sound.sampled はサンプリングデータを扱うAPI(.mp4はダメっぽい)
 * javax.sound.midi はMIDIを扱うAPI
 * -----------------------------------------------------------------
 */

// 音声データの読み込みと再生準備を行うクラス
// データの格納されたcontainerを返すので再生時はそれであーだこーだ？

public class KeySoundLoader {
    private final KeySoundContainer container = new KeySoundContainer();
    private String msgLoadClips;
    private final String dirSounds;
    private final String noSoundFile = "nosound.wav";

    // コンストラクタで読み取り先ディレクトリを指定
    public KeySoundLoader(String directory) {
        dirSounds = directory;
    }

    // 音源ファイルの再生準備
    public KeySoundContainer createContainer(int[] mainScoreAutoPlayCount, int[] subScoreAutoPlayCount) {
        int pitchCount = container.getPitchCount();
        int mpcc = container.getManualPlayCounterCycle();

        int allClipsCount
                = mpcc * pitchCount
                + Arrays.stream(mainScoreAutoPlayCount).sum()
                + Arrays.stream(subScoreAutoPlayCount).sum();
        int readyClips = 0;
        System.out.println("--------------------------------------");
        initMessageLoadClips();
        System.out.println(getMessageLoadClips(2));

        setMessageLoadClips(readyClips, allClipsCount);
        System.out.println(getMessageLoadClips(2));

        try {
            for (int p = 0; p < pitchCount; p++) {
                // 音源ファイルのインスタンス化
                String fileName = "org127" + container.getPitchesName(p) + ".wav";
                String address = "./" + dirSounds + "/" + fileName;
                File file = new File(address);

                // 各音程それぞれの発音回数だけ再生用Clipを用意する
                // 鳴動枠数 = 手動演奏固定枠 + ある音程pの楽曲のメロディの自動演奏回数 + ある音程pの楽曲の伴奏の自動演奏回数
                int playCount = mpcc + mainScoreAutoPlayCount[p] + subScoreAutoPlayCount[p];

                Clip[] clip = new Clip[playCount];
                for(int c = 0; c < playCount; c++) {
                    // fileは使いまわしでOK
                    // stream、format、infoはループの度に定義しないとバグる

                    // AudioSystemはサンプリング系のクラスの窓口となるクラス
                    // ファイルを読み込んだりなど
                    AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                    AudioFormat format = stream.getFormat();

                    // DataLineは音声入出力を行うインターフェイス
                    // サブインターフェイスのうち、出力用としてSourceDataLineとClipがある。
                    // Clipはデータをメモリ上に読み込んでおいてから再生する（短い音声に適している）
                    DataLine.Info info = new DataLine.Info(Clip.class, format);
                    clip[c] = (Clip) AudioSystem.getLine(info);

                    // 再生準備
                    clip[c].open(stream);
                    readyClips++;

                    // 読み込みログ
                    // TODO: コンソールではなく画面に出力する
                    setMessageLoadClips(readyClips, allClipsCount);
                    if(readyClips == allClipsCount) {
                        System.out.println(getMessageLoadClips(2));
                    }
                }
                container.setClip(p, clip);
            }
            completeMessageLoadClips();
            System.out.println(getMessageLoadClips(2));
            System.out.println("--------------------------------------");
            return container;
        }
        catch ( // エラーをまとめてポイ
            UnsupportedAudioFileException |
            LineUnavailableException |
            IOException e )
        {
            msgLoadClips = "Wave Loading Failed.";
            System.out.println(getMessageLoadClips(2));
            // これなに？
            throw new RuntimeException(e);
        }
    }

    // 無音ファイルを読み込んで作ったクリップを格納
    public void containNoSound() {
        try {
            String fileName = noSoundFile;
            String address = "./" + dirSounds + "/" + fileName;
            File file = new File(address);

            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip c = (Clip) AudioSystem.getLine(info);

            c.open(stream);
            container.setNoSoundClip(c);

        } catch (   // エラーをまとめてポイ
                    // メソッドのシグネチャに入れようかとも思ったけど
                    // メソッドを使ったメソッドにもシグネチャにエラー退避書かなきゃいけなくなるのが面倒でやめた
                UnsupportedAudioFileException |
                LineUnavailableException |
                IOException e
        ) {
            throw new RuntimeException(e);
        }
    }

    // ロード時のメッセージの出力
    public String getMessageLoadClips(int tab) {
        return msgLoadClips + "\t".repeat(tab) + "@KeySoundLoader";
    }
    private void initMessageLoadClips() {
        msgLoadClips = "Wave Loading...           ";
    }
    private void setMessageLoadClips(int readyClips, int allClipsCount) {
        msgLoadClips = "Loading Wave Files: " + readyClips + "/" + allClipsCount;
    }
    private void completeMessageLoadClips() {
        msgLoadClips = "Complete Wave Load.       ";
    }
}
