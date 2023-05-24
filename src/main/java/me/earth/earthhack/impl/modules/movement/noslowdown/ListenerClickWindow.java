package me.earth.earthhack.impl.modules.movement.noslowdown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;

final class ListenerClickWindow
        extends ModuleListener<NoSlowDown, PacketEvent.Post<CPacketClickWindow>>
{

    public ListenerClickWindow(NoSlowDown module)
    {
        super(module, PacketEvent.Post.class, CPacketClickWindow.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketClickWindow> event)
    {
        if (module.invStrict.getValue() && !module.invStrictAlways.getValue() && mc.player.isSprinting() && !mc.player.onGround)
        {
            {
                NetworkUtil.send(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
    }

}