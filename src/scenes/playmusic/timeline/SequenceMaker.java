package scenes.playmusic.timeline;

import scenes.playmusic.findstr.FindStrUtil;

import java.util.*;

public class SequenceMaker {
    // インスタンス
    private final FindStrUtil fsu = new FindStrUtil();
    private final PunchCard pc = new PunchCard();

    /**
     * @return sequence (シーケンス)
     *      <p>
     *          パンチカードを構文解析し、時系列と音程をリスト化したもの。
     *          音符を視覚化したノートオブジェクトの生成源として用いる。
     *      <p/>
     *      <p>
     *          Listの先頭が生成直近のノーツMapを指し、
     *          Mapのキーが PunchCard.TIME のとき、データはミリ秒時刻、キーがそれ以外の時データは音程を返す。
     *          出力された音程の値が fsu.UNDEFINED() のとき、そのタイミングにその種類のノートは存在しない。
     *      <p/>
     *      <p>
     *          Oノートは通常ノートである。
     *          A,P,Nノートはアルペジオノートであり、Aノートを基準として先方にPノート、後方にNノートが存在する。
     *          PノートもしくNノートが存在するとき、P-A, A-N はアルペジオの関係である。
     *      <p/>
     */
    // パンチカードをシーケンス化
    public List<Map<String, Integer>> makeSequence(List<String> punchCard, int changeKey){
        // 読み取りモード falseでヘッダ部分、trueでパンチカード本体読み取り
        boolean readSequenceMode = false;

        // 音のタイミングと音程の格納リスト
        List<Map<String, Integer>> sequence = new ArrayList<>();

        // 時刻
        int unitTime = 0;

        // 1行ずつ構文解析してデータ処理
        // lineの(区切り文字)を境に左が変数名、右がパラメータ
        for( String line : punchCard ){
            // ヘッダ部分の読み取り 曲名とかテンポとか
            if( !readSequenceMode ){
                String hd = line.split(PunchCard.SPLIT_TOKEN)[0];

                if( Objects.equals(hd, PunchCard.SEQUENCE) ){
                    readSequenceMode = true;
                }
            }
            // パンチカード本体読み取りする時はパラメータだけを読む
            else{
                String param = line.split(PunchCard.SPLIT_TOKEN)[1];
                String str = String.join("", param.split(" ")); // 空白の除去

                // <List>sequenceに格納する<Map>notesを用意する
                Map<String, Integer> notes = new HashMap<>();

                // ノートの有無
                boolean notesIsEmpty = true;

                for( String s : pc.collection() ){
                    int pitch = fsu.findStr(str, s);
                    notesIsEmpty &= fsu.isNotFind(pitch);

                    // ピッチの変更
                    int changedPitch = !fsu.isNotFind(pitch)
                            ? changedPitch(pitch, changeKey)
                            : FindStrUtil.UNDEFINED;

                    // ノートの格納
                    notes.put(s, changedPitch);
                }
                // ただし、その時刻にノートが1つも無い(＝notesの中身が全部UNDEFINED)なら格納しない
                if ( !notesIsEmpty ){
                    // 時刻の格納
                    notes.put(PunchCard.TIME, unitTime);

                    // ノーツの格納
                    sequence.add(notes);
                }
                unitTime++;
            }
        }
        return sequence;
    }

    // キーチェンジ ただし、範囲外の音は全部 Pitch._UNDEFINED 扱いにする
    private int changedPitch(int pitch, int changeKey){
        int changedPitch = Math.max(pitch + changeKey, FindStrUtil.UNDEFINED );
        return ( changedPitch < 32 ) ? changedPitch : FindStrUtil.UNDEFINED;
    }
}
