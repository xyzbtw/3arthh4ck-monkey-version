package me.earth.earthhack.impl.modules.combat.forclown2;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.Timer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ListenerUpdate extends ModuleListener<forclown2, UpdateEvent> {
    public ListenerUpdate(forclown2 module)
    {
        super(module, UpdateEvent.class, 1);
    }

    protected static ArrayList<BlockPos> scheduledPlacements = new ArrayList<>();

    Timer timer = new Timer();

    @Override
    public void invoke(UpdateEvent event) {
        ArrayList<BlockPos> copy = (ArrayList<BlockPos>) scheduledPlacements.clone(); //избавляемся от ConcurrentModificationException
        if(timer.passed(700)){
            timer.reset();

            scheduledPlacements.clear();
        }
        for(BlockPos pos : copy){
            module.placeBlock(pos);
        }
    }
}
