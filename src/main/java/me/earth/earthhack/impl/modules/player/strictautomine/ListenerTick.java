package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.minecraft.PhaseUtil;
import me.earth.earthhack.impl.util.minecraft.PushMode;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.otherplayers.IgnoreSelfClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

public class ListenerTick extends ModuleListener<StrictAutoMine, TickEvent> {
    public ListenerTick(StrictAutoMine module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if(mc.world==null || mc.player==null) return;
        if(module.current!=null) return;
        EntityPlayer target = IgnoreSelfClosest.GetClosestIgnoreFriends(module.range.getValue());
        if(target==null) return;

        
    }
}
