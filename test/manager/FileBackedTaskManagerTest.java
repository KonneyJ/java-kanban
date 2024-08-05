package manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private String pathToFile = "C:\\Users\\user\\IdeaProjects\\java-kanban\\src";
    private FileBackedTaskManager fileManager;
    private File file;

    @BeforeEach
    public void beforeEach() {
        file = new File(pathToFile, "file.csv");
        fileManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void afterEach() {
        file.delete();
    }

    @Test
    void shouldWorkFileBackedManagerAsInMemoryManager() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = fileManager.createTask(task);
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getStatus(), savedTask.getStatus());

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = fileManager.createEpic(epic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = fileManager.createSubtask(subtask);
        assertEquals(epic.getName(), savedEpic.getName());
        assertEquals(epic.getDescription(), savedEpic.getDescription());
        assertEquals(subtask.getName(), savedSubtask.getName());
        assertEquals(subtask.getDescription(), savedSubtask.getDescription());
        assertEquals(subtask.getEpicId(), savedSubtask.getEpicId());
    }

    /*@Test
    void shouldSaveAndLoadAnEmptyFile() {
        fileManager.save();
        assertEquals(file.getName(), new File(pathToFile, "file.csv").getName(), "Имена файлов не одинаковы");
        fileManager.loadFromFile();
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList, fileManager.getTasks());
        assertEquals(emptyList, fileManager.getEpics());
        assertEquals(emptyList, fileManager.getSubtasks());
    }

    @Test
    void shouldSaveAndLoadSeveralTasks() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = fileManager.createTask(task);
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = fileManager.createEpic(epic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = fileManager.createSubtask(subtask);

        fileManager.loadFromFile();
        assertEquals(List.of(task), fileManager.getTasks());
        assertEquals(List.of(epic), fileManager.getEpics());
        assertEquals(List.of(subtask), fileManager.getSubtasks());
    }*/
}
