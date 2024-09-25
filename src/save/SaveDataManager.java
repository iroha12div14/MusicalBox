package save;

import data.GameDataElements;
import data.GameDataIO;
import text.TextFilesManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

// 参考元
// Java（jdk1.8以降）のファイル入出力のサンプルプログラム - https://qiita.com/yasushi-jp/items/125f362e51ed0ff41069
// Javaプログラムからファイルを作成する方法とは【初心者向け】 - https://style.potepan.com/articles/36198.html

/**
 * セーブデータを作成・適用などの管理をする
 */
public class SaveDataManager {
    /**
     * セーブデータに書き込む
     * @param dataIO data
     * @param filePath セーブファイルの絶対パス（Path型）
     */
    public void makeSaveData(GameDataIO dataIO, Path filePath) {
        try (
                BufferedWriter bw = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
                PrintWriter pw = new PrintWriter(bw);
        ) {
            // 一般設定
            pw.println(categoryGeneral);
            writeIntPrint(pw, dataIO, FRAME_RATE, GameDataElements.FRAME_RATE);
            writeFloatPrint(pw, dataIO, MASTER_VOLUME, GameDataElements.MASTER_VOLUME);
            writeIntPrint(pw, dataIO, JUDGEMENT_SUB_DISPLAY, GameDataElements.JUDGE_SUB_DISPLAY);
            writeFloatPrint(pw, dataIO, NOTE_UNIT_MOVE, GameDataElements.NOTE_UNIT_MOVE);
            pw.println();

            // トロフィー
            pw.println(categoryTrophy);
            writeIntPrint(pw, dataIO, ACHIEVEMENT_POINT, GameDataElements.ACHIEVEMENT_POINT);
            writeIntPrint(pw, dataIO, PLAY_COUNT, GameDataElements.PLAY_COUNT);
            writeTrophy(pw, dataIO);
            pw.println();

            // 楽曲別トロフィー
            pw.println(categoryMusicTrophy);
            writeMusicTrophy(pw, dataIO);
            pw.println();

            // 楽曲別記録
            pw.println(categoryPlayRecord);
            writePlayRecord(pw, dataIO);
            pw.println();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // int型データの書き込み
    private void writeIntPrint(PrintWriter pw, GameDataIO dataIO, String elementStr, GameDataElements element) {
        int value = dataIO.get(element, Integer.class);
        pw.println(elementStr + splitToken + value);
    }
    // float型データの書き込み
    private void writeFloatPrint(PrintWriter pw, GameDataIO dataIO, String elementStr, GameDataElements element) {
        float value = dataIO.get(element, Float.class);
        pw.println(elementStr + splitToken + value);
    }
    // 一般実績の書き込み
    @SuppressWarnings("")
    private void writeTrophy(PrintWriter pw, GameDataIO dataIO) {
        List<Integer> trophies = dataIO.getIntList(GameDataElements.TROPHY);
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
        pw.println(TROPHY_LIST + splitToken + str);
    }
    // 楽曲別実績の書き込み
    private void writeMusicTrophy(PrintWriter pw, GameDataIO dataIO) {
        List<String> musicTrophies = dataIO.getStrList(GameDataElements.MUSIC_TROPHY);
        for(String trophy : musicTrophies) {
            pw.println(trophy);
        }
    }
    // 楽曲別記録の書き込み
    private void writePlayRecord(PrintWriter pw, GameDataIO dataIO) {
        Map<String, String> records = dataIO.getHashedPlayRecords(GameDataElements.PLAY_RECORD);
        List<String> hashes = dataIO.getStrList(GameDataElements.MUSIC_HASH_VALUE);
        for(String hash : hashes) {
            String record = records.getOrDefault(hash, playRecordDefault);
            pw.println(hash + splitToken + record);
        }
    }

    /**
     * セーブファイルを解析し、適用する
     * @param dataIO data
     * @param filePath セーブファイルの絶対パス（文字型）
     */
    public void applySaveData(GameDataIO dataIO, String filePath) {
        List<String> lines = new TextFilesManager().loadTextFile(filePath);
        int mode = 0;
        List<Integer> trophies = new ArrayList<>();
        List<String> musicHashValues = dataIO.getStrList(GameDataElements.MUSIC_HASH_VALUE);
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
                    if(Objects.equals(keyStr, FRAME_RATE) ) {
                        dataIO.put(GameDataElements.FRAME_RATE, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, MASTER_VOLUME) ) {
                        dataIO.put(GameDataElements.MASTER_VOLUME, Float.parseFloat(valStr) );
                    }
                    else if(Objects.equals(keyStr, JUDGEMENT_SUB_DISPLAY) ) {
                        dataIO.put(GameDataElements.JUDGE_SUB_DISPLAY, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, NOTE_UNIT_MOVE) ) {
                        dataIO.put(GameDataElements.NOTE_UNIT_MOVE, Float.parseFloat(valStr) );
                    }

                } else if(mode == 2) {
                    // 累積ポイントや回数
                    if(Objects.equals(keyStr, ACHIEVEMENT_POINT) ) {
                        dataIO.put(GameDataElements.ACHIEVEMENT_POINT, Integer.parseInt(valStr) );
                    }
                    else if(Objects.equals(keyStr, PLAY_COUNT) ) {
                        dataIO.put(GameDataElements.PLAY_COUNT, Integer.parseInt(valStr) );
                    }
                    // トロフィー
                    // (TROPHY_LIST): 1001,1002,1011,2001   みたいな感じ
                    else if(Objects.equals(keyStr, TROPHY_LIST) ) {
                        if( !valStr.isEmpty() ) {
                            String[] values = valStr.split(",");
                            for (String val : values) {
                                trophies.add(Integer.parseInt(val) );
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
        dataIO.putIntList(GameDataElements.TROPHY, trophies);
        dataIO.putStrList(GameDataElements.MUSIC_TROPHY, musicTrophies);
        if(mode == 4) { // 折角初期化したデータを空マップで埋めてしまうバグが出たので一時的対処
            dataIO.putHashedPlayRecords(GameDataElements.PLAY_RECORD, playRecord);
        }
    }

    // プレー記録の初期状態
    public static String playRecordDefault() {
        return playRecordDefault;
    }

    /**
     * プレー記録を再構成
     * @param playState プレー状態
     * @param achievement 達成率
     * @param playRecordStr 構成前のプレー記録（文字列）
     */
    public String makePlayRecord(int playState, float achievement, int playPart, String playRecordStr) {
        // 文字列の記録を構文解析
        String[] playRecords = playRecordStr.split("/");
        int[] playStateArr = new int[3];
        float[] achievementArr = new float[3];
        int i = 0;
        for(String playRecord : playRecords) {
            String[] rcd = playRecord.split(",");     // 文字型のプレー記録を分解
            playStateArr[i] = Integer.parseInt(rcd[0]);     // プレー状態を格納
            achievementArr[i] = Float.parseFloat(rcd[1]);   // 達成率を格納
            i++;
        }

        // 上回った記録を更新
        if(playState > playStateArr[playPart - 1]) {
            playStateArr[playPart - 1] = playState;
        }
        if(achievement > achievementArr[playPart - 1]) {
            achievementArr[playPart - 1] = achievement;
        }

        StringBuilder recordStr = new StringBuilder();
        for(int j = 0; j < 3; j++) {
            recordStr.append(playStateArr[j]);
            recordStr.append(",");
            recordStr.append(achievementArr[j]);
            if(j != 2) {
                recordStr.append("/");
            }
        }

        return recordStr.toString();
    }

    // ----------------------------------------------------------------------- //
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

    private static final String playRecordDefault = "0,0.00/0,0.00/0,0.00";
}
