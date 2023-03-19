package me.earth.earthhack.impl.modules.combat.forclown2;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockChange extends ModuleListener<forclown2, PacketEvent.Receive<SPacketBlockChange>> {

    public ListenerBlockChange(forclown2 module) {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        //hole check was redundant
        if(event.getPacket() != null){
            BlockPos pos = event.getPacket().getBlockPosition();

            //???
            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = mc.player.getPosition();
            BlockPos placePos = null;

            if(pos.equals(playerPos.add(0,2,0))){
                placePos = playerPos.add(0,3,0);
            }

            if (placePos != null) {
                ListenerUpdate.scheduledPlacements.add(placePos);
            }
        }
    }
}