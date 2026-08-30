package margit;

import java.time.LocalDate;

import margit.parser.Parser;
import margit.storage.Storage;
import margit.task.DeadlineTask;
import margit.task.EventTask;
import margit.task.Task;
import margit.task.TaskDateTime;
import margit.task.TaskList;
import margit.task.TodoTask;
import margit.ui.Ui;

/** A command-line task manager that coordinates user interaction, tasks, and storage. */
public class Margit {
    /** Handles all console interaction. */
    private final Ui ui;
    /** Holds the application's current tasks. */
    private final TaskList tasks;
    /** Persists tasks between application runs. */
    private final Storage storage;

    /**
     * Creates the application and loads any existing tasks from {@code filePath}.
     *
     * @param filePath location of the task save file
     */
    public Margit(String filePath) {
        ui = new Ui();
        tasks = new TaskList();
        storage = new Storage(filePath);
        storage.load(tasks);
    }

    /** Runs the interactive command loop until the user enters {@code bye}. */
    public void run() {
        String horizontalLine = "-".repeat(96);
        String space = "     ";

        String banner = "___  ___                _ _         _____ _           "
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
                + horizontalLine + "\n";

        String greet = space + "Foul tarnished... what is it thou dost seek?\n\n"
                        + space + horizontalLine + "\n";

        ui.showWelcome(banner + greet);
        while (true) {
            Parser.Command command = Parser.parse(ui.readCommand());

            if (command.type == Parser.CommandType.BYE) {
                break;
            }

            if (command.type == Parser.CommandType.LIST) {
                StringBuilder listOutput = new StringBuilder();
                listOutput.append(space).append("Here are the tasks in your list:\n");
                for (int i = 0; i < tasks.size(); i++) {
                    listOutput.append(space).append(i + 1).append(".").append(tasks.get(i)).append("\n");
                }
                ui.showFramed(listOutput.toString(), horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.ON) {
                String datePart = command.argument;
                if (datePart.isEmpty()) {
                    ui.showFramed(space + "Tell me which date thou wishest to inspect, tarnished.\n", horizontalLine);
                    continue;
                }

                LocalDate targetDate = TaskDateTime.parseDateOnly(datePart);
                if (targetDate == null) {
                    ui.showFramed(space + "That date makes no sense to me, tarnished.\n", horizontalLine);
                    continue;
                }

                StringBuilder onOutput = new StringBuilder();
                onOutput.append(space).append("Here is what falls upon ")
                        .append(targetDate.format(TaskDateTime.OUTPUT_DATE)).append(":\n");
                int matches = 0;
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).occursOn(targetDate)) {
                        matches++;
                        onOutput.append(space).append(matches).append(".").append(tasks.get(i)).append("\n");
                    }
                }
                if (matches == 0) {
                    onOutput.append(space).append("Nothing awaits thee that day.\n");
                }
                ui.showFramed(onOutput.toString(), horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.MARK || command.type == Parser.CommandType.UNMARK) {
                boolean listAction = command.type == Parser.CommandType.MARK;
                if (command.taskIndex == null) {
                    ui.showFramed(space + "Hmm, that doesn't look like a valid task number.\n", horizontalLine);
                    continue;
                }
                int index = command.taskIndex;
                if (index < 0 || index >= tasks.size()) {
                    ui.showFramed(space + "That task number doesn't exist, tarnished.\n", horizontalLine);
                    continue;
                }

                boolean success = listAction ? tasks.get(index).mark() : tasks.get(index).unmark();
                if (!success) {
                    String alreadyMessage = listAction
                            ? "This task is already marked as done, tarnished:"
                            : "This task is already marked as not done, tarnished:";
                    ui.showFramed(space + alreadyMessage + "\n" + space + "  " + tasks.get(index) + "\n",
                            horizontalLine);
                    continue;
                }

                storage.save(tasks);
                String message = listAction ? "Nice! I've marked this task as done:"
                        : "OK, I've marked this task as not done yet:";
                ui.showFramed(space + message + "\n" + space + "  " + tasks.get(index) + "\n", horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.DELETE) {
                if (command.taskIndex == null) {
                    ui.showFramed(space + "Hmm, that doesn't look like a valid task number.\n", horizontalLine);
                    continue;
                }
                int index = command.taskIndex;
                if (index < 0 || index >= tasks.size()) {
                    ui.showFramed(space + "That task number doesn't exist, tarnished.\n", horizontalLine);
                    continue;
                }

                Task removed = tasks.remove(index);
                storage.save(tasks);
                ui.showFramed(space + "Noted. I've removed this task:\n" + space + "  " + removed + "\n"
                        + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.TODO) {
                Parser.TodoArguments todo = Parser.parseTodo(command.argument);
                if (!todo.isValid) {
                    ui.showFramed(space + "A todo needs a description, tarnished.\n", horizontalLine);
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                tasks.add(new TodoTask(todo.description));
                storage.save(tasks);
                showTaskAdded(space, horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.DEADLINE) {
                Parser.DeadlineArguments deadline = Parser.parseDeadline(command.argument);
                if (deadline.status == Parser.DeadlineStatus.MISSING_SEPARATOR) {
                    ui.showFramed(space + "A deadline needs a description and a '/by' date, tarnished.\n",
                            horizontalLine);
                    continue;
                }
                if (deadline.status == Parser.DeadlineStatus.MISSING_COMPONENT) {
                    ui.showFramed(space + "A deadline needs both a description and a '/by' date, tarnished.\n",
                            horizontalLine);
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                tasks.add(new DeadlineTask(deadline.description, TaskDateTime.parse(deadline.byRaw)));
                storage.save(tasks);
                showTaskAdded(space, horizontalLine);
                continue;
            }

            if (command.type == Parser.CommandType.EVENT) {
                Parser.EventArguments event = Parser.parseEvent(command.argument);
                if (!event.isValid) {
                    ui.showFramed(
                            space + "An event needs a description, a '/from' time, and a '/to' time, tarnished.\n",
                            horizontalLine);
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                tasks.add(new EventTask(event.description, TaskDateTime.parse(event.fromRaw),
                        TaskDateTime.parse(event.toRaw)));
                storage.save(tasks);
                showTaskAdded(space, horizontalLine);
                continue;
            }

            ui.showFramed(space + "No idea what you mean\n", horizontalLine);
        }

        String farewell = space + horizontalLine + "\n"
                + space + "Tis well... put these foolish ambitions to rest.\n\n"
                + space + horizontalLine + "\n";
        ui.showFarewell(farewell);
        ui.close();
    }

    /** Displays the standard confirmation shown after adding a task. */
    private void showTaskAdded(String space, String horizontalLine) {
        ui.showFramed(space + "Got it. I've added this task:\n" + space + "  " + tasks.get(tasks.size() - 1)
                + "\n" + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
    }

    /** Starts the application using its default save-file location. */
    public static void main(String[] args) {
        new Margit("./data/Margit.txt").run();
    }
}
