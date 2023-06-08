package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;

public class ListenerPacket extends ModuleListener<Speedmine, PacketEvent> {

    public ListenerPacket(Speedmine module) {
        super(module, PacketEvent.class);
    }

    @Override
    public void invoke(PacketEvent event) {
        if (!module.strict.getValue())
            return;
        if (module.pos == null)
            return;
        if (event.getClass() == PacketEvent.Send.class) {
            if (event.getPacket() instanceof CPacketPlayerDigging) {
                if (((CPacketPlayerDigging) event.getPacket())
                        .getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM
                        || ((CPacketPlayerDigging) event.getPacket())
                                .getAction() == CPacketPlayerDigging.Action.SWAP_HELD_ITEMS) {
                    module.resetCD();
                    return;
                }
            }
            if (event.getPacket() instanceof CPacketHeldItemChange
                    && module.damages[mc.player.inventory.currentItem] < module.limit.getValue()) {
                module.resetCD();
                return;
            }
            /*
             * if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock
             * && !InventoryUtil.isHolding(Items.END_CRYSTAL)) {
             * module.resetCD();
             * return;
             * }
             */
        } else if (event.getClass() == PacketEvent.Send.class) {
            Packet<?> packet = event.getPacket();
            if (packet instanceof SPacketHeldItemChange
                    && module.damages[mc.player.inventory.currentItem] < module.limit.getValue()) {
                module.resetCD();
            }

        }

    }

}
