package com.justkato.Automatika.Other;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static void SaveData(String key, List<Location> values) {
        File folder = new File("plugins" + File.separator + "Automatika");
        File blocksDB = new File(folder.getPath() + File.separator + "blockPlacement.yml");
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            // Create missing files
            if (!folder.exists()) { folder.mkdirs(); }
            if (!blocksDB.exists()) { blocksDB.createNewFile(); }

            yaml.load(blocksDB); // Load the config so that we don't over-write it completely

            yaml.set(key, values);

            yaml.save(blocksDB); // Save the file
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Location> LoadData(String key) {
        File folder = new File("plugins" + File.separator + "Automatika");
        File blocksDB = new File(folder.getPath() + File.separator + "blockPlacement.yml");
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            // Create missing files
            if (!folder.exists()) { return new ArrayList<>(); }
            if (!blocksDB.exists()) { return new ArrayList<>(); }

            yaml.load(blocksDB); // Load the config so that we don't over-write it completely

            if ( yaml.getList(key) == null ) return new ArrayList<>();

            // Load locations and save them to memory
            return (List<Location>) yaml.getList(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

}
