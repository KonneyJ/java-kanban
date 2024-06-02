package tasks;
import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasksIds = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public HashMap<Integer, Subtask> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(HashMap<Integer, Subtask> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                    "id=" + getId() +
                    ", name='" + getName() + '\'' +
                    ", description='" + getDescription() + '\'' +
                    ", status=" + getStatus() +
                    '}';
    }
}
