package pl.jordii.mcauth.common;

public enum MessageChannel {

    AUTH_FINISHED("auth_finished"),
    MOVE_SECTOR("auth_move_sector")

    ;

    private String subChannel;

    MessageChannel(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getSubChannel() {
        return subChannel;
    }

}
