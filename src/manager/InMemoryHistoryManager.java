package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList = new ArrayList<>();
    private final int HISTORY_LENGTH = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        historyList.add(task);
            if (historyList.size() > HISTORY_LENGTH) {
                historyList.removeFirst();
            }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
