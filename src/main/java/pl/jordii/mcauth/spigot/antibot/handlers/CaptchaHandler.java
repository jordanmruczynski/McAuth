package pl.jordii.mcauth.spigot.antibot.handlers;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.UserRepository;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaFailureEvent;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaSuccessEvent;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CaptchaHandler implements Listener {

    private final UserRepository userRepository;

    @Inject
    public CaptchaHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void onCaptchaSuccess(CaptchaSuccessEvent event) {
        CaptchaPlayer player = event.getPlayer();

        player.getPlayer().kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.CAPTCHA_SUCCESS));
        //player.getPlayer().kickPlayer(AuthMessages.CAPTCHA_SUCCESS.getDefaultMessage());

        player.rollbackInventory();

        if (true) {
            userRepository.getCacheManager().add(player.getPlayer());
        }
        userRepository.getPlayerManager().remove(player);
    }

    @EventHandler
    public void onCaptchaFail(CaptchaFailureEvent event) {
        CaptchaPlayer player = event.getPlayer();

        if (player.getTries() >= (3 - 1)) {
            player.getPlayer().kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.CAPTCHA_KICK));
            //player.getPlayer().kickPlayer(AuthMessages.CAPTCHA_KICK.getDefaultMessage());
        } else {
            player.incrementTries();
            player.getPlayer().sendMessage(AuthMessagesManager.sendMessage(AuthMessages.CAPTCHA_TRIES).replace("{TRIES}", String.valueOf(player.getTries())));
            //player.getPlayer().sendMessage(AuthMessages.CAPTCHA_TRIES.getDefaultMessage().replace("{TRIES}", String.valueOf(player.getTries())));
        }
    }

}
