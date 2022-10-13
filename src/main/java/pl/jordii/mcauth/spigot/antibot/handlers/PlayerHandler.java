package pl.jordii.mcauth.spigot.antibot.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.UserRepository;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaFailureEvent;
import pl.jordii.mcauth.spigot.antibot.events.CaptchaSuccessEvent;
import pl.jordii.mcauth.spigot.antibot.player.CaptchaPlayer;
import pl.jordii.mcauth.spigot.util.ServerMainThread;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Random;

@Singleton
public class PlayerHandler {

    private final UserRepository mapcha;
    GuiHandler guiHandler;


    @Inject
    public PlayerHandler(UserRepository mapcha) {
        this.mapcha = mapcha;
    }

    public void giveGui(Player player) {
        guiHandler = new GuiHandler(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                this.cancel();
                guiHandler.open(player);
            }
        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 20L);
    }

    public ItemStack getRandomElement() {
        return this.guiHandler.getRandomElement();
    }

    public void giveCaptcha(Player player) {
        // checking if player has permission to bypass the captcha or player has already completed the captcha before
        // by default OPs have the '*' permission so this method will return true
//        if (player.hasPermission("mcauthspigot.bypasscaptcha") || (mapcha.getCacheManager().isCached(player))) {
//            return;
//        }

        player.getInventory().clear();
        // creating a captcha player
        CaptchaPlayer captchaPlayer = new CaptchaPlayer(
                player,
                genCaptcha()
        ).cleanPlayer();

        // getting the map itemstack depending ont he spigot version
        String version = Bukkit.getVersion();
        ItemStack itemStack;
        if (version.contains("1.13") ||
                version.contains("1.14") ||
                version.contains("1.15") ||
                version.contains("1.16") ||
                version.contains("1.17")) {
            itemStack = new ItemStack(Material.valueOf("LEGACY_EMPTY_MAP"));
        } else {
            itemStack = new ItemStack(Material.valueOf("EMPTY_MAP"));
        }

        // setting the item metadata
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6McAuth captcha");
        itemMeta.setLore(Collections.singletonList(AuthMessagesManager.sendMessage(AuthMessages.CAPTCHA_MAP_ITEM_LORE)));
        itemStack.setItemMeta(itemMeta);

        // giving the player the map and adding them to the captcha array
        //captchaPlayer.getPlayer().getInventory().setItem(4, itemStack);
        captchaPlayer.getPlayer().getInventory().setItemInHand(itemStack);
        mapcha.getPlayerManager().add(captchaPlayer);
    }

    public void removeCaptcha(Player player1) {
        CaptchaPlayer player = mapcha.getPlayerManager().getPlayer(player1);

        if (player == null) {
            return;
        }

        player.rollbackInventory();

        mapcha.getPlayerManager().remove(player);
    }

    public void checkCaptcha(Player player1, String message) {
        CaptchaPlayer player = mapcha.getPlayerManager().getPlayer(player1);

        if (player == null) {
            return;
        }

        if (message.equals(player.getCaptcha())) {
            Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new CaptchaSuccessEvent(player)));
            mapcha.captchaVerifiedPlayers.add(player1.getName());
            //UserRepository.captchaVerifiedPlayers.add(player1.getName());
            removeCaptcha(player1);
        } else {
            Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new CaptchaFailureEvent(player)));
        }
    }

    private String genCaptcha() {
        String charset = "0123456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNOPQRSTUVWXYZ";
        StringBuilder random = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            random.append(charset.charAt(new Random().nextInt(charset.length() - 1)));
        }
        return random.toString();
    }

}