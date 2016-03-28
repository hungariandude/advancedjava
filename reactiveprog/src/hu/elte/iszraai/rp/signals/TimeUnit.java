package hu.elte.iszraai.rp.signals;

public enum TimeUnit {

    MILLISECOND(1),
    SECOND(1000),
    MINUTE(1000 * 60),
    HOUR(1000 * 60 * 60),
    DAY(1000 * 60 * 60 * 24);

    private final long millis;

    private TimeUnit(final long millis) {
        this.millis = millis;
    }

    public long getMillis() {
        return millis;
    }

}
