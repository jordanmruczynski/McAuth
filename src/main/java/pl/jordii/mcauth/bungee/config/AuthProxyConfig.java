package pl.jordii.mcauth.bungee.config;

import com.google.common.collect.Lists;
import pl.jordii.mcauth.bungee.McAuthBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Singleton
public class AuthProxyConfig {

    private final File file;
    private ConfigurationProvider provider;
    private Configuration configuration;

    private List<String> authServers;
    private List<String> lobbyServers;
    private String apiKey;
    private boolean connectSectors;
    private boolean nonpremiumTickets;

    @Inject
    public AuthProxyConfig(McAuthBungee plugin) {
        this.file = new File("plugins//" + plugin.getDescription().getName(), "authConfig.yml");
    }

    public void loadFile() throws IOException {
        File folder = this.file.getParentFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        boolean created = this.file.createNewFile();

        this.provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        this.configuration = provider.load(this.file);
        if (created) {
            this.writeDefaults();
        } else {
            this.loadValues();
        }

    }

    public List<String> getAuthServers() {
        return this.authServers;
    }

    public List<String> getLobbyServers() {
        return lobbyServers;
    }

    public String getApiKey() {
        return apiKey;
    }

    public boolean shouldConnectSectors() {
        return connectSectors;
    }

    public boolean shouldConnectTickets() {
        return nonpremiumTickets;
    }

    private void loadValues() throws IOException {
        this.configuration = provider.load(this.file);
        this.authServers = this.configuration.getStringList("auth-servers");
        this.lobbyServers = this.configuration.getStringList("lobby-servers");
        this.apiKey = this.configuration.getString("api-key");
        this.connectSectors = this.configuration.getBoolean("connect-sectors");
        this.nonpremiumTickets = this.configuration.getBoolean("nonpremium-tickets");
    }

    private void writeDefaults() throws IOException {
        this.authServers = Lists.newArrayList("auth-1");
        this.configuration.set("auth-servers", authServers);

        this.lobbyServers = Lists.newArrayList("sector-1");
        this.configuration.set("lobby-servers", lobbyServers);

        this.configuration.set("api-key", "contact with Jordii#7622 to get api key");

        this.configuration.set("connect-sectors", false);

        this.configuration.set("nonpremium-tickets", false);

        this.provider.save(this.configuration, this.file);
    }

}
