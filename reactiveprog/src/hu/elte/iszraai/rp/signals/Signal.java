package hu.elte.iszraai.rp.signals;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Signal<T> {

    public static <T> Signal<T> createConstantSignal(final T constant) {
        Signal<T> signal = new Signal<>();
        signal.lastValue = constant;
        return signal;
    }

    protected T                lastValue;
    protected Runnable         action;
    protected ValueProvider<T> valueProvider;

    protected List<Signal<?>>  subscribedSignals = new LinkedList<>();

    public Signal() {
        this(null);
    }

    public Signal(final Runnable action) {
        this(null, action);
    }

    protected Signal(final ValueProvider<T> valueProvider, final Runnable action) {
        this.valueProvider = valueProvider;
        this.action = action;
    }

    public void changeValue(final T newValue) {
        this.lastValue = newValue;
        if (action != null) {
            action.run();
        }
        if (!subscribedSignals.isEmpty()) {
            for (Signal<?> signal : subscribedSignals) {
                signal.changeValue();
            }
        }
    }

    protected void changeValue() {
        if (valueProvider != null) {
            changeValue(valueProvider.provideValue());
        }
    }

    public T getLastValue() {
        return lastValue;
    }

    public void setAction(final Runnable action) {
        this.action = action;
    }

    public Signal<T> map(final Function<? super T, ? extends T> mapper) {
        Signal<T> mappedSignal = new Signal<>(() -> mapper.apply(lastValue), null);
        subscribedSignals.add(mappedSignal);
        return mappedSignal;
    }

    public <U, R> Signal<R> join(final Signal<U> other, final BiFunction<? super T, ? super U, ? extends R> joiner) {
        Signal<R> joinedSignal = new Signal<>(() -> joiner.apply(this.lastValue, other.lastValue), null);
        this.subscribedSignals.add(joinedSignal);
        other.subscribedSignals.add(joinedSignal);
        return joinedSignal;
    }

    public <R> Signal<R> accumulate(final BiFunction<? super R, ? super T, ? extends R> accumulator,
            final R startValue) {
        Signal<R> accumulatedSignal = new Signal<>();
        accumulatedSignal.lastValue = startValue;
        accumulatedSignal.valueProvider = () -> accumulator.apply(accumulatedSignal.lastValue, this.lastValue);
        subscribedSignals.add(accumulatedSignal);
        return accumulatedSignal;
    }

}
