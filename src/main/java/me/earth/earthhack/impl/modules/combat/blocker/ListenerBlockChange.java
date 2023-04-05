package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockChange extends ModuleListener<Blocker, PacketEvent.Receive<SPacketBlockChange>> {

    public ListenerBlockChange(Blocker module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if(!module.blockchange.getValue()) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if (event.getPacket() == null ) return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        if(event.getPacket().getBlockPosition().getDistance(PlayerUtil.getPlayerPos().getX(),
                                                            PlayerUtil.getPlayerPos().getY(),
                                                            PlayerUtil.getPlayerPos().getZ()) > 10) return;


        if (event.getPacket().getBlockState().getBlock() instanceof BlockAir) {
            final BlockPos blockPosition = event.getPacket().getBlockPosition();
            if(Blocker.speedminecache.contains(blockPosition)){
                Blocker.speedminecache.clear();
                return;
            }
            module.scanAndPlace(blockPosition, true);
        }
    }


}
