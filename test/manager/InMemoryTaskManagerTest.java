package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateTask() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        int expectedId = task.getId();
        int actualId = savedTask.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        final ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(savedTask, tasks.get(0), "Задачи не равны");
    }

    @Test
    void shouldCreateEpic() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        int expectedId = epic.getId();
        int actualId = savedEpic.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(savedEpic, epics.get(0), "Эпики не равны");
    }

    @Test
    void shouldCreateSubtask() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        int expectedId = subtask.getId();
        int actualId = savedSubtask.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(savedSubtask, subtasks.get(0), "Подзадачи не равны");
    }

    @Test
    void shouldCheckEpicStatus() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask1 = new Subtask(subtask.getId(), "Имя новое", "Описание новое", Status.IN_PROGRESS, epic.getId());
        Subtask updatedSubtask = taskManager.updateSubtask(subtask1);
        assertNotNull(updatedSubtask);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask2 = new Subtask(subtask.getId(), "Имя суперновое", "Описание суерновое", Status.DONE, epic.getId());
        Subtask updatedSubtask1 = taskManager.updateSubtask(subtask2);
        assertNotNull(updatedSubtask1);
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика неверный");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        Task task1 = new Task("Имя новое", "Описание новое", Status.DONE);
        Task updatedTask = taskManager.updateTask(task1);
        assertNotNull(updatedTask);

        int expectedId = task1.getId();
        int actualId = updatedTask.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks);
        assertEquals(updatedTask, tasks.get(0), "Задачи не равны");
    }

    @Test
    void shouldUpdateSubtask() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        Subtask subtask1 = new Subtask(subtask.getId(), "Имя новое", "Описание новое", Status.DONE, epic.getId());
        Subtask updatedSubtask = taskManager.updateSubtask(subtask1);
        assertNotNull(updatedSubtask);

        int expectedId = subtask1.getId();
        int actualId = updatedSubtask.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks);
        assertEquals(updatedSubtask, subtasks.get(0), "Подзадачи не равны");
    }

    @Test
    void shouldUpdateEpic() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        Epic epic1 = new Epic("Имя новое", "Описание новое");
        Epic updatedEpic = taskManager.updateEpic(epic1);
        assertNotNull(updatedEpic);

        int expectedId = epic1.getId();
        int actualId = updatedEpic.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(updatedEpic, epics.get(0), "Эпики не равны");
    }

    @Test
    void shouldDeleteTask() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        assertEquals(1, (taskManager.getTasks()).size(), "Списки задач не равны");

        taskManager.deleteTask(0);
        assertEquals(0, (taskManager.getTasks()).size(), "Списки задач не равны");
    }

    @Test
    void shouldDeleteSubtask() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        assertEquals(1, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");

        taskManager.deleteSubtask(subtask.getId());
        assertEquals(0, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");
    }

    @Test
    void shouldDeleteEpics() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        assertEquals(1, (taskManager.getEpics()).size(), "Списки эпиков не равны");

        taskManager.deleteEpics(0);
        assertEquals(0, (taskManager.getEpics()).size(), "Списки эпиков не равны");
    }

    @Test
    void shouldDeleteAllTasks() {
        Task task1 = new Task("Имя задачи1", "Описание задачи1", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        assertNotNull(savedTask1);

        Task task2 = new Task("Имя задачи2", "Описание задачи2", Status.NEW);
        Task savedTask2 = taskManager.createTask(task2);
        assertNotNull(savedTask2);

        assertEquals(2, (taskManager.getTasks()).size(), "Списки задач не равны");

        taskManager.deleteAllTasks();
        assertEquals(0, (taskManager.getTasks()).size(), "Списки задач не равны");
    }

    @Test
    void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Имя эпика1", "Описание эпика1");
        Epic savedEpic1 = taskManager.createEpic(epic1);
        assertNotNull(savedEpic1);

        Epic epic2 = new Epic("Имя эпика2", "Описание эпика2");
        Epic savedEpic2 = taskManager.createEpic(epic2);
        assertNotNull(savedEpic2);

        assertEquals(2, (taskManager.getEpics()).size(), "Списки эпиков не равны");

        taskManager.deleteAllEpics();
        assertEquals(0, (taskManager.getEpics()).size(), "Списки эпиков не равны");
    }

    @Test
    void shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask1 = new Subtask("Имя подзадачи1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);

        Subtask subtask2 = new Subtask("Имя подзадачи2", "Описание подзадачи2", Status.NEW, epic.getId());
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        assertNotNull(savedSubtask2);

        assertEquals(2, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");

        taskManager.deleteAllSubtasks();
        assertEquals(0, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");
    }

    @Test
    void shouldGetTaskById() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        Task findedTask = taskManager.getTaskById(0);
        assertNotNull(findedTask);
        assertEquals(findedTask, savedTask, "Задачи не равны");
    }

    @Test
    void shouldGetEpicById() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        Epic findedEpic = taskManager.getEpicById(0);
        assertNotNull(findedEpic);
        assertEquals(findedEpic, savedEpic, "Эпики не равны");
    }

    @Test
    void shouldGetSubtaskById() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        Subtask findedSubtask = taskManager.getSubtaskById(1);
        assertNotNull(findedSubtask);
        assertEquals(findedSubtask, savedSubtask, "Подзадачи не равны");
    }

    @Test
    void shouldGetSubtasksByEpic() {
        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask1 = new Subtask("Имя подзадачи1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);
        Subtask subtask2 = new Subtask("Имя подзадачи2", "Описание подзадачи2", Status.NEW, epic.getId());
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        assertNotNull(savedSubtask2);

        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        subtasksByEpic.add(subtask1);
        subtasksByEpic.add(subtask2);
        ArrayList<Subtask> savedSubtasksByEpic = taskManager.getSubtasksByEpic(savedEpic.getId());
        assertEquals(savedSubtasksByEpic, subtasksByEpic, "Списки подзадач определенного эпика не равны");
    }

    @Test
    void shouldTaskBeEqualAfterAddToManager() {
        Task task = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        assertEquals(task.getId(), savedTask.getId(), "Id не равны");
        assertEquals(task.getName(), savedTask.getName(), "Имена задач не равны");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания задач не равны");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не равны");
    }

    @Test
    void shouldTaskBeEqualWithAddAndGeneratedId() {
        Task task1 = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS);
        Task savedTask = taskManager.createTask(task1);
        assertNotNull(savedTask);
        Task task2 = new Task(0,"Имя задачи1", "Описание задачи1", Status.IN_PROGRESS);
        assertEquals(task2.getId(), savedTask.getId(), "Задачи не конфликтуют между собой");
    }

    @Test
    void shouldTaskBeEqualAfterAddToHistoryManager() {
        Task task1 = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS);
        Task savedTask = taskManager.createTask(task1);
        assertNotNull(savedTask);
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task1);

        Task task2 = new Task(task1.getId(), "Новое имя", "", Status.DONE);
        Task updatedTask = taskManager.updateTask(task2);
        historyManager.add(task2);

        ArrayList<Task> tasks = historyManager.getHistory();
        assertEquals(task1.getId(), tasks.get(0).getId(), "Id не равны");
        assertEquals(task1.getName(), tasks.get(0).getName(), "Имена не равны");
        assertEquals(task1.getDescription(), tasks.get(0).getDescription(), "Описания не равны");
        assertEquals(task1.getStatus(), tasks.get(0).getStatus(), "Статусы не равны");

        assertEquals(task2.getId(), tasks.get(1).getId(), "Id не равны");
        assertEquals(task2.getName(), tasks.get(1).getName(), "Имена не равны");
        assertEquals(task2.getDescription(), tasks.get(1).getDescription(), "Описания не равны");
        assertEquals(task2.getStatus(), tasks.get(1).getStatus(), "Статусы не равны");
    }
}