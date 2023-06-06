package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ListenerBlockChange extends ModuleListener<Blocker, PacketEvent.Receive<SPacketBlockChange>> {

    public ListenerBlockChange(Blocker module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if(module.modeSetting.getValue() != Blocker.mode.broken && module.modeSetting.getValue() != Blocker.mode.both) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if (event.getPacket() == null ) return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        if(event.getPacket().getBlockPosition().getDistance(PlayerUtil.getPlayerPos().getX(),
                                                            PlayerUtil.getPlayerPos().getY(),
                                                            PlayerUtil.getPlayerPos().getZ()) > 6) return;

        if(!module.blockmap.containsKey(event.getPacket().getBlockPosition())
                && (event.getPacket().getBlockState().getBlock() instanceof BlockObsidian
                        || event.getPacket().getBlockState().getBlock() instanceof BlockEnderChest)){
            if(module.debug.getValue()) ModuleUtil.sendMessage(module, "CACHED BLOCKCHANGE " + event.getPacket().getBlockPosition().toString());
            module.blockmap.put(event.getPacket().getBlockPosition(), event.getPacket().getBlockState());
            return;
        }

        if (event.getPacket().getBlockState().getBlock() instanceof BlockAir) {
            BlockPos blockPosition = event.getPacket().getBlockPosition();

            if(blockPosition == PlayerUtil.getPlayerPos().add(0,2,0) && !module.anticev.getValue()) return;
            if(module.debug.getValue()){
                ModuleUtil.sendMessage(module, "Received at blockchange " + blockPosition.toString());
            }

            module.scanAndPlace(blockPosition);
            Speedmine.compatibility.remove(blockPosition);
        }
    }


}
