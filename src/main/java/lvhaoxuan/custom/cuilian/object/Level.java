package lvhaoxuan.custom.cuilian.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.llib.api.LLibAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Level {

    public static HashMap<Integer, Level> levels = new HashMap<>();
    public Integer value;
    public List<String> lore;
    public HashMap<String, List<String>> attribute;
    public ProtectRune protectRune;
    public SuitEffect suitEffect;

    public Level(Integer value, List<String> lore, HashMap<String, List<String>> attribute, ProtectRune protectRune, SuitEffect suitEffect) {
        this.value = value;
        this.lore = lore;
        this.attribute = attribute;
        this.protectRune = protectRune;
        this.suitEffect = suitEffect;
        if (protectRune != null) {
            protectRune.level = this;
        }
    }

    public static Level byItemStack(ItemStack item) {
        if (LLibAPI.checkItemNull(item)) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, lore.get(i).replace(NewCustomCuiLianPro.LEVEL_JUDGE, ""));
                lore.set(i, lore.get(i).replace(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE, ""));
            }
            for (Level level : levels.values()) {
                if (lore.containsAll(level.lore)) {
                    return level;
                }
            }
        }
        return null;
    }

    public static Level byProtectRune(ItemStack item) {
        if (LLibAPI.checkItemNull(item)) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, lore.get(i).replace(NewCustomCuiLianPro.LEVEL_JUDGE, ""));
                lore.set(i, lore.get(i).replace(NewCustomCuiLianPro.PROTECT_RUNE_JUDGE, ""));
            }
            for (Level level : levels.values()) {
                if (level.protectRune != null && lore.contains(level.protectRune.lore)) {
                    return level;
                }
            }
        }
        return null;
    }

    public static Level deserialize(YamlConfiguration config, String path) {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("Hand", new ArrayList<>());
        map.put("Helmet", new ArrayList<>());
        map.put("Chestplate", new ArrayList<>());
        map.put("Leggings", new ArrayList<>());
        map.put("Boots", new ArrayList<>());
        ConfigurationSection cs = config.getConfigurationSection(path + ".Attribute");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                map.put(key, config.getStringList(path + ".Attribute." + key));
            }
        }
        return new Level(Integer.parseInt(path),
                config.getStringList(path + ".Lore"),
                map,
                ProtectRune.deserialize(config, path + ".ProtectRune"),
                SuitEffect.deserialize(config, path + ".SuitEffect"));
    }
}
