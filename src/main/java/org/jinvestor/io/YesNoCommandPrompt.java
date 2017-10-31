package org.jinvestor.io;

import java.util.Scanner;
import java.util.function.Supplier;

/**
 *
 * @author Adam
 */
public class YesNoCommandPrompt<T> {

    private String prompt;
    private Supplier<T> yesAction;
    private Supplier<T> noAction;

    public YesNoCommandPrompt(String prompt, Supplier<T> yesAction, Supplier<T> noAction) {
        this.prompt = prompt;
        this.yesAction = yesAction;
        this.noAction = noAction;
    }

    @SuppressWarnings("squid:S106")
    public T run() {
        final String yesOption = "y";
        final String noOption = "n";
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            while (true) {
                System.out.print(prompt + " [" + yesOption + "/" + noOption + "]: ");
                String option = scanner.next();
                if (yesOption.equalsIgnoreCase(option)){
                    return yesAction.get();
                }
                else if (noOption.equalsIgnoreCase(option)){
                    return noAction.get();
                }
            }
        }
    }
}
