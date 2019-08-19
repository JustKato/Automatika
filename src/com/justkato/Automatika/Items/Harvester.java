package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Harvester implements Listener {
    // Hopper settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Automatic Harvester";
    public static int range = 2;
    static String localized = "automatic_harvester";
    static Material material = Material.DISPENSER;
    static String[] lore = {
            ChatColor.GRAY + "The harvester will harvest all crops",
            ChatColor.GRAY + "in a range(x,y,z) of x:" + range*2 + " y:1 z:" + range*2 + " blocks.",
            ChatColor.GRAY + "The area's center is the harvester",
    };
    Main plugin;
    public Harvester(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
        InitializeHarvesterLoop();
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }



    public void InitializeHarvesterLoop() {

        BukkitRunnable loop = new BukkitRunnable() {
            @Override
            public void run() {
                for ( Location loc : locations ) {
                    if ( !loc.getChunk().isLoaded() ) continue; // Check if the chunk is loaded
                    if ( !loc.getBlock().isBlockPowered() ) continue; // Check if we've got redstone signal

                    Dispenser harvester = (Dispenser) loc.getBlock().getState();
                    Inventory harvester_inv = harvester.getInventory();

                    Location looper = loc.clone();
                    looper.setX(looper.getX() - range);
                    looper.setZ(looper.getZ() - range);
                    looper.setY(looper.getY() + 1);
                    Location og_start = looper.clone();
                    for ( int x = 0; x < range*2 + 1; x++ ) {
                        for ( int z = 0; z < range*2 + 1; z++ ) {
                            try {
                                Block block_target = looper.getBlock();
                                if ( block_target.getBlockData() instanceof  Ageable ) {
                                    Ageable target = (Ageable) block_target.getBlockData();
                                    if ( target.getAge() == target.getMaximumAge() ) {
                                        block_target.breakNaturally();
                                        looper.getWorld().spawnParticle(Particle.COMPOSTER, looper.clone().add(.5f, .5f, .5f), 5, 0f, 0.5f, 0f, 0f);
                                    }

                                    boolean is_inventory_full = true;

                                    for ( int i = harvester_inv.getSize() - 1; i >= 0; i-- ) {
                                        if ( harvester_inv.getItem(i) == null || harvester_inv.getItem(i).equals(Material.AIR) ){
                                            is_inventory_full = false;
                                            break;
                                        }
                                    }

                                    if ( !is_inventory_full ) {
                                        for ( Entity entity: block_target.getWorld().getNearbyEntities(looper.clone().add(.5f, .5f, .5f), 1, 1, 1)) {
                                            if ( entity.getType().equals(EntityType.DROPPED_ITEM) ) {
                                                Item item = (Item) entity;
                                                ItemStack itemStack = item.getItemStack();

                                                if ( itemStack.getType().isEdible() || itemStack.getType().isItem() ) {
                                                    harvester_inv.addItem(itemStack);
                                                    entity.remove();
                                                }
                                            }
                                        }

                                    }

                                }
                            } catch (Exception ex ) {
                                ex.printStackTrace();
                            }
                            looper.setZ(looper.getZ() + 1);
                        }
                        looper.setZ(og_start.getZ());
                        looper.setX(looper.getX() + 1);
                    }

                }
            }
        };

        loop.runTaskTimer(this.plugin, 60L, 30);
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        Location loc = event.getBlockPlaced().getLocation();
        Location particle_loc = loc.clone();
        particle_loc.add(0.5f, 0.65f, 0.5f);

        if (!hand.getItemMeta().getLocalizedName().equals(localized)) return;

        if ( Boolean.parseBoolean(FileManager.settings.get("global_particles")) ) {
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
            loc.getWorld().spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
        }

        if ( Boolean.parseBoolean(FileManager.settings.get("global_particles")) ) {
            loc.getWorld().playSound(particle_loc, Sound.BLOCK_STONE_PLACE, 1.25F, 0.8F);
        }

        locations.add(loc);
        FileManager.SaveData(localized, locations);
    }

    @EventHandler
    void onBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation(); // Get the location of the block
        Location particle_loc = loc.clone(); // initialize the particle spawn location
        particle_loc.add(0.5f, 0.65f, 0.5f); // Get the particle spawn location

        // Error check/Conditioin
        if ( !locations.contains(loc) ) return;
        // Spawn the particles
        if ( Boolean.parseBoolean(FileManager.settings.get("global_particles")) ) {
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
            loc.getWorld().spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
        }
        // Fix the block-drop
        event.setDropItems(false); // Cancel the fake block Breaker
        loc.getWorld().dropItemNaturally(event.getBlock().getLocation(), GenerateItem()); // Drops the Block Breaker

        // Remove from the list
        locations.remove(loc);
        // Save the block_breakers to DB
        FileManager.SaveData(localized, locations);
    }


}
