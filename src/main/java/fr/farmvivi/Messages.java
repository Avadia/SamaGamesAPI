package fr.farmvivi;

public enum Messages {
    HUB("hub"),
    GRAOU("Graou"),
    PIECE("pièce"),
    PIECES("pièces"),
    PERLE("perle"),
    VIP("VIP"),
    POWERUP("bonus");

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
