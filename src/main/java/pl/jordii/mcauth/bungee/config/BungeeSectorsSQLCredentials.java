package pl.jordii.mcauth.bungee.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.common.database.MySQLCredentials;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Singleton
public class BungeeSectorsSQLCredentials extends MySQLCredentials {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private final File file;
    private final ConfigurationProvider provider;
    private Configuration configuration;

    @Inject
    public BungeeSectorsSQLCredentials(McAuthBungee plugin) {
        this.file = new File("plugins//" + plugin.getDescription().getName(), "sectorsSql.yml");
        this.provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

    public void load() {
        try {
            if (this.createIfNotExists()) {
                this.writeDefaults();
            } else {
                this.configuration = provider.load(this.file);
            }

            this.host = configuration.getString("Database.MySQL.host");
            this.port = configuration.getInt("Database.MySQL.port");
            this.database = configuration.getString("Database.MySQL.database");
            this.username = configuration.getString("Database.MySQL.username");
            this.password = configuration.getString("Database.MySQL.password");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean createIfNotExists() throws IOException {
        File folder = this.file.getParentFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return file.createNewFile();
    }

    private void writeDefaults() throws IOException {
        this.configuration = provider.load(file);

        this.configuration.set("Database.MySQL.host", "127.0.0.1");
        this.configuration.set("Database.MySQL.port", 3306);
        this.configuration.set("Database.MySQL.database", "database");
        this.configuration.set("Database.MySQL.username", "username");
        this.configuration.set("Database.MySQL.password", "password");

        provider.save(configuration, file);
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public String getDatabase() {
        return this.database;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
