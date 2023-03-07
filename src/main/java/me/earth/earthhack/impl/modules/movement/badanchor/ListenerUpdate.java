package me.earth.earthhack.impl.modules.movement.badanchor;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.util.math.Vec3d;

import static me.earth.earthhack.impl.util.minecraft.PlayerUtil.getPlayerPos;

public class ListenerUpdate extends ModuleListener<BadAnchor, UpdateEvent> {

    public ListenerUpdate(BadAnchor module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (mc.world == null) {
            return;
        }
        if (mc.player.posY < 0) {
            return;
        }
        if (mc.player.rotationPitch >= module.pitch.getValue()) {
            if (
                    HoleUtil.isHole(getPlayerPos().down(1), false)[0]
                    || HoleUtil.isHole(getPlayerPos().down(2), false)[0]
                    || HoleUtil.isHole(getPlayerPos().down(3), false)[0]
                    || HoleUtil.isHole(getPlayerPos().down(4), false)[0]
            )
            {
                BadAnchor.AnchorING = true;
                if (!module.pull.getValue()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                } else {
                    Vec3d center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
                    double XDiff = Math.abs(center.x - mc.player.posX);
                    double ZDiff = Math.abs(center.z - mc.player.posZ);

                    if (XDiff <= 0.1 && ZDiff <= 0.1) {
                        center = Vec3d.ZERO;
                    }
                    else {
                        double MotionX = center.x-mc.player.posX;
                        double MotionZ = center.z-mc.player.posZ;

                        mc.player.motionX = MotionX/2;
                        mc.player.motionZ = MotionZ/2;
                    }
                }
            } else BadAnchor.AnchorING = false;
        }
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }
}
