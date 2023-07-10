package me.earth.earthhack.vanilla;

import me.earth.earthhack.impl.core.util.AsmUtil;
import me.earth.earthhack.tweaker.EarthhackTweaker;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.List;

public enum Environment {
    VANILLA,
    SEARGE,
    MCP;

    private static Environment environment;
    private static boolean forge;

    public static Environment getEnvironment() {
        return environment;
    }

    public static boolean hasForge() {
        return forge;
    }

    /**
     * Loads the environment based on the current setup.
     * {@link EarthhackTweaker#acceptOptions(List, File, File, String)}
     */
    public static void loadEnvironment() {
        Environment env = SEARGE;

        try {
            String forgeHooksClass = "net.minecraftforge.common.ForgeHooks";
            byte[] forgeBytes = Launch.classLoader.getClassBytes(forgeHooksClass);
            if (forgeBytes != null) {
                forge = true;
            } else {
                env = VANILLA;
            }
        } catch (IOException e) {
            env = VANILLA;
        }

        String worldClass = "net.minecraft.world.World";
        byte[] worldBytes = null;
        try {
            worldBytes = Launch.classLoader.getClassBytes(worldClass);
        } catch (IOException ignored) {
        }

        if (worldBytes != null) {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(worldBytes);
            reader.accept(node, 0);
            if (AsmUtil.findField(node, "loadedEntityList") != null) {
                env = MCP;
            }
        }

        environment = env;
    }
}
