package save;

import data.DataCaster;
import data.DataElements;
import scenes.playmusic.findstr.FindStrUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// 参考元
// Java（jdk1.8以降）のファイル入出力のサンプルプログラム - https://qiita.com/yasushi-jp/items/125f362e51ed0ff41069
// Javaプログラムからファイルを作成する方法とは【初心者向け】 - https://style.potepan.com/articles/36198.html

public class SaveDataManager {
    /**
     * セーブデータに書き込む
     * @param data data
     * @param saveDirectory ディレクトリ名（文字列）
     * @param saveFile ファイル名（文字列）
     */
    public void makeSaveData(Map<Integer, Object> data, String saveDirectory, String saveFile) {
        Path path = Paths.get("./" + saveDirectory, saveFile);
        try (
                BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(bw);
        ) {
            pw.println(categoryGeneral);

            writeIntPrint(pw, data, FRAME_RATE, elem.FRAME_RATE);
            writeFloatPrint(pw, data, MASTER_VOLUME, elem.MASTER_VOLUME);
            writeIntPrint(pw, data, JUDGEMENT_SUB_DISPLAY, elem.JUDGEMENT_SUB_DISPLAY);
            writeFloatPrint(pw, data, NOTE_UNIT_MOVE, elem.NOTE_UNIT_MOVE);

            pw.println();
            pw.println(categoryTrophy);

            writeIntPrint(pw, data, ACHIEVEMENT_POINT, elem.ACHIEVEMENT_POINT);

            pw.println();
            pw.println(categoryMusicTrophy);

            pw.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void writeIntPrint(PrintWriter pw, Map<Integer, Object> data, String element, int elem) {
        int value = cast.getIntData(data, elem);
        pw.println(element + splitToken + value);
    }
    private void writeFloatPrint(PrintWriter pw, Map<Integer, Object> data, String element, int elem) {
        float value = cast.getFloatData(data, elem);
        pw.println(element + splitToken + value);
    }

    /**
     * セーブファイルを解析し、適用する
     * @param data data
     * @param directory ディレクトリ名（文字列）
     * @param saveFile セーブファイル名（文字列）
     * @return 適用後のdata
     */
    public Map<Integer, Object> applySaveData(Map<Integer, Object> data, String directory, String saveFile) {
        data = new HashMap<>(data);
        List<String> lines = loadSaveData(directory, saveFile);
        int mode = 0;
        for(String line : lines) {
            if (line.equals(categoryGeneral) ) {
                mode = 1;
            } else if(line.equals(categoryTrophy) ) {
                mode = 2;
            } else if(line.equals(categoryMusicTrophy) ) {
                mode = 3;
            }
            else if( !line.isEmpty() && fsu.findStr(line, ":") != fsu.UNDEFINED() ) {
                String keyStr = line.split(splitToken)[0];
                String valStr = line.split(splitToken)[1];
                if(mode == 1) {
                    // TODO: なんとかして省略する
                    if(Objects.equals(keyStr, FRAME_RATE) ) {
                        data.put(elem.FRAME_RATE, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, MASTER_VOLUME) ) {
                        data.put(elem.MASTER_VOLUME, Float.parseFloat(valStr) );
                    }
                    else if(Objects.equals(keyStr, JUDGEMENT_SUB_DISPLAY) ) {
                        data.put(elem.JUDGEMENT_SUB_DISPLAY, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, NOTE_UNIT_MOVE) ) {
                        data.put(elem.NOTE_UNIT_MOVE, Float.parseFloat(valStr) );
                    }

                } else if(mode == 2) {
                    if(Objects.equals(keyStr, ACHIEVEMENT_POINT) ) {
                        data.put(elem.ACHIEVEMENT_POINT, Integer.parseInt(valStr) );
                    }

                } else if(mode == 3) {
                    // keyStrがそのままSHA-256になる
                    // valStrは","を区切りとして
                    //      [0]がboolean 曲別称号の有無
                    //      [1]がint プレー状態値（未プレイ、プレー済、フルコン、全パフェ）
                    //      [2]がfloat 達成率
                }
            }
        }
        return data;
    }
    /**
     * セーブファイルを読み込む
     * @param directory ディレクトリ名（文字列）
     * @param saveFile セーブファイル名（文字列）
     * @return セーブファイルの内容（文字列リスト）
     */
    public List<String> loadSaveData(String directory, String saveFile) {
        String filePath = "./" + directory + "/" + saveFile;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            List<String> lines = new ArrayList<>();
            String line;

            // 1行ずつ読んで中身があったら追加、を行末が来るまでやる
            while( ( line = bufferedReader.readLine() ) != null ){
                lines.add(line);
            }

            // ストリームを閉じて、それに関連するすべてのシステム・リソースを解放
            bufferedReader.close();
            return lines;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ----------------------------------------------------------------------- //
    private final DataCaster cast = new DataCaster();
    private final DataElements elem = new DataElements();
    private final FindStrUtil fsu = new FindStrUtil();

    private final String categoryGeneral = "#GENERAL";
    private final String categoryTrophy = "#TROPHY";
    private final String categoryMusicTrophy = "#MUSIC_TROPHY";
    private final String splitToken = ": ";

    private final String FRAME_RATE = "FRAME_RATE";
    private final String MASTER_VOLUME = "MASTER_VOLUME";
    private final String JUDGEMENT_SUB_DISPLAY = "JUDGEMENT_SUB_DISPLAY";
    private final String NOTE_UNIT_MOVE = "NOTE_UNIT_MOVE";

    private final String ACHIEVEMENT_POINT = "ACHIEVEMENT_POINT";
}
