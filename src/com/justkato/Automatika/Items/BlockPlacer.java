package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.apache.logging.log4j.core.tools.Generate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockPlacer implements Listener {
    public static List<Location> locations = new ArrayList<>();

    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Block Placer";
    static String localized = "block_placer";
    static Material material = Material.DISPENSER;
    static String[] lore = {
            ChatColor.GRAY + "Automatically place blocks from",
            ChatColor.GRAY + "The block's inventory on redstone signal",
    };

    Main plugin;
    public BlockPlacer(Main _plugin) {
        this.plugin = _plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        locations = FileManager.LoadData(localized);
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
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
            // Placing a BlockPlacer
            locations.add(loc); // Add the block Placer to the list
            // Play some particles/sounds
            if ( Boolean.parseBoolean(FileManager.settings.get("block_placer_particles")) ) {
                p.spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
                p.spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
            }
            Dispenser placed = (Dispenser) event.getBlockPlaced().getState();
            Inventory inv = placed.getInventory();

            for ( int i = 0; i < inv.getSize(); i++ ) inv.setItem(i, FillerItem.GenerateItem()); // Fill with filler :)
            inv.setItem(4, new ItemStack(Material.AIR)); // Replace the middle with AIR ( PICK SLOT )

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
            // Breaking a block placer
            // Play some pretty animations
            event.setDropItems(false); // Remove the Drops
            loc.getWorld().dropItemNaturally(loc, GenerateItem()); // Drop the fixed block
            if ( Boolean.parseBoolean(FileManager.settings.get("block_placer_particles")) ) {
                p.spawnParticle(Particle.SMOKE_NORMAL, particle_loc, 30, 0.3f, 0.2f, 0.3f, 0.06f);
                p.spawnParticle(Particle.SPIT, particle_loc, 20, 0.3f, 0.2f, 0.3f, 0.075f);
            }
            locations.remove(loc); // Remove the location from the location list
            FileManager.SaveData(localized, locations);
        }
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
    void onPower(BlockDispenseEvent event) {
        if ( !Boolean.parseBoolean(FileManager.settings.get("block_placer_enabled")) ) return;
        Block block = event.getBlock();
        Location loc = block.getLocation();
        if ( locations.contains(loc) ) { // The block is a BlockPlacer
            event.setCancelled(true);
            Dispenser dispenser_block = (Dispenser) block.getState(); // Get the dispenser
            ItemStack toplace = dispenser_block.getInventory().getItem(4); // Get the block to place
            if ( toplace == null || toplace.getType().equals(Material.AIR) || !toplace.getType().isBlock() ) {
                loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 1f, 0.75f);
                return;
            }

            // Get the block in front
            org.bukkit.block.data.type.Dispenser dispenser = (org.bukkit.block.data.type.Dispenser) block.getBlockData();
            BlockFace blockFace = dispenser.getFacing();
            Location front = loc.clone();
            front.add(blockFace.getDirection());
            Block front_block = front.getBlock();

            if ( front_block == null || !front_block.getType().equals(Material.AIR) ) {

                if ( Boolean.parseBoolean(FileManager.settings.get("block_placer_sound")) )
                    loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 1f, 0.75f);
                return;
            }

            front_block.setType(toplace.getType());
            toplace.setAmount(toplace.getAmount()-1);
        }
    }

}
