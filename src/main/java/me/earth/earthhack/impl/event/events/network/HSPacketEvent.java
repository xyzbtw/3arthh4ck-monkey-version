package me.earth.earthhack.impl.event.events.network;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class HSPacketEvent extends Event {
    private final Packet<?> packet;

    public HSPacketEvent(int stage, Packet<?> packet) {
        this.packet = packet;
    }


    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }

    @Cancelable
    public static class Receive
            extends HSPacketEvent {
        public Receive(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }

    @Cancelable
    public static class Send
            extends HSPacketEvent {
        public Send(int stage, Packet<?> packet) {
            super(stage, packet);
        }
    }
}
