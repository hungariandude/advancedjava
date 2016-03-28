package hu.elte.iszraai.rp.signals;

/**
 * Provides new values for Signals.
 */
public interface ValueProvider<T> {

    T provideValue();

}
