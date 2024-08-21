package scene.fps.time;

public interface TimeUtil {
    int getTime();

    long time();
    int time(Field field);

    enum Field {
        NANO,
        MICRO,
        MILLI,
        SECOND,
        MINUTE,
        HOUR
    }
}
