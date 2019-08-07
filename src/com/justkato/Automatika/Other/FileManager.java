package com.justkato.Automatika.Other;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    public static Map<String, String> settings = new HashMap<>();

    public static void SaveData(String key, List<Location> values) {
        File folder = new File("plugins" + File.separator + "Automatika");
        File blocksDB = new File(folder.getPath() + File.separator + "blockPlacement.yml");
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            // Create missing files
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!blocksDB.exists()) {
                blocksDB.createNewFile();
            }

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
            if (!folder.exists()) {
                return new ArrayList<>();
            }
            if (!blocksDB.exists()) {
                return new ArrayList<>();
            }

            yaml.load(blocksDB); // Load the config so that we don't over-write it completely

            if (yaml.getList(key) == null) return new ArrayList<>();

            // Load locations and save them to memory
            return (List<Location>) yaml.getList(key);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void InitializeConfig() {
        File folder = new File("plugins" + File.separator + "Automatika");
        File config = new File(folder.getPath() + File.separator + "config.yml");
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            // Create missing files
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!config.exists()) {
                // If the config doesn't exist then create the default config
                config.createNewFile();

                yaml.createSection("global");
                yaml.createSection("vaccum_hopper");
                yaml.createSection("block_breaker");
                yaml.createSection("block_placer");

                // Global Settings
                yaml.set("global.particles", "true");
                yaml.set("global.sounds", "true");

                // Vaccum Hopper Settings
                yaml.set("vaccum_hopper.enabled", "true");
                yaml.set("vaccum_hopper.range", "5");
                yaml.set("vaccum_hopper.particles", "true");
                yaml.set("vaccum_hopper.sound", "true");

                // BlockBreaker Settings
                yaml.set("block_breaker.enabled", "true");
                yaml.set("block_breaker.pick_tier", "true");
                yaml.set("block_breaker.particles", "true");
                yaml.set("block_breaker.sound", "true");

                // Block Placer Settings
                yaml.set("block_placer.enabled", "true");
                yaml.set("block_breaker.particles", "true");
                yaml.set("block_breaker.sound", "true");


                yaml.save(config); // Save the file

                InitializeConfig();
            } else {
                settings = LoadConfig();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static Map<String, String> LoadConfig() {
        Map<String, String> map = new HashMap<>();
        File folder = new File("plugins" + File.separator + "Automatika");
        File config = new File(folder.getPath() + File.separator + "config.yml");
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(config);

            // Global
            if ( yaml.get("global.particles") != null )
                map.put("global_particles", (String) yaml.get("global.particles"));
            else
                map.put("global_particles", "true");
            if ( yaml.get("global.sounds") != null )
                map.put("global_sounds", (String) yaml.get("global.sounds"));
            else
                map.put("global_sounds", "true");

            // Vaccum Hopper Settings
            if ( yaml.get("vaccum_hopper.enabled") != null )
                map.put("vaccum_hopper_enabled", (String) yaml.get("vaccum_hopper.enabled"));
            else
                map.put("vaccum_hopper_enabled", "true");

            if ( yaml.get("vaccum_hopper.range") != null )
                map.put("vaccum_hopper_range", (String) yaml.get("vaccum_hopper.range"));
            else
                map.put("vaccum_hopper_range", "5");

            if ( yaml.get("vaccum_hopper.particles") != null )
                map.put("vaccum_hopper_particles", (String) yaml.get("vaccum_hopper.particles"));
            else
                map.put("vaccum_hopper_particles", "true");

            if ( yaml.get("vaccum_hopper.sound") != null )
                map.put("vaccum_hopper_sound", (String) yaml.get("vaccum_hopper.sound"));
            else
                map.put("vaccum_hopper_sound", "true");

            // BlockBreaker Settings
            if ( yaml.get("block_breaker.enabled") != null )
                map.put("block_breaker_enabled", (String) yaml.get("block_breaker.enabled"));
            else
                map.put("block_breaker_enabled", "true");

            if ( yaml.get("block_breaker.pick_tier") != null )
                map.put("block_breaker_pick_tier", (String) yaml.get("block_breaker.pick_tier"));
            else
                map.put("block_breaker_pick_tier", "true");

            if ( yaml.get("block_breaker.particles") != null )
                map.put("block_breaker_particles", (String) yaml.get("block_breaker.particles"));
            else
                map.put("block_breaker_particles", "true");

            if ( yaml.get("block_breaker.sound") != null )
                map.put("block_breaker_sound", (String) yaml.get("block_breaker.sound"));
            else
                map.put("block_breaker_sound", "true");

            // Block Placer Settings
            if ( yaml.get("block_placer.enabled") != null )
                map.put("block_placer_enabled", (String) yaml.get("block_placer.enabled"));
            else
                map.put("block_placer_enabled", "true");

            if ( yaml.get("block_breaker.particles") != null )
                map.put("block_placer_particles", (String) yaml.get("block_breaker.particles"));
            else
                map.put("block_placer_particles", "true");

            if ( yaml.get("block_breaker.sound") != null )
                map.put("block_placer_sound", (String) yaml.get("block_breaker.sound"));
            else
                map.put("block_placer_sound", "true");


            for ( String key: map.keySet()) {
                if ( map.get(key) == null ) {
                    if ( !key.equals("vaccum_hopper_range") )
                        map.put(key, "true");
                    else
                        map.put(key, "5");
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
