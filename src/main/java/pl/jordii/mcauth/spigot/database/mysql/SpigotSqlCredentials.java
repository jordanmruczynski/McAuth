package pl.jordii.mcauth.spigot.database.mysql;

import pl.jordii.mcauth.common.database.MySQLCredentials;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

@Singleton
public class SpigotSqlCredentials extends MySQLCredentials {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private final File file;
    private YamlConfiguration configuration;

    public SpigotSqlCredentials() {
        this.file = new File("plugins//" + McAuthSpigot.getPluginName(), "mysql.yml");;
    }

    public void load() {
        try {
            if (this.createIfNotExists()) {
                this.writeDefaults();
            } else {
                this.configuration = YamlConfiguration.loadConfiguration(this.file);
            }
            this.cacheValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cacheValues() {
        this.host = this.configuration.getString("Database.MySQL.host");
        this.port = this.configuration.getInt("Database.MySQL.port");
        this.database = this.configuration.getString("Database.MySQL.database");
        this.username = this.configuration.getString("Database.MySQL.username");
        this.password = this.configuration.getString("Database.MySQL.password");
    }

    private boolean createIfNotExists() throws IOException {
        File folder = this.file.getParentFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return file.createNewFile();
    }

    private void writeDefaults() throws IOException {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        this.configuration.set("Database.MySQL.host", "127.0.0.1");
        this.configuration.set("Database.MySQL.port", 3306);
        this.configuration.set("Database.MySQL.database", "database");
        this.configuration.set("Database.MySQL.username", "jordii");
        this.configuration.set("Database.MySQL.password", "password");

        this.configuration.save(file);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getDatabase() {
        return database;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
