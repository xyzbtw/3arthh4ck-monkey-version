package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;

public class ListenerMultiBlockChang extends ModuleListener<Blocker, PacketEvent.Receive<SPacketMultiBlockChange>> {
    public ListenerMultiBlockChang(Blocker module) {
        super(module,PacketEvent.Receive.class, SPacketMultiBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketMultiBlockChange> event) {
        if(mc.world==null || mc.player==null)return;
        if (event.getPacket() == null ) return;
        if(module.modeSetting.getValue() != Blocker.mode.broken) return;




        for(SPacketMultiBlockChange.BlockUpdateData data : event.getPacket().getChangedBlocks()){
            BlockPos pos = data.getPos();

            if(!module.blockmap.containsKey(pos)
                    && (mc.world.getBlockState(pos).getBlock() instanceof BlockObsidian
                             || mc.world.getBlockState(pos).getBlock() instanceof BlockEnderChest)){
                ModuleUtil.sendMessage(module, "CACHED MULTIBLOCKCHANGE " + pos.toString());
                module.blockmap.put(pos, mc.world.getBlockState(pos));
                return;
            }


           if( mc.world.getBlockState(pos).getBlock() instanceof BlockAir){

               if(pos == PlayerUtil.getPlayerPos().add(0,2,0) && !module.anticev.getValue()) return;
               if(module.debug.getValue()){
                   ModuleUtil.sendMessage(module, "Received at multiblockchange " + pos.toString());
               }

               module.scanAndPlace(pos);
               Speedmine.compatibility.remove(pos);
           }
        }
    }
}
