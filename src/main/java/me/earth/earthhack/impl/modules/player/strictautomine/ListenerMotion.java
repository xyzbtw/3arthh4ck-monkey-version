package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.otherplayers.IgnoreSelfClosest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<StrictAutoMine, MotionUpdateEvent> {
    public ListenerMotion(StrictAutoMine module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if(mc.world == null || mc.player==null) return;
        if (event.getStage() == Stage.PRE
                && !PlayerUtil.isCreative(mc.player) && module.current!=null)
        {
            setRotations(module.current, event);
        }
        if(event.getStage()==Stage.POST){
            module.mine(module.current);
        }
    }
    private static final BlockStateHelper HELPER = new BlockStateHelper();
    private void setRotations(BlockPos pos, MotionUpdateEvent event)
    {
        if (module.raytrace.getValue())
        {
            if (rayTrace(pos, event))
            {
                return;
            }
        }
        else
        {
            module.facing = BlockUtil.getFacing(pos);
            if (module.facing != null)
            {
                setRotations(pos, event, module.facing);
                return;
            }
        }

    }

    private boolean rayTrace(BlockPos pos, MotionUpdateEvent event) {
        Entity entity = RotationUtil.getRotationPlayer();
        Ray ray = RayTraceFactory.fullTrace(entity, HELPER, pos, -1.0);
        if (ray != null && ray.isLegit()) {
            module.facing = ray.getFacing().getOpposite();
            module.rotations = ray.getRotations();
            if (module.rotations != null)
            {
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
            }

            return true;
        }

        return false;
    }
    private void setRotations(BlockPos pos,
                              MotionUpdateEvent event,
                              EnumFacing facing)
    {
        module.rotations = RotationUtil.getRotations(pos.offset(facing),
                facing.getOpposite());
        event.setYaw(module.rotations[0]);
        event.setPitch(module.rotations[1]);
    }
}
