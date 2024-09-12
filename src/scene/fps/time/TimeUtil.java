package scene.fps.time;

/**
 * 時刻の取得
 */
public interface TimeUtil {
    int getTime();

    long time();
    int time(Unit unit);

    enum Unit {
        NANO,
        MICRO,
        MILLI,
        SECOND,
        MINUTE,
        HOUR
    }
}
