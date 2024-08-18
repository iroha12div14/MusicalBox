package txt;

// reference: ChatGPT
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TextFilesManager {
    private final String dirPunchCard;

    // コンストラクタで読み取り先ディレクトリを指定
    public TextFilesManager(String directory) {
        dirPunchCard = directory;
    }

    public Map<String, List<String>> loadTextFiles() {
        Map<String, List<String>> textFiles = new HashMap<>();

        // ディレクトリをPathオブジェクトに変換
        Path dirPath = Paths.get("./" + dirPunchCard);

        // ディレクトリ内の.txtファイルを読み込む
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
            for (Path entry : stream) {
                String fileName = String.valueOf(entry.getFileName());
                List<String> textFile = Files.readAllLines(entry);
                textFiles.put(fileName, textFile);
            }
            return textFiles;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getTextFileNames() {
        List<String> textFileNames = new ArrayList<>();

        // ディレクトリをPathオブジェクトに変換
        Path dirPath = Paths.get("./" + dirPunchCard);

        // ディレクトリ内の.txtファイルを読み込まず、名前だけ拾う
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
            for (Path entry : stream) {
                String fileName = String.valueOf(entry.getFileName());
                textFileNames.add(fileName);
            }
            return textFileNames;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
