package com.justkato.Automatika;

import com.justkato.Automatika.Commands.CommandMaster;
import com.justkato.Automatika.Events.EventMaster;
import com.justkato.Automatika.Other.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static EventMaster eventMaster;
    public static CommandMaster commandMaster;

    @Override
    public void onEnable() {
        // Initialize the config
        FileManager.InitializeConfig();

        // Initialize the events and commands
        eventMaster = new EventMaster(this);
        commandMaster= new CommandMaster(this);
    }

    @Override
    public void onDisable() {

    }
}
