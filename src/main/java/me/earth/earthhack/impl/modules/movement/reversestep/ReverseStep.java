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
import net.minecraft.util.math.BlockPos;

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

    protected enum fallmode{
        normal,
        strict
    }
}
