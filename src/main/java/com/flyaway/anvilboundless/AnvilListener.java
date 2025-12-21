package com.flyaway.anvilboundless;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.Map;

public class AnvilListener implements Listener {
    private final AnvilBoundless plugin;

    public AnvilListener(AnvilBoundless plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack resultItem = event.getResult();

        AnvilView anvilView = event.getView();

        if (plugin.isBypassTooExpensive()) {
            anvilView.setMaximumRepairCost(Integer.MAX_VALUE);
        }

        if (firstItem == null || resultItem == null || !resultItem.hasItemMeta()) {
            return;
        }

        fixEnchantmentLevels(firstItem, secondItem, resultItem);

        fixRepairCost(anvilView);

        event.setResult(resultItem);
    }

    private void fixRepairCost(AnvilView anvilView) {
        int maxRepairCost = plugin.getMaxRepairCost();
        if (maxRepairCost > 0 && anvilView.getRepairCost() > maxRepairCost) {
            anvilView.setRepairCost(maxRepairCost);
        }
    }

    private void fixEnchantmentLevels(ItemStack firstItem, ItemStack secondItem, ItemStack resultItem) {
        if (!plugin.isKeepOverleveledEnchants()) return;

        Map<Enchantment, Integer> firstEnchants = getEnchantments(firstItem);
        Map<Enchantment, Integer> secondEnchants = getEnchantments(secondItem);
        Map<Enchantment, Integer> resultEnchants = getEnchantments(resultItem);

        Map<Enchantment, Integer> maxEnchants = new HashMap<>(firstEnchants);

        for (Map.Entry<Enchantment, Integer> entry : secondEnchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int secondLevel = entry.getValue();
            int firstLevel = maxEnchants.getOrDefault(ench, 0);

            if (secondLevel > firstLevel) {
                maxEnchants.put(ench, secondLevel);
            }
        }

        ItemMeta resultMeta = resultItem.getItemMeta();
        boolean changed = false;

        for (Map.Entry<Enchantment, Integer> entry : resultEnchants.entrySet()) {
            Enchantment ench = entry.getKey();
            int resultLevel = entry.getValue();
            int maxLevel = maxEnchants.getOrDefault(ench, 0);

            if (resultLevel < maxLevel) {
                resultMeta.addEnchant(ench, maxLevel, true);
                changed = true;
            }
        }

        if (changed) {
            resultItem.setItemMeta(resultMeta);
        }
    }

    private Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            enchantments.putAll(item.getItemMeta().getEnchants());
        }

        return enchantments;
    }
}
