package me.earth.earthhack.impl.modules.player.speedmine.mode;

import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.RenderUtilEuropa;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public enum ESPMode {
    None() {
        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb) {
            /* None means no ESP. */
        }
    },
    Outline() {
        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb) {
            RenderUtil.startRender();
            RenderUtil.drawOutline(bb, module.lineWidth.getValue(), module.outlinecolor.getValue());
            RenderUtil.endRender();
        }
    },
    Block() {
        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb) {
            RenderUtil.startRender();
            RenderUtil.drawBox(bb, module.color.getValue());
            RenderUtil.endRender();
        }
    },
    Box() {
        @Override
        public void drawEsp(Speedmine module, AxisAlignedBB bb) {
            Outline.drawEsp(module, bb);
            Block.drawEsp(module, bb);
        }
    },
    Europa()
            {
                @Override
                public void drawEsp(Speedmine module, AxisAlignedBB bb) {
                    RenderUtilEuropa.drawFilledBox(bb,module.color.getValue());
                    RenderUtilEuropa.drawBlockOutline(bb, module.color.getValue(), module.lineWidth.getValue());
                }
            };

    public abstract void drawEsp(Speedmine module, AxisAlignedBB bb);

}
