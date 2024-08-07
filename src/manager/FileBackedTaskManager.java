package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static String pathToFile = "./src";
    private static File file = new File(pathToFile, "file.csv");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) {
                Task returnTask = CSVFormatter.fromString(lines.get(i));
                if (returnTask.getId() > maxId) {
                    maxId = returnTask.getId();
                }
                if (returnTask.getTaskType().equals(TaskType.TASK)) {
                    fileBackedTaskManager.addTask(returnTask);
                } else if (returnTask.getTaskType().equals(TaskType.SUBTASK)) {
                    fileBackedTaskManager.addSubtask((Subtask) returnTask);
                } else {
                    fileBackedTaskManager.addEpic((Epic) returnTask);
                }
            }
            InMemoryTaskManager manager = new InMemoryTaskManager();
            manager.setNextId(maxId + 1);
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
        return fileBackedTaskManager;
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            if (file.isFile()) {
                file.delete();
            }
            file = new File(pathToFile, "file.csv");
            //header
            bw.write(CSVFormatter.getHeader());
            bw.newLine();
            //body
            for (Task task : getTasks()) {
                bw.write(CSVFormatter.toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                bw.write(CSVFormatter.toString(epic) + "\n");
            }

            for (Subtask subtask : getSubtasks()) {
                bw.write(CSVFormatter.toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        List<Subtask> subtasksInCurrentEpic = epic.getSubtasksInEpic();
        subtasksInCurrentEpic.add(subtask);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epic.setSubtasksInEpic(new ArrayList<>());
    }

    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;

    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public boolean deleteTask(Integer taskId) {
        boolean deleteTaskresult = super.deleteTask(taskId);
        save();
        return deleteTaskresult;

    }

    @Override
    public boolean deleteSubtask(Integer subtaskId) {
        boolean deleteSubtaskResult = super.deleteSubtask(subtaskId);
        save();
        return deleteSubtaskResult;
    }

    @Override
    public boolean deleteEpics(Integer epicId) {
        boolean deleteEpicResult = super.deleteEpics(epicId);
        save();
        return deleteEpicResult;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
