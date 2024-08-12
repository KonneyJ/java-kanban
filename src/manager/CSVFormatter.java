package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {

    private CSVFormatter() {
    }

    public static String toString(Task task) {
        StringBuilder sb = new StringBuilder()
                .append(task.getId()).append(",")
                .append(task.getTaskType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getDuration()).append(",")
                .append(task.getStartTime());
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            sb.append(",").append(task.getEpicId());
        }
        return sb.toString();
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1].toUpperCase());
        String name = parts[2];
        Status status = Status.valueOf(parts[3].toUpperCase());
        String description = parts[4];
        Duration duration = Duration.parse(parts[5]);
        LocalDateTime startTime = LocalDateTime.parse(parts[6]);

        if (type.equals(TaskType.TASK)) {
            return new Task(id, name, description, status, duration, startTime);
        } else if (type.equals(TaskType.SUBTASK)) {
            Subtask subtask = new Subtask(id, name, description, status, duration, startTime);
            subtask.setEpicId(Integer.parseInt(parts[7]));
            return subtask;
        } else {
            return new Epic(id, name, description, status, duration, startTime);
        }
    }

    public static String getHeader() {
        return "id,type,name,status,description,duration,startTime,epic";
    }
}
