package net.samagames.api.exceptions;

public class DataNotFoundException extends Exception {
    public DataNotFoundException() {
        super();
    }

    public DataNotFoundException(String message) {
        super(message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataNotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public void printStackTrace() {
        System.out.println("WARN: " + getMessage());
        //super.printStackTrace();
    }
}
