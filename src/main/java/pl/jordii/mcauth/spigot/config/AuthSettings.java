package pl.jordii.mcauth.spigot.config;

public enum AuthSettings {

    QUEUE_STATUS("queue.enable"),
    QUEUE_DELAY("queue.delay"),
    QUEUE_PLAYERS_PER_MOVE("queue.howManyPlayersShouldBeMoved"),
    ANTIBOT_GUICLICK_STATUS("antibot.guiClick.enable"),
    ANTIBOT_MAPCAPTCHA_STATUS("antibot.mapCaptcha.enable"),
    ANTIBOT_VOIDCHECK_STATUS("antibot.voidCheck.enable");

    private final String configPath;

    AuthSettings(String path) {
        this.configPath = path;
    }

    public String getConfigPath() {
        return configPath;
    }

}
