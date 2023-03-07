/*package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.render.RenderEntityInWorldEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.player.EntityPlayer;

final class ListenerRenderEntityInWorld extends ModuleListener<Notifications, RenderEntityInWorldEvent> {

    public ListenerRenderEntityInWorld(Notifications module) {
        super(module, RenderEntityInWorldEvent.class);
    }

    @Override
    public void invoke(RenderEntityInWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            module.onRenderEntityInWorld(event.getEntity());
        }
    }
}

 */