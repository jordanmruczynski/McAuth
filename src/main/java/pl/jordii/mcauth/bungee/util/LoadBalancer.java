package pl.jordii.mcauth.bungee.util;

import com.google.common.collect.Sets;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import net.md_5.bungee.api.config.ServerInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Singleton
public class LoadBalancer {

    private ServerInfo bestAuthServer = null;
    private ServerInfo bestLobbyServer = null;
    private final Set<String> availableLobbyServers = Sets.newHashSet();
    private final Set<String> availableAuthServers = Sets.newHashSet();

    private boolean initializedServers = false;

    private final ScheduledExecutorService scheduledExecutorService;

    private final AuthProxyConfig authProxyConfig;
    private final McAuthBungee plugin;

    @Inject
    public LoadBalancer(AuthProxyConfig authProxyConfig, McAuthBungee plugin) {
        this.authProxyConfig = authProxyConfig;
        this.plugin = plugin;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public void startBalancer() {
        pingServers();
        this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            pingServers();

            if (!availableLobbyServers.isEmpty()) {
                ServerInfo emptiestLobbyName = null;
                int emptiestServerCount = 999_999;
                for (String lobbyServer : availableLobbyServers) {
                    ServerInfo info = plugin.getProxy().getServerInfo(lobbyServer);
                    if (emptiestServerCount > info.getPlayers().size()) {
                        emptiestServerCount = info.getPlayers().size();
                        emptiestLobbyName = info;
                    }
                }
                //plugin.getLogger().log(Level.INFO, "Changed default lobby server to " + emptiestLobbyName);
                bestLobbyServer = emptiestLobbyName;
            }

            if (!availableAuthServers.isEmpty()) {
                ServerInfo emptiestServerName = null;
                int emptiestServerCount = 999_999;
                for (String authServer : availableAuthServers) {
                    ServerInfo info = plugin.getProxy().getServerInfo(authServer);
                    if (emptiestServerCount > info.getPlayers().size()) {
                        emptiestServerCount = info.getPlayers().size();
                        emptiestServerName = info;
                    }
                }
                //plugin.getLogger().log(Level.INFO, "Changed default auth server to " + emptiestServerName);
                bestAuthServer = emptiestServerName;
            }

            initializedServers = true;
        }, 2000, 5_000, TimeUnit.MILLISECONDS);
    }

    public void stopBalancer() {
        this.scheduledExecutorService.shutdownNow();
    }

    public boolean serversInitialized() {
        return initializedServers;
    }

    public ServerInfo getBestAuthServer() {
        return bestAuthServer;
    }

    public ServerInfo getBestLobbyServer() {
        return bestLobbyServer;
    }

    private void pingServers() {
        authProxyConfig.getAuthServers().forEach(currentServer -> {
            ServerInfo serverInfo = this.plugin.getProxy().getServerInfo(currentServer);
            serverInfo.ping((result, error) -> {
                if (error != null) {
                    availableAuthServers.remove(currentServer);
                } else {
                    availableAuthServers.add(currentServer);
                }
            });
        });

        authProxyConfig.getLobbyServers().forEach(currentServer -> {
            ServerInfo serverInfo = this.plugin.getProxy().getServerInfo(currentServer);
            serverInfo.ping((result, error) -> {
                if (error != null) {
                    availableLobbyServers.remove(currentServer);
                } else {
                    availableLobbyServers.add(currentServer);
                }
            });
        });
    }

}
