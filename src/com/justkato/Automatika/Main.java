package com.justkato.Automatika;

import com.justkato.Automatika.Commands.CommandMaster;
import com.justkato.Automatika.Events.EventMaster;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static EventMaster eventMaster;
    public static CommandMaster commandMaster;

    @Override
    public void onEnable() {
        eventMaster = new EventMaster(this);
        commandMaster= new CommandMaster(this);
    }

    @Override
    public void onDisable() {

    }
}
