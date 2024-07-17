package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.ArrayList;
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

    @Test
    void shouldBeHistoryMoreThanTen() {
        final int sizeForListPrev = 10;
        for (int i = 0; i <= sizeForListPrev; i++) {
            historyManager.add(task);
        }
        List<Task> testList = historyManager.getHistory();
        assertNotEquals(testList.size(), sizeForListPrev, "Ограничение на 10 пунктов больше нет.");
    }

    @Test
    void shouldAddToLinkedHashMap() {
        historyManager.add(task);
        final List<Task> listFromNodeMap = historyManager.getTasks();
        assertNotNull(listFromNodeMap, "Мапа для связного списка не пустая.");
    }

    @Test
    void shouldRemoveNodeFromLinkedHashMap() {
        historyManager.add(task);
        final List<Task> listFromNodeMap1 = historyManager.getTasks();
        assertNotNull(listFromNodeMap1, "Мапа для связного списка не пустая.");

        historyManager.remove(task.getId());
        final List<Task> listFromNodeMap2 = historyManager.getTasks();
        final List<Task> emptyList = new ArrayList<>();
        assertEquals(listFromNodeMap2.size(), emptyList.size(), "Мапа для связного списка пустая.");

    }
}