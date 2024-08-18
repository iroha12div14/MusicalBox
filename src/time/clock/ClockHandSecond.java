package time.clock;

// 秒針
public class ClockHandSecond extends ClockHand {
    @Override
    public int getTime() {
        return time(Field.SECOND);
    }

    @Override
    public int angleCalc() {
        int sec = getTime();
        return (270 + sec*6) % 360;
    }
}
