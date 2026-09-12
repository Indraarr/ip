package margit;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void getResponse_sortCommand_displaysGroupedTasksInPermanentOrder() {
        Margit margit = createMargit();
        margit.getResponse("todo read book");
        margit.getResponse("deadline someday /by someday");
        margit.getResponse("deadline report /by 2/12/2019");
        margit.getResponse("event meeting /from 2/12/2019 1800 /to 2/12/2019 1900");

        String sortResponse = margit.getResponse("sort");
        String listResponse = margit.getResponse("list");
        Margit reloadedMargit = createMargit();
        String reloadedListResponse = reloadedMargit.getResponse("list");

        assertTrue(sortResponse.contains("To Dos:"));
        assertTrue(sortResponse.contains("Unscheduled Tasks:"));
        assertTrue(sortResponse.contains("Scheduled Tasks:"));
        assertTrue(sortResponse.indexOf("read book") < sortResponse.indexOf("someday"));
        assertTrue(sortResponse.indexOf("someday") < sortResponse.indexOf("meeting"));
        assertTrue(sortResponse.indexOf("meeting") < sortResponse.indexOf("report"));
        assertTrue(listResponse.indexOf("read book") < listResponse.indexOf("someday"));
        assertTrue(listResponse.indexOf("someday") < listResponse.indexOf("meeting"));
        assertTrue(listResponse.indexOf("meeting") < listResponse.indexOf("report"));
        assertTrue(reloadedListResponse.indexOf("read book") < reloadedListResponse.indexOf("someday"));
        assertTrue(reloadedListResponse.indexOf("someday") < reloadedListResponse.indexOf("meeting"));
        assertTrue(reloadedListResponse.indexOf("meeting") < reloadedListResponse.indexOf("report"));
    }

    @Test
    void getResponse_sortOnlyTodos_omitsEmptyTaskGroups() {
        Margit margit = createMargit();
        margit.getResponse("todo read book");

        String sortResponse = margit.getResponse("sort");

        assertTrue(sortResponse.contains("To Dos:"));
        assertFalse(sortResponse.contains("Unscheduled Tasks:"));
        assertFalse(sortResponse.contains("Scheduled Tasks:"));
    }

    @Test
    void getResponse_sortWithArgument_returnsSortSyntaxError() {
        Margit margit = createMargit();

        String sortResponse = margit.getResponse("sort descending");

        assertTrue(sortResponse.contains("Sort does not accept arguments"));
    }

    /** Creates a Margit instance with a test-only save-file location. */
    private Margit createMargit() {
        return new Margit(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
