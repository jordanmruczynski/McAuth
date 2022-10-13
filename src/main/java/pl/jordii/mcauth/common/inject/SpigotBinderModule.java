package pl.jordii.mcauth.common.inject;

import com.google.inject.AbstractModule;
import pl.jordii.mcauth.common.database.MySQLCredentials;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.database.dao.RegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.mysql.MyRegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.mysql.SpigotSqlCredentials;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpigotBinderModule extends AbstractModule {

    private final McAuthSpigot pluginClass;

    public SpigotBinderModule(McAuthSpigot pluginClass) {
        this.pluginClass = pluginClass;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(this.pluginClass);
        bind(McAuthSpigot.class).toInstance(this.pluginClass);
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
        bind(RegisteredUserDatabase.class).to(MyRegisteredUserDatabase.class);
        bind(MySQLCredentials.class).to(SpigotSqlCredentials.class);
    }
}
