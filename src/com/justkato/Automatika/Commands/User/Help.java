package com.justkato.Automatika.Commands.User;

import com.justkato.Automatika.Commands.Admin.OpenGui;
import com.justkato.Automatika.Commands.Admin.Reload;
import com.justkato.Automatika.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ALL")
public class Help implements CommandExecutor, TabCompleter {
    public static String[] command_list = {"help", "gui", "reload"};

    public static String gui_name = "Item GUI";
    Main plugin;

    public Help(Main _plugin) {
        this.plugin = _plugin;
        this.plugin.getCommand("automatika").setExecutor(this::onCommand);
        this.plugin.getCommand("automatika").setTabCompleter(this::onTabComplete);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String command = "";
        if ( args.length > 0 ) command = args[0];

        switch (command) {
            case "reload" :
                // Run the help
                Reload.cmd(sender, cmd, label, args);
                break;

            case "gui" :
                // Run the help
                OpenGui.onCommand(sender, cmd, label, args);
                break;

            default:
                sender.sendMessage(ChatColor.AQUA + "==== Automatika ====");
                sender.sendMessage(ChatColor.WHITE + "/automatika help -" + ChatColor.GRAY + "Display this screen");
                sender.sendMessage(ChatColor.WHITE + "/automatika gui -" + ChatColor.GRAY + "Open the admin GUI");
                sender.sendMessage(ChatColor.WHITE + "/automatika reload -" + ChatColor.GRAY + "Reload the configuration file");
                sender.sendMessage(ChatColor.AQUA + "==================");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> pos_args = new ArrayList<>();
        if ( args.length <= 1 ) {
            for ( String str: command_list ) {
                if ( str.toLowerCase().contains(args[0].toLowerCase()) ) {
                    pos_args.add(str);
                }
            }
        }
        return pos_args;
    }
}
