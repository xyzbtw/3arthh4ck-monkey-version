package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.Timer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ListenerUpdate extends ModuleListener<forclown, UpdateEvent> {

    public ListenerUpdate(forclown module)
    {
        super(module, UpdateEvent.class, 1);
    }

    protected static ArrayList<BlockPos> scheduledPlacements = new ArrayList<>();

    StopWatch timer = new StopWatch();

    @Override
    public void invoke(UpdateEvent event) {
        if(timer.passed(module.surroundDelay.getValue())){
            timer.reset();
            scheduledPlacements.clear();
        }
    }
}
