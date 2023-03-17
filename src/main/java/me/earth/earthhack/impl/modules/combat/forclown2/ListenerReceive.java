package me.earth.earthhack.impl.modules.combat.forclown2;


import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

public class ListenerReceive extends ModuleListener<forclown2, PacketEvent.Receive<SPacketBlockBreakAnim>> {


    public ListenerReceive(forclown2 module) {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
            if(event.getPacket() != null && PlayerUtil.isInHole(mc.player)){

                BlockPos pos = event.getPacket().getPosition();

                if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

                BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
                BlockPos placePos = null;

                if(pos.equals(playerPos.add(0,2,0))){
                    placePos = playerPos.add(0,3,0);
                }

                if (placePos != null) {
                    module.placeBlock(placePos);
                }


            }
    }
}
