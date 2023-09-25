package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ListenerUpdate extends ModuleListener<Blocker, UpdateEvent> {

    public ListenerUpdate(Blocker module)
    {
        super(module, UpdateEvent.class, 1);
    }
    StopWatch timer = new StopWatch();

    @Override
    public void invoke(UpdateEvent event) {
//        if (module.mineStart != null && module.contains()) {
//            module.mineStart = null;
//        }
//
//        module.mining.removeIf(m -> System.currentTimeMillis() > m.getTime() + module.maxMineTime.getValue() * 1000 || (module.mineStart != null && m.getId() == module.mineStart.getId()) || !mc.world.getBlockState(m.getPos()).isTopSolid());
//        if (module.mineStart != null) {
//            module.mining.add(module.mineStart);
//            module.mineStart = null;
//        }
        if(timer.passed(module.clearDelay.getValue())){
            timer.reset();
            module.scheduledPlacements.clear();
        }
    }
}
