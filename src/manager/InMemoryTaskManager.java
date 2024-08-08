package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private int nextId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    //Создание задачи, подзадачи и эпика
    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        epic.setSubtasksInEpic(new ArrayList<>());
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        //Подзадаче присваивается собственный id
        subtask.setId(getNextId());
        //Добавление подзадачи в список подзадач
        subtasks.put(subtask.getId(), subtask);
        //Добавление подзадачи в эпик
        Epic epic = epics.get(subtask.getEpicId());
        List<Subtask> subtasksInCurrentEpic = epic.getSubtasksInEpic();
        subtasksInCurrentEpic.add(subtask);
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

        List<Subtask> subtasksInEpic = epic.getSubtasksInEpic();

        if (subtasksInEpic.isEmpty()) {
            return Status.NEW;
        } else {
            int subtaskDone = 0;
            int subtaskNew = 0;

            for (Subtask subtask : subtasksInEpic) {
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
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Печать списка всех задач, подзадач и эпиков
    @Override
    public void getAllTasks() {
        for (Integer key : tasks.keySet()) {
            Task task = tasks.get(key);
            System.out.println(task);
        }
    }

    @Override
    public void getAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            Subtask subtask = subtasks.get(key);
            System.out.println(subtask);
        }
    }

    @Override
    public void getAllEpics() {
        for (Integer key : epics.keySet()) {
            Epic epic = epics.get(key);
            System.out.println(epic);
        }
    }

    //Обновление задач, подзадач и эпиков
    @Override
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        tasks.put(taskId, task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        //Получаем id подзадачи и эпика, к которому эта подзадача относится
        Integer subtaskId = subtask.getId();
        Integer epicId = subtask.getEpicId();
        //Если id не существует и мапа подзадач не содержит такой ключ, возвращаем null
        if (subtaskId == null || !(subtasks.containsKey(subtaskId))) {
            return null;
        }
        //Добавляем в мапу подзадач обновленную подзадачу по ключу
        Subtask oldSubtask = subtasks.get(subtaskId);
        subtasks.put(subtaskId, subtask);
        //В объект типа Epic сохраняем значение из мапы эпиков по ключу
        Epic epic = epics.get(epicId);
        //Получаем cписок подзадач определенного эпика и сохраняем туда обновленную подзадачу
        List<Subtask> updatedSubtasks = epic.getSubtasksInEpic();
        updatedSubtasks.remove(oldSubtask);
        updatedSubtasks.add(subtask);
        //Добавляем подзадачу в мапу подзадач этого эпика
        epic.setSubtasksInEpic(updatedSubtasks);
        //Переопределяем статус эпика
        epic.setStatus(checkEpicStatus(epic));
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        epic.setSubtasksInEpic(epics.get(epic.getId()).getSubtasksInEpic());
        epic.setStatus(checkEpicStatus(epic));
        epics.put(epicId, epic);
        return epic;
    }

    //Удаление задач, подзадач и эпиков по id
    @Override
    public boolean deleteTask(Integer taskId) {
        historyManager.remove(taskId);
        return tasks.remove(taskId) != null;
    }

    @Override
    public boolean deleteSubtask(Integer subtaskId) {
        historyManager.remove(subtaskId);
        return subtasks.remove(subtaskId) != null;
    }

    @Override
    public boolean deleteEpics(Integer epicId) {
        Epic epic = epics.get(epicId);
        for (Subtask subtaskToDelete : getSubtasksByEpic(epicId)) {
            subtasks.remove(subtaskToDelete.getId());
            historyManager.remove(subtaskToDelete.getId());
        }
        (epics.get(epic.getId()).getSubtasksInEpic()).clear();
        historyManager.remove(epicId);
        return epics.remove(epicId) != null;
    }

    //Удаление всех задач, подзадач и эпиков
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    //Получение задачи, эпика и подзадачи по id
    @Override
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            return null;
        }
        historyManager.add(subtask);
        return subtask;
    }

    //Получение списка подзадач конкретного эпика
    @Override
    public List<Subtask> getSubtasksByEpic(Integer epicId) {
        if (epics.get(epicId) == null) {
            return null;
        } else {
            return epics.get(epicId).getSubtasksInEpic();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Генератор id
    public int getNextId() {
        return nextId++;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}
