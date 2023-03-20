package me.earth.earthhack.impl.modules.combat.forclown2;

import me.earth.earthhack.api.event.bus.api.EventBus;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;
import me.earth.earthhack.impl.util.helpers.blocks.util.TargetResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ListenerObsidian extends ObbyListener<forclown2> {
    public ListenerObsidian(forclown2 module)
    {
        super(module, EventBus.DEFAULT_PRIORITY);
    }

    @Override
    protected TargetResult getTargets(TargetResult result) {
        ArrayList<BlockPos> copy = (ArrayList<BlockPos>) ListenerUpdate.scheduledPlacements.clone();
        result.setTargets(copy);
        return result;
    }
}
