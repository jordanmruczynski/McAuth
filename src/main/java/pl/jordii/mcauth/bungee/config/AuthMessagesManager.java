package pl.jordii.mcauth.bungee.config;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.YamlConfiguration;
import pl.jordii.mcauth.bungee.McAuthBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AuthMessagesManager {
    private final File file;
    private ConfigurationProvider provider;
    private Configuration configuration;
    private static Map<AuthMessagesBungee, String> messagesCache = new HashMap<>();

    @Inject
    public AuthMessagesManager(McAuthBungee plugin) {
        this.file = new File("plugins//" + plugin.getDescription().getName(), "messages.yml");
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
        }
        this.loadValues();
    }

    private void loadValues() throws IOException {
        this.configuration = provider.load(this.file);
        for (AuthMessagesBungee messages : AuthMessagesBungee.values()) {
            messagesCache.put(messages, this.configuration.getString(messages.getPath()));
        }
    }

    public static TextComponent sendMessage(AuthMessagesBungee messages, Object... args) {
        String msg = messagesCache.get(messages);
        return new TextComponent(msg);
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

    public static String sendMessage2(AuthMessagesBungee messages, Object... args) {
        String msg = messagesCache.get(messages);
        return msg;
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

    private void writeDefaults() throws IOException {
        for (AuthMessagesBungee messages : AuthMessagesBungee.values()) {
            this.configuration.set(messages.getPath(), messages.getDefaultMessage());
        }
        this.provider.save(this.configuration, this.file);
    }

}
