package me.earth.earthhack.impl.modules.misc.coordthing;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.entityspeed.EntitySpeed;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.network.play.server.SPacketEntity;

public class Listener extends ModuleListener<Coord, PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove>> {


    public Listener(Coord module) {
        super(module, PacketEvent.Receive.class, SPacketEntity.S15PacketEntityRelMove.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketEntity.S15PacketEntityRelMove> event) {
        event.getPacket().getEntity(mc.world);
        ChatUtil.sendMessage(event.getPacket().getEntity(mc.world)
                + "\n " +
                event.getPacket().getX()
                + "\n " +
                event.getPacket().getZ());
    }
}

