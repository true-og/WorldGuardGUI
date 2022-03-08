package me.heymrau.worldguardguiplugin.inventories;

import com.hakan.inventoryapi.inventory.ClickableItem;
import com.hakan.inventoryapi.inventory.HInventory;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.RequiredArgsConstructor;
import me.heymrau.worldguardguiplugin.WorldGuardGUIPlugin;
import me.heymrau.worldguardguiplugin.model.CustomItem;
import me.heymrau.worldguardguiplugin.model.Template;
import me.heymrau.worldguardguiplugin.utils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class TemplateInventory {
    private final WorldGuardGUIPlugin plugin;

    private List<String> getLore(Template template) {
        final List<String> lore = new ArrayList<>();
        lore.add("&7");
        lore.add("&7Allowed Flags:");
        template.getEnabledFlags().forEach(flag -> {
            if (flag != null) lore.add(" &8- &a" + flag.getName());
        });
        lore.add("&7");
        lore.add("&7Denied Flags:");
        template.getDeniedFlags().forEach(flag -> {
            if (flag != null) lore.add(" &8- &c" + flag.getName());
        });
        lore.add("&7");
        lore.add("&eClick to set as template");
        return lore;
    }

    public void open(Player player, String regionName) {
        final HInventory inventory = plugin.getInventoryAPI().getInventoryCreator().setSize(5).setTitle(ChatColor.GRAY + "Template Management").create();
        inventory.guiAir();
        int i = 0;
        for (Template template : plugin.getTemplateManager().getTemplatesList()) {
            if (i <= 35) {
                final List<String> lore = getLore(template);
                final ItemStack itemStack = new CustomItem("&e" + template.getName(), lore, XMaterial.GRASS_BLOCK.parseMaterial(), false, (short) 0, 1).complete();
                inventory.setItem(i, ClickableItem.of(itemStack, event -> {
                    ProtectedRegion region = plugin.getWorldGuard().getRegionByName(regionName);
                    final HashMap<Flag<?>, Object> flags = new HashMap<>();
                    template.getEnabledFlags().stream().filter(Objects::nonNull).forEach(flag -> flags.put(flag, StateFlag.State.ALLOW));
                    template.getDeniedFlags().stream().filter(Objects::nonNull).forEach(flag -> flags.put(flag, StateFlag.State.DENY));
                    region.setFlags(flags);
                    player.sendMessage(ChatColor.YELLOW + "Region template changed");
                    inventory.close(player);

                }));
            }
            i++;
        }
        inventory.setItem(40, ClickableItem.of(new CustomItem("&cClose", null, Material.BARRIER, false, (short) 0, 1).complete(), (event) -> event.getWhoClicked().closeInventory()));
        inventory.open(player);
    }
}
