package pl.jordii.mcauth.spigot.antibot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;

public class AntibotSuccessEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public AntibotSuccessEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
