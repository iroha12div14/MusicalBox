package playmusic.timeline;

import findstr.FindStrUtil;

import java.util.*;

public class SequenceAnalyzer {
    private final PunchCard pc = new PunchCard();
    private final List<String> co  = pc.collection();
    private final List<String> com = pc.collectionMain();
    private final List<String> cos = pc.collectionSub();

    private final FindStrUtil fsu = new FindStrUtil();

    private final int NONE      = 0;
    private final int MAIN_PART = 1;
    private final int SUB_PART  = 2;
    private final int ALL_PART  = 3;

    // 色々解析
    public float[] analyzeSequence(List<Map<String, Integer>> sequence, float sequenceUnitTime, int arpDistanceTime, int playPart) {
        List<Map<String, Integer>> seq = new ArrayList<>(sequence);
        float playTime = getPlayTime(seq, sequenceUnitTime);

        int countMain = switch (playPart) {
            case MAIN_PART, ALL_PART -> getNotesCount(seq, MAIN_PART);
            default -> 0;
        };
        int countSub = switch (playPart) {
            case SUB_PART,  ALL_PART -> getNotesCount(seq, SUB_PART);
            default -> 0;
        };

        // 密度[notes/s]を計算
        int notesCount = countMain + countSub;
        float density = notesCount * 1000 / playTime;
        int peak = getPeak(seq, arpDistanceTime, sequenceUnitTime, playPart);

        return new float[]{density, (float) peak / 2000};
    }

    // ノーツ合計の算出
    private int getNotesCount(List<Map<String, Integer>> sequence, List<String> col) {
        int count = 0; // notes
        for(Map<String, Integer> notes : sequence) {
            for(String s : col) {
                if(notes.get(s) != fsu.UNDEFINED() ) {
                    count++;
                }
            }
        }
        return count;
    }
    public int getNotesCount(List<Map<String, Integer>> sequence, int playPart) {
        return switch (playPart) {
            case MAIN_PART -> getNotesCount(sequence, com);
            case SUB_PART  -> getNotesCount(sequence, cos);
            case ALL_PART  -> getNotesCount(sequence, co);
            default -> 0;
        };
    }

    // ピークの算出
    private int getPeak(List<Map<String, Integer>> sequence, int arpDistanceTime, float sequenceUnitTime, int playPart) {
        List<Integer> notesLifetime = new ArrayList<>();
        List<String > col = switch(playPart) {
            case 1 -> pc.collectionMain();
            case 2 -> pc.collectionSub();
            case 3 -> pc.collection();
            default -> new ArrayList<>();
        };
        int peak = 0;
        int playTime = (int) getPlayTime(sequence, sequenceUnitTime);
        Map<String, Integer> notes = sequence.get(0);
        for(int time = 0; time < playTime; time++) {
            // ノートの追加
            if( !sequence.isEmpty() ) {
                int seqTime = (int) (notes.get(PunchCard.TIME) * sequenceUnitTime);
                if(time == seqTime) {
                    for(String s : col ) {
                        if(notes.get(s) != fsu.UNDEFINED() ) {
                            int lifetime = getLifetime(arpDistanceTime, s);
                            notesLifetime.add(lifetime);
                        }
                    }
                    sequence.remove(0);
                    if( !sequence.isEmpty() ){
                        notes = sequence.get(0);
                    }
                }
            }

            // 瞬間密度を計算
            int lifetimeSum = 0;
            for(int noteLifetime : notesLifetime) {
                lifetimeSum += noteLifetime;
            }
            if(peak < lifetimeSum) {
                peak = lifetimeSum;
            }

            // 時間の経過
            List<Integer> pastLifetime = new ArrayList<>();
            for(int noteLifetime : notesLifetime) {
                pastLifetime.add(noteLifetime - 1);
            }
            notesLifetime = new ArrayList<>(pastLifetime);
        }
        return peak;
    }

    // 有効ノーツ時間を返す
    private static int getLifetime(int arpDistanceTime, String s) {
        int lifetime;
        if(Objects.equals(s, PunchCard.MP) || Objects.equals(s, PunchCard.SP) ) {
            lifetime = (1000 - arpDistanceTime) * 2;

        } else if(Objects.equals(s, PunchCard.MN) || Objects.equals(s, PunchCard.SN)) {
            lifetime = (1000 + arpDistanceTime) * 2;

        } else {
            lifetime = 1000 * 2;

        }
        return lifetime;
    }

    // 演奏時間を算出
    public float getPlayTime(List<Map<String, Integer>> sequence, float sequenceUnitTime) {
        int sequenceSize = sequence.size();
        return sequence.get(sequenceSize - 1).get(PunchCard.TIME) * sequenceUnitTime;
    }
}
