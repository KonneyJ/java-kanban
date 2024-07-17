package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    //Создание задачи, подзадачи и эпика
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    //Получение всех задач, подзадач и эпиков
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    //Печать списка всех задач, подзадач и эпиков
    void getAllTasks();

    void getAllSubtasks();

    void getAllEpics();

    //Обновление задач, подзадач и эпиков
    Task updateTask(Task task);

    Subtask updateSubtask(Subtask subtask);

    Epic updateEpic(Epic epic);

    //Удаление задач, подзадач и эпиков по id
    boolean deleteTask(Integer taskId);

    boolean deleteSubtask(Integer subtaskId);

    boolean deleteEpics(Integer epicId);

    //Удаление всех задач, подзадач и эпиков
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    //Получение задачи, эпика и подзадачи по id
    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    Subtask getSubtaskById(Integer subtaskId);

    List<Subtask> getSubtasksByEpic(Integer epicId);

    List<Task> getHistory();
}
