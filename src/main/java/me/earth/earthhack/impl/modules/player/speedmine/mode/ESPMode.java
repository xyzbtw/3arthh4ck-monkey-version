package me.earth.earthhack.impl.modules.player.speedmine.mode;

import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public enum ESPMode
{
    None()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                /* None means no ESP. */
            }
        },
    Outline()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                RenderUtil.startRender();
                RenderUtil.drawOutline(bb, module.lineWidth.getValue(), module.outlinecolor.getValue());
                RenderUtil.endRender();
            }
        },
    Block()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                RenderUtil.startRender();
                RenderUtil.drawBox(bb, module.color.getValue());
                RenderUtil.endRender();
            }
        },
    Box()
        {
            @Override
            public void drawEsp(Speedmine module, AxisAlignedBB bb, float damage)
            {
                Outline.drawEsp(module, bb, damage);
                Block.drawEsp(module, bb, damage);
            }
        };

    public abstract void drawEsp(Speedmine module, AxisAlignedBB bb, float damage);

}
