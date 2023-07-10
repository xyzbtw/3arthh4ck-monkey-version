package me.earth.earthhack.forge.util;

import me.earth.earthhack.tweaker.launch.DevArguments;
import net.minecraftforge.fml.common.ProgressManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Wraps Forge's ProgressManager since it's not available in Vanilla.
 */
public class ForgeSplashHelper {
    private static ProgressManager.ProgressBar bar;

    public static void push(String message, int steps) {
        if (!DevArguments.getInstance().getArgument("splash").getValue()) {
            return;
        }

        try {
            Field field = ProgressManager.class.getDeclaredField("bars");
            field.setAccessible(true);
            List<?> list = (List<?>) field.get(null);
            list.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        bar = ProgressManager.push(message, steps, true);
    }

    public static void setSubStep(String step) {
        if (bar != null) {
            bar.step(step);
        }
    }

    public static void clear() {
        if (bar != null) {
            ProgressManager.pop(bar);
        }

        bar = null;
    }
}