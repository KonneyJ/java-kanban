package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldNotNullAfterGetDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Объект не проинициализирован");
    }

    @Test
    void shouldNotNullAfterGetDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Объект не проинициализирован");
    }
}