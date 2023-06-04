package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
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
            if(module.getCurrent()==null) return;
                if (module.rotations == null) return;
                event.setYaw(module.rotations[0]);
                event.setPitch(module.rotations[1]);
                module.hasRotated = true;
                if(module.rotationSmoother.isRotating()){
                    return;
                }
                if (!mc.playerController.isHittingPosition(module.current)) {
                    module.attackPos(module.getCurrent());
                }
                 module.rotations = null;
                 module.setCurrent(null);

        }


    }




}
