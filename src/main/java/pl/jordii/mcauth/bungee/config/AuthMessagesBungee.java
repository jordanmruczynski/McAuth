package pl.jordii.mcauth.bungee.config;

import pl.jordii.mcauth.spigot.config.AuthMessages;

public enum AuthMessagesBungee {
    NOTICKET("Messages.noticket", "§7We detected you don't have a ticket.\n§6You can purchase it on www.yoursite.net"),
    TICKET("Messages.ticket", "§aYour ticket is correctly, click me to get more info."),
    GIVETICKET("Messages.give-ticket", "§aYou added a ticket."),
    REMOVETICKET("Messages.remove-ticket", "§cYou removed a ticket."),
    TICKETHOVER("Messages.tickethover", "§6Time left: §e{TIME_LEFT}"),
    TICKETUSEREXISTS("Messages.ticket-user-exists", "§cUser already has a ticket!"),
    TICKETUSERDOESNTEXISTS("Messages.ticket-user-doesnt-exists", "§cUser doesnt exists in database!"),
    PREMIUM_WELCOME("Messages.premiumwelcome", "hello premium account player"),
    PLAYER_ALREADY_ONLINE("Messages.already-online", "§cAnother player with this nickname is already online!"),
    AUTH_SERVERS_STILL_LOADING("Messages.auth-servers-loading", "§cThe server is not yet ready for players to join."),
    OFFLINE_TARGET_SERVER("Messages.offline-target-server", "§cTarget server is actually offline, please try again later.");

    private String path;
    private String defaultMessage;

    AuthMessagesBungee(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String getPath() {
        return path;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
