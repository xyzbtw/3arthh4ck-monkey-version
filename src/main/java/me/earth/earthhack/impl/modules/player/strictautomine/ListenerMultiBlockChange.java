package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

public class ListenerMultiBlockChange extends ModuleListener<StrictAutoMine, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChange(StrictAutoMine module) {
        super(module, PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if(module.current==null) return;
        for(SPacketMultiBlockChange.BlockUpdateData change : event.getPacket().getChangedBlocks() ){
            if(change == null) return;
            if(change.getPos() == module.current){
                module.current=null;
                module.hitting=false;
            }
        }
    }
}
