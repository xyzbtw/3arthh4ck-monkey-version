package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketEntityAction;

final class ListenerEntityAction extends ModuleListener<NoSlowDown, PacketEvent.Send<CPacketEntityAction>>
{
    public ListenerEntityAction(NoSlowDown module)
    {
        super(module, PacketEvent.Send.class, CPacketEntityAction.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketEntityAction> event)
    {
        if (module.invStrictAlways.getValue())
        {
            CPacketEntityAction p = event.getPacket();
            if (p.getAction() == CPacketEntityAction.Action.START_SPRINTING)
            {
                event.setCancelled(true);
            }
        }
    }

}