package com.justkato.Automatika.Events;

import com.justkato.Automatika.Items.*;
import com.justkato.Automatika.Main;
import org.bukkit.entity.ExperienceOrb;

import java.io.File;

public class EventMaster {
    Main plugin;
    public EventMaster(Main _plugin) {
        this.plugin = _plugin;
        InitializeEvents(_plugin);
    }

    public static void InitializeEvents(Main plugin) {
        // Initialize all the other Events
        VaccumHopper vaccumHopper = new VaccumHopper(plugin);
        EnderHopper enderHopper = new EnderHopper(plugin);
        MobHopper mobHopper = new MobHopper(plugin);
        ExpHopper expHopper = new ExpHopper(plugin);

        BlockBreaker autoBreaker = new BlockBreaker(plugin);
        BlockPlacer blockPlacer = new BlockPlacer(plugin);
        AutoDropper autoDropper = new AutoDropper(plugin);
        AutoPlanter autoPlanter = new AutoPlanter(plugin);
        MobGrinder mobGrinder = new MobGrinder(plugin);
        Harvester harvester = new Harvester(plugin);

        FillerItem fillerItem = new FillerItem(plugin);
    }


}
