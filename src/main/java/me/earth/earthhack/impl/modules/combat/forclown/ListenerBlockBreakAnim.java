package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockBreakAnim extends ModuleListener<forclown, PacketEvent.Receive<SPacketBlockBreakAnim>>{

    public ListenerBlockBreakAnim(forclown module) {
        super(module, PacketEvent.Receive.class);
    }



    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
        if(!module.blockanim.getValue()) return;
        if (event.getPacket() == null ) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;

        BlockPos blockPosition = event.getPacket().getPosition();
        module.scanAndPlace(blockPosition, false);
    }
}