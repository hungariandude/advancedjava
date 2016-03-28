package hu.elte.iszraai.rp.signals;

public class TimedSignal<T> extends Signal<T> {

    /**
     * Creates a constant TimedSignal which gives a signal every ({@code unit.getMillis() * amount}) milliseconds.
     * Has no action by default.
     */
    public static TimedSignal<SignalConstant> every(final int amount, final TimeUnit unit) {
        return new TimedSignal<>(() -> SignalConstant.SIGNAL, unit.getMillis() * amount);
    }

    private final long signalingInterval;

    public TimedSignal(final ValueProvider<T> valueProvider, final long signalingInterval, final Runnable action) {
        super(valueProvider, action);
        this.signalingInterval = signalingInterval;

        // if signalingInterval is less than zero, the Signal can only be fired by other Signals
        if (this.signalingInterval >= 0) {
            changeValue();
            if (this.signalingInterval > 0) {
                new Thread(() -> {
                    boolean interrupted = false;
                    do {
                        try {
                            Thread.sleep(this.signalingInterval);
                            changeValue();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                            interrupted = true;
                        }
                    } while (!interrupted);
                }).start();
            }
        }
    }

    public TimedSignal(final ValueProvider<T> valueProvider, final long signalingInterval) {
        this(valueProvider, signalingInterval, null);
    }

}
