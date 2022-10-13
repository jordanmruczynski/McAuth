package pl.jordii.mcauth.spigot.database;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import pl.jordii.mcauth.spigot.McAuthSpigot;
import pl.jordii.mcauth.spigot.antibot.events.*;
import pl.jordii.mcauth.spigot.antibot.handlers.GuiHandler;
import pl.jordii.mcauth.spigot.config.AuthMessages;
import pl.jordii.mcauth.spigot.config.AuthMessagesManager;
import pl.jordii.mcauth.spigot.database.dao.RegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.model.RegisteredUser;
import pl.jordii.mcauth.spigot.antibot.handlers.PlayerHandler;
import pl.jordii.mcauth.spigot.antibot.managers.CacheManager;
import pl.jordii.mcauth.spigot.antibot.managers.CaptchaPlayerManager;
import pl.jordii.mcauth.spigot.messenger.SpigotMessenger;
import pl.jordii.mcauth.spigot.util.Packets;
import pl.jordii.mcauth.spigot.util.Queue;
import pl.jordii.mcauth.spigot.util.ServerMainThread;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mindrot.jbcrypt.BCrypt;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class UserRepository implements Listener {

    private final Set<UUID> premiumAccount = Sets.newHashSet();
    private final ConcurrentMap<UUID, String> firstPassword = Maps.newConcurrentMap();
    private final ConcurrentMap<UUID, RegisteredUser> userCache = Maps.newConcurrentMap();
    private final Set<UUID> locked = Sets.newHashSet();
    private final Set<UUID> registering = Sets.newHashSet();

    private final RegisteredUserDatabase userDatabase;
    private final SpigotMessenger spigotMessenger;
    private final CaptchaPlayerManager playerManager = new CaptchaPlayerManager();
    private final CacheManager cacheManager = new CacheManager();
    GuiHandler guiHandler;
    public final List<String> captchaVerifiedPlayers = new CopyOnWriteArrayList<>();
    private final List<String> antibotVerifiedPlayers = new CopyOnWriteArrayList<>();
    private final List<String> guiVerifiedPlayers = new CopyOnWriteArrayList<>();
    String titleMain = AuthMessagesManager.sendMessage(AuthMessages.TITLES_TITLE_MAIN);

    @Inject
    public UserRepository(RegisteredUserDatabase userDatabase, SpigotMessenger spigotMessenger) {
        this.userDatabase = userDatabase;
        this.spigotMessenger = spigotMessenger;
    }


    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        //hide player and other players hide for him
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(player);
            player.hidePlayer(p);
        }

        player.teleport(Bukkit.getWorld("world").getSpawnLocation().add(0, 0.5, 0));
        //player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 81, 0.5));

        if (player.isOp()) {
            return;
        }


        check();
        guiHandler = new GuiHandler(player);
        event.setJoinMessage(null);
//        Location location = player.getLocation();
//        World world = player.getWorld();
//        world.setSpawnLocation(location.getBlockX(), location.getBlockY() + 1000, location.getBlockZ());
//        player.teleport(player.getWorld().getSpawnLocation());
        //System.out.println(captchaVerifiedPlayers);

        //locked.add(player.getUniqueId());

        if (!guiVerifiedPlayers.contains(player.getName())) {
            guiCheck(player);
            return;
        }

//        if (!guiVerifiedPlayers.contains(player.getName())) {
//            (player);
//            return;
//        }


//        if (!antibotVerifiedPlayers.contains(player.getName())) {
//            if (!locked.contains(player.getUniqueId())) {
//                locked.add(player.getUniqueId());
//            }
//            antibotCheck(player);
//            return;
//        }


        locked.add(player.getUniqueId());
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 10));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 10));

        userDatabase.loadUser(player.getUniqueId(), user -> {
            if (user == null) {
                registering.add(player.getUniqueId());

                player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.REGISTER_NONPREMIUM));
                player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_REGISTER));
                //player.sendMessage("§7We detected you have a non-premium account.");
                //player.sendMessage("§6Please choose a secure password to register yourself and type it in the chat now.");

                return;
            }

            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.WELCOME_BACK));
            //player.sendMessage(AuthMessages.WELCOME_BACK.getDefaultMessage());
            //player.sendMessage("§aWelcome back on the server!");

            player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.PASSWORD_TYPE));
            player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_PASSWORD));
            //player.sendMessage(AuthMessages.PASSWORD_TYPE.getDefaultMessage());
            //player.sendMessage("§7We detected a cracked account, please type your password in the chat now.");
            userCache.put(player.getUniqueId(), user);
        });
    }


    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        removeAll(event.getPlayer());
        event.setQuitMessage(null);
        GuiHandler.guiCache.remove(event.getPlayer());
    }

    @EventHandler
    public void handlePlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        //if (antibotVerifiedPlayers)
        if (!locked.contains(player.getUniqueId())) {
            return;
        }
        player.teleport(e.getFrom());
    }

    @EventHandler
    public void handlePlayerDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handlePlayerInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().contains(":")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                if (e.getCurrentItem().equals(GuiHandler.guiCache.get(player.getUniqueId()))) {
                    Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new GuiClickSuccessEvent((Player) e.getWhoClicked())));
                    locked.remove(e.getWhoClicked().getUniqueId());
                    guiVerifiedPlayers.add(e.getWhoClicked().getName());
                } else {
                    Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new GuiClickFailureEvent((Player) e.getWhoClicked())));
                }
            }
        }
    }

    @EventHandler
    public void handlePlayerCloseInventory(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getView().getTitle().contains(":")) {
            if (e.getPlayer() != null) {
                Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new GuiClickFailureEvent(player)));
            }
            //playerHandler.giveGui((Player) e.getPlayer());
            //playerHandler.giveGui((Player) e.getPlayer());
        }
    }

    private void check() {
        if (antibotVerifiedPlayers.size() >= 100){
            antibotVerifiedPlayers.clear();
        }
        if (guiVerifiedPlayers.size() >= 100){
            guiVerifiedPlayers.clear();
        }
        if (captchaVerifiedPlayers.size() >= 100){
            captchaVerifiedPlayers.clear();
        }
    }

    @EventHandler
    public void handleAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        //String message = event.getMessage().replace("%", "%%");

        event.setCancelled(true);

            if (!locked.contains(player.getUniqueId())) {
                return;
            }

            player.sendMessage("§cUzyj komendy §6/login §club §6/register");
            event.setCancelled(true);

//            if (firstPassword.containsKey(player.getUniqueId())) {
//                String password = firstPassword.get(player.getUniqueId());
//
//                if (!password.equals(message)) {
//                    ServerMainThread.RunParallel.run(() -> {
//                        player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_SECOND_PASSWORD));
//                    });
//                    return;
//                }
//
//                player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.CREATE_ACCOUNT));
//                player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_CREATEACCOUNT));
//
//                String salt = BCrypt.gensalt();
//
//                userDatabase.registerUser(new RegisteredUser(
//                        player.getUniqueId(), player.getName(),
//                        premiumAccount.contains(player.getUniqueId()),
//                        salt,
//                        BCrypt.hashpw(password, salt)));
//
//                removeAll(player);
//                spigotMessenger.movePlayerToLobby(player);
//                Bukkit.getScheduler().runTaskLater(McAuthSpigot.getProvidingPlugin(McAuthSpigot.class), () -> {
//                    if (player.isOnline()) {
//                        player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.OFFLINE_TARGET_SERVER));
//                    }
//                }, 20L);
//                //Queue.queuePlayers.add(player);
//                //addPlayerToQueue(player);
//            } else if (registering.contains(player.getUniqueId())) {
//                firstPassword.put(player.getUniqueId(), message);
//                player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.REPEAT_PASSWORD));
//                player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_REPEATPASSWORD));
//            } else {
//                RegisteredUser user = userCache.get(player.getUniqueId());
//                if (BCrypt.checkpw(message, user.getHashedPassword())) {
//                    player.sendMessage(AuthMessagesManager.sendMessage(AuthMessages.CORRECT_PASSWORD));
//                    player.sendTitle(titleMain, AuthMessagesManager.sendMessage(AuthMessages.TITLES_SUBTITLE_CORRECTPASSWORD));
//                    removeAll(player);
//                    //addPlayerToQueue(player);
//                    spigotMessenger.movePlayerToLobby(player);
//                    Bukkit.getScheduler().runTaskLater(McAuthSpigot.getProvidingPlugin(McAuthSpigot.class), () -> {
//                        if (player.isOnline()) {
//                            player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.OFFLINE_TARGET_SERVER));
//                        }
//                    }, 20L);
//                    return;
//                }
//
//                ServerMainThread.RunParallel.run(() -> {
//                    player.kickPlayer(AuthMessagesManager.sendMessage(AuthMessages.INCORRECT_PASSWORD));
//                    //player.kickPlayer(AuthMessages.INCORRECT_PASSWORD.getDefaultMessage());
//                });
//            }
    }


    private void removeAll(Player player) {
        ServerMainThread.RunParallel.run(() -> {
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        });
        premiumAccount.remove(player.getUniqueId());
        userCache.remove(player.getUniqueId());
        firstPassword.remove(player.getUniqueId());
        registering.remove(player.getUniqueId());
        locked.remove(player.getUniqueId());
    }

    private void guiCheck(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                this.cancel();
                guiHandler.open(player);
            }
        }.runTaskLater(McAuthSpigot.getPlugin(McAuthSpigot.class), 20L);
        //playerHandler.giveGui(player);
//        new BukkitRunnable() {
//            int counter = 0;
//            @Override
//            public void run() {
//                if (!player.isOnline()) {
//                    this.cancel();
//                }
//                if (counter >= 10) {
//                    this.cancel();
//                    if (!guiVerifiedPlayers.contains(player.getName())) {
//                        Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new GuiClickFailureEvent(player)));
//                    }
//                }
//                counter++;
//            }
//        }.runTaskTimerAsynchronously(McAuthSpigot.getPlugin(McAuthSpigot.class), 0L, 20L);
    }

//    private void addPlayerToQueue(Player player) {
//        Queue.queuePlayers.add(player);
//        ServerMainThread.RunParallel.run(() -> {
//            player.teleport(new Location(Bukkit.getWorld("kolejki"), 0, 65, 0, 180, 2));
//            player.setGameMode(GameMode.ADVENTURE);
//            player.setHealth(20.0);
//            player.setFoodLevel(20);
//            player.setSaturation(9999999);
//        });
//    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            e.setCancelled(true);
        }
    }

    private void antibotCheck(Player player) {
        double y = player.getLocation().getY();
        player.sendMessage(String.valueOf(y));
        new BukkitRunnable() {
            int counter = 0;
            @Override
            public void run() {
                if (counter >= 3) {
                    this.cancel();
                    if (player.getLocation().getY() < (y-3)) {
                        Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new AntibotSuccessEvent(player)));
                        antibotVerifiedPlayers.add(player.getName());
                        locked.remove(player.getUniqueId());
                    } else {
                        Bukkit.getScheduler().runTask(McAuthSpigot.getPlugin(McAuthSpigot.class), () -> Bukkit.getPluginManager().callEvent(new AntibotFailureEvent(player)));
                    }
                }

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent("§6MCAUTH ANTI-BOT §7| §eCHECKING..."));
                counter++;
            }

        }.runTaskTimerAsynchronously(McAuthSpigot.getPlugin(McAuthSpigot.class), 0L, 20L);
    }

    public CaptchaPlayerManager getPlayerManager() {
        return playerManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public List<String> getCaptchaVerifiedPlayers() {
        return captchaVerifiedPlayers;
    }

    public List<String> getAntibotVerifiedPlayers() {
        return antibotVerifiedPlayers;
    }

    public ConcurrentMap<UUID, String> getFirstPassword() {
        return firstPassword;
    }

    public ConcurrentMap<UUID, RegisteredUser> getUserCache() {
        return userCache;
    }

    public Set<UUID> getLocked() {
        return locked;
    }

    public Set<UUID> getRegistering() {
        return registering;
    }

    public List<String> getGuiVerifiedPlayers() {
        return guiVerifiedPlayers;
    }
}
