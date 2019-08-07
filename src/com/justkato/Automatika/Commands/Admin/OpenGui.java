package com.justkato.Automatika.Commands.Admin;

import com.justkato.Automatika.Items.FillerItem;
import com.justkato.Automatika.Items.ItemMaster;
import com.justkato.Automatika.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OpenGui implements CommandExecutor, Listener {
    public static String gui_name = "Item GUI";
    Main plugin;
    public OpenGui(Main _plugin) {
        this.plugin = _plugin;
        this.plugin.getCommand("opengui").setExecutor(this::onCommand);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = null;
        if ( sender instanceof Player) p = (Player) sender;
        else { sender.sendMessage(ChatColor.RED + "Only players can access the GUI"); return true; }
        if ( !sender.hasPermission("Automatika.admin.gui") ) { sender.sendMessage(ChatColor.RED + "You do not have permissions for this command"); return true; }
        p.openInventory(GenerateGui());
        return true;
    }

    public static Inventory GenerateGui() {
        Inventory inv = Bukkit.createInventory(null, 9*5, gui_name);
        // Fill around the inventory
        // Top and bottom
        for ( int i = 0; i < 9; i++ ) {
            inv.setItem(i, FillerItem.GenerateItem());
            inv.setItem(9*4 + i, FillerItem.GenerateItem());
        }
        // Left and Right
        for ( int i = 1; i < 5; i++ ) {
            inv.setItem(9 * i, FillerItem.GenerateItem());
            inv.setItem((9 * i) - 1, FillerItem.GenerateItem());
        }

        // Put the items in
        for (ItemStack item: ItemMaster.getItemlist() ) inv.addItem(item);

        return inv;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    void onGuiInteract(InventoryClickEvent event) {
        if ( !event.getView().getTitle().equals(gui_name)) return;
        if ( event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
        event.setCancelled(true);
        if ( event.getCurrentItem().getType().equals(FillerItem.GenerateItem().getType()) ) return;
        event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
    }

}
