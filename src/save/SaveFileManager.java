package save;

import data.DataCaster;
import data.DataElements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SaveFileManager {

    /**
     * セーブファイルを適用する
     * @param data data
     */
    public Map<Integer, Object> applySaveFile(Map<Integer, Object> data) {
        SaveDataManager manager = new SaveDataManager();
        DataCaster cast = new DataCaster();
        DataElements elem = new DataElements();

        String saveDirectory = cast.getStrData(data, elem.DIRECTORY_SAVE_DATA);
        File saveDir = new File(saveDirectory);
        String saveFile = cast.getStrData(data, elem.FILE_SAVE_DATA);
        Path filePath = Paths.get("./" + saveDirectory, saveFile);
        if(saveDir.exists() && Files.exists(filePath) ) {
            printMessage("セーブデータをロード中",2);
            return manager.applySaveData(data, saveDirectory, saveFile);
        } else {
            return data;
        }
    }

    /**
     * セーブファイルを作成する
     * @param data data
     */
    public void makeSaveFile(Map<Integer, Object> data) {
        SaveDataManager manager = new SaveDataManager();
        DataCaster cast = new DataCaster();
        DataElements elem = new DataElements();

        String saveDirectory = cast.getStrData(data, elem.DIRECTORY_SAVE_DATA);
        File saveDir = new File(saveDirectory);
        String saveFile = cast.getStrData(data, elem.FILE_SAVE_DATA);
        Path filePath = Paths.get("./" + saveDirectory, saveFile);
        try {
            // セーブファイルのディレクトリが無いなら作る
            if(!saveDir.exists() ) {
                saveDir.mkdir(); // 成否は一旦無視
            }

            if( !Files.exists(filePath) ) {
                Files.createFile(filePath); // ファイルを作成
                manager.makeSaveData(data, saveDirectory, saveFile); // ファイルを編集
                printMessage("セーブファイルを新規作成", 2);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ログ出力用
    private void printMessage(String msg, int tab) {
        String classFullName = this.getClass().getName();
        String[] classSplitName = classFullName.split("\\.");
        String className = classSplitName[classSplitName.length - 1];
        System.out.println(msg + "\t".repeat(tab) + "@" + className);
    }
}
