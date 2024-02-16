package lvhaoxuan.custom.cuilian.object;

import lvhaoxuan.llib.api.LLibAPI;
import lvhaoxuan.llib.loader.LoaderUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ProtectRune {

    public ItemStack item;
    public String lore;
    public Level level;

    public ProtectRune(ItemStack item, String lore) {
        this.item = item;
        this.lore = lore;
    }

    public static ProtectRune byItemStack(ItemStack item) {
        if (LLibAPI.checkItemNull(item)) {
            for (Level level : Level.levels.values()) {
                if (level.protectRune != null && level.protectRune.item.isSimilar(item)) {
                    return level.protectRune;
                }
            }
        }
        return null;
    }

    public static ProtectRune deserialize(YamlConfiguration config, String path) {
        if (config.get(path) == null) {
            return null;
        }
        return new ProtectRune(LoaderUtil.readItemStack(config, path),
                config.getString(path + ".AddLore"));
    }
}
