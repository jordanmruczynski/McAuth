package pl.jordii.mcauth.bungee.listener;

import pl.jordii.mcauth.bungee.util.SendPlayerToLogoutServer;
import pl.jordii.mcauth.common.MessageChannel;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ChannelListener implements Listener {

    private final SendPlayerToLogoutServer playerSender;

    @Inject
    public ChannelListener(SendPlayerToLogoutServer playerSender) {
        this.playerSender = playerSender;
    }

    @EventHandler
    public void handlePluginMessage(PluginMessageEvent event) {
        String mainChannel = event.getTag();

        if (!mainChannel.equalsIgnoreCase("BungeeCord")) {
            return;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.getData());
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        try {
            String subChannel = dataInputStream.readUTF();
            String payload = dataInputStream.readUTF();

            if (subChannel.equals(MessageChannel.AUTH_FINISHED.getSubChannel())) {
                ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                playerSender.sendPlayer(player);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
