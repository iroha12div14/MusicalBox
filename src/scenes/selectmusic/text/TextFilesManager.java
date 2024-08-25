package scenes.selectmusic.text;

// reference: ChatGPT
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TextFilesManager {
    /**
     * ディレクトリ内のテキストファイルを読み込み、ファイル名をキーとして内容を格納する
     * @param directory ディレクトリ名（文字列）
     * @return テキストの内容(キーがファイル名, 値は内容)
     */
    public Map<String, List<String>> loadTextFiles(String directory) {
        Map<String, List<String>> textFiles = new HashMap<>();

        // ディレクトリ名をPathオブジェクトに変換
        Path dirPath = Paths.get("./" + directory);

        // ディレクトリ内のファイルを読み込み、内容を格納
        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")
        ) {
            for (Path entry : stream) {
                String fileName = String.valueOf(entry.getFileName() );
                List<String> textFile = Files.readAllLines(entry);
                textFiles.put(fileName, textFile);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textFiles;
    }

    /**
     * ディレクトリ内のすべてのテキストファイルの名前を取得する
     * @param directory ディレクトリ名（文字列）
     * @return ファイル名（リスト）
     */
    public List<String> getTextFileNames(String directory) {
        List<String> textFileNames = new ArrayList<>();

        // ディレクトリをPathオブジェクトに変換
        Path dirPath = Paths.get("./" + directory);

        // ディレクトリ内の.txtファイルを読み込まず、名前だけ拾う
        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")
        ) {
            for (Path entry : stream) {
                String fileName = String.valueOf(entry.getFileName() );
                textFileNames.add(fileName);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textFileNames;
    }
}
