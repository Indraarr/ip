package margit.task;

import java.util.Arrays;

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
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks[index];
    }

    /**
     * Appends one or more tasks to the end of the list.
     *
     * @param tasksToAdd tasks to append, in order
     */
    public void add(Task... tasksToAdd) {
        assert size + tasksToAdd.length <= CAPACITY : "Task additions must not exceed list capacity";
        for (Task task : tasksToAdd) {
            assert task != null : "A task list cannot contain null tasks";
            tasks[size] = task;
            size++;
        }
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task remove(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        Task removed = tasks[index];
        for (int i = index; i < size - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[size - 1] = null;
        size--;
        return removed;
    }

    /** Returns the tasks whose descriptions contain {@code keyword}, in list order. */
    public TaskList findByKeyword(String keyword) {
        TaskList matchingTasks = new TaskList();
        Arrays.stream(tasks, 0, size)
                .filter(task -> task.hasKeyword(keyword))
                .forEach(matchingTasks::add);
        return matchingTasks;
    }

    /** Returns whether an index refers to a task currently held by the list. */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < size;
    }
}
