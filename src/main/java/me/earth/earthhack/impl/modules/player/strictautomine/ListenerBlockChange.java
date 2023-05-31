package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.server.SPacketBlockChange;

public class ListenerBlockChange extends ModuleListener<StrictAutoMine, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBlockChange(StrictAutoMine module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if(module.current==null) return;
        if(event.getPacket().getBlockPosition() == module.current){
            module.current=null;
            module.hitting=false;
        }
    }
}
