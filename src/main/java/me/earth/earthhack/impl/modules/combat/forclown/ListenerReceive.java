package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ListenerReceive extends ModuleListener<forclown, PacketEvent.Receive<SPacketBlockBreakAnim>>{

    public ListenerReceive(forclown module) {
        super(module, PacketEvent.Receive.class, SPacketBlockBreakAnim.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
        if (event.getPacket() != null ) {
            if(module.hole.getValue() && !PlayerUtil.isInHoleAll(mc.player))
                return;

            BlockPos pos = event.getPacket().getPosition();

            //???
            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos placePos = null;

            if (module.extend.getValue()) {

                for(EnumFacing face : EnumFacing.values()) {
                    if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                    if (pos.equals(playerPos.offset(face))){
                        placePos = ((playerPos.offset(face)).offset(face));
                    }

                    if (placePos != null) {
                        module.placeBlock(placePos);
                    }
                }
            }

            if (module.face.getValue()) {
                for(EnumFacing face : EnumFacing.values()) {
                    if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                    if (pos.equals(playerPos.offset(face))){
                        placePos = ((playerPos.offset(face)).add(0,1,0));
                    }

                    if (placePos != null) {
                        module.placeBlock(placePos);
                    }
                }
            }


        }

    }
}