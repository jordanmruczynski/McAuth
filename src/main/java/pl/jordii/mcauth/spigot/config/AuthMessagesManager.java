package pl.jordii.mcauth.spigot.config;

import pl.jordii.mcauth.spigot.McAuthSpigot;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AuthMessagesManager {
    private final File file = new File("plugins//" + McAuthSpigot.getInstance(McAuthSpigot.class).getDescription().getName(), "messages.yml");
    private static Map<AuthMessages, String> messagesCache = new HashMap<>();
    private YamlConfiguration configuration;

    @Inject
    public void load() throws IOException {
        if (this.createIfNotExists()) {
            this.writeDefaults();
        } else {
            this.configuration = YamlConfiguration.loadConfiguration(this.file);
        }
        this.cacheValues();
    }

    public static String sendMessage(AuthMessages messages, Object... args) {
        String msg = messagesCache.get(messages);
        return msg;
//        String msg = messagesCache.get(messages);
//        if (messages.equals(AuthMessages.COOLDOWN_MESSAGE)) {
//            String kitName = String.valueOf(args[0]);
//            UserKitsDatabase.getInstance().timeLeft(kitName, player, new Callback<Integer>() {
//                @Override
//                public void accept(Integer result) {
//                    player.sendMessage(Utils.fixColor(msg.replace("{TIME}", result.toString())));
//                }
//            });
//        }
    }

    private void cacheValues() {
        for (AuthMessages messages : AuthMessages.values()) {
            messagesCache.put(messages, configuration.getString(messages.getPath()));
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
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        for (AuthMessages messages : AuthMessages.values()) {
            this.configuration.set(messages.getPath(), messages.getDefaultMessage());
        }
        this.configuration.save(file);
    }

    public File getFile() {
        return file;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(YamlConfiguration configuration) {
        this.configuration = configuration;
    }
}
