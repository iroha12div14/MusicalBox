package scenes.playmusic.note;

import scenes.playmusic.timeline.PunchCard;

import java.util.List;
import java.util.Map;

/**
 * ノーツの楽譜への書き込みを管理する
 */
public class NotesManager {
    private final PunchCard pc = new PunchCard();
    private static final int UNDEFINED = -1;

    /**
     * 時刻を監視して、ノーツを楽譜に書き込むか決める
     * @param sequence          シーケンス
     * @param nowTime           現時刻
     * @param sequenceUnitTime  16分音符のミリ秒時間
     * @param offset            ノートの移動時間
     * @param notesMainScore    メロディ楽譜
     * @param notesSubScore     伴奏楽譜
     * @param arpDistanceTime   アルペジオの間隔時間
     */
    public void notesManage(
            List<Map<String, Integer>> sequence, int nowTime, float sequenceUnitTime, int offset,
            List<NoteObject> notesMainScore, List<NoteObject> notesSubScore, int arpDistanceTime
    ) {
        if( !sequence.isEmpty() ) {
            Map<String, Integer> note = sequence.get(0);
            int willArriveUnitTime = note.get(PunchCard.TIME);

            int scoreWriteTime = (int) (willArriveUnitTime * sequenceUnitTime); // 楽譜への書き込み時刻
            int willArriveTime = scoreWriteTime + offset; // ノートの到達予定時刻(offset分だけ遅れて判定線に到達)

            if (nowTime >= scoreWriteTime) {
                addNote(pc.collectionMain(), note, notesMainScore, willArriveTime, arpDistanceTime);
                addNote(pc.collectionSub(),  note, notesSubScore,  willArriveTime, arpDistanceTime);
                sequence.remove(0);
            }
        }
    }

    /**
     * 楽譜にノーツを書き込む
     * @param collection        パート別のノーツ種別一覧
     * @param note              その時刻における書き込むノーツのセット
     * @param score             楽譜の種別
     * @param arriveTime        到達予定時刻
     * @param arpDistanceTime   アルペジオの間隔時間
     */
    private void addNote(
            List<String> collection,
            Map<String, Integer> note,
            List<NoteObject> score,
            int arriveTime,
            int arpDistanceTime
    ) {
        for (String s : collection) {
            int pitch = note.get(s);
            if (pitch != UNDEFINED) {
                int pitchArp = pc.arpTo(s, note);
                int adt = arpDistanceTime * pc.StringToArpeggio(s);
                int at = arriveTime + adt;
                score.add(new NoteObject(at, pitch, s, pitchArp) );
            }
        }
    }
}
