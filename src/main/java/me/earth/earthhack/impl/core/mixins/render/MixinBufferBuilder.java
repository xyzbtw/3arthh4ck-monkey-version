package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.xray.XRay;
import me.earth.earthhack.impl.modules.render.xray.mode.XrayMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import net.minecraft.client.renderer.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.Color;
import java.nio.IntBuffer;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder
{
    private static final ModuleCache<XRay> XRAY = Caches.getModule(XRay.class);
     private static final ModuleCache<Ambience> AMBIENCE = Caches.getModule(Ambience.class);

    @Redirect(
        method = "putColorMultiplier",
        at = @At(
            value = "INVOKE",
            remap = false,
            target = "java/nio/IntBuffer.put(II)Ljava/nio/IntBuffer;"))
    public IntBuffer putColorMultiplierHook(IntBuffer buffer, int index, int iIn)
    {
        int i = iIn;
        // TODO: remove if needed
        if (AMBIENCE.isEnabled())
        {
            // TODO: gotta check if we need 2 swap!!!
            Color color = AMBIENCE.get().getColor();
            int[] bgr = MathUtil.toRGBAArray(i);
            int red = (bgr[2] + color.getRed()) / 2; // half-half mix of both colors
            int green = (bgr[1] + color.getGreen()) / 2;
            int blue = (bgr[0] + color.getBlue()) / 2;
            float[] hsb = Color.RGBtoHSB(red, green, blue, null);
            i = MathUtil.toRGBAReversed(red, green, blue);
        }

    
        if (XRAY.isEnabled() && XRAY.get().getMode() == XrayMode.Opacity)
        {
            i = XRAY.get().getOpacity() << 24 | i & 16777215;
            // Color color = new Color(i);
            // System.out.println("Color: " + color.getRed() + " " + color.getGreen() + " " + color.getBlue());

        }

        // return buffer.put(index, new Color(255, 0, 0, 255).getRGB());
        return buffer.put(index, i);
    }

}