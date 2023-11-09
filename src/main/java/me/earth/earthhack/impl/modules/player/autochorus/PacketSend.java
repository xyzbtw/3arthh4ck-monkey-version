package me.earth.earthhack.impl.modules.player.autochorus;

import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;

public class PacketSend extends ModuleListener<AutoChorus, PacketEvent.Send> {
    public PacketSend(AutoChorus module) {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send event) {
        Packet<?> packet = event.getPacket();
        if (module.chorus) {
            if (packet  instanceof CPacketPlayer) {
                if (((ICPacketPlayer) event.getPacket()).isMoving()) {
                    event.setCancelled(!module.doTeleport());
                }
            }
            if (packet instanceof CPacketConfirmTeleport) {
                module.teleportID = ((CPacketConfirmTeleport) packet).getTeleportId();
                event.setCancelled(!module.doTeleport());
            }
        }

    }
}
