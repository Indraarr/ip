package margit.task;

import java.time.LocalDate;

/** Represents a task that occurs from one date or time to another. */
public class EventTask extends Task {
    private final TaskDateTime from;
    private final TaskDateTime to;

    /** Creates an event task with its description, start, and end. */
    public EventTask(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate fromDate = from.toLocalDate();
        LocalDate toDate = to.toLocalDate();
        return fromDate != null && toDate != null && !date.isBefore(fromDate) && !date.isAfter(toDate);
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
