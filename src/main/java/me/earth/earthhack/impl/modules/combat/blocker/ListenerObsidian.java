package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ListenerObsidian extends ObbyListener<Blocker> {
    public ListenerObsidian(Blocker module)
    {
        super(module, EventBus.DEFAULT_PRIORITY);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        ArrayList<BlockPos> copy = (ArrayList<BlockPos>) module.scheduledPlacements.clone();
        result.setTargets(copy);
        return result;
    }
    @Override
    public void onModuleToggle()
    {
        super.onModuleToggle();
    }

    @Override
    public boolean update(){
        {
            if (updatePlaced())
            {
                return false;
            }

            module.slot = getSlot();
            if (module.slot == -1)
            {
                return false;
            }

            if (hasTimerNotPassed())
            {
                return false;
            }

            TargetResult result = getTargets(new TargetResult());
            targets = result.getTargets();
            return result.isValid();
        }
    }
}
