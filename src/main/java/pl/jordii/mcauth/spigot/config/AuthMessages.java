package pl.jordii.mcauth.spigot.config;

import pl.jordii.mcauth.bungee.config.AuthMessagesBungee;

public enum AuthMessages {
    REGISTER_NONPREMIUM("Messages.register-nonpremium", "§7We detected you have a non-premium account.\n§6Please choose a secure password to register yourself and type it in the chat now."),
    WELCOME_BACK("Messages.welcomeback", "§aWelcome back on the server!"),
    PASSWORD_TYPE("Messages.password-type", "§7We detected a cracked account, please type your password in the chat now."),
    INCORRECT_SECOND_PASSWORD("Messages.incorrect-second-password", "§cThe two passwords do not match. Please try again."),
    CREATE_ACCOUNT("Messages.create-account", "§aSuccessfully created an account. Have fun!"),
    REPEAT_PASSWORD("Messages.repeat-password", "§7Please repeat your password."),
    CORRECT_PASSWORD("Messages.correct-password", "§aCorrect login data! You are being sent to the server or queue..."),
    INCORRECT_PASSWORD("Messages.incorrect-password", "§cWrong password. Please try again."),
    PLAYER_SENDER_NEEDED("Messages.player-sender-needed", "§cYou must be a player to do that."),
    PLAYER_ALREADY_REGISTERED("Messages.player-already-registered", "§cYou are already registered, use /login command"),
    INCORRECT_REGISTER_COMMAND_USAGE("Messages.incorrect-register-command-usage", "§cUsage: /register <pass> <pass>"),
    PLAYER_NOT_REGISTERED_FIRSTLY("Messages.player-not-registered-firstly", "§cYou are not registered, please use /register command"),
    INCORRECT_LOGIN_COMMAND_USAGE("Messages.incorrect-login-command-usage", "§cUsage: /login <pass>"),
    OFFLINE_TARGET_SERVER("Messages.offline-server-target", "§cTarget server is actually offline. \n§bPlease try again later"),
    CAPTCHA_SUCCESS("Messages.antibot.map.captcha-success", "§cCaptcha ok"),
    CAPTCHA_KICK("Messages.antibot.map.captcha-kick", "§cCaptcha incorrect, please try again."),
    CAPTCHA_TRIES("Messages.antibot.map.captcha-tries", "§cIncorrect! Tries left: {TRIES}"),
    CAPTCHA_MAP_TITLE("Messages.antibot.map.captcha-map-title", "McAuth"),
    CAPTCHA_MAP_ITEM_LORE("Messages.antibot.map.captcha-map-item-lore", "§7Please open map to get the code."),
    GUI_SUCCESS("Messages.antibot.gui.success", "§aSuccessfully logged in!"),
    GUI_KICK("Messages.antibot.gui.kick", "§cWrong captcha, please try again."),
    VOID_CHECK_ACTIONBAR_CHECKING("Messages.antibot.void.actionbar-checking", "§6MCAUTH ANTI-BOT §7| §eCHECKING..."),
    VOID_CHECK_ACTIONBAR_SUCCESS("Messages.antibot.void.check-actionbar-success", "§6MCAUTH ANTI-BOT §7| §aSUCCESS."),
    VOID_CHECK_ACTIONBAR_FAILURE("Messages.antibot.void.check-actionbar-failure", "§6MCAUTH ANTI-BOT §7| §cFAILURE."),
    VOID_CHECK_KICK_SUCCESS("Messages.antibot.void.check-kick-success", "§aYour account has been verified. \n§7Server access: §aOK"),
    VOID_CHECK_KICK_FAILURE("Messages.antibot.void.check-kick-failure", "§cYour account has been detected as bot. \n§7Server access: §cDENY"),
    TITLES_TITLE_MAIN("Messages.titles.title.main", "YourServer.com"),
    TITLES_SUBTITLE_REGISTER("Messages.titles.subtitles.register", "Please register yourself"),
    TITLES_SUBTITLE_PASSWORD("Messages.titles.subtitles.password", "Please enter your password"),
    TITLES_SUBTITLE_CREATEACCOUNT("Messages.titles.subtitles.createaccount", "Correctly created account"),
    TITLES_SUBTITLE_REPEATPASSWORD("Messages.titles.subtitles.repeatpassword", "Please repeat your password"),
    TITLES_SUBTITLE_CORRECTPASSWORD("Messages.titles.subtitles.correctpassword", "Password is correctly!");

    private String path;
    private String defaultMessage;

    AuthMessages(String path, String defaultMessage) {
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
