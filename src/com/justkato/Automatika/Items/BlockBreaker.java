package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBreaker implements Listener {
    // AutoBreaker settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Block Breaker";
    static String localized = "block_breaker";
    static Material material = Material.DISPENSER;
    static String[] lore = {
            ChatColor.GRAY + "A block that will break any other",
            ChatColor.GRAY + "block that is in front if it",
    };

    Main plugin;
    public BlockBreaker(Main _plugin) {
        this.plugin = _plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        locations = FileManager.LoadData("block_breaker");
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        Location loc = event.getBlockPlaced().getLocation();
        Location particle_loc = loc.clone();
        particle_loc.add(0.5f, 0.65f, 0.5f);

        if (hand == null || loc == null || particle_loc == null || !hand.getItemMeta().getLocalizedName().equals(localized)) return;

        if ( Boolean.parseBoolean(FileManager.settings.get("block_breaker_particles")) ) {
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
            loc.getWorld().spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
        }

        if ( Boolean.parseBoolean(FileManager.settings.get("block_breaker_sound")) ) {
            loc.getWorld().playSound(particle_loc, Sound.BLOCK_STONE_PLACE, 1.25F, 0.8F);
        }

        Dispenser placed = (Dispenser) event.getBlockPlaced().getState();
        Inventory inv = placed.getInventory();

        for ( int i = 0; i < inv.getSize(); i++ ) inv.setItem(i, FillerItem.GenerateItem()); // Fill with filler :)
        inv.setItem(4, new ItemStack(Material.AIR)); // Replace the middle with AIR ( PICK SLOT )

        BlockBreaker.locations.add(loc);
        FileManager.SaveData("block_breaker", locations);
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
    void onBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation(); // Get the location of the block
        Location particle_loc = loc.clone(); // initialize the particle spawn location
        particle_loc.add(0.5f, 0.65f, 0.5f); // Get the particle spawn location

        // Error check/Conditioin
        if ( !locations.contains(loc) ) return;
        // Spawn the particles
        if ( Boolean.parseBoolean(FileManager.settings.get("block_breaker_particles")) ) {
            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
            loc.getWorld().spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
        }
        // Fix the block-drop
        event.setDropItems(false); // Cancel the fake block Breaker
        loc.getWorld().dropItemNaturally(event.getBlock().getLocation(), GenerateItem()); // Drops the Block Breaker

        // Remove from the list
        locations.remove(loc);
        // Save the block_breakers to DB
        FileManager.SaveData("block_breaker", locations);
    }

    @EventHandler
    void onRedstone(BlockDispenseEvent event) {
        if ( !Boolean.parseBoolean(FileManager.settings.get("block_breaker_enabled")) ) return;
        Block bloc = event.getBlock();
        Location loc = bloc.getLocation();
        if ( locations.contains(loc) ) {
            event.setCancelled(true);
            org.bukkit.block.data.type.Dispenser dispenser = (org.bukkit.block.data.type.Dispenser) bloc.getBlockData();
            Dispenser dispenser_block = (Dispenser) bloc.getState();
            BlockFace blockFace = dispenser.getFacing();
            Location front = loc.clone();
            front.add(blockFace.getDirection());

            front.getBlock().breakNaturally(dispenser_block.getInventory().getItem(4));

        }
    }


}
