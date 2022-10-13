package pl.jordii.mcauth.spigot.messenger;

import pl.jordii.mcauth.common.MessageChannel;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Singleton
public class SpigotMessenger {

    private final McAuthSpigot plugin;

    @Inject
    public SpigotMessenger(McAuthSpigot plugin) {
        this.plugin = plugin;
    }

    public void movePlayerToLobby(Player player) {
        sendToBungee(player, MessageChannel.AUTH_FINISHED, "move_me_to_lobby");
    }

    private void sendToBungee(Player player, MessageChannel subChannel, String payload) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF(subChannel.getSubChannel());
            dataOutputStream.writeUTF(payload);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        player.sendPluginMessage(this.plugin, "BungeeCord", byteArrayOutputStream.toByteArray());
    }

}
