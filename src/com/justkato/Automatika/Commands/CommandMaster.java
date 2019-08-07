package com.justkato.Automatika.Commands;

import com.justkato.Automatika.Commands.Admin.OpenGui;
import com.justkato.Automatika.Main;

public class CommandMaster {
    Main plugin;
    public CommandMaster(Main _plugin) {
        this.plugin = _plugin;
        InitializeCommands(_plugin);
    }

    public static void InitializeCommands(Main _plugin) {
        // Initialize all the other commands
        OpenGui openGui = new OpenGui(_plugin);

    }

}
