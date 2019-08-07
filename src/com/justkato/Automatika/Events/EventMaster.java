package com.justkato.Automatika.Events;

import com.justkato.Automatika.Items.BlockBreaker;
import com.justkato.Automatika.Items.BlockPlacer;
import com.justkato.Automatika.Items.FillerItem;
import com.justkato.Automatika.Items.VaccumHopper;
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
        BlockPlacer blockPlacer =  new BlockPlacer(plugin);

        FillerItem fillerItem = new FillerItem(plugin);
    }


}
