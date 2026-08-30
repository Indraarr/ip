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

    @Test
    void findByKeyword_matchingTasks_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        Task firstMatch = new TodoTask("read book");
        Task nonMatch = new TodoTask("buy milk");
        Task secondMatch = new TodoTask("return book");
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        TaskList matchingTasks = tasks.findByKeyword("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatch, matchingTasks.get(0));
        assertSame(secondMatch, matchingTasks.get(1));
    }

    @Test
    void findByKeyword_noMatchingTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("buy milk"));

        TaskList matchingTasks = tasks.findByKeyword("book");

        assertEquals(0, matchingTasks.size());
    }
}
