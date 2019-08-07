package com.justkato.Automatika.Items;

import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemMaster {

    public static List<ItemStack> getItemlist() {
        List<ItemStack> item_list = new ArrayList<>();
        item_list.add(VaccumHopper.GenerateItem());
        item_list.add(BlockBreaker.GenerateItem());
        item_list.add(BlockPlacer.GenerateItem());

        return item_list;
    }

}
