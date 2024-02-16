package lvhaoxuan.custom.cuilian.listener;

import java.util.HashMap;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.message.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.ProtectRune;
import lvhaoxuan.llib.api.LLibAPI;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

public class ProtectRuneListener implements Listener {

    public static HashMap<String, ItemMeta> userMap = new HashMap<>();
    public static HashMap<String, ProtectRune> protectRuneMap = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getRawSlot() >= 0 && e.isRightClick() && (e.getInventory().getType() != InventoryType.CRAFTING || e.getInventory().getType() != InventoryType.PLAYER)) {
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (userMap.containsKey(p.getName())) {
                if (e.getInventory().getType() != InventoryType.PLAYER && e.getInventory().getType() != InventoryType.CRAFTING) {
                    p.sendMessage(Message.MUST_ADD_IN_BAG);
                    return;
                }
                if (!LLibAPI.checkItemNull(item)) {
                    cancelWithMessage(p, Message.CANCEL_ADD);
                    e.setCancelled(true);
                    return;
                }
                ProtectRune protectRune = protectRuneMap.get(p.getName());
                Level level = Level.byItemStack(item);
                int basicLevel = (level != null ? level.value : 0);
                if (!CuiLianAPI.canCuiLian(item) || !LLibAPI.inventoryHasItem(p.getInventory(), protectRune.item) || !(basicLevel >= protectRune.level.value)) {
                    cancelWithMessage(p, Message.CAN_NOT_ADD);
                    e.setCancelled(true);
                    return;
                }
                e.setCurrentItem(CuiLianAPI.addProtectRune(item, protectRune));
                p.sendMessage(Message.ADD_SUCCESS.replace("%s", protectRune.lore));
                ItemStack clone = protectRune.item;
                clone.setAmount(1);
                LLibAPI.inventoryRemoveItem(p.getInventory(), clone);
                userMap.remove(p.getName());
                protectRuneMap.remove(p.getName());
                p.closeInventory();
                e.setCancelled(true);
            } else if (LLibAPI.checkItemNull(item)) {
                ProtectRune protectRune = ProtectRune.byItemStack(item);
                if (protectRune != null) {
                    ItemStack clone = protectRune.item;
                    clone.setAmount(1);
                    if (protectRune != null) {
                        if (!LLibAPI.inventoryHasItem(p.getInventory(), clone)) {
                            cancelWithMessage(p, Message.MUST_ADD_IN_BAG);
                            e.setCancelled(true);
                            return;
                        }
                        for (String str : Message.ADD_MESSAGE) {
                            p.sendMessage(str.replace("%s", protectRune.lore));
                        }
                        userMap.put(p.getName(), meta);
                        protectRuneMap.put(p.getName(), protectRune);
                        p.closeInventory();
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    public void cancelWithMessage(Player p, String msg) {
        p.closeInventory();
        p.sendMessage(msg);
        userMap.remove(p.getName());
        protectRuneMap.remove(p.getName());
    }
}
