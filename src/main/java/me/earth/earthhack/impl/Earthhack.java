package me.earth.earthhack.impl;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.GlobalExecutor;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.misc.IconUtil;
import me.earth.earthhack.impl.util.render.SplashScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class Earthhack implements Globals
{
    private static final Logger LOGGER = LogManager.getLogger("3arthh4ck");
    public static final String NAME = "3arthh4ck";
    public static final String VERSION = "1.8.9";

    public static void preInit()
    {
        GlobalExecutor.EXECUTOR.submit(() -> Sphere.cacheSphere(LOGGER));
    }

    public static void init()
    {
        LOGGER.info("\n\nInitializing 3arthh4ck.");
        SplashScreenHelper.setSplashScreen("Initializing 3arthh4ck (monkey hack)", 7);
        setIcon();
        Display.setTitle(NAME + " | Looking up at Phobos");
        Managers.load();
        LOGGER.info("Prefix is " + Commands.getPrefix());
        SplashScreenHelper.clear();
        LOGGER.info("\n3arthh4ck initialized (monkey hack)\n");
    }

    public static void setWindowIcon() {
        //Why am I skidding Zori's setWindowIcon? I don't even know.
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/earthhack/textures/client/phobos16.png");
                 InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/earthhack/textures/client/phobos32.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                LOGGER.error("Couldn't set Windows Icon", e);
            }
        }
    }
    public static void setIcon() {
        setWindowIcon();
    }
    public static void postInit()
    {
        // For Plugins if they need it.
    }
    
    public static Logger getLogger()
    {
        return LOGGER;
    }

    public static boolean isRunning()
    {
        return ((IMinecraft) mc).isEarthhackRunning();
    }

}