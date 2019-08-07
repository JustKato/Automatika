package com.justkato.Automatika.Items;


import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VaccumHopper implements Listener {
    // Hopper settings
    static int range = Integer.parseInt(FileManager.settings.get("vaccum_hopper_range"));
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Vaccum Hopper";
    static String localized = "vaccum_hopper";
    static Material material = Material.HOPPER;
    static String[] lore = {
            ChatColor.GRAY + "A hopper that will suck items in",
            ChatColor.GRAY + "from a range of " + range + " blocks",
    };
    Main plugin;
    public VaccumHopper(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData("vaccum_hopper");
        InitializeHopperLoop();
    }

    public static ItemStack GenerateItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setLocalizedName(localized);
        meta.setLore(Arrays.asList(lore));
        meta.setCustomModelData(23);

        item.setItemMeta(meta);
        return item;
    }

    public void InitializeHopperLoop() {

        if ( !Boolean.parseBoolean(FileManager.settings.get("vaccum_hopper_enabled")) )
            return;

        BukkitRunnable loop = new BukkitRunnable() {
            Location particle_loc = null;
            @Override
            public void run() {
                for ( Location hopper: locations ) {
                    // Error Checking
                    if ( !hopper.getBlock().getType().equals(Material.HOPPER) ) { locations.remove(hopper); break; }
                    particle_loc = hopper.clone();
                    particle_loc.add(.5f, .85f, .5f);
                    if ( Boolean.parseBoolean(FileManager.settings.get("vaccum_hopper_particles")) )
                        hopper.getWorld().spawnParticle(Particle.SPELL_WITCH, particle_loc, 25, 0.3f, 0.2f, 0.3f, 0.1f);
                    try {
                        for (Entity nearby : hopper.getWorld().getNearbyEntities(hopper, VaccumHopper.range, VaccumHopper.range, VaccumHopper.range)) {
                            if (nearby.getType().equals(EntityType.DROPPED_ITEM)) {
                                Location aboveHopper = hopper.clone();
                                aboveHopper.add(.5f, 1.25f, .5f);
                                Vector direction = aboveHopper.toVector().subtract(nearby.getLocation().toVector()).normalize();
                                nearby.setVelocity(nearby.getVelocity().add(direction.divide(new Vector(2, 2, 2))));
                            }
                        }
                    } catch (Exception ex ) {
                        Bukkit.broadcastMessage(ex.toString());
                    }

                }
            }
        };

        loop.runTaskTimer(this.plugin, 0L, 35L);
    }

    @EventHandler
    void onHopperPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        Location loc = event.getBlockPlaced().getLocation();
        Location particle_loc = loc.clone();
        particle_loc.add(0.5f, 0.65f, 0.5f);

        // Error checking
        if ( hand == null || loc == null || hand.getType().equals(Material.AIR) || !hand.getType().equals(Material.HOPPER)) return;

        if ( Boolean.parseBoolean(FileManager.settings.get("vaccum_hopper_particles"))) {
            loc.getWorld().spawnParticle(Particle.COMPOSTER, particle_loc, 25, 0.3f, 0.2f, 0.3f, 0.1f);
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
        }
        if ( Boolean.parseBoolean(FileManager.settings.get("vaccum_hopper_sound")))
            loc.getWorld().playSound(particle_loc, Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 0.1F);

        locations.add(loc);
        FileManager.SaveData("vaccum_hopper", locations);
    }

    @EventHandler
    void onHopperBreak(BlockBreakEvent event) {
        if ( locations.contains(event.getBlock().getLocation()) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            locations.remove(event.getBlock().getLocation());
            event.setDropItems(false);
            event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), GenerateItem());
            FileManager.SaveData("vaccum_hopper", locations);
        }
    }

}

