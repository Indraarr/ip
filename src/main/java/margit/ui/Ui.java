package margit.ui;

import java.util.Scanner;

/** Handles console input and output for the application. */
public class Ui {
    private static final String INDENT = "     ";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Reads and returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the application's welcome banner and greeting. */
    public void showWelcome(String welcomeMessage) {
        System.out.println(welcomeMessage);
    }

    /** Displays a message between two horizontal separator lines. */
    public void showFramed(String message, String horizontalLine) {
        System.out.println(INDENT + horizontalLine + "\n" + message + "\n");
        System.out.println(INDENT + horizontalLine + "\n");
    }

    /** Displays the application's farewell message. */
    public void showFarewell(String farewellMessage) {
        System.out.println(farewellMessage);
    }

    /** Releases the console input resource when the program ends. */
    public void close() {
        scanner.close();
    }
}
