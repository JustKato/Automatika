package com.justkato.Automatika.Events;

import com.justkato.Automatika.Items.*;
import com.justkato.Automatika.Main;

public class EventMaster {
    Main plugin;
    public EventMaster(Main _plugin) {
        this.plugin = _plugin;
        InitializeEvents(_plugin);
    }

    public static void InitializeEvents(Main plugin) {
        // Initialize all the other Events
        VaccumHopper vaccumHopper = new VaccumHopper(plugin);
        BlockBreaker autoBreaker = new BlockBreaker(plugin);
        BlockPlacer blockPlacer = new BlockPlacer(plugin);
        AutoDropper autoDropper = new AutoDropper(plugin);
        AutoPlanter autoPlanter = new AutoPlanter(plugin);
        MobGrinder mobGrinder = new MobGrinder(plugin);

        FillerItem fillerItem = new FillerItem(plugin);
    }


}
