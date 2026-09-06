package margit.ui;

import java.time.LocalDate;
import java.util.Scanner;

import margit.task.Task;
import margit.task.TaskDateTime;
import margit.task.TaskList;

/** Handles console input and output for the application. */
public class Ui {
    private final Scanner scanner;

    private static final String INDENT = "     ";
    private static final String HORIZONTAL_LINE = "-".repeat(96);
    private static final String BANNER = "___  ___                _ _         _____ _           "
                + "______   _ _   _____                      \n"
                + "|  \\/  |               (_) |       |_   _| |          "
                + "|  ___| | | | |  _  |                     \n"
                + "| .  . | __ _ _ __ __ _ _| |_        | | | |__   ___  "
                + "| |_ ___| | | | | | |_ __ ___   ___ _ __  \n"
                + "| |\\/| |/ _` | '__/ _` | | __|       | | | '_ \\ / _ \\ "
                + "|  _/ _ \\ | | | | | | | '_ ` _ \\ / _ \\ '_ \\ \n"
                + "| |  | | (_| | | | (_| | | |_   _    | | | | | | |  __/ "
                + "| ||  __/ | | \\ \\_/ / | | | | | | |  __/ | | |\n"
                + "\\_|  |_/\\__,_|_|  \\__, |_|\\__| ( )   \\_/ |_| |_|\\___| "
                + "\\_| \\___|_|_|  \\___/|_| |_| |_| |_|\\___|_| |_|\n"
                + "                   __/ |       |/                                                               \n"
                + "                  |___/                                                                         \n"
                + HORIZONTAL_LINE;

    private static final String GREETINGS = "Foul tarnished... what is it thou dost seek?";

    private static final String FAREWELL = "Tis well... put these foolish ambitions to rest.";

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the application's welcome banner and greeting. */
    public void showWelcome() {
        System.out.println(BANNER);
        System.out.println(INDENT + GREETINGS + "\n");
        System.out.println(INDENT + HORIZONTAL_LINE + "\n");
    }

    /** Reads and returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays all tasks currently in the task list. */
    public void showTaskList(TaskList tasks) {
        showTasks("Here are the tasks in your list:", tasks, "");
    }

    /** Displays all tasks whose descriptions match a find command. */
    public void showFindResults(TaskList matchingTasks) {
        showTasks("Here are the matching tasks in your list:", matchingTasks, "No matching tasks found.");
    }

    /** Displays tasks that occur on the specified date. */
    public void showTasksOnDate(LocalDate date, TaskList matchingTasks) {
        String heading = "Here is what falls upon " + date.format(TaskDateTime.OUTPUT_DATE) + ":";
        showTasks(heading, matchingTasks, "Nothing awaits thee that day.");
    }

    /** Displays an error when a find command has no keyword. */
    public void showMissingFindKeyword() {
        showFramed("A find command needs a keyword, tarnished.");
    }

    /** Displays an error when an on command has no date. */
    public void showMissingDate() {
        showFramed("Tell me which date thou wishest to inspect, tarnished.");
    }

    /** Displays an error when an on command contains an invalid date. */
    public void showInvalidDate() {
        showFramed("That date makes no sense to me, tarnished.");
    }

    /** Displays an error when a task number is not a number. */
    public void showInvalidTaskNumber() {
        showFramed("Hmm, that doesn't look like a valid task number.");
    }

    /** Displays an error when a task number is outside the task list. */
    public void showTaskNotFound() {
        showFramed("That task number doesn't exist, tarnished.");
    }

    /** Displays an unchanged completion status for a task. */
    public void showUnchangedTaskStatus(Task task, boolean isMarked) {
        String message = isMarked ? "This task is already marked as done, tarnished:"
                : "This task is already marked as not done, tarnished:";
        showFramed(message + "\n  " + task);
    }

    /** Displays a changed completion status for a task. */
    public void showChangedTaskStatus(Task task, boolean isMarked) {
        String message = isMarked ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showFramed(message + "\n  " + task);
    }

    /** Displays a confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        showFramed("Noted. I've removed this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.");
    }

    /** Displays an error when a todo has no description. */
    public void showMissingTodoDescription() {
        showFramed("A todo needs a description, tarnished.");
    }

    /** Displays an error when no more tasks can be added. */
    public void showTaskListFull() {
        showFramed("Thy task list can hold no more.");
    }

    /** Displays an error when a deadline omits its /by separator. */
    public void showMissingDeadlineSeparator() {
        showFramed("A deadline needs a description and a '/by' date, tarnished.");
    }

    /** Displays an error when a deadline omits its description or /by date. */
    public void showMissingDeadlineComponent() {
        showFramed("A deadline needs both a description and a '/by' date, tarnished.");
    }

    /** Displays an error when an event omits required information. */
    public void showInvalidEvent() {
        showFramed("An event needs a description, a '/from' time, and a '/to' time, tarnished.");
    }

    /** Displays a confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        showFramed("Got it. I've added this task:\n  " + task
                + "\nNow you have " + taskCount + " tasks in the list.");
    }

    /** Displays an error for an unrecognized command. */
    public void showUnknownCommand() {
        showFramed("No idea what you mean");
    }

    /** Displays the application's farewell message. */
    public void showFarewell() {
        showFramed(FAREWELL);
    }

    /** Releases the console input resource when the program ends. */
    public void close() {
        scanner.close();
    }

    /** Displays a message between two horizontal separator lines. */
    private void showFramed(String message) {
        System.out.println(INDENT + HORIZONTAL_LINE);
        System.out.println(INDENT + message.replace("\n", "\n" + INDENT));
        System.out.println(INDENT + HORIZONTAL_LINE + "\n");
    }

    /** Displays a heading followed by numbered tasks or an empty-list message. */
    private void showTasks(String heading, TaskList tasks, String emptyMessage) {
        StringBuilder message = new StringBuilder(heading);
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        if (tasks.size() == 0 && !emptyMessage.isEmpty()) {
            message.append("\n").append(emptyMessage);
        }
        showFramed(message.toString());
    }
}
