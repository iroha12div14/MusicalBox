package scene.fps.time;

// 秒針
public class ClockHandMilli extends ClockHand {
    @Override
    public int getTime() {
        return time(Field.MILLI);
    }
    @Override
    public int angleCalc() {
        // ミリ秒針が存在しないので0のみ返す
        return 0;
    }
}
