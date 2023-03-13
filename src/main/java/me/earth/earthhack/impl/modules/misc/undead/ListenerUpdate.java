package me.earth.earthhack.impl.modules.misc.undead;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerUpdate extends ModuleListener<Undead, UpdateEvent> {
    public ListenerUpdate(Undead module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (mc.player.getHealth() == 0.0f) {
            mc.player.setHealth(20.0f);
            mc.player.isDead = false;
            module.explorer = true;
            mc.displayGuiScreen(null);
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    }
}
