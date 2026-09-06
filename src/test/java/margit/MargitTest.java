package margit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command responses produced by {@link Margit}. */
class MargitTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_todoAndListCommands_returnsTaskResponses() {
        Margit margit = createMargit();

        String addResponse = margit.getResponse("todo read book");
        String listResponse = margit.getResponse("list");

        assertTrue(addResponse.contains("I've added this task"));
        assertTrue(addResponse.contains("read book"));
        assertTrue(listResponse.contains("Here are the tasks in your list"));
        assertTrue(listResponse.contains("[T][ ] read book"));
    }

    @Test
    void getResponse_markAndDeleteCommands_updatesTaskState() {
        Margit margit = createMargit();
        margit.getResponse("todo read book");

        String markResponse = margit.getResponse("mark 1");
        String deleteResponse = margit.getResponse("delete 1");

        assertTrue(markResponse.contains("marked this task as done"));
        assertTrue(markResponse.contains("[T][X] read book"));
        assertTrue(deleteResponse.contains("I've removed this task"));
        assertTrue(deleteResponse.contains("Now you have 0 tasks"));
    }

    /** Creates a Margit instance with a test-only save-file location. */
    private Margit createMargit() {
        return new Margit(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
