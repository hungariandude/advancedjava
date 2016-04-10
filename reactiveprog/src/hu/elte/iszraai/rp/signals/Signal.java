package hu.elte.iszraai.rp.signals;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Signal<T> {

    public static <T> Signal<T> createConstantSignal(final T constant) {
        return new Signal<>(constant);
    }

    protected T                lastValue;
    protected Runnable         action;
    protected ValueProvider<T> valueProvider;

    public Signal() {
    }

    public Signal(final Runnable action) {
        this(null, action);
    }

    protected Signal(final T value) {
        this.lastValue = value;
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

    protected Runnable wrapAction(final Runnable actionToWrap, final Signal<?> signalToCall) {
        if (actionToWrap != null) {
            return () -> {
                actionToWrap.run();
                signalToCall.changeValue();
            };
        } else {
            return () -> signalToCall.changeValue();
        }
    }

    public Signal<T> map(final Function<? super T, ? extends T> mapper) {
        Signal<T> mappedSignal = new Signal<>(() -> mapper.apply(lastValue), null);

        action = wrapAction(action, mappedSignal);

        return mappedSignal;
    }

    public <U, R> Signal<R> join(final Signal<U> other, final BiFunction<? super T, ? super U, ? extends R> joiner) {
        Signal<R> joinedSignal = new Signal<>(() -> joiner.apply(this.lastValue, other.lastValue), null);

        action = wrapAction(action, joinedSignal);
        other.action = wrapAction(other.action, joinedSignal);

        return joinedSignal;
    }

    public <R> Signal<R> accumulate(final BiFunction<? super R, ? super T, ? extends R> accumulator,
            final R startValue) {
        Signal<R> accumulatedSignal = new Signal<>(startValue);
        accumulatedSignal.valueProvider = () -> accumulator.apply(accumulatedSignal.lastValue, this.lastValue);

        action = wrapAction(action, accumulatedSignal);

        return accumulatedSignal;
    }

}
