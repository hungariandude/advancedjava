package hu.elte.iszraai.rp.signals;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TimedSignal<T> extends Signal<T> {

    /**
     * Creates a constant TimedSignal which gives a signal every ({@code unit.getMillis() * amount}) milliseconds.
     * Has no action by default.
     */
    public static TimedSignal<SignalConstant> every(final int amount, final TimeUnit unit) {
        return new TimedSignal<>(() -> SignalConstant.SIGNAL, unit.getMillis() * amount);
    }

    private final long                     signalingInterval;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TimedSignal(final ValueProvider<T> valueProvider, final long signalingInterval, final Runnable action) {
        super(valueProvider, action);
        this.signalingInterval = signalingInterval;

        // if signalingInterval is less than zero, the Signal can only be fired by other Signals
        if (this.signalingInterval >= 0) {
            changeValue();

            if (this.signalingInterval > 0) {
                scheduler.scheduleAtFixedRate(() -> changeValue(), this.signalingInterval, this.signalingInterval,
                        java.util.concurrent.TimeUnit.MILLISECONDS);
            }
        }
    }

    public TimedSignal(final ValueProvider<T> valueProvider, final long signalingInterval) {
        this(valueProvider, signalingInterval, null);
    }

}
