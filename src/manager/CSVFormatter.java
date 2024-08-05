package manager;

import tasks.*;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String toString(Task task) {
        return new StringBuilder()
                .append(task.getId()).append(",")
                .append(task.getTaskType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getEpicId())
                .toString();
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1].toUpperCase());
        String name = parts[2];
        Status status = Status.valueOf(parts[3].toUpperCase());
        String description = parts[4];
        Integer epicId = null;
        if (type.equals(TaskType.SUBTASK)) {
            epicId = Integer.parseInt(parts[5]);
        }

        if (type.equals(TaskType.TASK)) {
            Task task = new Task(id, name, description, status, type);
            return task;
        } else if (type.equals(TaskType.SUBTASK)) {
            Subtask subtask = new Subtask(name, description, status, type);
            subtask.setId(id);
            subtask.setEpicId(epicId);
            return subtask;
        } else {
            Epic epic = new Epic(name, description, status, type);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        }
    }

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }
}
