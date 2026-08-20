import java.util.Scanner;

public class Margit {

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
    }
    
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
        int taskCount = 0;

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
                               .append(tasks[i].getStatusIcon())
                               .append(" ")
                               .append(tasks[i].description)
                               .append("\n");
                }
                System.out.println(space + horizontalLine + "\n" + listOutput + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            // Mark & Unmark List
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
                            + space + "  " + tasks[index].getStatusIcon() + " " + tasks[index].description + "\n");
                    System.out.println(space + horizontalLine + "\n");
                    continue;
                }

                String message = listAction
                        ? "Nice! I've marked this task as done:"
                        : "OK, I've marked this task as not done yet:";

                System.out.println(space + horizontalLine + "\n"
                        + space + message + "\n"
                        + space + "  " + tasks[index].getStatusIcon() + " " + tasks[index].description + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }


            // Create new task

            tasks[taskCount] = new Task(line);
            taskCount++;

            System.out.println(space + horizontalLine + "\n" + space + "added: " + line + "\n");
            System.out.println(space + horizontalLine + "\n");
        }

        String farewell = space + horizontalLine + "\n"
                        + space + "Tis well... put these foolish ambitions to rest.\n\n"
                        + space + horizontalLine + "\n";

        System.out.println(farewell);

        scanner.close();
    }
}
