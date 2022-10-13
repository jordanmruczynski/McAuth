package pl.jordii.mcauth.spigot.antibot.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CaptchaPlayer {

    private final Player player;
    private final String captcha;

    private final ItemStack[] contents;
    private final ItemStack[] armour;
    private int tries;

    public CaptchaPlayer(Player player, String captcha) {
        this.player = player;
        this.captcha = captcha;

        contents = player.getInventory().getContents();
        armour = player.getInventory().getArmorContents();

        tries = 0;
    }

    public BufferedImage render() {
        Color background = Color.BLACK;
        Color foreground = Color.WHITE;

        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(background);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        String title = AuthMessagesManager.sendMessage(AuthMessages.CAPTCHA_MAP_TITLE);
        g.setColor(foreground);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(title, (int) ((image.getWidth() - g.getFontMetrics().getStringBounds(title, g).getWidth()) / 2), 30);

        g.setFont(new Font("Arial", Font.BOLD, 15));

        String sTries = "";
        g.setColor(foreground);
        g.setColor((3 - tries) == 1 ? Color.RED : Color.GREEN);
        g.drawString("> " + String.valueOf((3 - tries) + " <"), (int) ((image.getWidth() - g.getFontMetrics().getStringBounds(sTries, g).getWidth()) / 3), 45);



        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.setColor(foreground);
        g.drawString(captcha, (int) ((image.getWidth() - g.getFontMetrics().getStringBounds(captcha, g).getWidth()) / 2), 105);
        return image;
    }

    public void rollbackInventory() {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armour);
        player.updateInventory();
    }

    public CaptchaPlayer cleanPlayer() {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
        return this;
    }


    public String getCaptcha() {
        return captcha;
    }

    public Player getPlayer() {
        return player;
    }

    public int getTries() {
        return tries;
    }

    public void incrementTries() {
        this.tries++;
    }
}