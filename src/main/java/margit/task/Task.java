package margit.task;

import java.time.LocalDate;

/** Represents a task with a description and completion status. */
public class Task {
    private final String description;
    private boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
    }

    /** Marks this task as complete, returning whether its status changed. */
    public boolean mark() {
        if (isDone) {
            return false;
        }
        isDone = true;
        return true;
    }

    /** Marks this task as incomplete, returning whether its status changed. */
    public boolean unmark() {
        if (!isDone) {
            return false;
        }
        isDone = false;
        return true;
    }

    /** Returns the completion icon displayed with this task. */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /** Returns the representation used when this task is saved. */
    public String toSaveFormat() {
        return "T | " + (isDone ? "1" : "0") + " | " + description;
    }

    /** Returns whether this task's description contains {@code keyword}. */
    public boolean hasKeyword(String keyword) {
        return description.contains(keyword);
    }

    /** Returns whether this task falls on {@code date}. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns the user-facing status and description of this task. */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
