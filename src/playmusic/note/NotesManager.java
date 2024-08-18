package playmusic.note;

import findstr.FindStrUtil;
import playmusic.timeline.PunchCard;

import java.util.List;
import java.util.Map;

public class NotesManager {
    private final PunchCard pc = new PunchCard();
    private final FindStrUtil fsu = new FindStrUtil();

    public void notesManage(
            List<Map<String, Integer>> sequence,
            int nowTime,
            float sequenceUnitTime,
            int offset,
            List<NoteObject> notesMainScore,
            List<NoteObject> notesSubScore,
            int arpDistanceTime
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
    private void addNote(
            List<String> collection,
            Map<String, Integer> note,
            List<NoteObject> score,
            int arriveTime,
            int arpDistanceTime
    ) {
        for (String s : collection) {
            int pitch = note.get(s);
            if ( !fsu.isNotFind(pitch) ) {
                int pitchArp = pc.arpTo(s, note);
                int adt = arpDistanceTime * pc.StringToArpeggio(s);
                int at = arriveTime + adt;
                score.add(new NoteObject(at, pitch, s, pitchArp) );
            }
        }
    }
}
