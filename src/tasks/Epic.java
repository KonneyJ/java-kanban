package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasksInEpic;
    protected static TaskType epicType = TaskType.EPIC;

    public Epic(String name, String description) {
        super(name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
        subtasksInEpic = new ArrayList<>();
    }

    public TaskType getEpicType() {
        return epicType;
    }

    public Epic(String name, String description, Status status, final TaskType epicType) {
        super(name, description, status);
        this.epicType = epicType;
    }

    public ArrayList<Subtask> getSubtasksInEpic() {
        return subtasksInEpic;
    }

    public void setSubtasksInEpic(ArrayList<Subtask> subtasksInEpic) {
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
