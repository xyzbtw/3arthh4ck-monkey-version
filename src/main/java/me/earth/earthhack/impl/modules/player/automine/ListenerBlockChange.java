package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBlockChange extends
        ModuleListener<AutoMine, PacketEvent.Receive<SPacketBlockChange>>
{
    public ListenerBlockChange(AutoMine module)
    {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event)
    {
        if (!module.resetOnPacket.getValue())
        {
            return;
        }

        SPacketBlockChange packet = event.getPacket();

        mc.addScheduledTask(() ->
        {
            if(module.current == packet.getBlockPosition()){
                module.current=null;
            }
            if (module.constellation != null && module.constellation.isAffected(
                    packet.getBlockPosition(), packet.getBlockState()))
            {
                module.constellation = null;
            }
        });
    }

}
