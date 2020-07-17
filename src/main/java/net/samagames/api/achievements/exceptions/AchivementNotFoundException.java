package net.samagames.api.achievements.exceptions;

public class AchivementNotFoundException extends Exception {
    public AchivementNotFoundException() {
        super();
    }

    public AchivementNotFoundException(String message) {
        super(message);
    }

    public AchivementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AchivementNotFoundException(Throwable cause) {
        super(cause);
    }
}
