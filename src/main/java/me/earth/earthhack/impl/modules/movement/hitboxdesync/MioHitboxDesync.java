package me.earth.earthhack.impl.modules.movement.hitboxdesync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

import static java.lang.Math.abs;

public class MioHitboxDesync extends Module {

    public MioHitboxDesync() {
        super("MioHitboxDesync", Category.Movement);

        this.listeners.add(new LambdaListener<>(TickEvent.class, e-> {
            if (mc.world == null) return;
            EnumFacing f = mc.player.getHorizontalFacing();
            AxisAlignedBB bb = mc.player.getEntityBoundingBox();
            Vec3d center = bb.getCenter();
            Vec3d offset = new Vec3d(f.getDirectionVec());

            Vec3d fin = merge(new Vec3d(new BlockPos(center)).add(.5, 0, .5).add(offset.scale(MAGIC_OFFSET)), f);
            mc.player.setPosition(fin.x == 0 ? mc.player.getPosition().getX() : fin.x,
                    mc.player.getPosition().getY(),
                    fin.z == 0 ? mc.player.getPosition().getZ() : fin.z);
            disable();
        }));
    }

    private static final double MAGIC_OFFSET = .200009968835369999878673424677777777777761;

    private Vec3d merge(Vec3d a, EnumFacing facing) {
        return new Vec3d(a.x * abs(facing.getDirectionVec().x), a.y * abs(facing.getDirectionVec().y), a.z * abs(facing.getDirectionVec().z));
    }

}
