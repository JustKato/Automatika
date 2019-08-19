package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ExpHopper implements Listener {
    // Hopper settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Experience Hopper";
    public static int range = 5;
    public static int exp_threshold = 8;
    static String localized = "exp_hopper";
    static Material material = Material.HOPPER;
    static String[] lore = {
            ChatColor.GRAY + "The hopper will attract experience drops",
            ChatColor.GRAY + "in a range of " + range + " blocks.",
            ChatColor.GRAY + "All collected XP will be stored in bottles",
    };
    Main plugin;
    public ExpHopper(Main plugin) {
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

                    if ( !loc.getChunk().isLoaded() ) continue;

                    Location arrival = loc.clone(); arrival.add(.5, .5, .5);
                    Entity closest = null;
                    boolean cancel = false;
                    Hopper hopper = (Hopper) loc.getBlock().getState();
                    boolean keepGoing = false;

                    // check if inventory is full
                    for ( int i = 0; i < hopper.getInventory().getSize(); i++ ) {
                        if ( hopper.getInventory().getItem(i) == null || hopper.getInventory().getItem(i).getType().equals(Material.AIR ) ) {
                            keepGoing = true;
                            break;
                        }
                    }

                    if ( !keepGoing ) continue;
                    // Run the hopper logic
                    for (Entity entity : arrival.getWorld().getNearbyEntities(arrival, range, range, range)) {
                        if ( entity instanceof ExperienceOrb ){
                            if ( entity.getLocation().distance(arrival) > 1.25f ) {
                                Vector dir = arrival.toVector().subtract(entity.getLocation().toVector());
                                entity.setVelocity(dir.multiply(0.25f));
                            } else {
                                ExperienceOrb orb = (ExperienceOrb) entity;
                                if ( orb.getExperience() >= exp_threshold ) {
                                    hopper.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, orb.getExperience()/exp_threshold ));
                                    orb.setExperience(orb.getExperience()%exp_threshold );
                                }
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
