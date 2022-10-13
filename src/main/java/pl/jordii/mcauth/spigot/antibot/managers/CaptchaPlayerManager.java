package pl.jordii.mcauth.spigot.antibot.managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CaptchaPlayerManager {

    private final List<CaptchaPlayer> playerList = new CopyOnWriteArrayList<>();

    public void add(CaptchaPlayer player) {
        playerList.add(player);
    }

    public void remove(CaptchaPlayer player) {
        playerList.remove(player);
    }

    @Nullable
    public CaptchaPlayer getPlayer(Player player) {
        return playerList.stream()
                .filter(p -> p.getPlayer().getUniqueId() == Objects.requireNonNull(player.getPlayer()).getUniqueId())
                .findFirst()
                .orElse(null);
    }
}