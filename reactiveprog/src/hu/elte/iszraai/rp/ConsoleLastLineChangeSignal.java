package hu.elte.iszraai.rp;

import java.util.Scanner;

import hu.elte.iszraai.rp.signals.Signal;

/**
 * Signal that changes value each time the user inputs a new line to the console.
 */
public class ConsoleLastLineChangeSignal extends Signal<String> {

    public ConsoleLastLineChangeSignal() {
        super();

        new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (scanner.hasNext()) {
                    changeValue(scanner.nextLine());
                }
            }
        }).start();
    }

}
