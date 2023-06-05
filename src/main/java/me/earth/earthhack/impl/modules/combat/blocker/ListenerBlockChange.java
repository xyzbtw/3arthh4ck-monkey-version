package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockChange extends ModuleListener<Blocker, PacketEvent.Receive<SPacketBlockChange>> {

    public ListenerBlockChange(Blocker module) {
        super(module, PacketEvent.Receive.class, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        if(module.modeSetting.getValue() != Blocker.mode.broken) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if (event.getPacket() == null ) return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        if(event.getPacket().getBlockPosition().getDistance(PlayerUtil.getPlayerPos().getX(),
                                                            PlayerUtil.getPlayerPos().getY(),
                                                            PlayerUtil.getPlayerPos().getZ()) > 6) return;

        if (mc.world.getEntityByID(event.getPacket().getBlockPosition().getY()) instanceof EntityEnderCrystal) return;

        if (event.getPacket().getBlockState().getBlock() instanceof BlockAir) {
            BlockPos blockPosition = event.getPacket().getBlockPosition();
            Speedmine.compatibility.remove(blockPosition);
            if(blockPosition == PlayerUtil.getPlayerPos().add(0,2,0) && !module.anticev.getValue()) return;
            if(module.debug.getValue()){
                ModuleUtil.sendMessage(module, "Received at " + blockPosition.toString(), "Blocker");
            }

            module.scanAndPlace(blockPosition);
        }
    }


}
