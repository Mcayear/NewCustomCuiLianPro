package lvhaoxuan.custom.cuilian.movelevel;

import java.util.ArrayList;
import java.util.List;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.loader.Loader;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.llib.gui.Gui;
import lvhaoxuan.llib.gui.GuiButton;
import lvhaoxuan.llib.gui.GuiSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MoveLevelHandle {

    public static ScriptEngine engine;
    public static String moveLevelInvTitle;
    public static boolean enableMoveLevel = true;

    public static void init() {
        engine = Loader.loadMoveLevelScript();
    }

    public static void open(Player p) {
        if (enableMoveLevel) {
            ItemStack background;
            try {
                background = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) 7);
                ItemMeta meta = background.getItemMeta();
                meta.setDisplayName(" ");
                background.setItemMeta(meta);
            } catch (Exception ex) {
                background = new ItemStack(Material.getMaterial("BLACK_STAINED_GLASS_PANE"));
                ItemMeta meta = background.getItemMeta();
                meta.setDisplayName(" ");
                background.setItemMeta(meta);
            }
            Gui gui = new Gui(moveLevelInvTitle, 9, background);
            GuiSlot slot1 = new GuiSlot(2);
            gui.addSlot(slot1);
            GuiSlot slot2 = new GuiSlot(4);
            gui.addSlot(slot2);
            ItemStack item = new ItemStack(Material.ANVIL);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a淬炼移星");
            List<String> lore = new ArrayList();
            lore.add("§7把左边装备所有等级移给右边装备。");
            meta.setLore(lore);
            item.setItemMeta(meta);
            GuiButton button = new GuiButton(6, item) {

                @Override
                public void click(InventoryClickEvent e) {
                    Inventory inv = e.getInventory();
                    ItemStack left = inv.getItem(2);
                    ItemStack right = inv.getItem(4);
                    if (CuiLianAPI.canCuiLian(left) && CuiLianAPI.canCuiLian(right)) {
                        try {
                            Level level1 = Level.byItemStack(left);
                            int leftLevel = (level1 != null ? level1.value : 0);
                            if (leftLevel != 0) {
                                Level level2 = Level.byItemStack(right);
                                int rightLevel = (level2 != null ? level2.value : 0);
                                Invocable invocable = (Invocable) engine;
                                int targetLevel = Integer.parseInt((String) invocable.invokeFunction("handle", leftLevel, rightLevel));
                                targetLevel = targetLevel <= Level.levels.size() ? targetLevel : Level.levels.size();
                                Level targetLevelObj = Level.levels.get(targetLevel);
                                inv.setItem(4, CuiLianAPI.setItemLevel(right, targetLevelObj));
                                inv.setItem(2, CuiLianAPI.setItemLevel(left, null));
                            }
                        } catch (ScriptException | NoSuchMethodException ex) {
                            enableMoveLevel = false;
                        }
                    }
                }
            };
            gui.addButton(button);
            gui.open(p);
        }
    }
}
