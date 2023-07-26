package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerMove extends ModuleListener<ReverseStep, UpdateEvent> {
    public ListenerMove(ReverseStep module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if(module.mode.getValue() != ReverseStep.fallmode.forever) return;
        if(mc.world== null || mc.player == null) return;

        if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb) return;
        if (module.distance.getValue() > 0 && (module.traceDown()  >module.distance.getValue())) return;
        if (mc.player.onGround){
            mc.player.motionY -=  module.speed.getValue() / 10;
        }

    }
}
