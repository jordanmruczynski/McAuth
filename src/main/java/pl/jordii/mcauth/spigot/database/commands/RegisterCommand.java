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
public class RegisterCommand implements CommandExecutor {

    private UserRepository userRepository;
    private RegisteredUserDatabase userDatabase;
    private SpigotMessenger spigotMessenger;
    String titleMain = AuthMessagesManager.sendMessage(AuthMessages.TITLES_TITLE_MAIN);

    @Inject
    public RegisterCommand(UserRepository userRepository, RegisteredUserDatabase userDatabase, SpigotMessenger spigotMessenger) {
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

        if (!userRepository.getRegistering().contains(player.getUniqueId())) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.PLAYER_ALREADY_REGISTERED));
            //player.sendMessage("§cJestes juz zarejestrowany, zaloguj sie /login <haslo>");
            return false;
        }

        if (args.length != 2) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_REGISTER_COMMAND_USAGE));
            return false;
        }

        String pass1 = args[0];
        String pass2 = args[1];

        if (!pass2.equals(pass1)) {
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_SECOND_PASSWORD));
            return false;
        }

        String salt = BCrypt.gensalt();
        userDatabase.registerUser(new RegisteredUser(
                player.getUniqueId(),
                player.getName(),
                0>1,
                salt,
                BCrypt.hashpw(pass1, salt)
        ));
        player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.CREATE_ACCOUNT));
        player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_CREATEACCOUNT));
        spigotMessenger.movePlayerToLobby(player);
        Bukkit.getScheduler().runTaskLater(McAuthSpigot.getProvidingPlugin(McAuthSpigot.class), () -> {
            if (player.isOnline()) {
                player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.OFFLINE_TARGET_SERVER));
            }
        }, 30L);
        return true;
    }
}
