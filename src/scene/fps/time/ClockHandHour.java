package scene.fps.time;

// 時針
public class ClockHandHour extends ClockHand {
    @Override
    public int getTime() {
        return time(Unit.HOUR);
    }
    @Override
    public int angleCalc() {
        // 最小でも1度あればいいかなって思って秒を計算に入れてない
        int min = time(Unit.MINUTE);
        int hr = getTime();
        return (270 + hr*30 + min/2) % 360;
    }
}
