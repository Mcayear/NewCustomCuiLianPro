package lvhaoxuan.custom.cuilian.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.event.EventPriority;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.Stone;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class FurnaceListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.hasBlock() && e.getClickedBlock().getType().equals(Material.FURNACE)) {
            Player p = e.getPlayer();
            Furnace furnace = (Furnace) e.getClickedBlock().getState();
            furnace.setMetadata("FurnaceOwner", new FixedMetadataValue(NewCustomCuiLianPro.ins, p.getName()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void FurnaceBurnEvent(FurnaceBurnEvent e) {
        Furnace furnace = (Furnace) e.getBlock().getState();
        ItemStack fuel = e.getFuel();
        ItemStack smelt = furnace.getInventory().getSmelting();
        Stone stone = Stone.byItemStack(fuel);
        Level level = Level.byItemStack(smelt);
        if (CuiLianAPI.canCuiLian(smelt)) {
            if (stone != null && Level.levels.get((level != null ? level.value : 0) + stone.riseLevel) != null) {
                furnace.setMetadata("FurnaceFuel", new FixedMetadataValue(NewCustomCuiLianPro.ins, stone));
                e.setBurning(true);
                e.setBurnTime(200);
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void FurnaceSmeltEvent(FurnaceSmeltEvent e) {
        ItemStack smelt = e.getSource();
        Furnace furnace = (Furnace) e.getBlock().getState();
        if (furnace.hasMetadata("FurnaceFuel")) {
            Stone stone = (Stone) furnace.getMetadata("FurnaceFuel").get(0).value();
            String name = furnace.hasMetadata("FurnaceOwner") ? furnace.getMetadata("FurnaceOwner").get(0).asString() : "";
            Player p = Bukkit.getPlayer(name);
            smelt.setAmount(1);
            smelt = CuiLianAPI.cuilian(stone, smelt, p);
            e.setResult(smelt);
            furnace.removeMetadata("FurnaceFuel", NewCustomCuiLianPro.ins);
        } else if (CuiLianAPI.canCuiLian(smelt)) {
            e.setResult(smelt);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void InventoryClickEvent(InventoryClickEvent e) {
        if (e.getInventory().getType() == InventoryType.FURNACE && e.getSlotType() == InventoryType.SlotType.FUEL && Stone.byItemStack(e.getCursor()) != null) {
            ItemStack cursor = e.getCursor();
            ItemStack currentItem = e.getCurrentItem();
            e.setCursor(currentItem);
            e.setCurrentItem(cursor);
            e.setCancelled(true);
        }
    }
}
