package com.justkato.Automatika.Commands;

import com.justkato.Automatika.Commands.Admin.OpenGui;
import com.justkato.Automatika.Commands.Admin.Reload;
import com.justkato.Automatika.Commands.User.Help;
import com.justkato.Automatika.Main;

public class CommandMaster {
    Main plugin;
    public CommandMaster(Main _plugin) {
        this.plugin = _plugin;
        InitializeCommands(_plugin);
    }

    public static void InitializeCommands(Main _plugin) {
        // Initialize all the other commands
        Help help = new Help(_plugin);
        OpenGui openGui = new OpenGui(_plugin);

    }

}
