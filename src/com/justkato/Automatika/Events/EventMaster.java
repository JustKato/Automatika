package com.justkato.Automatika.Events;

import com.justkato.Automatika.Items.AutoBreaker;
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
        AutoBreaker autoBreaker = new AutoBreaker(plugin);
        FillerItem fillerItem = new FillerItem(plugin);
    }


}
