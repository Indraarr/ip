package margit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests save-file representations produced by task types. */
class TaskTest {
    @Test
    void toSaveFormat_taskTypes_returnsExpectedTypedRepresentations() {
        Task todo = new TodoTask("read book");
        Task deadline = new DeadlineTask("return book", TaskDateTime.parse("2/12/2019 1800"));
        Task event = new EventTask("study", TaskDateTime.parse("2/12/2019 1800"),
                TaskDateTime.parse("3/12/2019 1800"));
        deadline.mark();

        assertEquals("T | 0 | read book", todo.toSaveFormat());
        assertEquals("D | 1 | return book | DT:2019-12-02 1800", deadline.toSaveFormat());
        assertEquals("E | 0 | study | DT:2019-12-02 1800 | DT:2019-12-03 1800", event.toSaveFormat());
    }
}
