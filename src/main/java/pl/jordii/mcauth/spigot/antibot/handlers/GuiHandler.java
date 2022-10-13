package pl.jordii.mcauth.spigot.antibot.handlers;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

public class GuiHandler {

    private Inventory inventory;
    private Random rand = new Random();
    private ItemStack randomElement;

    public static final Map<UUID, ItemStack> guiCache = Maps.newHashMap();

    private List<ItemStack> materials = Arrays.asList(
            new ItemStack(Material.DIAMOND), new ItemStack(Material.EMERALD_BLOCK),
            new ItemStack(Material.STONE_SWORD), new ItemStack(Material.CACTUS),
            new ItemStack(Material.YELLOW_BANNER), new ItemStack(Material.STONE_PICKAXE),
            new ItemStack(Material.DARK_OAK_DOOR), new ItemStack(Material.LAVA_BUCKET),
            new ItemStack(Material.BEACON), new ItemStack(Material.DIRT),
            new ItemStack(Material.SANDSTONE),new ItemStack(Material.STICK),
            new ItemStack(Material.BRICK),new ItemStack(Material.APPLE),
            new ItemStack(Material.GOLD_NUGGET), new ItemStack(Material.DIAMOND_AXE),
            new ItemStack(Material.HAY_BLOCK), new ItemStack(Material.GLOWSTONE),
            new ItemStack(Material.LADDER),new ItemStack(Material.DIORITE),
            new ItemStack(Material.SLIME_BALL), new ItemStack(Material.PAPER),
            new ItemStack(Material.BONE), new ItemStack(Material.REDSTONE),
            new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.GOLDEN_HELMET));

    public GuiHandler(Player player){

        randomElement = materials.get(rand.nextInt(materials.size()));
        inventory = Bukkit.createInventory(null, 27, "§6Kliknij: §f" + randomElement.getType().toString());
        Collections.shuffle(materials);
        for (ItemStack x : materials){
            inventory.addItem(x);
        }
        guiCache.put(player.getUniqueId(), randomElement);

//        if (!guiCache.containsKey(player.getUniqueId())) {
//            randomElement = materials.get(rand.nextInt(materials.size()));
//            inventory = Bukkit.createInventory(null, 27, "§6Kliknij: §f" + randomElement.getType().toString());
//            Collections.shuffle(materials);
//            for (ItemStack x : materials){
//                inventory.addItem(x);
//            }
//            guiCache.put(player.getUniqueId(), randomElement);
//        } else {
//            inventory = Bukkit.createInventory(null, 27, "§6Kliknij: §f" + guiCache.get(player.getUniqueId()).getType().toString());
//            Collections.shuffle(materials);
//            for (ItemStack x : materials){
//                inventory.addItem(x);
//            }
//        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public ItemStack getRandomElement() {
        return randomElement;
    }
}
