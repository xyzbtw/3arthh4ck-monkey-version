package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public class ListenerRender extends ModuleListener<ForeverSpeedMine, Render3DEvent> {
    public ListenerRender(ForeverSpeedMine module) {
        super(module,Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if(mc.player == null || mc.world== null) return;
        if (module.renderMode.getValue() != ForeverSpeedMine.RenderMode.None && module.checkCurrentPos()) {
            AxisAlignedBB bb = mc.world.getBlockState(module.currentPos).getSelectedBoundingBox(mc.world, module.currentPos);
            float progress = module.mineDamage;
            if (module.mineDamage >= module.speed.getValue() - 0.05F) {
                if (module.renderMode.getValue() == ForeverSpeedMine.RenderMode.Rise) {
                    Renderer3D.drawBoxESP(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ), module.readyColor.getValue(), 1f, true, true, module.readyColor.getValue().getAlpha(), 255);
                } else {
                    Renderer3D.drawBoxESP(bb, module.readyColor.getValue(), 1f, true, true, module.readyColor.getValue().getAlpha(), 255);
                }
                if (module.showProgress.getValue()) {
                    RenderUtil.drawText(bb.offset(0.0, (double) (1.0f - 1 / 2.0f) - 0.4, 0.0), "0%");
                }
            } else {

                switch (module.renderMode.getValue()) {
                    case Fade:
                        Renderer3D.drawBoxESP(bb, new Color((int) (module.color.getValue().getRed() * Math.abs(progress - 1)), (int) (module.readyColor.getValue().getGreen() * progress), module.color.getValue().getBlue()), 1f, true, true, module.color.getValue().getAlpha(), 255);
                        break;
                    case Expand:
                        Renderer3D.drawProgressBox(bb, 1 - progress, module.color.getValue());
                        break;
                    case Rise:
                        Renderer3D.drawRiseBox(bb, 1 - progress, new Color((int) (module.color.getValue().getRed() * Math.abs(progress - 1)), (int) (module.readyColor.getValue().getGreen() * progress), module.color.getValue().getBlue(), 75));
                        break;
                }
                if (module.showProgress.getValue()) {
                    RenderUtil.drawText(bb.offset(0.0, (double) (1.0f - 1 / 2.0f) - 0.4, 0.0), String.format("%.1f%%", progress * 100));
                }

            }
        }
    }
}
