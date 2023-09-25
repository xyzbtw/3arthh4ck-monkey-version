package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.events.misc.RightClickItemEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRightClick extends ModuleListener<ForeverSpeedMine, ClickBlockEvent.Right> {
    public ListenerRightClick(ForeverSpeedMine module) {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event) {
        if (module.currentPos == null) return;
        module.isPlaced = module.checkPos(event.getPos());
    }
}
