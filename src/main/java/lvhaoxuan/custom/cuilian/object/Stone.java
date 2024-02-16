package lvhaoxuan.custom.cuilian.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lvhaoxuan.llib.api.LLibAPI;
import lvhaoxuan.llib.loader.LoaderUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Stone {

    public static HashMap<String, Stone> stones = new HashMap<>();
    public static Random rnd = new Random();
    public Map<Level, Double> chance;
    public ItemStack item;
    public String id;
    public LevelDrop dropLevel;
    public int riseLevel;

    public Stone(ItemStack item, String id, LevelDrop dropLevel, int riseLevel, Map<Level, Double> chance) {
        this.item = item;
        this.id = id;
        this.dropLevel = dropLevel;
        this.riseLevel = riseLevel;
        this.chance = chance;
    }

    public static Stone byItemStack(ItemStack item) {
        if (LLibAPI.checkItemNull(item)) {
            for (Stone stone : stones.values()) {
                if (stone.item.isSimilar(item)) {
                    return stone;
                }
            }
        }
        return null;
    }

    public static Stone deserialize(YamlConfiguration config, String path) {
        HashMap<Level, Double> map = new HashMap<>();
        ConfigurationSection cs = config.getConfigurationSection(path + ".Chance");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                Level level = Level.levels.get(Integer.parseInt(key));
                if (level != null) {
                    map.put(level, config.getDouble(path + ".Chance." + key));
                }
            }
        }
        return new Stone(LoaderUtil.readItemStack(config, path),
                path,
                new LevelDrop(config.getString(path + ".dropLevel")),
                config.getInt(path + ".riseLevel"),
                map);
    }

    public static class LevelDrop {

        public int min;
        public int max;

        public LevelDrop(String dropStr) {
            if (dropStr.contains(" ")) {
                String[] args = dropStr.split(" ");
                init(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            } else {
                init(Integer.parseInt(dropStr));
            }
        }

        public void init(int drop) {
            this.min = drop;
            this.max = drop;
        }

        public void init(int up, int down) {
            this.min = Math.min(up, down);
            this.max = Math.max(up, down);
        }

        public int toInteger() {
            return min == max ? min : LLibAPI.getRandom(min, max);
        }
    }
}
