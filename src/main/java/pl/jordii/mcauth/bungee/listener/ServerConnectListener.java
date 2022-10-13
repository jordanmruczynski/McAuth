package pl.jordii.mcauth.bungee.listener;

import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesManager;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import pl.jordii.mcauth.bungee.database.TicketDatabase;
import pl.jordii.mcauth.bungee.util.AuthUserCache;
import pl.jordii.mcauth.bungee.util.LoadBalancer;
import pl.jordii.mcauth.bungee.util.SendPlayerToLogoutServer;
import pl.jordii.mcauth.common.rest.PremiumAccountCheckResponse;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ServerConnectListener implements Listener {

    private final AuthUserCache userCache;
    private final SendPlayerToLogoutServer playerSender;
    private final LoadBalancer loadBalancer;
    private final AuthProxyConfig proxyConfig;

    @Inject
    public ServerConnectListener(AuthUserCache userCache,
                                 LoadBalancer loadBalancer,
                                 SendPlayerToLogoutServer playerSender, AuthProxyConfig proxyConfig) {
        this.userCache = userCache;
        this.playerSender = playerSender;
        this.loadBalancer = loadBalancer;
        this.proxyConfig = proxyConfig;
    }

    @EventHandler
    public void handleServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PremiumAccountCheckResponse userInfo = userCache.getInfo(player.getName());

        if (userInfo == null) {
            return;
        }

        userCache.forget(player.getName());
        if (userInfo.isPremium()) {
            playerSender.sendPlayer(player, event);
            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.PREMIUM_WELCOME));
        } else {
            if (!proxyConfig.shouldConnectTickets()) {
                event.setTarget(loadBalancer.getBestAuthServer());
            } else {
                McAuthBungee.getInstance(TicketDatabase.class).isExists(player.getName(), result -> {
                    if (!result) {
                        player.disconnect(AuthMessagesManager.sendMessage(AuthMessagesBungee.NOTICKET));
                    } else {
                        player.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.TICKET));
                        event.setTarget(loadBalancer.getBestAuthServer());
                    }
                });
            }
        }
    }
}
