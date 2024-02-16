package lvhaoxuan.custom.cuilian.runnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.openjdk.nashorn.api.scripting.*;
import lvhaoxuan.custom.cuilian.api.CuiLianAPI;
import lvhaoxuan.custom.cuilian.loader.Loader;
import lvhaoxuan.custom.cuilian.object.Level;
import lvhaoxuan.llib.particle.ParticleModel;
import lvhaoxuan.llib.particle.ParticleModel2D;
import lvhaoxuan.llib.particle.ParticleModel3D;
import lvhaoxuan.llib.particle.ParticleModelLine;
import lvhaoxuan.llib.particle.ParticleUtil;
import org.bukkit.entity.LivingEntity;

public class ScriptRunnable implements Runnable {

    public static boolean enbaleScript = true;
    public static HashMap<UUID, HashMap<String, Bindings>> bindingsMap = new HashMap<>();
    public static HashMap<String, ScriptEngine> engineMap = new HashMap<>();

    @Override
    public void run() {
        List<LivingEntity> entities = SyncEffectRunnable.getEntities();
        for (LivingEntity le : entities) {
            sync(le);
        }
    }

    public void sync(LivingEntity le) {
        UUID uuid = le.getUniqueId();
        Level minLevel = CuiLianAPI.getMinLevel(le, le.getEquipment());
        if (minLevel != null && minLevel.suitEffect != null) {
            try {
                if (enbaleScript) {
                    for (String script : minLevel.suitEffect.script) {
                        if (!engineMap.containsKey(script)) {
                            engineMap.put(script, getDefaultScriptEngine(script));
                        }
                        ScriptEngine engine = engineMap.get(script);
                        bindingsMap.putIfAbsent(uuid, new HashMap<>());
                        if (bindingsMap.get(uuid).containsKey(script)) {
                            engine.setBindings(bindingsMap.get(uuid).get(script), ScriptContext.ENGINE_SCOPE);
                        }
                        Invocable invocable = (Invocable) engine;
                        invocable.invokeFunction("onEffectTick", le);
                        bindingsMap.get(uuid).put(script, engine.getBindings(ScriptContext.ENGINE_SCOPE));
                    }
                }
            } catch (NoSuchMethodException | ScriptException | ClassCastException ex) {
                Logger.getLogger(SyncEffectRunnable.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                enbaleScript = false;
            }
        }
    }

    public ScriptEngine getDefaultScriptEngine(String script) {
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();
        try {
            uploadScriptClass(engine, ParticleModel.class);
            uploadScriptClass(engine, ParticleModel2D.class);
            uploadScriptClass(engine, ParticleModel3D.class);
            uploadScriptClass(engine, ParticleModelLine.class);
            uploadScriptClass(engine, ParticleUtil.class);
            engine.eval(script);
        } catch (ScriptException ex) {
            Logger.getLogger(Loader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return engine;
    }

    public static void uploadScriptClass(ScriptEngine engine, Class clazz) throws ScriptException {
        String totalName = clazz.getSimpleName() + "StaticClass";
        String staticClassName = clazz.getSimpleName();
        engine.put(totalName, clazz);
        engine.eval("var " + staticClassName + " = " + totalName + ".static;");
    }
}
