package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MobHopper implements Listener {
    // Hopper settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Mob Hopper";
    public static int range = 5;
    public static float distance_threashold = 1.6f;
    static String localized = "mob_hopper";
    static Material material = Material.HOPPER;
    static String[] lore = {
            ChatColor.GRAY + "The hopper will attract all mobs",
            ChatColor.GRAY + "in a range of " + range + " blocks.",
            ChatColor.GRAY + "Once a mob is caught, it won't be able",
            ChatColor.GRAY + "to escape, and the hopper will stop.",
            ChatColor.GRAY + "Runs every 1.5 seconds ( 30 ticks ).",
    };
    Main plugin;
    public MobHopper(Main plugin) {
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
                    Location arrival = loc.clone(); arrival.add(.5, .35, .5);
                    Location search_start = loc.clone().add(.5, .5, .5);
                    Entity closest = null;
                    boolean cancel = false;

                    for (Entity entity : search_start.getWorld().getNearbyEntities(search_start, range, range, range)) {
                        if ( entity instanceof Player || entity.getType().equals(EntityType.EXPERIENCE_ORB) || entity.getType().equals(EntityType.DROPPED_ITEM) ) continue;
                        if ( entity instanceof  LivingEntity) {
                            if ( ((LivingEntity) entity ).hasAI()  ) {
                                if (closest == null)
                                    closest = entity;
                                else if (search_start.distance(closest.getLocation()) > search_start.distance(entity.getLocation()))
                                    closest = entity;
                            } else {
                                entity.teleport(arrival);
                                cancel = true;
                            }
                        }
                    }

                    if ( cancel ) continue;

                    if ( closest == null ) continue;

                    if ( search_start.distance(closest.getLocation()) <= distance_threashold ) {
                        closest.teleport(arrival);
                        ((LivingEntity) closest).setAI(false);

                        BukkitRunnable wake_up = new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Entity entity : search_start.getWorld().getNearbyEntities(search_start, 3, 3, 3)) {
                                    if ( entity instanceof Player || entity.getType().equals(EntityType.DROPPED_ITEM) ) continue;
                                    ((LivingEntity) entity).setAI(false);
                                }
                            }
                        };

                        wake_up.runTaskLater(plugin,20l);

                        cancel = true;
                    }

                    if ( cancel ) continue;

                    for (Entity entity : search_start.getWorld().getNearbyEntities(search_start, range, range, range)) {
                        if ( entity instanceof Player || entity.getType().equals(EntityType.EXPERIENCE_ORB) || entity.getType().equals(EntityType.DROPPED_ITEM) ) continue;
                        if ( entity instanceof LivingEntity ) {
                            if ( entity.getLocation().distance(search_start) > distance_threashold ) {
                                Vector dir = search_start.clone().subtract(entity.getLocation()).toVector().normalize();
                                entity.setVelocity(entity.getVelocity().add( dir.multiply(1.2f) ));
                            }
                        }
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
