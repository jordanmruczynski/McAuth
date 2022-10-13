package pl.jordii.mcauth.spigot.database.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.UserRepository;
import pl.jordii.mcauth.spigot.database.dao.RegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.model.RegisteredUser;
import pl.jordii.mcauth.spigot.messenger.SpigotMessenger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginCommand implements CommandExecutor {

    private UserRepository userRepository;
    private RegisteredUserDatabase userDatabase;
    private SpigotMessenger spigotMessenger;
    String titleMain = AuthMessagesManager.sendMessage(AuthMessages.TITLES_TITLE_MAIN);

    @Inject
    public LoginCommand(UserRepository userRepository, RegisteredUserDatabase userDatabase, SpigotMessenger spigotMessenger) {
        this.userRepository = userRepository;
        this.userDatabase = userDatabase;
        this.spigotMessenger = spigotMessenger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.PLAYER_SENDER_NEEDED));
            return false;
        }

        Player player = (Player) sender;

        if (userRepository.getRegistering().contains(player.getUniqueId())) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.PLAYER_NOT_REGISTERED_FIRSTLY));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_LOGIN_COMMAND_USAGE));
            return false;
        }

        String pass1 = args[0];

        RegisteredUser user = userRepository.getUserCache().get(player.getUniqueId());
        if (BCrypt.checkpw(pass1, user.getHashedPassword())) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.CORRECT_PASSWORD));
            player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_CORRECTPASSWORD));
            spigotMessenger.movePlayerToLobby(player);
            Bukkit.getScheduler().runTaskLater(McAuthSpigot.getProvidingPlugin(McAuthSpigot.class), () -> {
                if (player.isOnline()) {
                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.OFFLINE_TARGET_SERVER));
                }
            }, 30L);
        } else {
            player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_PASSWORD));
        }

        return false;
    }
}
