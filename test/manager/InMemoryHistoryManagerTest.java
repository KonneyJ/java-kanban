package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Имя", "Тест", Status.NEW);
    }

    @Test
    void shouldAddTaskInHistory() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}