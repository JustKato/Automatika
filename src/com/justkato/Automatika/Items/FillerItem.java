package com.justkato.Automatika.Items;

import com.justkato.Automatika.Main;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FillerItem implements Listener {
    Main plugin;
    public FillerItem(Main _plugin) {
        this.plugin = _plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public static String localized = "filler";

    public static ItemStack GenerateItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(" ");
        meta.setLocalizedName(localized);

        item.setItemMeta(meta);
        return item;
    }

    // Prevent the filter item from ever dropping
    @EventHandler
    void onFillerDrop(ItemSpawnEvent event ) {
        if ( event.getEntity().getItemStack().equals(GenerateItem())) {
            event.getEntity().remove();
        }
    }

}
