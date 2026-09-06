package margit.parser;

/** Interprets a raw user command as a command type and its argument. */
public class Parser {
    /** Recognized commands accepted by the application. */
    public enum CommandType {
        BYE, LIST, FIND, ON, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN
    }

    /** Parsed result containing the command type and its remaining argument. */
    public static class Command {
        public final CommandType type;
        public final String argument;
        /** Zero-based task index for indexed commands, or {@code null} when it is invalid or irrelevant. */
        public final Integer taskIndex;

        private Command(CommandType type, String argument) {
            this(type, argument, null);
        }

        private Command(CommandType type, String argument, Integer taskIndex) {
            this.type = type;
            this.argument = argument;
            this.taskIndex = taskIndex;
        }
    }

    /** Parsed description from a todo command. */
    public static class TodoArguments {
        public final boolean isValid;
        public final String description;

        private TodoArguments(boolean isValid, String description) {
            this.isValid = isValid;
            this.description = description;
        }
    }

    /** Validation outcomes for a deadline command's required components. */
    public enum DeadlineStatus {
        VALID, MISSING_SEPARATOR, MISSING_COMPONENT
    }

    /** Parsed description and date text from a deadline command. */
    public static class DeadlineArguments {
        public final DeadlineStatus status;
        public final String description;
        public final String byRaw;

        private DeadlineArguments(DeadlineStatus status, String description, String byRaw) {
            this.status = status;
            this.description = description;
            this.byRaw = byRaw;
        }
    }

    /** Parsed description and time text from an event command. */
    public static class EventArguments {
        public final boolean isValid;
        public final String description;
        public final String fromRaw;
        public final String toRaw;

        private EventArguments(boolean isValid, String description, String fromRaw, String toRaw) {
            this.isValid = isValid;
            this.description = description;
            this.fromRaw = fromRaw;
            this.toRaw = toRaw;
        }
    }

    /** Parses an input line without validating command-specific arguments. */
    public static Command parse(String input) {
        if (input.equals("bye")) {
            return new Command(CommandType.BYE, "");
        }
        if (input.equals("list")) {
            return new Command(CommandType.LIST, "");
        }
        if (input.equals("find") || input.startsWith("find ")) {
            return commandWithTrimmedArgument(CommandType.FIND, input, 4);
        }
        if (input.equals("on") || input.startsWith("on ")) {
            return commandWithTrimmedArgument(CommandType.ON, input, 2);
        }
        if (input.startsWith("mark ")) {
            return indexCommand(CommandType.MARK, input.substring(5));
        }
        if (input.startsWith("unmark ")) {
            return indexCommand(CommandType.UNMARK, input.substring(7));
        }
        if (input.startsWith("delete ")) {
            return indexCommand(CommandType.DELETE, input.substring(7));
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

    /** Parses and validates the required description of a todo command. */
    public static TodoArguments parseTodo(String argument) {
        return new TodoArguments(!argument.isEmpty(), argument);
    }

    /** Parses the description and required {@code /by} value of a deadline command. */
    public static DeadlineArguments parseDeadline(String argument) {
        int byIndex = argument.indexOf("/by");
        if (argument.isEmpty() || byIndex == -1) {
            return new DeadlineArguments(DeadlineStatus.MISSING_SEPARATOR, "", "");
        }
        String description = argument.substring(0, byIndex).trim();
        String byRaw = argument.substring(byIndex + 3).trim();
        if (description.isEmpty() || byRaw.isEmpty()) {
            return new DeadlineArguments(DeadlineStatus.MISSING_COMPONENT, description, byRaw);
        }
        return new DeadlineArguments(DeadlineStatus.VALID, description, byRaw);
    }

    /** Parses the description and required {@code /from} and {@code /to} values of an event command. */
    public static EventArguments parseEvent(String argument) {
        int fromIndex = argument.indexOf("/from");
        int toIndex = argument.indexOf("/to");
        if (argument.isEmpty() || fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            return new EventArguments(false, "", "", "");
        }
        String description = argument.substring(0, fromIndex).trim();
        String fromRaw = argument.substring(fromIndex + 5, toIndex).trim();
        String toRaw = argument.substring(toIndex + 3).trim();
        boolean isValid = !description.isEmpty() && !fromRaw.isEmpty() && !toRaw.isEmpty();
        return new EventArguments(isValid, description, fromRaw, toRaw);
    }

    /** Extracts and trims the text following a keyword and one separating space. */
    private static Command commandWithTrimmedArgument(CommandType type, String input, int keywordLength) {
        String argument = input.length() > keywordLength ? input.substring(keywordLength + 1).trim() : "";
        return new Command(type, argument);
    }

    /** Parses the one-based task number supplied to an indexed command. */
    private static Command indexCommand(CommandType type, String argument) {
        try {
            return new Command(type, argument, Integer.parseInt(argument.trim()) - 1);
        } catch (NumberFormatException e) {
            return new Command(type, argument, null);
        }
    }
}
