package scene.fps.time;

import java.time.LocalTime;

public abstract class ClockHand implements TimeUtil {
    public abstract int angleCalc();

    public int time(Field field) {
        currentTime = LocalTime.now();

        return switch (field) {
            case NANO   -> currentTime.getNano();
            case MICRO  -> Math.round((float) currentTime.getNano() / 1000);
            case MILLI  -> Math.round((float) currentTime.getNano() / 1000000);
            case SECOND -> currentTime.getSecond();
            case MINUTE -> currentTime.getMinute();
            case HOUR   -> currentTime.getHour();
        };
    }

    // 引数を省略で時刻を丸ごと返す
    public long time() {
        currentTime = LocalTime.now();

        int nano = currentTime.getNano();
        int sec  = currentTime.getSecond();
        int min  = currentTime.getMinute();
        int hour = currentTime.getHour();

        return ((hour*60 + min)*60 + sec)*1000000000 + nano;
    }

    LocalTime currentTime;
}
