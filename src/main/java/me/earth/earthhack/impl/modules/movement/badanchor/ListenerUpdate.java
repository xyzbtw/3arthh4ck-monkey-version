package me.earth.earthhack.impl.modules.movement.badanchor;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.util.math.Vec3d;

import static me.earth.earthhack.impl.util.minecraft.PlayerUtil.getPlayerPos;

public class ListenerUpdate extends ModuleListener<BadAnchor, UpdateEvent> {

    public ListenerUpdate(BadAnchor module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        double x = getPlayerPos().getX();
        double y = getPlayerPos().getY();
        double z = getPlayerPos().getZ();
        if (mc.world == null) {
            return;
        }
        if (mc.player.posY < 0) {
            BadAnchor.pulling =false;
            return;
        }
        if (mc.player.rotationPitch >= module.pitch.getValue()) {
            if (
                    (HoleUtil.isHole(getPlayerPos().down(1), false)[0]
                            && RayTraceUtil.canBeSeen(x, y-1, z, mc.player))
                    || (HoleUtil.isHole(getPlayerPos().down(2), false)[0]
                            && RayTraceUtil.canBeSeen(x, y-2, z, mc.player))
                    || (HoleUtil.isHole(getPlayerPos().down(3), false)[0]
                            && RayTraceUtil.canBeSeen(x, y-3, z, mc.player))
                    || (HoleUtil.isHole(getPlayerPos().down(4), false)[0]
                            && RayTraceUtil.canBeSeen(x, y-4, z, mc.player))
            )
            {
                if (!module.pull.getValue()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                } else {
                    BadAnchor.pulling =true;
                    Vec3d center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
                    double MotionX = center.x-mc.player.posX;
                    double MotionZ = center.z-mc.player.posZ;
                    mc.player.motionX = MotionX/2;
                    mc.player.motionZ = MotionZ/2;
                }
            } else BadAnchor.pulling =false;
        } else BadAnchor.pulling =false;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }
}
