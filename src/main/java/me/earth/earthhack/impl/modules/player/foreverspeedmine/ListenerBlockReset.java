package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.event.events.misc.ResetBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerBlockReset extends ModuleListener<ForeverSpeedMine, ResetBlockEvent> {
    public ListenerBlockReset(ForeverSpeedMine module) {
        super(module, ResetBlockEvent.class);
    }

    @Override
    public void invoke(ResetBlockEvent event) {
        if (module.noReset.getValue()) event.setCancelled(true);
    }
}
