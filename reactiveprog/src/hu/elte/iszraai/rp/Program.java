package hu.elte.iszraai.rp;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import hu.elte.iszraai.rp.signals.Signal;
import hu.elte.iszraai.rp.signals.SignalConstant;
import hu.elte.iszraai.rp.signals.TimeUnit;
import hu.elte.iszraai.rp.signals.TimedSignal;

public class Program {

    public static void main(final String[] args) throws FileNotFoundException {
        // redirect the console output to a file
        PrintStream outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream("out.txt")), true);

        System.setOut(outputStream);
        System.setErr(outputStream);

        ConsoleLastLineChangeSignal consoleLastLineChangeSignal = new ConsoleLastLineChangeSignal();

        // Signal that changes value every second
        Signal<SignalConstant> timedSignal = TimedSignal.every(1, TimeUnit.SECOND);

        Signal<Integer> elapsedSecondCounter = timedSignal.accumulate((i, c) -> i + 1, 0);

        // joining the signals
        Signal<String> joinedSignal = consoleLastLineChangeSignal.join(elapsedSecondCounter, (s, i) -> s + " " + i);
        joinedSignal.setAction(() -> {
            System.out.println(joinedSignal.getLastValue());
        });
    }

}
