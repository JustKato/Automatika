package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutoPlanter implements Listener {
    // Hopper settings
    public static List<Location> locations = new ArrayList<>();
    // Default item generator Settings
    static String displayName = ChatColor.GREEN + "Automatic Planter";
    static String localized = "auto_planter";
    static Material material = Material.DISPENSER;
    static String[] lore = {
            ChatColor.GRAY + "This item will plant seeds",
            ChatColor.GRAY + "from it's inventory on the block it's facing"
    };
    Main plugin;
    public AutoPlanter(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
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
        loc.getWorld().dropItemNaturally(event.getBlock().getLocation(), AutoPlanter.GenerateItem()); // Drops the Block Breaker

        // Remove from the list
        locations.remove(loc);
        // Save the block_breakers to DB
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
//        if ( !Boolean.parseBoolean(FileManager.settings.get("block_breaker_enabled")) ) return;
        Block bloc = event.getBlock();
        Location loc = bloc.getLocation();
        if ( locations.contains(loc) ) {
            event.setCancelled(true);
            org.bukkit.block.data.type.Dispenser dispenser = (org.bukkit.block.data.type.Dispenser) bloc.getBlockData();
            Dispenser dispenser_block = (Dispenser) bloc.getState();
            BlockFace blockFace = dispenser.getFacing();
            Location front = loc.clone();
            front.add(blockFace.getDirection());

            Block block_in_front = front.getBlock();
            Location particle_location = block_in_front.getLocation().clone();
            particle_location.add(.5f, 1.25f, .5f);

            ItemStack to_plant = dispenser_block.getInventory().getItem(4);
            Block plant = block_in_front.getLocation().clone().add(0, 1, 0).getBlock();

            if ( !plant.getType().equals(Material.AIR) ) {
                loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 1f, 0.75f);
                return;
            }

            if ( getSeedBlock(to_plant) != null ) {
                Material plant_material = getSeedBlock(to_plant);
                Material plant_on_mater = getPlantSoil(plant_material);

                if ( block_in_front.getType().equals(plant_on_mater) || (block_in_front.getType().equals(Material.DIRT) && plant_on_mater.equals(Material.GRASS_BLOCK)) ) {
                    plant.setType(plant_material); // Set the plant's block
                    to_plant.setAmount(to_plant.getAmount() - 1); // Take 1 away from the inventory
                } else {
                    loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 1f, 0.75f);
                    return;
                }
            } else {
                loc.getWorld().playSound(loc, Sound.ITEM_SHIELD_BREAK, 1f, 0.75f);
                return;
            }

        }
    }

    public static Material getSeedBlock(ItemStack item) {
        return getSeedBlock(item.getType());
    }

    public static Material getSeedBlock(Material item) {
        Material[][] plants = {
                {Material.WHEAT_SEEDS, Material.WHEAT},
                {Material.POTATO, Material.POTATO},
                {Material.CARROT, Material.CARROT},
                {Material.BEETROOT, Material.BEETROOT_SEEDS},
                {Material.MELON_SEEDS, Material.MELON_STEM},
                {Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM},
                {Material.BAMBOO, Material.BAMBOO_SAPLING},
                {Material.CACTUS, Material.CACTUS},

                {Material.OAK_SAPLING, Material.OAK_SAPLING},
                {Material.ACACIA_SAPLING, Material.ACACIA_SAPLING},
                {Material.BIRCH_SAPLING, Material.BIRCH_SAPLING},
                {Material.DARK_OAK_SAPLING, Material.DARK_OAK_SAPLING},
                {Material.JUNGLE_SAPLING, Material.JUNGLE_SAPLING},
                {Material.SPRUCE_SAPLING, Material.SPRUCE_SAPLING}
        };

        for ( int i = 0; i < plants.length; i++ ) {
            if ( item.equals(plants[i][0])) {
                return plants[i][1];
            }
        }

        return null;
    }

    public static Material getPlantSoil(Material plant) {
        Material[][] soils = {
                {Material.WHEAT_SEEDS, Material.FARMLAND},
                {Material.POTATO, Material.FARMLAND},
                {Material.CARROT, Material.FARMLAND},
                {Material.BEETROOT, Material.FARMLAND},
                {Material.MELON_STEM, Material.FARMLAND},
                {Material.PUMPKIN_STEM, Material.FARMLAND},
                {Material.BAMBOO, Material.GRASS_BLOCK},
                {Material.CACTUS, Material.SAND},

                {Material.OAK_SAPLING, Material.GRASS_BLOCK},
                {Material.ACACIA_SAPLING, Material.GRASS_BLOCK},
                {Material.BIRCH_SAPLING, Material.GRASS_BLOCK},
                {Material.DARK_OAK_SAPLING, Material.GRASS_BLOCK},
                {Material.JUNGLE_SAPLING, Material.GRASS_BLOCK},
                {Material.SPRUCE_SAPLING, Material.GRASS_BLOCK}
        };

        for ( int i = 0; i < soils.length; i++ ) {
            if ( plant.equals(soils[i][0])) {
                return soils[i][1];
            }
        }

        return Material.FARMLAND;
    }

}
