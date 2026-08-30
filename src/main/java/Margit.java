import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Margit {

    public static void main(String[] args) {

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

        System.out.println(banner + greet);

        Ui ui = new Ui();
        String line = "";

        Task[] tasks = new Task[100];
        int taskCount = loadTasks(tasks);

        while (true) {
            line = ui.readCommand();

            // End Conversation
            if (line.equals("bye")) {
                break;
            }

            // List
            if (line.equals("list")) {
                StringBuilder listOutput = new StringBuilder();
                listOutput.append(space).append("Here are the tasks in your list:\n");
                for (int i = 0; i < taskCount; i++) {
                    listOutput.append(space)
                               .append(i + 1)
                               .append(".")
                               .append(tasks[i])
                               .append("\n");
                }
                System.out.println(space + horizontalLine + "\n" + listOutput + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // STRETCH GOAL: list tasks occurring on a specific date, e.g. "on 2019-12-02" or "on 2/12/2019"
            if (line.equals("on") || line.startsWith("on ")) {
                String datePart = line.length() > 2 ? line.substring(3).trim() : "";

                if (datePart.isEmpty()) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Tell me which date thou wishest to inspect, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                LocalDate targetDate = TaskDateTime.parseDateOnly(datePart);
                if (targetDate == null) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "That date makes no sense to me, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                StringBuilder onOutput = new StringBuilder();
                onOutput.append(space).append("Here is what falls upon ")
                        .append(targetDate.format(TaskDateTime.OUTPUT_DATE)).append(":\n");
                int matches = 0;
                for (int i = 0; i < taskCount; i++) {
                    if (tasks[i].occursOn(targetDate)) {
                        matches++;
                        onOutput.append(space).append(matches).append(".").append(tasks[i]).append("\n");
                    }
                }
                if (matches == 0) {
                    onOutput.append(space).append("Nothing awaits thee that day.\n");
                }
                System.out.println(space + horizontalLine + "\n" + onOutput + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // LIST TASKS
            if (line.startsWith("mark ") || line.startsWith("unmark ")) {
                boolean listAction = line.startsWith("mark ");
                String indexPart = listAction ? line.substring(5) : line.substring(7);

                // Not an integer
                int index;
                try {
                    index = Integer.parseInt(indexPart.trim()) - 1;
                } catch (NumberFormatException e) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Hmm, that doesn't look like a valid task number.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                // No task number
                if (index < 0 || index >= taskCount) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "That task number doesn't exist, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }


                // Mark / Unmark logic
                boolean success = listAction ? tasks[index].mark() : tasks[index].unmark();

                if (!success) {
                    String alreadyMessage = listAction
                            ? "This task is already marked as done, tarnished:"
                            : "This task is already marked as not done, tarnished:";
                    System.out.println(space + horizontalLine + "\n"
                            + space + alreadyMessage + "\n"
                            + space + "  " + tasks[index] + "\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                saveTasks(tasks, taskCount);

                String message = listAction
                        ? "Nice! I've marked this task as done:"
                        : "OK, I've marked this task as not done yet:";

                System.out.println(space + horizontalLine + "\n"
                        + space + message + "\n"
                        + space + "  " + tasks[index] + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // Delete
            if (line.startsWith("delete ")) {
                String indexPart = line.substring(7);

                // Not an integer
                int index;
                try {
                    index = Integer.parseInt(indexPart.trim()) - 1;
                } catch (NumberFormatException e) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Hmm, that doesn't look like a valid task number.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                // No task number
                if (index < 0 || index >= taskCount) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "That task number doesn't exist, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                Task removed = tasks[index];

                // Shift everything after index down by one
                for (int i = index; i < taskCount - 1; i++) {
                    tasks[i] = tasks[i + 1];
                }
                tasks[taskCount - 1] = null;
                taskCount--;

                saveTasks(tasks, taskCount);

                System.out.println(space + horizontalLine + "\n"
                        + space + "Noted. I've removed this task:\n"
                        + space + "  " + removed + "\n"
                        + space + "Now you have " + taskCount + " tasks in the list.\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }


            // CREATING NEW LIST TASKS
            // Todo
            if (line.equals("todo") || line.startsWith("todo ")) {
                String description = line.length() > 4 ? line.substring(5).trim() : "";

                // missing description
                if (description.isEmpty()) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "A todo needs a description, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                // Exceed list size
                if (taskCount >= tasks.length) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Thy task list can hold no more.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                tasks[taskCount] = new TodoTask(description);
                taskCount++;

                saveTasks(tasks, taskCount);

                System.out.println(space + horizontalLine + "\n"
                        + space + "Got it. I've added this task:\n"
                        + space + "  " + tasks[taskCount - 1].toString() + "\n"
                        + space + "Now you have " + taskCount + " tasks in the list.\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // Deadline
            if (line.equals("deadline") || line.startsWith("deadline ")) {
                String rest = line.length() > 8 ? line.substring(9).trim() : "";
                int byIndex = rest.indexOf("/by");

                if (rest.isEmpty() || byIndex == -1) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "A deadline needs a description and a '/by' date, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                String description = rest.substring(0, byIndex).trim();
                String byRaw = rest.substring(byIndex + 3).trim();


                // missing description
                if (description.isEmpty() || byRaw.isEmpty()) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "A deadline needs both a description and a '/by' date, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                // exceed list size
                if (taskCount >= tasks.length) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Thy task list can hold no more.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                TaskDateTime by = TaskDateTime.parse(byRaw);

                tasks[taskCount] = new DeadlineTask(description, by);
                taskCount++;

                saveTasks(tasks, taskCount);

                System.out.println(space + horizontalLine + "\n"
                        + space + "Got it. I've added this task:\n"
                        + space + "  " + tasks[taskCount - 1].toString() + "\n"
                        + space + "Now you have " + taskCount + " tasks in the list.\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // Event
            if (line.equals("event") || line.startsWith("event ")) {
                String rest = line.length() > 5 ? line.substring(6).trim() : "";
                int fromIndex = rest.indexOf("/from");
                int toIndex = rest.indexOf("/to");

                // missing description
                if (rest.isEmpty() || fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "An event needs a description, a '/from' time, and a '/to' time, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                String description = rest.substring(0, fromIndex).trim();
                String fromRaw = rest.substring(fromIndex + 5, toIndex).trim();
                String toRaw = rest.substring(toIndex + 3).trim();

                // missing date
                if (description.isEmpty() || fromRaw.isEmpty() || toRaw.isEmpty()) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "An event needs a description, a '/from' time, and a '/to' time, tarnished.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }


                // exceed list size
                if (taskCount >= tasks.length) {
                    System.out.println(space + horizontalLine + "\n" + space
                            + "Thy task list can hold no more.\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                TaskDateTime from = TaskDateTime.parse(fromRaw);
                TaskDateTime to = TaskDateTime.parse(toRaw);

                tasks[taskCount] = new EventTask(description, from, to);
                taskCount++;

                saveTasks(tasks, taskCount);

                System.out.println(space + horizontalLine + "\n"
                        + space + "Got it. I've added this task:\n"
                        + space + "  " + tasks[taskCount - 1].toString() + "\n"
                        + space + "Now you have " + taskCount + " tasks in the list.\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }


            // No task type specified

            System.out.println(space + horizontalLine + "\n" + space
                    + "No idea what you mean\n");
            System.out.println(space + horizontalLine + "\n");


        }


        // Farewell
        String farewell = space + horizontalLine + "\n"
                        + space + "Tis well... put these foolish ambitions to rest.\n\n"
                        + space + horizontalLine + "\n";

        System.out.println(farewell);

        ui.close();
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

    /** Relative path (from project root) of the save file. */
    private static final String SAVE_FILE_PATH = "./data/Margit.txt";

    private static void saveTasks(Task[] tasks, int taskCount) {
        java.io.File saveFile = new java.io.File(SAVE_FILE_PATH);
        java.io.File parentDir = saveFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (java.io.FileWriter writer = new java.io.FileWriter(saveFile)) {
            for (int i = 0; i < taskCount; i++) {
                writer.write(tasks[i].toSaveFormat() + System.lineSeparator());
            }
        } catch (java.io.IOException e) {
            System.out.println("     Warning: could not save tasks to disk (" + e.getMessage() + ")");
        }
    }

    /**
     * Reads tasks from {@link #SAVE_FILE_PATH} into {@code tasks} and returns
     * how many were loaded. If the file doesn't exist yet (e.g. first run),
     * simply returns 0 with an empty list. Any line that doesn't parse cleanly
     * is skipped with a warning rather than crashing startup.
     */
    private static int loadTasks(Task[] tasks) {
        java.io.File saveFile = new java.io.File(SAVE_FILE_PATH);
        if (!saveFile.exists()) {
            return 0;
        }

        int taskCount = 0;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null && taskCount < tasks.length) {
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
                    tasks[taskCount] = task;
                    taskCount++;
                } catch (RuntimeException e) {
                    System.out.println("     Warning: skipping corrupted line in save file: " + line);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("     Warning: could not load tasks from disk (" + e.getMessage() + ")");
        }

        return taskCount;
    }
}

/**
 * Handles console input for the application.
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

    /** Releases the console input resource when the program ends. */
    void close() {
        scanner.close();
    }
}
