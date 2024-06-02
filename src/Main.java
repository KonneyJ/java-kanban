import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        System.out.println("Тест 1: пустой список");
        ArrayList<Task> tasks = taskManager.getTasks();
        System.out.println("Список задач должен быть пуст: " + tasks.isEmpty());
        System.out.println();

        System.out.println("Тест 2: создание задачи №1");
        Task task1 = new Task("Задача1", "Описание1", Status.NEW);
        Task task1Created = taskManager.createTask(task1);
        System.out.println("Созданная задача должна содержать id: " + (task1Created.getId() != null));
        System.out.println("Список задач должен содержать нашу задачу");
        taskManager.getAllTasks();
        System.out.println();

        System.out.println("Тест 3: создание задачи №2");
        Task task2 = new Task("Задача2", "Описание2", Status.NEW);
        Task task2Created = taskManager.createTask(task2);
        System.out.println("Созданная задача должна содержать id: " + (task2Created.getId() != null));
        System.out.println("Список задач должен содержать нашу задачу");
        taskManager.getAllTasks();
        System.out.println();

        System.out.println("Тест 4: пустой список эпиков");
        ArrayList<Epic> epics = taskManager.getEpics();
        System.out.println("Список эпиков должен быть пуст: " + epics.isEmpty());
        System.out.println();

        System.out.println("Тест 5: создание эпика");
        Epic epic1 = new Epic("Эпик1", "Описание1");
        Epic epic1Created = taskManager.createEpic(epic1);
        System.out.println("Созданный эпик должен содержать id: " + (epic1Created.getId() != null));
        System.out.println("Список эпиков должен содержать наш эпик ");
        taskManager.getAllEpics();
        System.out.println();

        System.out.println("Тест 6: пустой список подзадач");
        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        System.out.println("Список подзадач должен быть пуст: " + subtasks.isEmpty());
        System.out.println();

        System.out.println("Тест 7: создание первой подзадачи для одного эпика");
        Subtask subtask1 = new Subtask("Имя сабтаски", "Описание сабтаски", Status.NEW, epic1Created.getId());
        Subtask subtask1Created = taskManager.createSubtask(subtask1);
        System.out.println("Созданная подзадача должна содержать id: " + (subtask1Created.getId() != null));
        System.out.println("Список подзадач должен содержать нашу подзадачу");
        taskManager.getAllSubtasks();
        System.out.println();

        System.out.println("Тест 8: создание второй подзадачи для одного эпика");
        Subtask subtask2 = new Subtask("Имя сабтаски", "Описание сабтаски", Status.NEW, 2);
        Subtask subtask2Created = taskManager.createSubtask(subtask2);
        System.out.println("Созданная подзадача должна содержать id: " + (subtask2Created.getId() != null));
        System.out.println("Список подзадач должен содержать нашу подзадачу");
        taskManager.getAllSubtasks();
        System.out.println();

        System.out.println("Тест 9: создание второго эпика");
        Epic epic2 = new Epic("Эпик2", "Описание2");
        Epic epic2Created = taskManager.createEpic(epic2);
        System.out.println("Созданный эпик должен содержать id: " + (epic2Created.getId() != null));
        System.out.println("Список эпиков должен содержать наш эпик ");
        taskManager.getAllEpics();
        System.out.println();

        System.out.println("Тест 10: создание первой подзадачи для второго эпика");
        Subtask subtask3 = new Subtask("Имя сабтаски", "Описание сабтаски", Status.NEW, epic2Created.getId());
        Subtask subtask3Created = taskManager.createSubtask(subtask3);
        System.out.println("Созданная подзадача должна содержать id: " + (subtask3Created.getId() != null));
        System.out.println("Список подзадач должен содержать нашу подзадачу");
        taskManager.getAllSubtasks();
        System.out.println();


        System.out.println("Тест 11: обновление задачи");
        Task task3 = new Task(task1Created.getId(), "Имя новое", "Описание новое", Status.IN_PROGRESS);
        Task task3Updated = taskManager.updateTask(task3);
        System.out.println("Обновленная задача должна иметь обновленные поля " + task3Updated);
        System.out.println();

        System.out.println("Тест 12: обновление подзадачи");
        Subtask subtask4 = new Subtask(subtask1Created.getId(), "Имя новое", "Описание новое",
                Status.DONE, subtask1Created.getEpicId());
        Subtask subtask4Updated = taskManager.updateSubtask(subtask4);
        System.out.println("Обновленная подзадача должна иметь обновленные поля " + subtask4Updated);
        System.out.println();

        System.out.println("Тест 13: обновление эпика");
        Epic epic4 = new Epic(epic1Created.getId(), "Имя новое", "Описание новое");
        Epic epic4Updated = taskManager.updateEpic(epic4);
        System.out.println("Обновленный эпик должен иметь обновленные поля " + epic4Updated);
        System.out.println();

        System.out.println("Тест 14: удаление задачи");
        boolean deleteResult = taskManager.deleteTask(task3Updated.getId());
        System.out.println("Удаление должно пройти успешно " + deleteResult);
        System.out.println("Список задач должен содержать одну задачу");
        taskManager.getAllTasks();
        System.out.println();

        System.out.println("Тест 15: удаление эпика");
        boolean deleteResultEpic = taskManager.deleteEpics(epic4Updated.getId());
        System.out.println("Удаление должно пройти успешно " + deleteResultEpic);
        System.out.println("Список эпиков должен содержать один эпик");
        taskManager.getAllEpics();
        System.out.println();
    }
}
