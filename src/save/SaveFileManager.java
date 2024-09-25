package save;

import data.GameDataElements;
import data.GameDataIO;
import logger.MessageLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * セーブファイルの作成・適用をする。
 */
public class SaveFileManager {
    /**
     * セーブファイルを適用する
     * @param dataIO ゲーム内でやり取りされるデータの入出力を行う
     */
    public void applySaveFile(GameDataIO dataIO) {
        SaveDataManager manager = new SaveDataManager();

        String dirPathStr = dataIO.getDirectoryPathStr(GameDataElements.DIR_SAVE_DATA);
        File saveDir = new File(dirPathStr);
        Path filePathPath = dataIO.getFilePathPath(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);
        String filePathStr = dataIO.getFilePathStr(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);

        if(saveDir.exists() && Files.exists(filePathPath) ) {
            MessageLogger.printMessage(this, "セーブデータをロード中", 2);
            manager.applySaveData(dataIO, filePathStr);
        }
    }

    /**
     * セーブファイルを作成する
     * @param dataIO ゲーム内でやり取りされるデータの入出力を行う
     */
    public void makeSaveFile(GameDataIO dataIO) {
        SaveDataManager manager = new SaveDataManager();

        String dirPathStr = dataIO.getDirectoryPathStr(GameDataElements.DIR_SAVE_DATA);
        File saveDir = new File(dirPathStr);
        Path filePath = dataIO.getFilePathPath(GameDataElements.DIR_SAVE_DATA, GameDataElements.FILE_SAVE_DATA);
        try {
            // セーブファイルのディレクトリが無いなら作る
            if(!saveDir.exists() ) {
                saveDir.mkdir(); // 成否は一旦無視
            }

            if( !Files.exists(filePath) ) {
                Files.createFile(filePath); // ファイルを作成
                manager.makeSaveData(dataIO, filePath); // ファイルを編集
                MessageLogger.printMessage(this, "セーブファイルを新規作成", 2);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
