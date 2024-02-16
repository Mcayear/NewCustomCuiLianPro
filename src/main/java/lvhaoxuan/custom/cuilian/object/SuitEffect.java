package lvhaoxuan.custom.cuilian.object;

import java.util.ArrayList;
import java.util.List;
import lvhaoxuan.custom.cuilian.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;

public class SuitEffect {

    public static String defaultScript = "var a = [\n"
            + "[1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1],\n"
            + "[0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0],\n"
            + "[0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0],\n"
            + "[0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0],\n"
            + "[0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0],\n"
            + "[0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0],\n"
            + "[0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0]];\n"
            + "\n"
            + "function onEffectTick(entity){\n"
            + "	var Particle = Java.type(\"org.bukkit.Particle\");\n"
            + "	for (var i = a.length - 1; i >= 0; i--) {\n"
            + "		for (var j = a[i].length - 1; j >= 0; j--) {\n"
            + "			if (a[i][j] != 0) {\n"
            + "				draw(entity.getLocation(), entity.getLocation().add(-a[i].length * 1.0 / 10 + j * 1.0 / 5 + 0.1, 0.2, 0), 1.8 - i * 1.0 / 5 + 0.2, Particle.FLAME, 20);\n"
            + "			}\n"
            + "		}\n"
            + "	}\n"
            + "}\n"
            + "\n"
            + "function draw(PlayerLocation, EffectLocation, g, pe, addAngle) {\n"
            + "	var Ploc = PlayerLocation.clone();\n"
            + "	var Eloc = EffectLocation.clone();\n"
            + "	var jdc = getAngle(Ploc.getX(), Ploc.getY(), Eloc.getX(), Eloc.getY());\n"
            + "	if (jdc < 90) {\n"
            + "		jdc += addAngle;\n"
            + "	} else {\n"
            + "		jdc -= addAngle;\n"
            + "	}\n"
            + "	var jl = getDistance(Ploc.getX(), Ploc.getY(), Eloc.getX(), Eloc.getY());\n"
            + "	var radians = toRadians(jdc + Ploc.getYaw() - 180);\n"
            + "	var x = Math.cos(radians) * jl;\n"
            + "	var y = Math.sin(radians) * jl;\n"
            + "	Ploc.add(x, g, y);\n"
            + "	Ploc.getWorld().spawnParticle(pe, Ploc, 0);\n"
            + "	Ploc.subtract(x, g, y);\n"
            + "}\n"
            + "\n"
            + "function toRadians(angdeg) {\n"
            + "	return angdeg / 180.0 * Math.PI;\n"
            + "}\n"
            + "\n"
            + "function getDistance(x1, y1, x2, y2) {\n"
            + "	var x = Math.abs(x2 - x1);\n"
            + "	var y = Math.abs(y2 - y1);\n"
            + "	return Math.sqrt(x * x + y * y);\n"
            + "}\n"
            + "\n"
            + "function getAngle(x1, y1, x2, y2) {\n"
            + "	var x = x2 - x1;\n"
            + "	var y = y2 - y1;\n"
            + "	var hypotenuse = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));\n"
            + "	var cos = x / hypotenuse;\n"
            + "	var radian = Math.acos(cos);\n"
            + "	var angle = 180 / (Math.PI / radian);\n"
            + "	if (y < 0) {\n"
            + "		angle = -angle;\n"
            + "	} else if ((y == 0) && (x < 0)) {\n"
            + "		angle = 180;\n"
            + "	}\n"
            + "	return angle;\n"
            + "}";
    public List<String> potionEffect;
    public List<String> attribute;
    public List<String> script;

    public SuitEffect(List<String> potionEffect, List<String> attribute, List<String> script) {
        this.potionEffect = potionEffect;
        this.attribute = attribute;
        this.script = script;
    }

    public static SuitEffect deserialize(YamlConfiguration config, String path) {
        if (config.get(path) == null) {
            return null;
        }
        List<String> names = config.getStringList(path + ".Script");
        List<String> script = new ArrayList<>();
        for (String name : names) {
            script.add(Loader.loadSuitEffectScriptStr(name));
        }
        return new SuitEffect(config.getStringList(path + ".PotionEffect"),
                config.getStringList(path + ".Attribute"),
                script);
    }
}
