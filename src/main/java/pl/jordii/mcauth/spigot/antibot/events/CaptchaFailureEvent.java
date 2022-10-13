package pl.jordii.mcauth.spigot.antibot.events;


import org.jetbrains.annotations.NotNull;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CaptchaFailureEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final CaptchaPlayer player;

    public CaptchaFailureEvent(CaptchaPlayer player) {
        this.player = player;
    }

    public CaptchaPlayer getPlayer() {
        return player;
    }

    @Override
    public @NotNull
    HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
