package margit.task;

/** Stores the application's tasks and performs basic list operations. */
public class TaskList {
    private static final int CAPACITY = 100;
    private final Task[] tasks = new Task[CAPACITY];
    private int size;

    /** Returns the number of tasks currently in the list. */
    public int size() {
        return size;
    }

    /** Returns whether no further tasks can be added to the list. */
    public boolean isFull() {
        return size == CAPACITY;
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks[index];
    }

    /** Appends a task to the end of the list. */
    public void add(Task task) {
        tasks[size] = task;
        size++;
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task remove(int index) {
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }
}
