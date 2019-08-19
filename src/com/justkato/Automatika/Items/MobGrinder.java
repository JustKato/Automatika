package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.Calculator;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MobGrinder implements Listener {
    // Hopper settings
    public static int range = 5;
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Mob grinder";
    static String localized = "mob_grinder";
    static Material material = Material.DISPENSER;
    static String[] lore = {
            ChatColor.GRAY + "Kill any mob in a " + range + " block range " + ChatColor.BLUE + "" + ChatColor.BOLD + "(AOE)",
            ChatColor.GRAY + "Item drops are " + ChatColor.BLUE + "" + ChatColor.BOLD + "NOT" + ChatColor.GRAY + " collected.",
            "",
            ChatColor.GRAY + "Apply a redstone signal to power On/Off",
            "",
            ChatColor.GRAY + "The mob will be damaged by the",
            ChatColor.GRAY + "item inside of the block inventory.",
            "",
            ChatColor.RED + "UNIMPLEMENTED :",
            ChatColor.GRAY + "Mob drops are influenced by the.",
            ChatColor.GRAY + "block's inventory item.",

    };
    Main plugin;

    public MobGrinder(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
        InitializeGrinderLoop();
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }

    void InitializeGrinderLoop() {
        BukkitRunnable loop = new BukkitRunnable() {
            @Override
            public void run() {
                for ( Location loc : locations ) {

                    if ( !loc.getChunk().isLoaded() ) continue;

                    Block block = loc.getBlock(); // Get the block
                    Dispenser grinder_block = (Dispenser) block.getState();
                    ItemStack attack_item = grinder_block.getInventory().getItem(4);
                    Location particle_location = loc.clone();
                    particle_location.add(.5, .5, .5);

                    // Check if the block is powered
                    if ( block.isBlockPowered() ) {
                        for (Entity ent : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
                            if ( ent instanceof Monster ) {
                                if ( ent.getType().equals(EntityType.EXPERIENCE_ORB) ) continue;
                                Monster monster = (Monster) ent;
                                loc.getWorld().spawnParticle(Particle.CRIT, ent.getLocation(), 20, 0.3f, 0.2f, 0.3f, 0.06f);
                                monster.damage(Calculator.CalculateItemDamage(attack_item, ent));
                            }
                        }
                    }

                }
            }
        };

        loop.runTaskTimer(this.plugin, 10l, 20l);
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

        Dispenser placed = (Dispenser) event.getBlockPlaced().getState();
        Inventory inv = placed.getInventory();

        for ( int i = 0; i < inv.getSize(); i++ ) inv.setItem(i, FillerItem.GenerateItem()); // Fill with filler :)
        inv.setItem(4, new ItemStack(Material.AIR)); // Replace the middle with AIR ( PICK SLOT )

        locations.add(loc);
        FileManager.SaveData(localized, locations);
    }

    @EventHandler
    void onAutoBreakerInv(InventoryClickEvent event) {
        if ( event.getView().getTitle().equals(displayName)) {
            if ( !event.getClickedInventory().getType().equals(InventoryType.PLAYER) )
                if ( event.getSlot() != 4 ) {
                    event.setCancelled(true);
                    return;
                }
        }
    }

    @EventHandler
    void onRedstone(BlockDispenseEvent event) {
        Block bloc = event.getBlock();
        Location loc = bloc.getLocation();
        if ( locations.contains(loc) ) {
            event.setCancelled(true);
        }
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
