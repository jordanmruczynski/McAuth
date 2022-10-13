package pl.jordii.mcauth.spigot.antibot.handlers;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.antibot.events.AntibotFailureEvent;
import pl.jordii.mcauth.spigot.antibot.events.AntibotSuccessEvent;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaFailureEvent;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaSuccessEvent;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.UserRepository;
import pl.jordii.mcauth.spigot.util.Packets;
import pl.jordii.mcauth.spigot.util.ServerMainThread;

import javax.inject.Inject;
import java.awt.*;

public class AntibotHandler implements Listener {
    private final UserRepository userRepository;

    @Inject
    public AntibotHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void onAntibotSuccess(AntibotSuccessEvent event) {
        Player player = event.getPlayer();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(String.valueOf(AuthMessages.VOID_CHECK_ACTIONBAR_SUCCESS)));
       // Packets.sendActionBar(player, AuthMessagesManager.sendMessage(AuthMessages.VOID_CHECK_ACTIONBAR_SUCCESS));
        //Packets.sendActionBar(player, "§6MCAUTH ANTI-BOT §7| §aSUCCESS");
        new BukkitRunnable() {

            @Override
            public void run() {
                this.cancel();
                ServerMainThread.RunParallel.run(() -> {
                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.VOID_CHECK_KICK_SUCCESS));
                    //player.kickPlayer(AuthMessages..getMessage(AuthMessages.ANTIBOT_SUCCESS));
                   // player.kickPlayer("§aYour account has been verified. \n§7Server access: §aOK");
                    //player.kickPlayer(AuthMessages.INCORRECT_PASSWORD.getDefaultMessage());
                });
            }

        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 20);
    }

    @EventHandler
    public void onAntibotFail(AntibotFailureEvent event) {
        Player player = event.getPlayer();
        System.out.println("[MCAUTH ANTI-BOT] " + player.getName() + " has been detected as bot by void check.");
        //Packets.sendActionBar(player, "§6MCAUTH ANTI-BOT §7| §cFAILURE");
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(String.valueOf(AuthMessages.VOID_CHECK_ACTIONBAR_FAILURE)));

        //Packets.sendActionBar(player, AuthMessagesManager.sendMessage(AuthMessages.VOID_CHECK_ACTIONBAR_FAILURE));
        new BukkitRunnable() {

            @Override
            public void run() {
                this.cancel();
                ServerMainThread.RunParallel.run(() -> {
                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.VOID_CHECK_KICK_FAILURE));
                    //player.kickPlayer("§cYour account has been detected as bot. \n§7Server access: §cDENY");
                    //player.kickPlayer(AuthMessages.INCORRECT_PASSWORD.getDefaultMessage());
                });
            }

        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 20);
    }
}
