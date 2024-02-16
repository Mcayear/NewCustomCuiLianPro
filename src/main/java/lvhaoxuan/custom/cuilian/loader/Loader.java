package lvhaoxuan.custom.cuilian.loader;

import java.io.*;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jetbrains.annotations.Nullable;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro;
import lvhaoxuan.custom.cuilian.NewCustomCuiLianPro.ItemType;
import lvhaoxuan.custom.cuilian.movelevel.MoveLevelHandle;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.custom.cuilian.object.Stone;
import lvhaoxuan.custom.cuilian.object.SuitEffect;
import lvhaoxuan.llib.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

public class Loader {

    public static void loadLevels() {
        Level.levels.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "cuilian.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("cuilian.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                Level level = Level.deserialize(config, key);
                Level.levels.put(level.value, level);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadStones() {
        Stone.stones.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "stone.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("stone.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                Stone stone = Stone.deserialize(config, key);
                Stone.stones.put(key, stone);
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadItems() {
        NewCustomCuiLianPro.types.clear();
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "items.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("items.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            for (String key : config.getKeys(false)) {
                for (String strType : config.getStringList(key)) {
                    ItemType type = new ItemType(key, strType);
                    NewCustomCuiLianPro.types.add(type);
                    NewCustomCuiLianPro.typesInBag.put(type.type, type.typeInBag);
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static void loadConfig() {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "config.yml");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("config.yml", true);
        }
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
            NewCustomCuiLianPro.otherEntitySuitEffect = config.getBoolean("OtherEntitySuitEffect");
            NewCustomCuiLianPro.PROTECT_RUNE_JUDGE = config.getString("PROTECT_RUNE_JUDGE");
            NewCustomCuiLianPro.LEVEL_JUDGE = config.getString("LEVEL_JUDGE");
            MoveLevelHandle.moveLevelInvTitle = config.getString("MoveLevelInvTitle");
            NewCustomCuiLianPro.judgeOffHand = config.getBoolean("JudgeOffHand");
            NewCustomCuiLianPro.displayNameFormat = config.getInt("DisplayNameFormat");
            NewCustomCuiLianPro.replaceLore = config.getStringList("ReplaceLore");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        }
    }

    public static String loadSuitEffectScriptStr(String name) {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File folder = new File(NewCustomCuiLianPro.ins.getDataFolder(), "script");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileUtil.write(file, SuitEffect.defaultScript);
            } catch (IOException ex) {
            }
        }
        return FileUtil.read(file);
    }

    public static ScriptEngine loadMoveLevelScript() {
        if (!NewCustomCuiLianPro.ins.getDataFolder().exists()) {
            NewCustomCuiLianPro.ins.getDataFolder().mkdir();
        }
        File file = new File(NewCustomCuiLianPro.ins.getDataFolder(), "movelevelscript.js");
        if (!file.exists()) {
            NewCustomCuiLianPro.ins.saveResource("movelevelscript.js", true);
        }
        return loadScript(file);
    }

    public static @Nullable ScriptEngine loadScript(File file) {
        if (file.exists()) {
            try {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("javascript");
                if (engine == null) {
                    throw new RuntimeException("Failed to initialize JavaScript engine");
                }
                engine.eval(new FileReader(file));
                return engine;
            } catch (ScriptException | IOException ex) {
                Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, "Script file does not exist: " + file.getAbsolutePath());
        }
        return null;
    }
}
