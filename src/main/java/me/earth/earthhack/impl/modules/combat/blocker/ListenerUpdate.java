package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.StopWatch;
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
        if(timer.passed(module.clearDelay.getValue())){
            timer.reset();
            module.scheduledPlacements.clear();
        }
    }
}
