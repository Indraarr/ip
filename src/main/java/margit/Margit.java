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
        ui.showWelcome();
        while (true) {
            Parser.Command command = Parser.parse(ui.readCommand());

            if (command.type == Parser.CommandType.BYE) {
                break;
            }

            if (command.type == Parser.CommandType.LIST) {
                ui.showTaskList(tasks);
                continue;
            }

            if (command.type == Parser.CommandType.FIND) {
                if (command.argument.isEmpty()) {
                    ui.showMissingFindKeyword();
                    continue;
                }

                ui.showFindResults(tasks.findByKeyword(command.argument));
                continue;
            }

            if (command.type == Parser.CommandType.ON) {
                String datePart = command.argument;
                if (datePart.isEmpty()) {
                    ui.showMissingDate();
                    continue;
                }

                LocalDate targetDate = TaskDateTime.parseDateOnly(datePart);
                if (targetDate == null) {
                    ui.showInvalidDate();
                    continue;
                }

                TaskList matchingTasks = new TaskList();
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).occursOn(targetDate)) {
                        matchingTasks.add(tasks.get(i));
                    }
                }
                ui.showTasksOnDate(targetDate, matchingTasks);
                continue;
            }

            if (command.type == Parser.CommandType.MARK || command.type == Parser.CommandType.UNMARK) {
                boolean listAction = command.type == Parser.CommandType.MARK;
                if (command.taskIndex == null) {
                    ui.showInvalidTaskNumber();
                    continue;
                }
                int index = command.taskIndex;
                if (index < 0 || index >= tasks.size()) {
                    ui.showTaskNotFound();
                    continue;
                }

                boolean success = listAction ? tasks.get(index).mark() : tasks.get(index).unmark();
                if (!success) {
                    ui.showUnchangedTaskStatus(tasks.get(index), listAction);
                    continue;
                }

                storage.save(tasks);
                ui.showChangedTaskStatus(tasks.get(index), listAction);
                continue;
            }

            if (command.type == Parser.CommandType.DELETE) {
                if (command.taskIndex == null) {
                    ui.showInvalidTaskNumber();
                    continue;
                }
                int index = command.taskIndex;
                if (index < 0 || index >= tasks.size()) {
                    ui.showTaskNotFound();
                    continue;
                }

                Task removed = tasks.remove(index);
                storage.save(tasks);
                ui.showTaskDeleted(removed, tasks.size());
                continue;
            }

            if (command.type == Parser.CommandType.TODO) {
                Parser.TodoArguments todo = Parser.parseTodo(command.argument);
                if (!todo.isValid) {
                    ui.showMissingTodoDescription();
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showTaskListFull();
                    continue;
                }

                tasks.add(new TodoTask(todo.description));
                storage.save(tasks);
                ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                continue;
            }

            if (command.type == Parser.CommandType.DEADLINE) {
                Parser.DeadlineArguments deadline = Parser.parseDeadline(command.argument);
                if (deadline.status == Parser.DeadlineStatus.MISSING_SEPARATOR) {
                    ui.showMissingDeadlineSeparator();
                    continue;
                }
                if (deadline.status == Parser.DeadlineStatus.MISSING_COMPONENT) {
                    ui.showMissingDeadlineComponent();
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showTaskListFull();
                    continue;
                }

                tasks.add(new DeadlineTask(deadline.description, TaskDateTime.parse(deadline.byRaw)));
                storage.save(tasks);
                ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                continue;
            }

            if (command.type == Parser.CommandType.EVENT) {
                Parser.EventArguments event = Parser.parseEvent(command.argument);
                if (!event.isValid) {
                    ui.showInvalidEvent();
                    continue;
                }
                if (tasks.isFull()) {
                    ui.showTaskListFull();
                    continue;
                }

                tasks.add(new EventTask(event.description, TaskDateTime.parse(event.fromRaw),
                        TaskDateTime.parse(event.toRaw)));
                storage.save(tasks);
                ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
                continue;
            }

            ui.showUnknownCommand();
        }

        ui.showFarewell();
        ui.close();
    }

    /** Starts the application using its default save-file location. */
    public static void main(String[] args) {
        new Margit("./data/Margit.txt").run();
    }
}
