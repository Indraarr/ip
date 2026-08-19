import java.util.Scanner;

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

        String[] tasks = new String[100];
        int taskCount = 0;

        while (true) {
            line = scanner.nextLine();

            if (line.equals("bye")) {
                break;
            }

            if (line.equals("list")) {
                StringBuilder listOutput = new StringBuilder();
                for (int i = 0; i < taskCount; i++) {
                    listOutput.append(space)
                               .append(i + 1)
                               .append(". ")
                               .append(tasks[i])
                               .append("\n");
                }
                System.out.println(space + horizontalLine + "\n" + listOutput + "\n");
                System.out.println(space + horizontalLine + "\n");
                continue;
            }

            tasks[taskCount] = line;
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
