package playmusic.note;

/*
 * 初期化
 *     List<NoteObject> notesMainTrack = ArrayList<>();
 *
 * 描画用ノートを生成
 *     notesMainTrack.add(new NoteObject(arriveTime, pitch, s, pitchArp));
 *
 * 判定対象のノートの判定線到達時刻、音程
 *     note = notesMainTrack.getFirst();
 *     note.arriveTime();
 *     note.pitch();
 *
 * 判定が済んだノートを削除
 *     notesMainTrack.removeFirst();
 */

// コンストラクタの引数と出力が正規化されていると
// レコードクラスとして扱えるらしい　すげぇ

/**
 * @param arriveTime   到達時刻(ミリ秒)
 * @param pitch        音程(0~31)
 * @param kind         種類(通常(O)、アルペジオ(A,P,N))
 * @param arpConnectTo アルペジオノーツであれば結線の接続先ノート(P→A→N)の音程
 */
public record NoteObject(int arriveTime, int pitch, String kind, int arpConnectTo) {

}
