package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class ListenerSlotChange extends ModuleListener<Speedmine, PacketEvent.Send<CPacketHeldItemChange>> {
    public ListenerSlotChange(Speedmine module) {
        super(module, PacketEvent.class, CPacketHeldItemChange.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketHeldItemChange> event) {
        if(mc.world == null || mc.player == null) return;
        if(module.strict.getValue()) {
            module.resetSLot();
        }
    }
}
