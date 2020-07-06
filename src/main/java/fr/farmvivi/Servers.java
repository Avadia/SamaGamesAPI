package fr.farmvivi;

public enum Servers {
    DEFAULT("avadia", "Avadia", "play.avadia.fr", "AvadiaMC"),
    SAMAGAMES("samagames", "SamaGames", "mc.samagames.net", "SamaGames_Mc");

    private final String name;
    private final String displayName;
    private final String ip;
    private final String twitter;

    Servers(String name, String displayName, String ip, String twitter) {
        this.name = name;
        this.displayName = displayName;
        this.ip = ip;
        this.twitter = twitter;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIp() {
        return ip;
    }

    public String getTwitter() {
        return twitter;
    }
}
