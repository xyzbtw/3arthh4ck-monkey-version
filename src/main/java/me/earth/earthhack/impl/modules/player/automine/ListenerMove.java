package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.raytrace.Ray;
import me.earth.earthhack.impl.util.math.raytrace.RayTraceFactory;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ListenerMove extends ModuleListener<AutoMine, MotionUpdateEvent> {
    public ListenerMove(AutoMine module) {
        super(module,MotionUpdateEvent.class);
    }
    private static final BlockStateHelper HELPER = new BlockStateHelper();
    @Override
    public void invoke(MotionUpdateEvent event) {
        if(!module.rotate.getValue()) return;
        if(event.getStage()!= Stage.PRE) return;
        if (event.getStage() == Stage.PRE)
        {
         /*   if(module.current!=null) module.rotations=RotationUtil.getRotations(module.getCurrent(), BlockUtil.getFacing(module.getCurrent()));
            else return;
            if(module.rotate.getValue() && module.current!=null){
                EnumFacing facing = RayTraceUtil.getFacing(
                        RotationUtil.getRotationPlayer(), module.current, true);
                assert facing !=null;
                module.rotations = RotationUtil.getRotations(module.current, facing, mc.player);
            }
            if (module.current != null)
            {
                setRotations(module.current, event);
            }

          */
            if(module.rotations==null) return;
            event.setYaw(module.rotations[0]);
            event.setPitch(module.rotations[1]);
            module.hasRotated=true;
        }if(event.getStage()==Stage.POST){
            if(module.current==null)return;
            module.attackPos(module.current);
            module.hasRotated=false;
        }

    }

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
            if (module.rotate.getValue() && module.rotations != null)
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
        if (module.rotate.getValue() && module.rotations != null)
        {
            event.setYaw(module.rotations[0]);
            event.setPitch(module.rotations[1]);
        }
    }

}
