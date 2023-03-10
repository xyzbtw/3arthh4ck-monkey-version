package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.EntityPlayer;

public class ListenerUpdate extends ModuleListener<Chams, UpdateEvent> {
    public ListenerUpdate(Chams module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (module.disableanimations.getValue()) {
            for (EntityPlayer player : mc.world.playerEntities) {
                player.limbSwing = module.animation.getValue();
                player.limbSwingAmount = module.animation.getValue();
                player.prevLimbSwingAmount = module.animation.getValue();
            }
        }
    }
}
