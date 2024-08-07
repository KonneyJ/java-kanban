package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;

    @Test
    void shouldCreateTask() {
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        Task task1 = new Task(savedTask.getId(), "Имя новое", "Описание новое", Status.DONE);
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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        Epic epic1 = new Epic("Имя новое", "Описание новое");
        Epic updatedEpic = taskManager.updateEpic(epic1);

        int expectedId = epic1.getId();
        int actualId = updatedEpic.getId();
        assertEquals(expectedId, actualId, "Id не равны");

        ArrayList<Epic> epics = taskManager.getEpics();
        assertNotNull(epics);
        assertEquals(updatedEpic, epics.get(0), "Эпики не равны");
    }

    @Test
    void shouldDeleteTask() {
        taskManager = new InMemoryTaskManager();

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        assertEquals(1, (taskManager.getTasks()).size(), "Списки задач не равны");

        taskManager.deleteTask(savedTask.getId());
        assertEquals(0, (taskManager.getTasks()).size(), "Списки задач не равны");
    }

    @Test
    void shouldDeleteSubtask() {
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        assertEquals(1, (taskManager.getEpics()).size(), "Списки эпиков не равны");

        taskManager.deleteEpics(savedEpic.getId());
        assertEquals(0, (taskManager.getEpics()).size(), "Списки эпиков не равны");
    }

    @Test
    void shouldDeleteAllTasks() {
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        Task findedTask = taskManager.getTaskById(savedTask.getId());
        assertNotNull(findedTask);
        assertEquals(findedTask, savedTask, "Задачи не равны");
    }

    @Test
    void shouldGetEpicById() {
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        Epic findedEpic = taskManager.getEpicById(savedEpic.getId());
        assertNotNull(findedEpic);
        assertEquals(findedEpic, savedEpic, "Эпики не равны");
    }

    @Test
    void shouldGetSubtaskById() {
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        Subtask findedSubtask = taskManager.getSubtaskById(savedSubtask.getId());
        assertEquals(findedSubtask, savedSubtask, "Подзадачи не равны");
    }

    @Test
    void shouldGetSubtasksByEpic() {
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask1 = new Subtask("Имя подзадачи1", "Описание подзадачи1", Status.NEW, epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);
        Subtask subtask2 = new Subtask("Имя подзадачи2", "Описание подзадачи2", Status.NEW, epic.getId());
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        assertNotNull(savedSubtask2);

        List<Subtask> subtasksByEpic = new ArrayList<>();
        subtasksByEpic.add(subtask1);
        subtasksByEpic.add(subtask2);
        List<Subtask> savedSubtasksByEpic = taskManager.getSubtasksByEpic(savedEpic.getId());
        assertEquals(savedSubtasksByEpic, subtasksByEpic, "Списки подзадач определенного эпика не равны");
    }

    @Test
    void shouldTaskBeEqualAfterAddToManager() {
        taskManager = new InMemoryTaskManager();

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
        taskManager = new InMemoryTaskManager();

        Task task1 = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS);
        Task savedTask = taskManager.createTask(task1);
        assertNotNull(savedTask);
        Task task2 = new Task(savedTask .getId(), "Имя задачи1", "Описание задачи1", Status.IN_PROGRESS);
        assertEquals(task2.getId(), savedTask.getId(), "Задачи не конфликтуют между собой");
    }

    @Test
    void shouldNotContainOldSubtaskIdInEpic() {
        taskManager = new InMemoryTaskManager();

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
    void shouldNotChangeDataTaskInManagerAfterChangeBySetters() {
        taskManager = new InMemoryTaskManager();

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW);
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        savedTask.setName("Новое имя");
        savedTask.setDescription("Новое описание");
        savedTask.setStatus(Status.IN_PROGRESS);
        assertEquals(savedTask.getName(), taskManager.getTaskById(savedTask.getId()).getName(),
                "Имена задачи не равны");
        assertEquals(savedTask.getDescription(), taskManager.getTaskById(savedTask.getId()).getDescription(),
                "Описания задачи не равны");
        assertEquals(savedTask.getStatus(), taskManager.getTaskById(savedTask.getId()).getStatus(),
                "Статусы задачи не равны");
    }

    @Test
    void shouldNotChangeDataEpicInManagerAfterChangeBySetters() {
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        savedEpic.setName("Новое имя");
        savedEpic.setDescription("Новое описание");
        assertEquals(savedEpic.getName(), taskManager.getEpicById(savedEpic.getId()).getName(),
                "Имена задачи не равны");
        assertEquals(savedEpic.getDescription(), taskManager.getEpicById(savedEpic.getId()).getDescription(),
                "Описания задачи не равны");
    }

    @Test
    void shouldNotChangeDataSubtaskInManagerAfterChangeBySetters() {
        taskManager = new InMemoryTaskManager();

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW, epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        savedSubtask.setName("Новое имя");
        savedSubtask.setDescription("Новое описание");
        savedSubtask.setStatus(Status.IN_PROGRESS);
        assertEquals(savedSubtask.getName(), taskManager.getSubtaskById(savedSubtask.getId()).getName(),
                "Имена подзадачи не равны");
        assertEquals(savedSubtask.getDescription(), taskManager.getSubtaskById(savedSubtask.getId()).getDescription(),
                "Описания подзадачи не равны");
        assertEquals(savedSubtask.getStatus(), taskManager.getSubtaskById(savedSubtask.getId()).getStatus(),
                "Статусы подзадачи не равны");
    }
}