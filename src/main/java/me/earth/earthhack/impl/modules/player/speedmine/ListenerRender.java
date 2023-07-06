package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.mode.ESPMode;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

final class ListenerRender extends ModuleListener<Speedmine, Render3DEvent>
{
    public ListenerRender(Speedmine module)
    {
        super(module, Render3DEvent.class);
    }
    private AxisAlignedBB cachedBB;

    public double Easing(double x)
    {
        if (x < 0.5) {
            return 4 * x * x * x;
        } else {
            double f = ((2 * x) - 2);
            return 0.5 * f * f * f + 1;
        }
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        if (!PlayerUtil.isCreative(mc.player)
                && module.esp.getValue() != ESPMode.None
                && module.bb != null) {
            if (cachedBB == null || !cachedBB.equals(module.bb)) {
                float max = Math.min(module.maxDamage, 1.0f);
                AxisAlignedBB renderBB = module.bb;
                if (module.growRender.getValue() && max < 1.0f) {
                    double easedMax = Easing(max);
                    renderBB = renderBB.grow(-0.5 + (easedMax / 2.0));
                }

                cachedBB = Interpolation.interpolateAxis(renderBB);
            }

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

            AxisAlignedBB bb = cachedBB;
            module.esp.getValue().drawEsp(module, bb);

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

}
