package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int nextId;

    //Создание задачи, подзадачи и эпика
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        //Подзадаче присваивается собственный id
        subtask.setId(getNextId());
        //Добавление подзадачи в список подзадач
        subtasks.put(subtask.getId(), subtask);
        //Добавление подзадачи в эпик
        epic.getSubtasksIds().put(subtask.getId(), subtask);
        //Проверка статуса эпика
        epic.setStatus(checkEpicStatus(epic));
        return subtask;
    }

    //Проверка статуса эпика
    public Status checkEpicStatus(Epic epic) {
        Integer epicId = epic.getId();

        if ((epicId == null) || !epics.containsKey(epicId)) {
            return null;
        }

        HashMap<Integer, Subtask> subtasksInEpic = epic.getSubtasksIds();

        if (subtasksInEpic.size() == 0) {
            return Status.NEW;
        } else {
            int subtaskDone = 0;
            int subtaskNew = 0;

            for (Subtask subtask : subtasksInEpic.values()) {
                if (subtask.getStatus() == Status.DONE) {
                    subtaskDone = subtaskDone + 1;
                }
                if (subtask.getStatus() == Status.NEW) {
                    subtaskNew = subtaskNew + 1;
                }
            }

            if (subtasksInEpic.size() == subtaskDone) {
                return Status.DONE;
            } else if (subtasksInEpic.size() == subtaskNew) {
                return Status.NEW;
            } else {
                return Status.IN_PROGRESS;
            }
        }
    }

    //Получение всех задач, подзадач и эпиков
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Печать списка всех задач, подзадач и эпиков
    public void getAllTasks() {
        for (Integer key : tasks.keySet()) {
            Task task = tasks.get(key);
            System.out.println(task);
        }
    }

    public void getAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            Subtask subtask = subtasks.get(key);
            System.out.println(subtask);
        }
    }

    public void getAllEpics() {
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            System.out.println(epic);
        }
    }

    //Обновление задач, подзадач и эпиков
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        tasks.put(taskId, task);
        return task;
    }

    public Subtask updateSubtask(Subtask subtask) {
        //Получаем id подзадачи и эпика, к которому эта подзадача относится
        Integer subtaskId = subtask.getId();
        Integer epicId = subtask.getEpicId();
        //Если id не существует и мапа подзадач не содержит такой ключ, возвращаем null
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return null;
        }
        //Добавляем в мапу подзадач обновленную подзадачу по ключу
        subtasks.put(subtaskId, subtask);
        //В объект типа Epic сохраняем значение из мапы эпиков по ключу
        Epic epic = epics.get(epicId);
        //Переопределяем статус эпика
        epic.setStatus(checkEpicStatus(epic));
        HashMap<Integer, Subtask> updateSubtasks = epic.getSubtasksIds();
        //Получаем мапу подзадач определенного эпика и сохраняем туда обновленную подзадачу
        updateSubtasks.put(subtask.getId(), subtask);
        //Добавляем подзадачу в мапу подзадач этого эпика
        epic.setSubtasksIds(updateSubtasks);
        return subtask;
    }

    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        epic.setSubtasksIds(epics.get(epic.getId()).getSubtasksIds());
        epic.setStatus(checkEpicStatus(epic));
        epics.put(epicId, epic);
        return epic;
    }

    //Удаление задач, подзадач и эпиков по id
    public boolean deleteTask(Integer taskId) {
        return tasks.remove(taskId) != null;
    }

    public boolean deleteSubtask(Integer subtaskId) {
        return subtasks.remove(subtaskId) != null;
    }

    public boolean deleteEpics(Integer epicId) {
        return epics.remove(epicId) != null;
    }

    //Удаление всех задач, подзадач и эпиков
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    //Получение задачи, эпика и подзадачи по id
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        return task;
    }

    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        return epic;
    }

    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            return null;
        }
        return subtask;
    }

    //Получение списка подзадач конкретного эпика
    public HashMap<Integer, Subtask> getSubtasksByEpic(Integer epicId) {
        if (epics.get(epicId) == null) {
            return null;
        } else {
            return epics.get(epicId).getSubtasksIds();
        }
    }

    //Генератор id
    private int getNextId() {
        return nextId++;
    }
}
