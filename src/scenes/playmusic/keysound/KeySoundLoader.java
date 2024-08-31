package scenes.playmusic.keysound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
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
    private String message;
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

        printBorder();
        messageInit(2);

        for (int p = 0; p < pitchCount; p++) {
            // 音源ファイルのインスタンス化
            String fileName = "org127" + container.getPitchesName(p) + ".wav";
            String address = "./" + dirSounds + "/" + fileName;
            File file = new File(address);

            // 各音程それぞれの発音回数だけ再生用Clipを用意する
            // 鳴動枠数 = 手動演奏固定枠 + ある音程pの楽曲のメロディの自動演奏回数 + ある音程pの楽曲の伴奏の自動演奏回数
            int playCount = mpcc + mainScoreAutoPlayCount[p] + subScoreAutoPlayCount[p];

            Clip[] clip = new Clip[playCount];
            boolean loadFail = false;
            for(int c = 0; c < playCount; c++) {
                // fileは使いまわしでOK
                // stream、format、infoはループの度に定義しないとバグる
                try {
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
                }
                catch (FileNotFoundException e) {
                    // プレビューファイルが無い場合はここを通る
                    // clip[c]はnullになるのでぬるぽが出ないよう各メソッド側で弾いておく
                    if( !loadFail ) {
                        loadFail = true;
                    }
                }
                catch ( // エラーをまとめてポイ
                        UnsupportedAudioFileException |
                        LineUnavailableException |
                        IOException e )
                {
                    messageFailed(2);
                    throw new RuntimeException(e);
                }
            }
            container.setClip(p, clip);
            if(loadFail) {
                messageFileNodFound(address);
            }
        }
        messageLoading(readyClips, allClipsCount, 2);
        if(readyClips == allClipsCount) {
            messageComplete(2);
        } else {
            messageFailed(2);
        }
        printBorder();
        return container;
    }

    // 無音ファイルを読み込んで作ったクリップを格納
    public void containNoSound() {
        String address = "./" + dirSounds + "/" + noSoundFile;
        File file = new File(address);
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();

            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip c = (Clip) AudioSystem.getLine(info);

            c.open(stream);
            container.setNoSoundClip(c);
        }
        catch (FileNotFoundException e) {
            // プレビューファイルが無い場合はここを通る
            // clip[f]はnullになるのでぬるぽが出ないよう各メソッド側で弾いておく
            messageFileNodFound(address);
        }
        catch (   // エラーをまとめてポイ
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
    private void printMessageLoadClips(int tab) {
        String msg = message + "\t".repeat(tab) + "@KeySoundLoader";
        System.out.println(msg);
    }
    private void printBorder() {
        System.out.println("--------------------------------------");
    }
    private void messageInit(int tab) {
        message = "Wave Loading...           ";
        printMessageLoadClips(tab);
    }
    private void messageLoading(int readyClips, int allClipsCount, int tab) {
        message = "Loaded Wave Files: " + readyClips + "/" + allClipsCount;
        printMessageLoadClips(tab);
    }
    private void messageComplete(int tab) {
        message = "Complete Wave Load.       ";
        printMessageLoadClips(tab);
    }
    private void messageFailed(int tab) {
        message = "Wave Loading Failed.      ";
        printMessageLoadClips(tab);
    }
    private void messageFileNodFound(String address) {
        message = "  <Error> " + address + "が見つかりません。";
        System.out.println(message);
    }
}
