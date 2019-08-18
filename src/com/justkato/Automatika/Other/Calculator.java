package com.justkato.Automatika.Other;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class Calculator {

    public static float CalculateItemDamage(ItemStack item) {
        return CalculateItemDamage(item, null);
    }

    public static float CalculateItemDamage(ItemStack item, Entity entity) {
        float damage;

        if (item == null || item.getType().equals(Material.AIR)) return 1;

        switch (item.getType() ) {
            case WOODEN_SWORD:
            case GOLDEN_SWORD:
                damage = 4;
                break;

            case STONE_SWORD:
                damage = 5;
                break;

            case IRON_SWORD:
                damage = 6;
                break;

            case DIAMOND_SWORD:
            case WOODEN_AXE:
            case GOLDEN_AXE:
                damage = 7;
                break;

            case STONE_AXE:
            case IRON_AXE:
            case DIAMOND_AXE:
                damage = 9;
                break;

            default:
                damage = 1;
                break;
        }

        try { damage += item.getEnchantmentLevel(Enchantment.DAMAGE_ALL) * 0.5f; } catch (Exception ex ) {};

        try {
            if (entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Wither || entity instanceof WitherSkeleton
                || entity instanceof PigZombie || entity instanceof Stray || entity instanceof Husk || entity instanceof Drowned) {
                damage += item.getEnchantmentLevel(Enchantment.DAMAGE_UNDEAD) * 2.5;
            }
        } catch (Exception ex ) {};
        try {
            if ( entity instanceof Spider ||entity instanceof CaveSpider ) {
                damage += item.getEnchantmentLevel(Enchantment.DAMAGE_ARTHROPODS) * 2.5;
            }
        } catch (Exception ex ) {};
        System.out.println(damage);
        return damage;
    }

}
