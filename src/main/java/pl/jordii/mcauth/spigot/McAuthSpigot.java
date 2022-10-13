package pl.jordii.mcauth.spigot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.bukkit.plugin.PluginManager;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import pl.jordii.mcauth.bungee.util.LoadBalancer;
import pl.jordii.mcauth.common.database.MySQL;
import pl.jordii.mcauth.spigot.config.AuthConfig;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.commands.LoginCommand;
import pl.jordii.mcauth.spigot.database.commands.RegisterCommand;
import pl.jordii.mcauth.spigot.database.dao.RegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.mysql.MyRegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.mysql.SpigotSqlCredentials;
import pl.jordii.mcauth.common.inject.SpigotBinderModule;
import pl.jordii.mcauth.spigot.listener.EventHandlerRegistration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pl.jordii.mcauth.spigot.util.Queue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public final class McAuthSpigot extends JavaPlugin {

    private static Injector injector;

    @Override
    public void onEnable() {

        SpigotBinderModule spigotBinderModule = new SpigotBinderModule(this);
        injector = Guice.createInjector(spigotBinderModule);
        injector.injectMembers(this);

        try {
            getInstance(AuthMessagesManager.class).load();
            //getInstance(AuthConfig.class).loadValues();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        injector.getInstance(SpigotSqlCredentials.class).load();
        boolean connected = injector.getInstance(MySQL.class).connect();

        if (!connected) {
            Bukkit.getConsoleSender().sendMessage("§cTHERE WAS A FATAL ERROR WHEN STARTING UP MC AUTH SYSTEM 2.1!");
            System.out.println("Connection to the database was unsuccessful. Please check the following:");
            System.out.println("- Is your database online and accessible from this host?");
            System.out.println("- Is the access to your database restricted by any firewall, DDos protection?");
            System.out.println("- Are your credentials correct? Check the mysql.yml file again.");
            System.out.println("- Eventually contact your system administrator or plugin author Jordii#7622");
            System.out.println("Cannot start up auth system. Shutting down the server...");
            Bukkit.getConsoleSender().sendMessage("§c------------------------------------------------");
            Bukkit.shutdown();
            return;
        }

        //getInstance(Queue.class).schedule();

        try {
            getInstance(MyRegisteredUserDatabase.class).prepare();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getInstance(EventHandlerRegistration.class).initialize("pl.jordii.mcauth");

        getCommand("login").setExecutor(getInstance(LoginCommand.class));
        getCommand("register").setExecutor(getInstance(RegisterCommand.class));

    }

    @Override
    public void onDisable() {
        getInstance(MySQL.class).disconnect();
        //getInstance(Queue.class).stopScheduler();
    }

    public static <T> T getInstance(Class<T> tClass) {
        return injector.getInstance(tClass);
    }

    public static Injector getInjector() {
        return injector;
    }

    public static String getPluginName() {
        return getInstance(McAuthSpigot.class).getDescription().getName();
    }

}
