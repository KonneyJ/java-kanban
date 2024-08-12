package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static InMemoryHistoryManager historyManager;
    private static Task task;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        task = new Task(0, "Имя", "Тест", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
    }

    @Test
    void shouldBeAnEmptyHistory() {
        final List<Task> emptyHistory = historyManager.getHistory();
        assertEquals(0, emptyHistory.size(), "История пустая");
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
    void shouldNotDoubleTaskInHistory() {
        historyManager.add(task);
        final List<Task> listFromNodeMap1 = historyManager.getTasks();
        assertEquals(listFromNodeMap1.size(), List.of(task).size(), "Мапа для связного списка содержит 1 задачу");
        historyManager.add(task);
        final List<Task> listFromNodeMap2 = historyManager.getTasks();
        assertEquals(listFromNodeMap2.size(), List.of(task).size(), "Мапа для связного списка содержит 1 задачу");
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

    @Test
    void shouldRemoveNodeFromMiddleOfLinkedHashMap() {
        Task task1 = new Task(1, "Имя1", "Тест1", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 16, 30));
        Task task2 = new Task(2, "Имя2", "Тест2", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 17, 30));
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> listFromNodeMap1 = historyManager.getTasks();
        assertEquals(listFromNodeMap1.size(), List.of(task, task1, task2).size(),
                "Мапа для связного списка содержит правильное количество задач");

        historyManager.remove(task1.getId());
        final List<Task> listFromNodeMap2 = historyManager.getTasks();
        assertEquals(listFromNodeMap2.size(), List.of(task, task2).size(),
                "Мапа для связного списка содержит правильное количество задач");
    }
}