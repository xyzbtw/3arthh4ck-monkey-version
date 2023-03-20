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
    StopWatch delay = new StopWatch();

    @Override
    public void invoke(UpdateEvent event) {
        ArrayList<BlockPos> copy = (ArrayList<BlockPos>) scheduledPlacements.clone(); //избавляемся от ConcurrentModificationException
        if(timer.passed(700)){
            timer.reset();

            scheduledPlacements.clear();
        }

        for(BlockPos pos : copy){
            if(!delay.passed(module.delay.getValue())) return;
            module.placeBlock(pos);
            delay.reset();
        }
    }
}
