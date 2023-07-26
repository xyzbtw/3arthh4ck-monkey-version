package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class ReverseStep extends Module
{  protected boolean jumped;
    protected boolean waitForOnGround;
    protected boolean shouldstopmotion =false;
    protected int packets;

    protected final Setting<fallmode> mode =
            register(new EnumSetting<>("Mode", fallmode.normal));
    protected final Setting<Double> speed =
            register(new NumberSetting<>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Float> strictSpeed =
            register(new NumberSetting<>("StrictSpeed", 4.0f, 0.1f, 10.0f));
    protected final Setting<Double> distance =
            register(new NumberSetting<>("Distance", 3.0, 0.1, 10.0));
    protected final Setting<Boolean> strictLiquid =
            register(new BooleanSetting("StrictLiquid", false));
    protected final Setting<Boolean> movementkeys =
            register(new BooleanSetting("noMovement", false));

    public ReverseStep()
    {
        super("ReverseStep", Category.Movement);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMove(this));
    }

    // y not raytrace???
    protected double getNearestBlockBelow()
    {
        for (double y = mc.player.posY; y > 0; y -= 0.001) {
            if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0)) != null) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) {
                    return -1;
                }
                return y;
            }
        }
        return -1;
    }
    public int traceDown() {
        int ret = 0;

        int y = (int) Math.round(mc.player.posY) - 1;

        for (int tracey = y; tracey >= 0; tracey--) {
            RayTraceResult trace = mc.world.rayTraceBlocks(
                    mc.player.getPositionVector(),
                    new Vec3d(mc.player.posX, tracey, mc.player.posZ),
                    false
            );

            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
                return ret;

            ret++;
        }

        return ret;
    }

    private boolean trace() {
        AxisAlignedBB bbox = mc.player.getEntityBoundingBox();
        Vec3d basepos = bbox.getCenter();

        double minX = bbox.minX;
        double minZ = bbox.minZ;
        double maxX = bbox.maxX;
        double maxZ = bbox.maxZ;

        Map<Vec3d, Vec3d> positions = new HashMap<>();

        positions.put(
                basepos,
                new Vec3d(basepos.x, basepos.y - 1, basepos.z));

        positions.put(
                new Vec3d(minX, basepos.y, minZ),
                new Vec3d(minX, basepos.y - 1, minZ));

        positions.put(
                new Vec3d(maxX, basepos.y, minZ),
                new Vec3d(maxX, basepos.y - 1, minZ));

        positions.put(
                new Vec3d(minX, basepos.y, maxZ),
                new Vec3d(minX, basepos.y - 1, maxZ));

        positions.put(
                new Vec3d(maxX, basepos.y, maxZ),
                new Vec3d(maxX, basepos.y - 1, maxZ));

        for (Vec3d key : positions.keySet()) {
            RayTraceResult result = mc.world.rayTraceBlocks(key, positions.get(key), true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
                return false;
        }

        IBlockState state = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));

        return state.getBlock() == Blocks.AIR;
    }

    protected enum fallmode{
        normal,
        strict,
        forever
    }
}
