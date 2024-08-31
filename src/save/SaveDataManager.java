package save;

import data.DataCaster;
import data.DataElements;
import scenes.playmusic.findstr.FindStrUtil;
import text.TextFilesManager;

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
            // 一般設定
            pw.println(categoryGeneral);
            writeIntPrint(pw, data, FRAME_RATE, elem.FRAME_RATE);
            writeFloatPrint(pw, data, MASTER_VOLUME, elem.MASTER_VOLUME);
            writeIntPrint(pw, data, JUDGEMENT_SUB_DISPLAY, elem.JUDGEMENT_SUB_DISPLAY);
            writeFloatPrint(pw, data, NOTE_UNIT_MOVE, elem.NOTE_UNIT_MOVE);
            pw.println();

            // トロフィー
            pw.println(categoryTrophy);
            writeIntPrint(pw, data, ACHIEVEMENT_POINT, elem.ACHIEVEMENT_POINT);
            writeIntPrint(pw, data, PLAY_COUNT, elem.PLAY_COUNT);
            writeTrophy(pw, data);
            pw.println();

            // 楽曲別トロフィー
            pw.println(categoryMusicTrophy);
            writeMusicTrophy(pw, data);
            pw.println();

            // 楽曲別記録
            pw.println(categoryPlayRecord);
            writePlayRecord(pw, data);
            pw.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // int型データの書き込み
    private void writeIntPrint(PrintWriter pw, Map<Integer, Object> data, String element, int elem) {
        int value = cast.getIntData(data, elem);
        pw.println(element + splitToken + value);
    }
    // float型データの書き込み
    private void writeFloatPrint(PrintWriter pw, Map<Integer, Object> data, String element, int elem) {
        float value = cast.getFloatData(data, elem);
        pw.println(element + splitToken + value);
    }
    // 一般実績の書き込み
    private void writeTrophy(PrintWriter pw, Map<Integer, Object> data) {
        List<Integer> trophies = cast.getIntListData(data, elem.TROPHY);
        StringBuilder str = new StringBuilder();
        int len = trophies.size();
        if(len != 0) {
            int tailStr = trophies.get(len - 1);
            for(int trophy : trophies) {
                str.append(trophy);
                if(trophy != tailStr) {
                    str.append(",");
                }
            }
        }
        pw.println(TROPHY_LIST + splitToken + str.toString() );
    }
    // 楽曲別実績の書き込み
    private void writeMusicTrophy(PrintWriter pw, Map<Integer, Object> data) {
        List<String> musicTrophies = cast.getStrListData(data, elem.MUSIC_TROPHY);
        for(String trophy : musicTrophies) {
            pw.println(trophy);
        }
    }
    // 楽曲別記録の書き込み
    private void writePlayRecord(PrintWriter pw, Map<Integer, Object> data) {
        Map<String, String> records = cast.getHashedStringData(data, elem.PLAY_RECORD);
        List<String> hashes = cast.getStrListData(data, elem.MUSIC_HASH_VALUE);
        for(String hash : hashes) {
            String record = records.get(hash);
            pw.println(hash + splitToken + record);
        }
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
        List<String> lines = new TextFilesManager().loadTextFile(directory, saveFile);
        int mode = 0;
        List<Integer> trophies = new ArrayList<>();
        List<String> musicHashValues = cast.getStrListData(data, elem.MUSIC_HASH_VALUE);
        List<String> musicTrophies = new ArrayList<>();
        Map<String, String > playRecord = new HashMap<>();
        for(String line : lines) {
            if (line.equals(categoryGeneral) ) {
                mode = 1;
            } else if(line.equals(categoryTrophy) ) {
                mode = 2;
            } else if(line.equals(categoryMusicTrophy) ) {
                mode = 3;
            } else if(line.equals(categoryPlayRecord) ) {
                mode = 4;
            }
            else if( !line.isEmpty() ) {
                String[] splitStr = line.split(splitToken);
                String keyStr = splitStr[0];
                String valStr;
                if(splitStr.length > 1) {
                    valStr = splitStr[1];
                } else {
                    valStr = "";
                }

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
                    // 累積ポイントや回数
                    if(Objects.equals(keyStr, ACHIEVEMENT_POINT) ) {
                        data.put(elem.ACHIEVEMENT_POINT, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, PLAY_COUNT) ) {
                        data.put(elem.PLAY_COUNT, Integer.parseInt(valStr) );
                    }
                    // トロフィー
                    // (TROPHY_LIST): 1001,1002,1011,2001   みたいな感じ
                    else if(Objects.equals(keyStr, TROPHY_LIST) ) {
                        if( !valStr.isEmpty() ) {
                            String[] values = valStr.split(",");
                            for (String val : values) {
                                trophies.add(Integer.parseInt(val));
                            }
                        }
                    }

                } else if(mode == 3) {
                    // keyStrがそのままSHA-256になる
                    // 列挙された分だけトロフィーとして追加する
                    if(musicHashValues.contains(keyStr) ) {
                        musicTrophies.add(keyStr);
                    }

                } else if(mode == 4) {
                    // keyStrがそのままSHA-256になる
                    // valStrは文字列化されたプレーデータ
                    //      (keyStr): 0,0.0/0,0.0/2,99.02
                    //      (keyStr): 2,97.65/1,96.12/1,87.03   みたいな感じ
                    if(musicHashValues.contains(keyStr) ) {
                        playRecord.put(keyStr, valStr);
                    }
                }
            }
        }
        data.put(elem.TROPHY, trophies);
        data.put(elem.MUSIC_TROPHY, musicTrophies);
        if(mode == 4) { // 折角初期化したデータを空マップを埋めてしまうバグが出たので一時的対処
            data.put(elem.PLAY_RECORD, playRecord);
        }
        return data;
    }

    // ----------------------------------------------------------------------- //
    private final DataCaster cast = new DataCaster();
    private final DataElements elem = new DataElements();

    private final String categoryGeneral = "#GENERAL";
    private final String categoryTrophy = "#TROPHY";
    private final String categoryMusicTrophy = "#MUSIC_TROPHY";
    private final String categoryPlayRecord = "#PLAY_RECORD";
    private final String splitToken = ": ";

    private final String FRAME_RATE = "FRAME_RATE";
    private final String MASTER_VOLUME = "MASTER_VOLUME";
    private final String JUDGEMENT_SUB_DISPLAY = "JUDGEMENT_SUB_DISPLAY";
    private final String NOTE_UNIT_MOVE = "NOTE_UNIT_MOVE";

    private final String ACHIEVEMENT_POINT = "ACHIEVEMENT_POINT";
    private final String PLAY_COUNT = "PLAY_COUNT";
    private final String TROPHY_LIST = "LIST";
}
