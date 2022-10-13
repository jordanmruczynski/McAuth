package pl.jordii.mcauth.spigot.config;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public class AuthConfig {

    private final McAuthSpigot mainClass;
    private YamlConfiguration configuration;

    private static final Map<String, Object> valueCache = Maps.newHashMap();

    @Inject
    public AuthConfig(McAuthSpigot plugin) {
        this.mainClass = plugin;
    }

    public void loadFile() {
        mainClass.saveResource("authConfig.yml", false);
        File file = new File("plugins//" + mainClass.getDescription().getName(), "authConfig.yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void loadValues() {
        this.configuration.getKeys(true).forEach(current
                -> valueCache.put(current, this.configuration.get(current)));
    }

    public int getInteger(AuthSettings settings) {
        return (int) valueCache.get(settings.getConfigPath());
    }

    public double getDouble(AuthSettings settings) {
        return (double) valueCache.get(settings.getConfigPath());
    }

    public float getFloat(AuthSettings settings) {
        return (float) valueCache.get(settings.getConfigPath());
    }

    public String getString(AuthSettings settings) {
        return (String) valueCache.get(settings.getConfigPath());
    }

    public String getString(String path) {
        return (String) valueCache.get(path);
    }

    public List<String> getStringList(String path) {
        return (List<String>) valueCache.get(path);
    }

    public boolean getBoolean(AuthSettings settings) {
        return (boolean) valueCache.get(settings.getConfigPath());
    }

}
