package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

final class ListenerMultiBlockChange extends
        ModuleListener<AutoMine, PacketEvent.Receive<SPacketMultiBlockChange>>
{
    public ListenerMultiBlockChange(AutoMine module)
    {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event)
    {
        if (!module.resetOnPacket.getValue())
        {
            return;
        }

        SPacketMultiBlockChange packet = event.getPacket();
        mc.addScheduledTask(() ->
        {
            if (module.constellation == null)
            {
                return;
            }

            for (SPacketMultiBlockChange.BlockUpdateData data :
                    packet.getChangedBlocks())
            {
                if(data.getPos() == module.current){
                    module.current=null;
                }
                if (module.constellation.isAffected(
                        data.getPos(), data.getBlockState()))
                {
                    module.constellation = null;
                    break;
                }
            }
        });
    }
}
