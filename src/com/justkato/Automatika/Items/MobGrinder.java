package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
            ChatColor.GRAY + "The mob will be damaged by the",
            ChatColor.GRAY + "item inside of the block inventory.",
            "",
            ChatColor.GRAY + "Apply a redstone signal to power On/Off"
    };
    Main plugin;

    public MobGrinder(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        locations = FileManager.LoadData(localized);
    }

    public static ItemStack GenerateItem() {
        return ItemMaster.GenerateGenericItem(material, displayName, localized, lore);
    }



}
