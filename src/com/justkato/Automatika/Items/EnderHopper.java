package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import net.minecraft.server.v1_14_R1.EntityProjectileThrowable;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class EnderHopper implements Listener {
    // Hopper settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Ender Hopper";
    static String localized = "ender_hopper";
    static Material material = Material.HOPPER;
    static String[] lore = {
            ChatColor.GRAY + "The hopper will teleport all item-drops",
            ChatColor.GRAY + "from within the chunk on-top of itself.",
            ChatColor.GRAY + "Runs once every 20 seconds",
    };
    Main plugin;
    public EnderHopper(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
        InitializeHopperLoop();
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }

    public void InitializeHopperLoop() {
        BukkitRunnable loop = new BukkitRunnable() {
            @Override
            public void run() {
                for ( Location loc : locations ) {
                    Location arrival = loc.clone(); arrival.add(.5, 1, .5);
                    boolean playParticles = false;

                    try {
                        Hopper hopper = (Hopper) loc.getBlock().getState();
                        boolean should_continue = false;

                        for (int i = 0; i < 5; i++) {
                            if (hopper.getInventory().getItem(i) == null ||
                                    hopper.getInventory().getItem(i).getType().equals(Material.AIR)) {
                                should_continue = true;
                                break;
                            }
                        }

                        if (!should_continue) return;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    };

                    for (Entity entity : loc.getChunk().getEntities()) {
                        if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                            entity.teleport(arrival);
                            playParticles = true;
                        }
                    }

                    if ( playParticles ) {
                        arrival.getWorld().spawnParticle(Particle.END_ROD, arrival, 10, 0.2, .1, .1, .06);
                    }
                }
            }
        };

        loop.runTaskTimer(this.plugin, 60L, 20*20);
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        Location loc = event.getBlockPlaced().getLocation();
        Location particle_loc = loc.clone();
        particle_loc.add(0.5f, 0.65f, 0.5f);

        Chunk current_chunk = loc.getChunk();

        for ( Location hopper_location : locations ) {
            if ( hopper_location.getChunk().equals(current_chunk) ) {
                event.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Automatika" + ChatColor.GRAY + "]" + ChatColor.WHITE +
                        ": There already is an " +
                        ChatColor.GREEN + "Ender Hopper" + ChatColor.WHITE +
                        " in this chunk!");
                event.setCancelled(true);
                return;
            }
        }

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
