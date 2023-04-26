package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.gui.GuiGameOver;

public class ListenerUpdate extends ModuleListener<AutoRespawn, UpdateEvent> {
    public ListenerUpdate(AutoRespawn module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if(!(mc.currentScreen instanceof GuiGameOver)){
            module.respawndelay.reset();
        }
    }
}
