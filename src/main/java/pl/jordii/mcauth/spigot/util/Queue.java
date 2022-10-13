package pl.jordii.mcauth.spigot.util;

import org.bukkit.entity.Player;
import pl.jordii.mcauth.spigot.config.AuthConfig;
import pl.jordii.mcauth.spigot.config.AuthSettings;
import pl.jordii.mcauth.spigot.messenger.SpigotMessenger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class Queue {

    private final ScheduledExecutorService scheduledExecutorService;
    public static List<Player> queuePlayers = new ArrayList<>();
    private final SpigotMessenger spigotMessenger;

    @Inject
    public Queue(SpigotMessenger spigotMessenger) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);;
        this.spigotMessenger = spigotMessenger;
    }

    public void schedule() {
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (queuePlayers.size() > 0) {
//                Packets.sendTitle(queuePlayers.get(0), "§b§lMC AUTH", "§aYou have been moved to the lobby", 0, 20, 0);
//                spigotMessenger.movePlayerToLobby(queuePlayers.get(0));
//                queuePlayers.remove(0);
                for (Player player : queuePlayers) {
                    player.sendTitle("§b§lMC AUTH", "§fYour queue position §a" + (queuePlayers.indexOf(player)+1) + " §7 ┃ §2" + queuePlayers.size());
                    //Packets.sendActionBar(player, "§b§lMC AUTH §7⇒ §fYour queue position §a" + (queuePlayers.indexOf(player)+1) + " §7┃ §2" + queuePlayers.size() + " §fEstimated time: §7" + getTimeFormatted(queuePlayers.size()*2));
                    //Packets.sendTitle(player, "§b§lMC AUTH", "§fYour queue position §a" + (queuePlayers.indexOf(player)+1) + " §7 ┃ §2" + queuePlayers.size(), 0, 20, 0);
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void stopScheduler() {
        this.scheduledExecutorService.shutdownNow();
    }

    //create method to get time formatted with seconds as parameter
    public static String getTimeFormatted(int seconds) {
        int hours = seconds / 3600;
        int remainder = seconds - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        String time = "";
        if (hours > 0) {
            time = hours + "h ";
        }
        if (mins > 0) {
            time = time + mins + "m ";
        }
        if (secs > 0) {
            time = time + secs + "s";
        }
        return time;
    }
}
