package scene.fps.time;

// 分針
public class ClockHandMinute extends ClockHand {
    @Override
    public int getTime() {
        return time(Field.MINUTE);
    }
    @Override
    public int angleCalc() {
        int sec = time(Field.SECOND);
        int min = getTime();
        return (270 + min*6 + sec/10) % 360;
    }
}
