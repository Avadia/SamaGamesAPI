package net.samagames.api.exceptions;

import net.samagames.api.SamaGamesAPI;

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
        SamaGamesAPI.get().getPlugin().getLogger().warning(getMessage());
        //super.printStackTrace();
    }
}
