import java.util.Scanner;
import java.io.File;

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

        Scanner scanner = new Scanner(System.in);
        String line = "";

        Task[] tasks = new Task[100];
        int taskCount = loadTasks(tasks);

        while (true) {
            line = scanner.nextLine();

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
                String by = rest.substring(byIndex + 3).trim();
 

                // missing description
                if (description.isEmpty() || by.isEmpty()) {
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
                String from = rest.substring(fromIndex + 5, toIndex).trim();
                String to = rest.substring(toIndex + 3).trim();
 
                // missing date
                if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
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

        scanner.close();
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
        private String by;

        public DeadlineTask(String description, String by) {
            super(description);
            this.by = by;
        }

        @Override
        public String toSaveFormat() {
            return "D | " + super.toSaveFormat().substring(4) + " | " + by;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + by + ")";
        }
    }

    private static class EventTask extends Task {
        private String from;
        private String to;

        public EventTask(String description, String from, String to) {
            super(description);
            this.from = from;
            this.to = to;
        }

        @Override
        public String toSaveFormat() {
            return "E | " + super.toSaveFormat().substring(4) + " | " + from + " - " + to;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
        }
    }

    /** Relative path (from project root) of the save file. */
    private static final String SAVE_FILE_PATH = "./data/Margit.txt";
 
    /**
     * Writes the current task list to disk at {@link #SAVE_FILE_PATH}, overwriting
     * any previous contents. Creates the parent "data" directory if it does not
     * already exist. This is the "happy path" implementation: it assumes the
     * data directory is writable and does not attempt recovery on failure beyond
     * printing a warning, since reading the file back in is a future feature.
     */

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
                        task = new DeadlineTask(description, parts[3]);
                        break;
                    case "E":
                        String[] fromTo = parts[3].split(" - ", 2);
                        task = new EventTask(description, fromTo[0], fromTo[1]);
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
