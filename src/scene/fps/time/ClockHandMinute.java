package scene.fps.time;

// 分針
public class ClockHandMinute extends ClockHand {
    @Override
    public int getTime() {
        return time(Unit.MINUTE);
    }
    @Override
    public int angleCalc() {
        int sec = time(Unit.SECOND);
        int min = getTime();
        return (270 + min*6 + sec/10) % 360;
    }
}
