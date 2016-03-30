package hu.elte.iszraai.rp.signals;

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
        if (action != null) {
            Runnable newAction = () -> {
                action.run();
                mappedSignal.changeValue();
            };
            action = newAction;
        } else {
            action = () -> mappedSignal.changeValue();
        }
        return mappedSignal;
    }

    public <U, R> Signal<R> join(final Signal<U> other, final BiFunction<? super T, ? super U, ? extends R> joiner) {
        Signal<R> joinedSignal = new Signal<>(() -> joiner.apply(this.lastValue, other.lastValue), null);
        if (action != null) {
            Runnable newAction = () -> {
                action.run();
                joinedSignal.changeValue();
            };
            action = newAction;
        } else {
            action = () -> joinedSignal.changeValue();
        }
        if (other.action != null) {
            Runnable newAction = () -> {
                other.action.run();
                joinedSignal.changeValue();
            };
            other.action = newAction;
        } else {
            other.action = () -> joinedSignal.changeValue();
        }
        return joinedSignal;
    }

    public <R> Signal<R> accumulate(final BiFunction<? super R, ? super T, ? extends R> accumulator,
            final R startValue) {
        Signal<R> accumulatedSignal = new Signal<>();
        accumulatedSignal.lastValue = startValue;
        accumulatedSignal.valueProvider = () -> accumulator.apply(accumulatedSignal.lastValue, this.lastValue);
        if (action != null) {
            Runnable newAction = () -> {
                action.run();
                accumulatedSignal.changeValue();
            };
            action = newAction;
        } else {
            action = () -> accumulatedSignal.changeValue();
        }
        return accumulatedSignal;
    }

}
