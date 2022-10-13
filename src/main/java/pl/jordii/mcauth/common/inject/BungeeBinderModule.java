package pl.jordii.mcauth.common.inject;

import com.google.inject.AbstractModule;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.BungeeSectorsSQLCredentials;
import pl.jordii.mcauth.bungee.database.*;
import pl.jordii.mcauth.common.database.MySQLCredentials;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BungeeBinderModule extends AbstractModule {

    private final McAuthBungee pluginClass;

    public BungeeBinderModule(McAuthBungee pluginClass) {
        this.pluginClass = pluginClass;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(this.pluginClass);
        bind(McAuthBungee.class).toInstance(this.pluginClass);
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
        bind(MySQLCredentials.class).to(BungeeSectorsSQLCredentials.class);
        bind(TicketDatabase.class).to(MyTicketsDatabase.class);
        bind(PlayerSyncDatabase.class).to(MyPlayerSyncDatabase.class);
        bind(SectorDatabase.class).to(MySectorDatabase.class);
    }
}
