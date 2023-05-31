package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.otherplayers.IgnoreSelfClosest;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public class ListenerUpdate extends ModuleListener<StrictAutoMine, UpdateEvent> {
    public ListenerUpdate(StrictAutoMine module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if(mc.world == null || mc.player==null) return;
        EntityPlayer target = IgnoreSelfClosest.GetClosestIgnoreFriends(module.range.getValue());
        if(target ==null) return;
        if(module.current!=null && mc.player.getDistanceSq(module.current) <= MathUtil.square(module.range.getValue())) {
            EnumFacing facing = RayTraceUtil.getFacing(mc.player, module.current, true);
            if (mc.world.getBlockState(module.current).getBlock() instanceof BlockAir) {
                module.current = null;
            }
            if (!PlayerUtil.isInHole(target) && !module.isBurrow(target)) {
                module.current = null;
            }

        }
        module.current=module.isBurrow(target)
                ? module.getBurrowBlock(target)
                : module.getCityBlockSurround(target);

    }
}
