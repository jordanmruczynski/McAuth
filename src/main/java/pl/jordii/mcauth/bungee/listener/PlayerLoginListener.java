package pl.jordii.mcauth.bungee.listener;

import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesManager;
import pl.jordii.mcauth.bungee.util.AuthUserCache;
import pl.jordii.mcauth.bungee.util.TextUtil;
import pl.jordii.mcauth.bungee.util.LoadBalancer;
import pl.jordii.mcauth.bungee.util.AuthRequest;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerLoginListener implements Listener {

    private final McAuthBungee plugin;
    private final AuthRequest authRequest;
    private final LoadBalancer loadBalancer;
    private final AuthUserCache userCache;

    @Inject
    public PlayerLoginListener(McAuthBungee plugin,
                               AuthRequest authRequest,
                               LoadBalancer loadBalancer,
                               AuthUserCache userCache) {
        this.plugin = plugin;
        this.authRequest = authRequest;
        this.loadBalancer = loadBalancer;
        this.userCache = userCache;
    }

    @EventHandler
    public void handlePlayerLogin(PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        String nickName = connection.getName();

        // if auth and premium servers haven't been pinged at least once, block players from joining
        // this is only a simple security measurement and will only occur a few seconds after each
        // proxy startup
        if (!this.loadBalancer.serversInitialized()) {
            BaseComponent[] message = TextUtil.newComponent(AuthMessagesManager.sendMessage2(AuthMessagesBungee.AUTH_SERVERS_STILL_LOADING));
            connection.disconnect(message);
            event.setCancelReason(message);
            event.setCancelled(true);
        }

        if (plugin.getProxy().getPlayer(nickName) != null) {
            BaseComponent[] message = TextUtil.newComponent(AuthMessagesManager.sendMessage2(AuthMessagesBungee.PLAYER_ALREADY_ONLINE));
            connection.disconnect(message);
            event.setCancelReason(message);
            event.setCancelled(true);
            return;
        }

        if (!nickName.matches("[a-zA-Z0-9_]{3,16}")) {
            BaseComponent[] message = TextUtil.newComponent(
                    "§cYour nickname is invalid! It has to fulfil the following criteria§8:",
                    "§8- §7A minimum length of 3 characters",
                    "§8- §7A maximum length of 16 characters",
                    "§8- §7Only letters, numbers, underscores",
                    "§8- §7No other special characters",
                    "§8McAuth software by Jordan \"Jordii\" Mruczynski"
            );
            connection.disconnect(message);
            event.setCancelReason(message);
            event.setCancelled(true);
        }

        // check if event is cancelled by any other plugin
        if (event.isCancelled()) {
            return;
        }

        event.registerIntent(this.plugin);

        this.authRequest.request(nickName, response -> {
            if (response.isPremium()) {
                connection.setOnlineMode(true);
            } else {
                connection.setOnlineMode(false);
            }

            userCache.handshake(nickName, response);
            event.completeIntent(this.plugin);
        });

    }

}
