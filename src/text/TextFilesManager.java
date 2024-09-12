package text;

// reference: ChatGPT
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * テキストファイルの読み込みや、ディレクトリ内のファイル名の一括取得を行うクラス。
 */
public class TextFilesManager {
    /**
     * セーブファイルを読み込む
     * @param filePath ファイルの絶対パス（文字列）
     * @return セーブファイルの内容（文字列リスト）
     */
    public List<String> loadTextFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (
                FileReader fr = new FileReader(filePath);
                BufferedReader br = new BufferedReader(fr);
        ) {
            // 1行ずつ読んで中身があったら追加、を行末が来るまでやる
            String line;
            while( ( line = br.readLine() ) != null ) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lines;
    }
    /**
     * ディレクトリ内のテキストファイルを読み込み、ファイル名をキーとして内容を格納する
     * @param dirPath ディレクトリのパス（Path型）
     * @return テキストの内容(キーがファイル名, 値は内容)
     */
    public Map<String, List<String>> loadTextFiles(Path dirPath) {
        Map<String, List<String>> textFiles = new HashMap<>();
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
     * @param dirPath ディレクトリのパス（Path型）
     * @return ファイル名（リスト）
     */
    public List<String> getTextFileNames(Path dirPath) {
        List<String> textFileNames = new ArrayList<>();

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
