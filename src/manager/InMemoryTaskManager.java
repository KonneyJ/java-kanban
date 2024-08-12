package manager;

import exception.InvalidTaskException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private int nextId = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private TreeSet<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    //Создание задачи, подзадачи и эпика
    @Override
    public Task createTask(Task task) {
        validateAndAddToSorted(task, null);
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epic.setStatus(Status.NEW);
        epic.setDuration(Duration.ofMinutes(0));
        epics.put(epic.getId(), epic);
        epic.setSubtasksInEpic(new ArrayList<>());
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        validateAndAddToSorted(subtask, null);
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
        checkEpicStartAndEndTime(epic);
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
                if (subtask.getStatus() == Status.IN_PROGRESS) {
                    return Status.IN_PROGRESS;
                }
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

    //Проверка времени эпика
    public void checkEpicStartAndEndTime(Epic epic) {
        List<Subtask> subtaskList = epic.getSubtasksInEpic().stream()
                .sorted(Comparator.comparing(Task::getStartTime)).toList();
        if (subtaskList.isEmpty()) {
            return;
        }
        if (!(subtaskList.getFirst().getStartTime() == null) &&
                !(subtaskList.getLast().getEndTime() == null)) {
            epic.setStartTime(subtaskList.getFirst().getStartTime());
            epic.setEndTime(subtaskList.getLast().getEndTime());
            for (Subtask s : subtaskList) {
                epic.setDuration(epic.getDuration().plus(s.getDuration()));
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
        tasks.values().forEach(System.out::println);
    }

    @Override
    public void getAllSubtasks() {
        subtasks.values().forEach(System.out::println);
    }

    @Override
    public void getAllEpics() {
        epics.values().forEach(System.out::println);
    }

    //Обновление задач, подзадач и эпиков
    @Override
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        validateAndAddToSorted(task, tasks.get(task.getId()));
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
        validateAndAddToSorted(subtask, subtasks.get(subtask.getId()));
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
        checkEpicStartAndEndTime(epic);
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
        checkEpicStartAndEndTime(epic);
        epics.put(epicId, epic);
        return epic;
    }

    //Удаление задач, подзадач и эпиков по id
    @Override
    public boolean deleteTask(Integer taskId) {
        historyManager.remove(taskId);
        sortedTasks.remove(tasks.get(taskId));
        return tasks.remove(taskId) != null;
    }

    @Override
    public boolean deleteSubtask(Integer subtaskId) {
        if (subtasks.get(subtaskId) != null) {
            historyManager.remove(subtaskId);
            sortedTasks.remove(subtasks.get(subtaskId));
            Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
            epic.getSubtasksInEpic().remove(subtasks.get(subtaskId));
            epic.setStatus(checkEpicStatus(epic));
            checkEpicStartAndEndTime(epic);
            subtasks.remove(subtaskId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteEpics(Integer epicId) {
        Epic epic = epics.get(epicId);
        for (Subtask subtaskToDelete : getSubtasksByEpic(epicId)) {
            subtasks.remove(subtaskToDelete.getId());
            sortedTasks.remove(subtasks.get(subtaskToDelete.getId()));
            historyManager.remove(subtaskToDelete.getId());
        }
        (epics.get(epic.getId()).getSubtasksInEpic()).clear();
        historyManager.remove(epicId);
        return epics.remove(epicId) != null;
    }

    //Удаление всех задач, подзадач и эпиков
    @Override
    public void deleteAllTasks() {
        sortedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().stream().map(s -> (Task) s).toList().forEach(sortedTasks::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasksInEpic().clear();
            epic.setStatus(Status.NEW);
        });
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    public boolean isIntersect(Task addedTask) {
        List<Task> sortedTaskList = new ArrayList<>(getPrioritizedTasks());
        if (sortedTaskList.isEmpty()) {
            return false;
        }
        for (int i = 1; i < sortedTaskList.size(); i++) {
            if (addedTask.getEndTime().isBefore(sortedTaskList.get(i).getStartTime()) &&
                    addedTask.getStartTime().isAfter(sortedTaskList.get(i - 1).getEndTime())) {
                return false;
            }
        }
        if (addedTask.getEndTime().isBefore(sortedTaskList.getFirst().getStartTime()) ||
                addedTask.getStartTime().isAfter(sortedTaskList.getLast().getEndTime())) {
            return false;
        }
        return true;
    }

    private void validateAndAddToSorted(Task addedTask, Task existTask) {
        if (addedTask.getStartTime() == null || addedTask.getEndTime() == null) {
            return;
        }
        if (existTask != null) {
            sortedTasks.remove(existTask);
        }
        if (isIntersect(addedTask)) {
            throw new InvalidTaskException("Задача пересекается по времени с другой задачей");
        }
        sortedTasks.add(addedTask);
    }

    //Генератор id
    public int getNextId() {
        return nextId++;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }
}
