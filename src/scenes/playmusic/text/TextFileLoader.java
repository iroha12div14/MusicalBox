package scenes.playmusic.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/* --------＜メモ＞-------- */
// パンチカード List<String>型
//     外部テキストファイルを読み出したもの

// 外部テキストファイルを読み込むクラス
public class TextFileLoader {
    private String msgTextLoad = "Text Loading...";
    private int lineCount;
    private final String dirPunchCard;

    // コンストラクタで読み取り先ディレクトリを指定
    public TextFileLoader(String directory) {
        dirPunchCard = directory;
    }

    // テキストファイルの読み込み
    public List<String> loadText(String fileName) {
        String filePath = "./" + dirPunchCard + "/" + fileName;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            List<String> readLine = new ArrayList<>();
            String line;
            lineCount = 0;

            // 1行ずつ読んで中身があったら追加、を行末が来るまでやる
            while( ( line = bufferedReader.readLine() ) != null ){
                readLine.add(line);

                // TODO: コンソールじゃなくて画面に出力する
                lineCount++;
                setMessageTextLoad();
                //System.out.println(getMessageTextLoad() );
            }

            // ストリームを閉じて、それに関連するすべてのシステム・リソースを解放
            bufferedReader.close();
            return readLine;

        } catch (IOException e) {
            msgTextLoad = "Text Loading Failed.";
            System.out.println(getMessageTextLoad() );
            throw new RuntimeException(e);
        }
    }

    // ロード中メッセージ出力
    public String getMessageTextLoad() {
        return msgTextLoad;
    }
    public void setMessageTextLoad() {
        this.msgTextLoad = "Loading Text Line: " + lineCount;
    }
}
