package com.justkato.Automatika.Items;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemMaster {

    public static List<ItemStack> getItemlist() {
        List<ItemStack> item_list = new ArrayList<>();
        item_list.add(VaccumHopper.GenerateItem());
        item_list.add(EnderHopper.GenerateItem());
        item_list.add(MobHopper.GenerateItem());
        item_list.add(ExpHopper.GenerateItem());

        item_list.add(AutoDropper.GenerateItem());
        item_list.add(AutoPlanter.GenerateItem());
        item_list.add(Harvester.GenerateItem());

        item_list.add(BlockBreaker.GenerateItem());
        item_list.add(BlockPlacer.GenerateItem());
        item_list.add(MobGrinder.GenerateItem());

        return item_list;
    }

    public static ItemStack GenerateGenericItem(Material material, String displayName, String localized, String[] lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setLocalizedName(localized);
        meta.setLore(Arrays.asList(lore));
        meta.setCustomModelData(23);

        item.setItemMeta(meta);
        return item;
    }

}
