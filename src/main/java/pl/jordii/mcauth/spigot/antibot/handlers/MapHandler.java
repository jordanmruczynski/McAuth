package pl.jordii.mcauth.spigot.antibot.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.database.UserRepository;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class MapHandler implements Listener {

    private final UserRepository mapcha;

    @Inject
    public MapHandler(UserRepository mapcha) {
        this.mapcha = mapcha;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMapInitialize(MapInitializeEvent event) {
        MapView map = event.getMap();
        List<MapRenderer> old = map.getRenderers();

        map.setScale(MapView.Scale.NORMAL);
        map.getRenderers().forEach(map::removeRenderer);

        map.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
                CaptchaPlayer p = mapcha.getPlayerManager().getPlayer(player);
                if (p == null) {
                    Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> old.forEach(map::addRenderer));
                } else {
                    mapCanvas.drawImage(0, 0, p.render());
                }
            }
        });
    }
}
