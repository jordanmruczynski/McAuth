package pl.jordii.mcauth.spigot.antibot.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.antibot.events.GuiClickFailureEvent;
import pl.jordii.mcauth.spigot.antibot.events.GuiClickSuccessEvent;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.util.Packets;
import pl.jordii.mcauth.spigot.util.ServerMainThread;

public class GuiClickHandler implements Listener {

    @EventHandler
    public void onGuiClickSuccess(GuiClickSuccessEvent event) {
        Player player = event.getPlayer();
        //player.kickPlayer("gui ok");
//        if (player != null) {
//            player.kickPlayer("gui ok");
//        }
        new BukkitRunnable() {
            @Override
            public void run() {
                this.cancel();
                ServerMainThread.RunParallel.run(() -> {
                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.GUI_SUCCESS));
                });
            }
        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 5L);
    }

    @EventHandler
    public void onGuiClickFail(GuiClickFailureEvent event) {
        Player player = event.getPlayer();
        //player.kickPlayer("gui nie ok");
//        if (player != null) {
//            player.kickPlayer("gui nie ok");
//        }
        new BukkitRunnable() {
            @Override
            public void run() {
                this.cancel();
                ServerMainThread.RunParallel.run(() -> {
                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.GUI_KICK));
                });
            }
        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 5L);
    }
}
