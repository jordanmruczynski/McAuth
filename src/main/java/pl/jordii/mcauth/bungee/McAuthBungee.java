package pl.jordii.mcauth.bungee;

import com.google.inject.Guice;
import com.google.inject.Injector;
import pl.jordii.mcauth.bungee.commands.McAuthCommand;
import pl.jordii.mcauth.bungee.config.AuthMessagesManager;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import pl.jordii.mcauth.bungee.config.BungeeSectorsSQLCredentials;
import pl.jordii.mcauth.bungee.config.BungeeTicketsSQLCredentials;
import pl.jordii.mcauth.bungee.database.PlayerSyncDatabase;
import pl.jordii.mcauth.bungee.database.SectorDatabase;
import pl.jordii.mcauth.bungee.database.TicketDatabase;
import pl.jordii.mcauth.bungee.listener.EventHandlerRegistration;
import pl.jordii.mcauth.bungee.util.LoadBalancer;
import pl.jordii.mcauth.common.database.MySQL;
import pl.jordii.mcauth.common.inject.BungeeBinderModule;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

public class McAuthBungee extends Plugin {

    private static Injector injector;

    @Override
    public void onEnable() {
        BungeeBinderModule bungeeBinderModule = new BungeeBinderModule(this);
        injector = Guice.createInjector(bungeeBinderModule);
        injector.injectMembers(this);
        ProxyServer.getInstance().registerChannel("mcauth");

        try {
            getInstance(AuthMessagesManager.class).loadFile();
            getInstance(AuthProxyConfig.class).loadFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        boolean shouldUseMysql = false;

        try {
            if (getInstance(AuthProxyConfig.class).shouldConnectTickets()) {
                shouldUseMysql = true;
                if (!injector.getInstance(MySQL.class).isConnected()) {
                    injector.getInstance(MySQL.class).connect();
                }
                getInstance(BungeeTicketsSQLCredentials.class).load();
                getInstance(TicketDatabase.class).prepare();
                getInstance(TicketDatabase.class).loadTickets();
                this.getLogger().log(Level.INFO, "MC-AUTH => Tickets has been loaded.");
            }
            if (getInstance(AuthProxyConfig.class).shouldConnectSectors()) {
                shouldUseMysql = true;
                if (!injector.getInstance(MySQL.class).isConnected()) {
                    injector.getInstance(MySQL.class).connect();
                }
                getInstance(BungeeSectorsSQLCredentials.class).load();
                getInstance(SectorDatabase.class).prepare();
                getInstance(PlayerSyncDatabase.class).prepare();
                this.getLogger().log(Level.INFO, "MC-AUTH => Sectors has been loaded.");
            }

        } catch (SQLException exception) {
            this.getLogger().log(Level.SEVERE, "THERE WAS A FATAL ERROR WHEN STARTING UP MC AUTH SYSTEM 2.0!");
            this.getLogger().log(Level.SEVERE, "Connection to the database was unsuccessful. Please check the following:");
            this.getLogger().log(Level.SEVERE, "- Is your database online and accessible from this host?");
            this.getLogger().log(Level.SEVERE, "- Is the access to your database restricted by any firewall, DDos protection?");
            this.getLogger().log(Level.SEVERE, "- Are your credentials correct? Check the mysql.yml file again.");
            this.getLogger().log(Level.SEVERE, "- Eventually contact your system administrator or plugin author Jordii#7622");
            this.getLogger().log(Level.SEVERE, "Cannot start up auth system.");
            this.getLogger().log(Level.SEVERE, "------------------------------------------------");
            exception.printStackTrace();
        }

        getProxy().getPluginManager().registerCommand(this, new McAuthCommand());
        getInstance(EventHandlerRegistration.class).initialize("pl.jordii.mcauth.bungee");
        getInstance(LoadBalancer.class).startBalancer();

    }

    @Override
    public void onDisable() {
        getInstance(LoadBalancer.class).stopBalancer();
    }

    public static Injector getInjector() {
        return injector;
    }

    public static <T> T getInstance(Class<T> tClass) {
        return injector.getInstance(tClass);
    }

    public static ProxyServer getProxyServer() {
        return getInstance(McAuthBungee.class).getProxy();
    }

}
