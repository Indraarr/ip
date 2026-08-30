import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
        String horizontalLine = "------------------------------------------------------------------------------------------------";
        String space = "     ";

        String banner = "___  ___                _ _         _____ _           ______   _ _   _____                      \n"
                + "|  \\/  |               (_) |       |_   _| |          |  ___| | | | |  _  |                     \n"
                + "| .  . | __ _ _ __ __ _ _| |_        | | | |__   ___  | |_ ___| | | | | | |_ __ ___   ___ _ __  \n"
                + "| |\\/| |/ _` | '__/ _` | | __|       | | | '_ \\ / _ \\ |  _/ _ \\ | | | | | | '_ ` _ \\ / _ \\ '_ \\ \n"
                + "| |  | | (_| | | | (_| | | |_   _    | | | | | |  __/ | ||  __/ | | \\ \\_/ / | | | | |  __/ | | |\n"
                + "\\_|  |_/\\__,_|_|  \\__, |_|\\__| ( )   \\_/ |_| |_|\\___| \\_| \\___|_|_|  \\___/|_| |_| |_|\\___|_| |_|\n"
                + "                   __/ |       |/                                                               \n"
                + "                  |___/                                                                         \n"
                + horizontalLine + "\n";

        String greet = space + "Foul tarnished... what is it thou dost seek?\n\n"
                        + space + horizontalLine + "\n";

        ui.showWelcome(banner + greet);
        while (true) {
            Parser.Command command = Parser.parse(ui.readCommand());

            // End Conversation
            if (command.type == Parser.CommandType.BYE) {
                break;
            }

            // List
            if (command.type == Parser.CommandType.LIST) {
                StringBuilder listOutput = new StringBuilder();
                listOutput.append(space).append("Here are the tasks in your list:\n");
                for (int i = 0; i < tasks.size(); i++) {
                    listOutput.append(space)
                               .append(i + 1)
                               .append(".")
                               .append(tasks.get(i))
                               .append("\n");
                }
                ui.showFramed(listOutput.toString(), horizontalLine);
                continue;
            }

            // STRETCH GOAL: list tasks occurring on a specific date, e.g. "on 2019-12-02" or "on 2/12/2019"
            if (command.type == Parser.CommandType.ON) {
                String datePart = command.argument;

                if (datePart.isEmpty()) {
                    ui.showFramed(space + "Tell me which date thou wishest to inspect, tarnished.\n",
                            horizontalLine);
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

            // LIST TASKS
            if (command.type == Parser.CommandType.MARK || command.type == Parser.CommandType.UNMARK) {
                boolean listAction = command.type == Parser.CommandType.MARK;
                String indexPart = command.argument;

                // Not an integer
                int index;
                try {
                    index = Integer.parseInt(indexPart.trim()) - 1;
                } catch (NumberFormatException e) {
                    ui.showFramed(space + "Hmm, that doesn't look like a valid task number.\n", horizontalLine);
                    continue;
                }

                // No task number
                if (index < 0 || index >= tasks.size()) {
                    ui.showFramed(space + "That task number doesn't exist, tarnished.\n", horizontalLine);
                    continue;
                }


                // Mark / Unmark logic
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

                String message = listAction
                        ? "Nice! I've marked this task as done:"
                        : "OK, I've marked this task as not done yet:";

                ui.showFramed(space + message + "\n" + space + "  " + tasks.get(index) + "\n", horizontalLine);
                continue;
            }

            // Delete
            if (command.type == Parser.CommandType.DELETE) {
                String indexPart = command.argument;

                // Not an integer
                int index;
                try {
                    index = Integer.parseInt(indexPart.trim()) - 1;
                } catch (NumberFormatException e) {
                    ui.showFramed(space + "Hmm, that doesn't look like a valid task number.\n", horizontalLine);
                    continue;
                }

                // No task number
                if (index < 0 || index >= tasks.size()) {
                    ui.showFramed(space + "That task number doesn't exist, tarnished.\n", horizontalLine);
                    continue;
                }

                Task removed = tasks.remove(index);

                storage.save(tasks);

                ui.showFramed(space + "Noted. I've removed this task:\n"
                        + space + "  " + removed + "\n"
                        + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
                continue;
            }


            // CREATING NEW LIST TASKS
            // Todo
            if (command.type == Parser.CommandType.TODO) {
                String description = command.argument;

                // missing description
                if (description.isEmpty()) {
                    ui.showFramed(space + "A todo needs a description, tarnished.\n", horizontalLine);
                    continue;
                }

                // Exceed list size
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                tasks.add(new TodoTask(description));

                storage.save(tasks);

                ui.showFramed(space + "Got it. I've added this task:\n"
                        + space + "  " + tasks.get(tasks.size() - 1) + "\n"
                        + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
                continue;
            }

            // Deadline
            if (command.type == Parser.CommandType.DEADLINE) {
                String rest = command.argument;
                int byIndex = rest.indexOf("/by");

                if (rest.isEmpty() || byIndex == -1) {
                    ui.showFramed(space + "A deadline needs a description and a '/by' date, tarnished.\n",
                            horizontalLine);
                    continue;
                }

                String description = rest.substring(0, byIndex).trim();
                String byRaw = rest.substring(byIndex + 3).trim();


                // missing description
                if (description.isEmpty() || byRaw.isEmpty()) {
                    ui.showFramed(space + "A deadline needs both a description and a '/by' date, tarnished.\n",
                            horizontalLine);
                    continue;
                }

                // exceed list size
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                TaskDateTime by = TaskDateTime.parse(byRaw);

                tasks.add(new DeadlineTask(description, by));

                storage.save(tasks);

                ui.showFramed(space + "Got it. I've added this task:\n"
                        + space + "  " + tasks.get(tasks.size() - 1) + "\n"
                        + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
                continue;
            }

            // Event
            if (command.type == Parser.CommandType.EVENT) {
                String rest = command.argument;
                int fromIndex = rest.indexOf("/from");
                int toIndex = rest.indexOf("/to");

                // missing description
                if (rest.isEmpty() || fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                    ui.showFramed(space + "An event needs a description, a '/from' time, and a '/to' time, tarnished.\n",
                            horizontalLine);
                    continue;
                }

                String description = rest.substring(0, fromIndex).trim();
                String fromRaw = rest.substring(fromIndex + 5, toIndex).trim();
                String toRaw = rest.substring(toIndex + 3).trim();

                // missing date
                if (description.isEmpty() || fromRaw.isEmpty() || toRaw.isEmpty()) {
                    ui.showFramed(space + "An event needs a description, a '/from' time, and a '/to' time, tarnished.\n",
                            horizontalLine);
                    continue;
                }


                // exceed list size
                if (tasks.isFull()) {
                    ui.showFramed(space + "Thy task list can hold no more.\n", horizontalLine);
                    continue;
                }

                TaskDateTime from = TaskDateTime.parse(fromRaw);
                TaskDateTime to = TaskDateTime.parse(toRaw);

                tasks.add(new EventTask(description, from, to));

                storage.save(tasks);

                ui.showFramed(space + "Got it. I've added this task:\n"
                        + space + "  " + tasks.get(tasks.size() - 1) + "\n"
                        + space + "Now you have " + tasks.size() + " tasks in the list.\n", horizontalLine);
                continue;
            }


            // No task type specified

            ui.showFramed(space + "No idea what you mean\n", horizontalLine);


        }


        // Farewell
        String farewell = space + horizontalLine + "\n"
                        + space + "Tis well... put these foolish ambitions to rest.\n\n"
                        + space + horizontalLine + "\n";

        ui.showFarewell(farewell);

        ui.close();
    }

    /** Starts the application using its default save-file location. */
    public static void main(String[] args) {
        new Margit("./data/Margit.txt").run();
    }

    
    private static class TaskDateTime {

        // Formats accepted as *input* from the user or from the save file.
        private static final DateTimeFormatter[] INPUT_DATETIME_FORMATS = {
                DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
                DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("MMM d yyyy HHmm"),
        };
        private static final DateTimeFormatter[] INPUT_DATE_FORMATS = {
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        };

        // Format used when printing to the user.
        static final DateTimeFormatter OUTPUT_DATE = DateTimeFormatter.ofPattern("MMM d yyyy");
        static final DateTimeFormatter OUTPUT_DATETIME = DateTimeFormatter.ofPattern("MMM d yyyy, h:mma");

        // Format used when writing to / reading from the save file (unambiguous, sortable).
        private static final DateTimeFormatter SAVE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final DateTimeFormatter SAVE_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

        private final LocalDateTime dateTime; // null if it couldn't be parsed
        private final boolean hasTime;
        private final String raw; // original text, used only if dateTime is null

        private TaskDateTime(LocalDateTime dateTime, boolean hasTime, String raw) {
            this.dateTime = dateTime;
            this.hasTime = hasTime;
            this.raw = raw;
        }

        static TaskDateTime parse(String input) {
            input = input.trim();

            for (DateTimeFormatter f : INPUT_DATETIME_FORMATS) {
                try {
                    return new TaskDateTime(LocalDateTime.parse(input, f), true, null);
                } catch (DateTimeParseException ignored) {
                }
            }

            for (DateTimeFormatter f : INPUT_DATE_FORMATS) {
                try {
                    LocalDate d = LocalDate.parse(input, f);
                    return new TaskDateTime(d.atStartOfDay(), false, null);
                } catch (DateTimeParseException ignored) {
                }
            }

            return new TaskDateTime(null, false, input);
        }

        static LocalDate parseDateOnly(String input) {
            input = input.trim();
            for (DateTimeFormatter f : INPUT_DATE_FORMATS) {
                try {
                    return LocalDate.parse(input, f);
                } catch (DateTimeParseException ignored) {
                }
            }
            TaskDateTime parsed = parse(input);
            return parsed.dateTime == null ? null : parsed.dateTime.toLocalDate();
        }

        /** Reconstructs a TaskDateTime from the save file representation. */
        static TaskDateTime fromSaveFormat(String saved) {
            if (saved.startsWith("DT:")) {
                LocalDateTime ldt = LocalDateTime.parse(saved.substring(3), SAVE_DATETIME);
                return new TaskDateTime(ldt, true, null);
            } else if (saved.startsWith("D:")) {
                LocalDate d = LocalDate.parse(saved.substring(2), SAVE_DATE);
                return new TaskDateTime(d.atStartOfDay(), false, null);
            } else if (saved.startsWith("RAW:")) {
                return new TaskDateTime(null, false, saved.substring(4));
            }
            return new TaskDateTime(null, false, saved);
        }

        String toSaveFormat() {
            if (dateTime == null) {
                return "RAW:" + raw;
            }
            return hasTime ? "DT:" + dateTime.format(SAVE_DATETIME) : "D:" + dateTime.format(SAVE_DATE);
        }

        LocalDate toLocalDate() {
            return dateTime == null ? null : dateTime.toLocalDate();
        }

        @Override
        public String toString() {
            if (dateTime == null) {
                return raw;
            }
            return hasTime ? dateTime.format(OUTPUT_DATETIME) : dateTime.format(OUTPUT_DATE);
        }
    }


    // Task class
    private static class Task {
        private String description;
        private boolean isDone;

        public Task(String description) {
            this.description = description;
            this.isDone = false;
        }

        public boolean mark() {
            if (this.isDone == true) {
                return false;
            }
            this.isDone = true;
            return true;
        }

        public boolean unmark() {
            if (this.isDone == false) {
                return false;
            }

            this.isDone = false;
            return true;
        }

        public String getStatusIcon() {
            return isDone ? "[X]" : "[ ]";
        }

        public String toSaveFormat() {
            return "T | " + (isDone ? "1" : "0") + " | " + description;
        }

        /** Whether this task falls on the given date. Overridden by date-aware subclasses. */
        public boolean occursOn(LocalDate date) {
            return false;
        }

        @Override
        public String toString() {
            return getStatusIcon() + " " + description;
        }
    }


    // Todo class
    private static class TodoTask extends Task {

        public TodoTask(String description) {
            super(description);
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    // Deadline class
    private static class DeadlineTask extends Task {
        private TaskDateTime by;

        public DeadlineTask(String description, TaskDateTime by) {
            super(description);
            this.by = by;
        }

        @Override
        public boolean occursOn(LocalDate date) {
            LocalDate d = by.toLocalDate();
            return d != null && d.equals(date);
        }

        @Override
        public String toSaveFormat() {
            return "D | " + super.toSaveFormat().substring(4) + " | " + by.toSaveFormat();
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    private static class EventTask extends Task {
        private TaskDateTime from;
        private TaskDateTime to;

        public EventTask(String description, TaskDateTime from, TaskDateTime to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean occursOn(LocalDate date) {
            LocalDate fromDate = from.toLocalDate();
            LocalDate toDate = to.toLocalDate();
            if (fromDate == null || toDate == null) {
                return false;
            }
            return !date.isBefore(fromDate) && !date.isAfter(toDate);
        }

        @Override
        public String toSaveFormat() {
            return "E | " + super.toSaveFormat().substring(4) + " | " + from.toSaveFormat() + " | " + to.toSaveFormat();
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    /** Loads tasks from and saves tasks to the application's data file. */
    private static class Storage {
        private final String filePath;

        /** Creates storage that reads from and writes to {@code filePath}. */
        Storage(String filePath) {
            this.filePath = filePath;
        }

        /** Saves the tasks in their current order. */
        void save(TaskList tasks) {
            java.io.File saveFile = new java.io.File(filePath);
            java.io.File parentDir = saveFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (java.io.FileWriter writer = new java.io.FileWriter(saveFile)) {
                for (int i = 0; i < tasks.size(); i++) {
                    writer.write(tasks.get(i).toSaveFormat() + System.lineSeparator());
                }
            } catch (java.io.IOException e) {
                System.out.println("     Warning: could not save tasks to disk (" + e.getMessage() + ")");
            }
        }

        /**
         * Reads tasks from the configured file into {@code tasks}. If the file
         * doesn't exist yet (e.g. first run), the list remains empty. Any line that
         * doesn't parse cleanly is skipped with a warning rather than crashing startup.
         */
        void load(TaskList tasks) {
            java.io.File saveFile = new java.io.File(filePath);
            if (!saveFile.exists()) {
                return;
            }

            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(saveFile))) {
                String line;
                while ((line = reader.readLine()) != null && !tasks.isFull()) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\s*\\|\\s*");
                    try {
                        String type = parts[0];
                        boolean isDone = parts[1].equals("1");
                        String description = parts[2];

                        Task task;
                        switch (type) {
                        case "T":
                            task = new TodoTask(description);
                            break;
                        case "D":
                            task = new DeadlineTask(description, TaskDateTime.fromSaveFormat(parts[3]));
                            break;
                        case "E":
                            task = new EventTask(description,
                                    TaskDateTime.fromSaveFormat(parts[3]),
                                    TaskDateTime.fromSaveFormat(parts[4]));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown task type: " + type);
                        }

                        if (isDone) {
                            task.mark();
                        }
                        tasks.add(task);
                    } catch (RuntimeException e) {
                        System.out.println("     Warning: skipping corrupted line in save file: " + line);
                    }
                }
            } catch (java.io.IOException e) {
                System.out.println("     Warning: could not load tasks from disk (" + e.getMessage() + ")");
            }
        }
    }

    /**
     * Stores the application's tasks and performs basic list operations.
     *
     * <p>The fixed capacity matches the previous array-based implementation.</p>
     */
    private static class TaskList {
        private static final int CAPACITY = 100;

        private final Task[] tasks = new Task[CAPACITY];
        private int size;

        /** Returns the number of tasks currently in the list. */
        int size() {
            return size;
        }

        /** Returns whether no further tasks can be added to the list. */
        boolean isFull() {
            return size == CAPACITY;
        }

        /** Returns the task at the specified zero-based index. */
        Task get(int index) {
            return tasks[index];
        }

        /** Appends a task to the end of the list. */
        void add(Task task) {
            tasks[size] = task;
            size++;
        }

        /** Removes and returns the task at the specified zero-based index. */
        Task remove(int index) {
            Task removed = tasks[index];
            for (int i = index; i < size - 1; i++) {
                tasks[i] = tasks[i + 1];
            }
            tasks[size - 1] = null;
            size--;
            return removed;
        }
    }
}

/**
 * Handles console input and output for the application.
 *
 * <p>Keeping input behind this class lets the application logic avoid depending
 * directly on {@link java.util.Scanner}.</p>
 */
class Ui {
    private final java.util.Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    Ui() {
        scanner = new java.util.Scanner(System.in);
    }

    /** Reads and returns the next command entered by the user. */
    String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the application's welcome banner and greeting. */
    void showWelcome(String welcomeMessage) {
        System.out.println(welcomeMessage);
    }

    /** Displays a message between two horizontal separator lines. */
    void showFramed(String message, String horizontalLine) {
        System.out.println(INDENT + horizontalLine + "\n" + message + "\n");
        System.out.println(INDENT + horizontalLine + "\n");
    }

    /** Displays the application's farewell message. */
    void showFarewell(String farewellMessage) {
        System.out.println(farewellMessage);
    }

    /** Releases the console input resource when the program ends. */
    void close() {
        scanner.close();
    }

    private static final String INDENT = "     ";
}

/** Interprets a raw user command as a command type and its argument. */
class Parser {
    /** Recognized commands accepted by the application. */
    enum CommandType {
        BYE, LIST, ON, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN
    }

    /** Parsed result containing the command type and its remaining argument. */
    static class Command {
        final CommandType type;
        final String argument;

        /** Creates a parsed command. */
        Command(CommandType type, String argument) {
            this.type = type;
            this.argument = argument;
        }
    }

    /**
     * Parses an input line without validating command-specific arguments.
     *
     * @param input raw command entered by the user
     * @return the identified command and its argument, or {@code UNKNOWN}
     */
    static Command parse(String input) {
        if (input.equals("bye")) {
            return new Command(CommandType.BYE, "");
        }
        if (input.equals("list")) {
            return new Command(CommandType.LIST, "");
        }
        if (input.equals("on") || input.startsWith("on ")) {
            return commandWithTrimmedArgument(CommandType.ON, input, 2);
        }
        if (input.startsWith("mark ")) {
            return new Command(CommandType.MARK, input.substring(5));
        }
        if (input.startsWith("unmark ")) {
            return new Command(CommandType.UNMARK, input.substring(7));
        }
        if (input.startsWith("delete ")) {
            return new Command(CommandType.DELETE, input.substring(7));
        }
        if (input.equals("todo") || input.startsWith("todo ")) {
            return commandWithTrimmedArgument(CommandType.TODO, input, 4);
        }
        if (input.equals("deadline") || input.startsWith("deadline ")) {
            return commandWithTrimmedArgument(CommandType.DEADLINE, input, 8);
        }
        if (input.equals("event") || input.startsWith("event ")) {
            return commandWithTrimmedArgument(CommandType.EVENT, input, 5);
        }
        return new Command(CommandType.UNKNOWN, "");
    }

    /** Extracts and trims the text following a keyword and one separating space. */
    private static Command commandWithTrimmedArgument(CommandType type, String input, int keywordLength) {
        String argument = input.length() > keywordLength ? input.substring(keywordLength + 1).trim() : "";
        return new Command(type, argument);
    }
}
