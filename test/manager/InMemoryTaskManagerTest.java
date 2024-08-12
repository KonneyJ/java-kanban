package manager;

import exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateTask() {

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.now());
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
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.now(), epic.getId());
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

        Subtask subtask1 = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 0),
                epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask2 = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 15),
                epic.getId());
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        assertNotNull(savedSubtask2);
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask1Updated = new Subtask(subtask1.getId(), "Имя новое", "Описание новое",
                Status.IN_PROGRESS, Duration.ofMinutes(10), subtask1.getStartTime(), epic.getId());
        Subtask updatedSubtask = taskManager.updateSubtask(subtask1Updated);
        assertNotNull(updatedSubtask);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask2Updated = new Subtask(subtask2.getId(), "Имя суперновое", "Описание суперновое",
                Status.IN_PROGRESS, Duration.ofMinutes(10), subtask2.getStartTime(), epic.getId());
        Subtask updatedSubtask1 = taskManager.updateSubtask(subtask2Updated);
        assertNotNull(updatedSubtask1);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask1Done = new Subtask(subtask1Updated.getId(), "Имя суперновое", "Описание суперновое",
                Status.DONE, Duration.ofMinutes(10), subtask1Updated.getStartTime(), epic.getId());
        Subtask doneSubtask1 = taskManager.updateSubtask(subtask1Done);
        assertNotNull(doneSubtask1);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask2Done = new Subtask(subtask2Updated.getId(), "Имя суперновое", "Описание суперновое",
                Status.DONE, Duration.ofMinutes(10), subtask2Updated.getStartTime(), epic.getId());
        Subtask doneSubtask2 = taskManager.updateSubtask(subtask2Done);
        assertNotNull(doneSubtask2);
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика неверный");

        Subtask subtask3 = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask3 = taskManager.createSubtask(subtask3);
        assertNotNull(savedSubtask3);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус эпика неверный");
    }

    @Test
    void shouldUpdateTask() {

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 16, 0));
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

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 16, 30),
                epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        Subtask subtask1 = new Subtask(subtask.getId(), "Имя новое", "Описание новое", Status.DONE,
                epic.getId());
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
        assertEquals(updatedEpic, epics.get(savedEpic.getId()), "Эпики не равны");
    }

    @Test
    void shouldDeleteTask() {

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        assertEquals(1, (taskManager.getTasks()).size(), "Списки задач не равны");

        taskManager.deleteTask(savedTask.getId());
        assertEquals(0, (taskManager.getTasks()).size(), "Списки задач не равны");
    }

    @Test
    void shouldDeleteSubtask() {

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
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

        taskManager.deleteEpics(savedEpic.getId());
        assertEquals(0, (taskManager.getEpics()).size(), "Списки эпиков не равны");
    }

    @Test
    void shouldDeleteAllTasks() {

        Task task1 = new Task("Имя задачи1", "Описание задачи1", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
        Task savedTask1 = taskManager.createTask(task1);
        assertNotNull(savedTask1);

        Task task2 = new Task("Имя задачи2", "Описание задачи2", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 50));
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
        Subtask subtask1 = new Subtask("Имя подзадачи1", "Описание подзадачи1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);

        Subtask subtask2 = new Subtask("Имя подзадачи2", "Описание подзадачи2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 50),
                epic.getId());
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        assertNotNull(savedSubtask2);

        assertEquals(2, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");

        taskManager.deleteAllSubtasks();
        assertEquals(0, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");
    }

    @Test
    void shouldGetTaskById() {

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        Task findedTask = taskManager.getTaskById(savedTask.getId());
        assertNotNull(findedTask);
        assertEquals(findedTask, savedTask, "Задачи не равны");
    }

    @Test
    void shouldGetEpicById() {

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);

        Epic findedEpic = taskManager.getEpicById(savedEpic.getId());
        assertNotNull(findedEpic);
        assertEquals(findedEpic, savedEpic, "Эпики не равны");
    }

    @Test
    void shouldGetSubtaskById() {

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        Subtask findedSubtask = taskManager.getSubtaskById(savedSubtask.getId());
        assertEquals(findedSubtask, savedSubtask, "Подзадачи не равны");
    }

    @Test
    void shouldGetSubtasksByEpic() {

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask1 = new Subtask("Имя подзадачи1", "Описание подзадачи1", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(savedSubtask1);
        Subtask subtask2 = new Subtask("Имя подзадачи2", "Описание подзадачи2", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 45),
                epic.getId());
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

        Task task = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        assertEquals(task.getId(), savedTask.getId(), "Id не равны");
        assertEquals(task.getName(), savedTask.getName(), "Имена задач не равны");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания задач не равны");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не равны");
        assertEquals(task.getDuration(), savedTask.getDuration(), "Продолжительность задач не равна");
        assertEquals(task.getStartTime(), savedTask.getStartTime(), "Время начала задач не равна");
        assertEquals(task.getEndTime(), savedTask.getEndTime(), "Время окончания задач не равна");
    }

    @Test
    void shouldTaskBeEqualWithAddAndGeneratedId() {

        Task task1 = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS);
        Task savedTask = taskManager.createTask(task1);
        assertNotNull(savedTask);
        Task task2 = new Task(savedTask.getId(), "Имя задачи1", "Описание задачи1", Status.IN_PROGRESS);
        assertEquals(task2.getId(), savedTask.getId(), "Задачи не конфликтуют между собой");
    }

    @Test
    void shouldNotContainOldSubtaskIdInEpic() {

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        assertEquals(1, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");

        taskManager.deleteSubtask(subtask.getId());
        assertEquals(0, (taskManager.getSubtasks()).size(), "Списки подзадач не равны");
    }

    @Test
    void shouldNotChangeDataTaskInManagerAfterChangeBySetters() {

        Task task = new Task("Имя задачи", "Описание задачи", Status.NEW, Duration.ofMinutes(15),
                LocalDateTime.of(2024, 8, 10, 15, 30));
        Task savedTask = taskManager.createTask(task);
        assertNotNull(savedTask);

        savedTask.setName("Новое имя");
        savedTask.setDescription("Новое описание");
        savedTask.setStatus(Status.IN_PROGRESS);
        savedTask.setDuration(Duration.ofMinutes(50));
        savedTask.setStartTime(LocalDateTime.of(2024, 8, 10, 16, 30));
        assertEquals(savedTask.getName(), taskManager.getTaskById(savedTask.getId()).getName(),
                "Имена задачи не равны");
        assertEquals(savedTask.getDescription(), taskManager.getTaskById(savedTask.getId()).getDescription(),
                "Описания задачи не равны");
        assertEquals(savedTask.getStatus(), taskManager.getTaskById(savedTask.getId()).getStatus(),
                "Статусы задачи не равны");
        assertEquals(savedTask.getDuration(), taskManager.getTaskById(savedTask.getId()).getDuration(),
                "Продолжительности задачи не равны");
        assertEquals(savedTask.getStartTime(), taskManager.getTaskById(savedTask.getId()).getStartTime(),
                "Время начала задач не равны");
        assertEquals(savedTask.getEndTime(), taskManager.getTaskById(savedTask.getId()).getEndTime(),
                "Время окончания задач не равны");
    }

    @Test
    void shouldNotChangeDataEpicInManagerAfterChangeBySetters() {

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

        Epic epic = new Epic("Имя эпика", "Описание эпика");
        Epic savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic);
        Subtask subtask = new Subtask("Имя подзадачи", "Описание подзадачи", Status.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2024, 8, 10, 15, 30),
                epic.getId());
        Subtask savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask);

        savedSubtask.setName("Новое имя");
        savedSubtask.setDescription("Новое описание");
        savedSubtask.setStatus(Status.IN_PROGRESS);
        savedSubtask.setDuration(Duration.ofMinutes(50));
        savedSubtask.setStartTime(LocalDateTime.of(2024, 8, 10, 16, 30));
        assertEquals(savedSubtask.getName(), taskManager.getSubtaskById(savedSubtask.getId()).getName(),
                "Имена подзадачи не равны");
        assertEquals(savedSubtask.getDescription(), taskManager.getSubtaskById(savedSubtask.getId()).getDescription(),
                "Описания подзадачи не равны");
        assertEquals(savedSubtask.getStatus(), taskManager.getSubtaskById(savedSubtask.getId()).getStatus(),
                "Статусы подзадачи не равны");
        assertEquals(savedSubtask.getDuration(), taskManager.getSubtaskById(savedSubtask.getId()).getDuration(),
                "Продолжительности задачи не равны");
        assertEquals(savedSubtask.getStartTime(), taskManager.getSubtaskById(savedSubtask.getId()).getStartTime(),
                "Время начала задач не равны");
        assertEquals(savedSubtask.getEndTime(), taskManager.getSubtaskById(savedSubtask.getId()).getEndTime(),
                "Время окончания задач не равны");
    }

    @Test
    void shouldCorreclyFindIntesectionsTasks() {
        Task task1 = new Task("Имя задачи", "Описание задачи", Status.IN_PROGRESS,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 10, 15, 0));
        Task savedTask1 = taskManager.createTask(task1);
        assertNotNull(savedTask1);
        Task task2 = new Task("Имя задачи1", "Описание задачи1", Status.IN_PROGRESS,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 10, 15, 20));
        Task savedTask2 = taskManager.createTask(task2);
        assertNotNull(savedTask2);
        Task task3 = new Task("Имя задачи1", "Описание задачи1", Status.IN_PROGRESS,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 10, 15, 10));
        assertThrows(InvalidTaskException.class, () -> {
                    Task savedTask3 = taskManager.createTask(task2);
                },
                "Создание задачи с пересечением по времени должно приводить к исключению");
    }
}