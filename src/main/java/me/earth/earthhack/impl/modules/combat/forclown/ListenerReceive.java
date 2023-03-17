package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

public class ListenerReceive extends ModuleListener<forclown, PacketEvent.Receive<SPacketBlockBreakAnim>>{

    public ListenerReceive(forclown module) {
        super(module, PacketEvent.Receive.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
        if (event.getPacket() != null && PlayerUtil.isInHoleAll(mc.player)) {

            BlockPos pos = event.getPacket().getPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos placePos = null;

            if (module.extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }

            if (module.face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                module.placeBlock(placePos);
            }
        }

    }
}
