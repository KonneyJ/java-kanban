package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasksInEpic;

    public Epic(String name, String description) {
        super(name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        subtasksInEpic = new ArrayList<>();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public List<Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    public void setSubtasksInEpic(List<Subtask> subtasksInEpic) {
        this.subtasksInEpic = subtasksInEpic;
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
