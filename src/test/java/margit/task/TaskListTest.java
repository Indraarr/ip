package margit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Tests the collection operations provided by {@link TaskList}. */
class TaskListTest {
    @Test
    void add_tasks_appendsThemAndUpdatesSize() {
        TaskList tasks = new TaskList();
        Task first = new TodoTask("first");
        Task second = new TodoTask("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void remove_middleTask_returnsRemovedTaskAndShiftsLaterTasks() {
        TaskList tasks = new TaskList();
        Task first = new TodoTask("first");
        Task middle = new TodoTask("middle");
        Task last = new TodoTask("last");
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);

        Task removed = tasks.remove(1);

        assertSame(middle, removed);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
    }
}
