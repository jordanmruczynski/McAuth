package pl.jordii.mcauth.bungee.util;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesManager;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import pl.jordii.mcauth.bungee.database.PlayerSyncDatabase;
import pl.jordii.mcauth.bungee.database.SectorDatabase;
import pl.jordii.mcauth.bungee.database.model.SimpleSector;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SendPlayerToLogoutServer {

    private final AuthProxyConfig config;
    private final McAuthBungee plugin;
    private final LoadBalancer loadBalancer;
    private final PlayerSyncDatabase playerSyncDatabase;
    private final SectorDatabase sectorDatabase;

    @Inject
    public SendPlayerToLogoutServer(AuthProxyConfig config,
                                    McAuthBungee plugin,
                                    LoadBalancer loadBalancer,
                                    PlayerSyncDatabase playerSyncDatabase,
                                    SectorDatabase sectorDatabase) {
        this.config = config;
        this.plugin = plugin;
        this.loadBalancer = loadBalancer;
        this.playerSyncDatabase = playerSyncDatabase;
        this.sectorDatabase = sectorDatabase;
    }

    public void sendPlayer(ProxiedPlayer player, ServerConnectEvent event) {
        if (config.shouldConnectSectors()) {
            playerSyncDatabase.getPlayerLogoutType(player.getUniqueId().toString(), playerLocation -> {
                if (playerLocation.getLogoutType().equalsIgnoreCase("channel")) {
                    if (event != null) {
                        event.setTarget(loadBalancer.getBestLobbyServer());
                    } else {
                        player.connect(loadBalancer.getBestLobbyServer());
                    }
                } else if (playerLocation.getLogoutType().equalsIgnoreCase("sector")) {
                    System.out.println("player loc info");
                    System.out.println(playerLocation.getLogoutType());
                    System.out.println(playerLocation.getSectorX());
                    System.out.println(playerLocation.getSectorZ());
                    for (SimpleSector sector : sectorDatabase.getSectors()) {
                        System.out.println("iterating sector " + sector.getId());
                        if (sector.contains(playerLocation.getSectorX(), playerLocation.getSectorZ())) {
                            ServerInfo serverInfo = plugin.getProxy().getServerInfo("sector-" + sector.getId());
                            if (event != null) {
                                event.setTarget(serverInfo);
                            } else {
                                player.connect(serverInfo);
                            }
                            return;
                        }
                    }
                    System.out.println("no sector found, teleporting to lobby");
                    if (event != null) {
                        event.setTarget(loadBalancer.getBestLobbyServer());
                    } else {
                        player.connect(loadBalancer.getBestLobbyServer());
                    }
                } else if (playerLocation.getLogoutType().equalsIgnoreCase("pvp")) {

                }
            });
        } else {
            if (event != null) {
//                event.getTarget().ping((result, error) -> {
//                    if(error!=null){
//                        player.disconnect(AuthMessagesManager.sendMessage(AuthMessagesBungee.OFFLINE_TARGET_SERVER));
//                        return;
//                    }
//                });
                event.setTarget(loadBalancer.getBestLobbyServer());
            } else {
                player.connect(loadBalancer.getBestLobbyServer());
            }
        }
    }

    public void sendPlayer(ProxiedPlayer player) {
        this.sendPlayer(player, null);
    }

}
