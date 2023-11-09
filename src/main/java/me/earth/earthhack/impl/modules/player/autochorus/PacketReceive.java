package me.earth.earthhack.impl.modules.player.autochorus;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

public class PacketReceive extends ModuleListener<AutoChorus, PacketEvent.Receive> {
    public PacketReceive(AutoChorus module) {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();

        if (module.chorus) {

            if (packet instanceof SPacketPlayerPosLook) {
                SPacketPlayerPosLook spacket = (SPacketPlayerPosLook) packet;
                module.teleport = new BlockPos(spacket.getX(), spacket.getY(), spacket.getZ());
                event.setCancelled(!module.doTeleport());
            }
        }

    }
}
