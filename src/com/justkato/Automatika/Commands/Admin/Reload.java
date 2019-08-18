package com.justkato.Automatika.Commands.Admin;

import com.justkato.Automatika.Main;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload {
    public static String gui_name = "Item GUI";
    Main plugin;

    public Reload(Main _plugin) {
        this.plugin = _plugin;
//        this.plugin.getCommand("automatika").setExecutor(this::onCommand);
    }


    public static void cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if ( args[0].toLowerCase().equals("reload") ) {
            try {
                FileManager.InitializeConfig();
                sender.sendMessage(ChatColor.GREEN + "Succesfully reloaded the Automatika config");
            } catch (Exception ex) {
                ex.printStackTrace();
                sender.sendMessage(ChatColor.RED + "There has been a problem reloading the config");
                sender.sendMessage(ChatColor.RED + "If the problem persists, please delete the config file");
            }
        }
    }

}
