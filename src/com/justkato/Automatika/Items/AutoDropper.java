package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;


public class AutoDropper implements Listener {
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Automatic Dropper";
    static String localized = "auto_dropper";
    static Material material = Material.DROPPER;
    static String[] lore = {
            ChatColor.GRAY + "A dropper that will automatically drop",
            ChatColor.GRAY + "any item that is put in it instantly",
            "",
            ChatColor.GRAY + "(Doesn't work manually)",
    };
    Main plugin;

    public AutoDropper(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }

    @EventHandler
    void onItemInteract(InventoryMoveItemEvent event) {
        if ( locations.contains(event.getDestination().getLocation()) ) {
            Dropper dropper = ((Dropper) event.getDestination().getLocation().getBlock().getState());
            BukkitRunnable loop = new BukkitRunnable() {
                Inventory inv = dropper.getInventory();
                @Override
                public void run() {
                    if (dropper == null) return;
                    if (inv == null ) return;
                    boolean should_we_cancel = true;
                    try {
                        for (ItemStack item : inv.getContents()) {
                            if (item != null) { should_we_cancel = false; break;}
                            if (!item.getType().equals(Material.AIR)) { should_we_cancel = false; break;}
                        }
                    } catch (Exception ex ) {

                    }

                    if ( should_we_cancel ) {
                        this.cancel();
                        return;
                    }
                    dropper.drop();
                }
            };

            loop.runTaskTimer(this.plugin, 10l, 10l);
        }
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlockPlaced();
        Location loc = block.getLocation();
        Location particle_loc = loc.clone(); // initialize the particle spawn location
        particle_loc.add(0.5f, 0.65f, 0.5f); // Get the particle spawn location
        ItemStack hand = event.getItemInHand();

        if ( hand.getItemMeta().getLocalizedName().equals(localized)) {
            // Placing a Auto Dropper
            locations.add(loc); // Add the auto_dropper to the list
            // Play some particles/sounds
            if ( Boolean.parseBoolean(FileManager.settings.get("auto_dropper_particles")) ) {
                p.spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
                p.spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
            }

            Dropper placed = (Dropper) event.getBlockPlaced().getState();

            FileManager.SaveData(localized, locations);
        }
    }

    @EventHandler
    void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Location particle_loc = loc.clone(); // initialize the particle spawn location
        particle_loc.add(0.5f, 0.65f, 0.5f); // Get the particle spawn location
        if ( loc == null ) return;
        if ( locations.contains(loc) ) {
            // Breaking the Auto Dropper Play some pretty animations
            event.setDropItems(false); // Remove the Drops
            loc.getWorld().dropItemNaturally(loc, GenerateItem()); // Drop the fixed block
            if ( Boolean.parseBoolean(FileManager.settings.get("auto_dropper_particles")) ) {
                p.spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
                p.spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
            }
            locations.remove(loc); // Remove the location from the location list
            FileManager.SaveData(localized, locations);
        }
    }


}
