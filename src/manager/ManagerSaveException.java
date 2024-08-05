package manager;

public class ManagerSaveException extends RuntimeException {
    private static final String MESSAGE_SAVE = "Произошла ошибка при сохранении файла";
    private static final String MESSAGE_LOAD = "Произошла ошибка при загрузке файла";

    private ManagerSaveException(String message, Exception e) {
        super(message, e);
    }

    public static ManagerSaveException saveException(Exception e) {
        return new ManagerSaveException(MESSAGE_SAVE, e);
    }

    public static ManagerSaveException loadException(Exception e) {
        return new ManagerSaveException(MESSAGE_LOAD, e);
    }
}
