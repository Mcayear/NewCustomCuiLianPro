package lvhaoxuan.custom.cuilian.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.message.Message;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.ProtectRune;
import lvhaoxuan.custom.cuilian.object.Stone;
import lvhaoxuan.llib.api.LLibAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class CuiLianAPI {

    public static boolean hasOffHand;

    static {
        hasOffHand = NewCustomCuiLianPro.judgeOffHand;
        if (hasOffHand) {
            try {
                EntityEquipment.class.getMethod("getItemInOffHand");
            } catch (NoSuchMethodException | SecurityException ex) {
                hasOffHand = false;
            }
        }
    }

    public static boolean canCuiLian(ItemStack item) {
        if (LLibAPI.checkItemNull(item)) {
            for (NewCustomCuiLianPro.ItemType type : NewCustomCuiLianPro.types) {
                if (type.type == item.getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack cuilian(Stone stone, ItemStack item, Player p) {
        if (canCuiLian(item)) {
            Level basicLevelObj = Level.byItemStack(item);
            int basicLevel = (basicLevelObj != null ? basicLevelObj.value : 0);
            Level toLevel;
            double probability = LLibAPI.getRandom(0, 100);
            boolean success = probability <= stone.chance.get(Level.levels.get(basicLevel + stone.riseLevel));
            String sendMessage = null;
            if (success) {
                toLevel = Level.levels.get(basicLevel + stone.riseLevel);
                item = setItemLevel(item, toLevel);

                String lvText = toLevel.lore.get(0);
                p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼成功", NamedTextColor.GREEN));
                p.sendTitlePart(TitlePart.SUBTITLE, Component.text(lvText));

                sendMessage = Message.SUCCESS.replace("%s", toLevel.lore.get(0));
                if (toLevel.value >= 5) {
                    Bukkit.broadcastMessage(Message.SERVER_SUCCESS.replace("%p", p.getDisplayName()).replace("%d", stone.item.getItemMeta().getDisplayName()).replace("%s", toLevel.lore.get(0)));
                }
            } else {
                int dropLevel = stone.dropLevel.toInteger();
                Level protectRune = Level.byProtectRune(item);
                if (protectRune != null) {
                    if (protectRune.value <= basicLevel) {
                        if (basicLevel - protectRune.value <= dropLevel) {
                            dropLevel = basicLevel - protectRune.value != 0 ? LLibAPI.getRandom(0, basicLevel - protectRune.value) : 0;
                        }
                        toLevel = Level.levels.get(basicLevel - dropLevel);
                        item = setItemLevel(item, toLevel);
                        sendMessage = Message.CUILIAN_FAIL_PROTECT_RUNE.replace("%s", toLevel.lore.get(0)).replace("%d", String.valueOf(dropLevel));
                    } else {
                        toLevel = Level.levels.get(basicLevel - dropLevel);
                        item = setItemLevel(item, toLevel);

                        String lvText = toLevel.lore.get(0);
                        p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼失败", NamedTextColor.RED));
                        p.sendTitlePart(TitlePart.SUBTITLE, Component.text(lvText));

                        sendMessage = Message.CUILIAN_FAIL.replace("%s", lvText).replace("%d", String.valueOf(dropLevel));
                    }
                } else {
                    toLevel = Level.levels.get(basicLevel - dropLevel);
                    item = setItemLevel(item, toLevel);
                    String lvText = toLevel != null ? toLevel.lore.get(0) : "§c§l淬炼消失";

                    p.sendTitlePart(TitlePart.TITLE, Component.text("淬炼失败", NamedTextColor.RED));
                    p.sendTitlePart(TitlePart.SUBTITLE, Component.text(lvText));
                    sendMessage = Message.CUILIAN_FAIL.replace("%s", lvText).replace("%d", String.valueOf(dropLevel));
                }
            }
            if (p != null) {
                p.sendMessage(sendMessage);
            }
        }
        return item;
    }

    public static ItemStack setItemLevel(ItemStack item, Level level) {
        if (canCuiLian(item)) {
            int basicLevel = (level != null ? level.value : 0);
            ItemMeta meta = item.getItemMeta();
            setDisplayName(item.getType(), meta, basicLevel);
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore = replaceLore(lore);
            lore = cleanLevel(lore);
            lore = cleanProtectRune(lore);
            if (level != null) {
                if (!Message.UNDER_LINE.isEmpty()) {
                    lore.add(Message.UNDER_LINE);
                }
                for (String line : level.lore) {
                    lore.add(NewCustomCuiLianPro.LEVEL_JUDGE + line);
                }
                for (String line : level.attribute.get(NewCustomCuiLianPro.typesInBag.get(item.getType()))) {
                    lore.add(NewCustomCuiLianPro.LEVEL_JUDGE + line);
                }
            }
            Level protectRuneLevel = Level.byProtectRune(item);
            if (protectRuneLevel != null && protectRuneLevel.protectRune != null) {
                lore.add(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE + protectRuneLevel.protectRune.lore);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void setDisplayName(Material type, ItemMeta meta, int basicLevel) {
        if (NewCustomCuiLianPro.displayNameFormat == 1) {
            String displayName = meta.hasDisplayName() ? meta.getDisplayName() : chineseDisplayName(type);
            displayName = displayName.replaceAll("\\+[0-9]* ", "");
            displayName = "§f+" + basicLevel + " " + displayName;
            meta.setDisplayName(displayName);
        } else if (NewCustomCuiLianPro.displayNameFormat == 2) {
            String displayName = meta.hasDisplayName() ? meta.getDisplayName() : chineseDisplayName(type);
            displayName = displayName.replaceAll(" \\+[0-9]*", "");
            displayName = displayName + " +" + basicLevel;
            meta.setDisplayName(displayName);
        }
    }

    public static ItemStack addProtectRune(ItemStack item, ProtectRune protectRune) {
        if (LLibAPI.checkItemNull(item)) {
            if (protectRune != null) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore = cleanProtectRune(lore);
                lore.add(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE + protectRune.lore);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }

    public static List<String> cleanLevel(List<String> lore) {
        Iterator<String> iterator = lore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.contains(NewCustomCuiLianPro.LEVEL_JUDGE) || line.equals(Message.UNDER_LINE)) {
                iterator.remove();
            }
        }
        return lore;
    }

    public static List<String> cleanProtectRune(List<String> lore) {
        Iterator<String> iterator = lore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.contains(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE)) {
                iterator.remove();
            }
        }
        return lore;
    }

    public static List<String> replaceLore(List<String> lore) {
        Iterator<String> iterator = lore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            for (String replace : NewCustomCuiLianPro.replaceLore) {
                if (line.contains(replace)) {
                    iterator.remove();
                }
            }
        }
        return lore;
    }

    public static Level getMinLevel(LivingEntity entity, EntityEquipment equipment) {
        int ret = -1;
        for (ItemStack item : equipment.getArmorContents()) {
            Level level = Level.byItemStack(item);
            int basicLevel = (level != null ? level.value : 0);
            ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        }
        ItemStack item = LLibAPI.getItemInHand(entity);
        Level level = Level.byItemStack(item);
        int basicLevel = (level != null ? level.value : 0);
        ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        if (hasOffHand) {
            item = LLibAPI.getItemInOffHand(entity);
            level = Level.byItemStack(item);
            basicLevel = (level != null ? level.value : 0);
            ret = (ret == -1 ? basicLevel : Math.min(ret, basicLevel));
        }
        return Level.levels.get(ret);
    }

    public static String chineseDisplayName(Material type) {
        switch (type) {
            case BOW:
                return "§f弓";
            case IRON_SWORD:
                return "§f铁剑";
            case WOODEN_SWORD:
                return "§f木剑";
            case STONE_SWORD:
                return "§f石剑";
            case DIAMOND_SWORD:
                return "§f钻石剑";
            case GOLDEN_SWORD:
                return "§f金剑";
            case LEATHER_HELMET:
                return "§f皮头盔";
            case LEATHER_CHESTPLATE:
                return "§f皮胸甲";
            case LEATHER_LEGGINGS:
                return "§f皮护腿";
            case LEATHER_BOOTS:
                return "§f皮靴子";
            case CHAINMAIL_HELMET:
                return "§f锁链头盔";
            case CHAINMAIL_CHESTPLATE:
                return "§f锁链胸甲";
            case CHAINMAIL_LEGGINGS:
                return "§f锁链护腿";
            case CHAINMAIL_BOOTS:
                return "§f锁链靴子";
            case IRON_HELMET:
                return "§f铁头盔";
            case IRON_CHESTPLATE:
                return "§f铁胸甲";
            case IRON_LEGGINGS:
                return "§f铁护腿";
            case IRON_BOOTS:
                return "§f铁靴子";
            case DIAMOND_HELMET:
                return "§f钻石头盔";
            case DIAMOND_CHESTPLATE:
                return "§f钻石胸甲";
            case DIAMOND_LEGGINGS:
                return "§f钻石护腿";
            case DIAMOND_BOOTS:
                return "§f钻石靴子";
            case GOLDEN_HELMET:
                return "§f金头盔";
            case GOLDEN_CHESTPLATE:
                return "§f金胸甲";
            case GOLDEN_LEGGINGS:
                return "§f金护腿";
            case GOLDEN_BOOTS:
                return "§f金靴子";
            default:
                return type.name();
        }
    }
}
