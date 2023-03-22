package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockBreakAnim extends ModuleListener<Blocker, PacketEvent.Receive<SPacketBlockBreakAnim>>{

    public ListenerBlockBreakAnim(Blocker module) {
        super(module, PacketEvent.Receive.class, SPacketBlockBreakAnim.class);
    }



    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
        if(!module.anticev.getValue()) return;
        if(event.getPacket() == null ) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;

        BlockPos playerPos = PlayerUtil.getPlayerPos();
        BlockPos blockPosition = event.getPacket().getPosition();

        if(blockPosition.equals(playerPos.add(0,2,0))){
            module.scanAndPlace(playerPos.add(0,3,0), false);
        }
    }
}