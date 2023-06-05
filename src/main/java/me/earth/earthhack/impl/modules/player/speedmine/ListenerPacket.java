package me.earth.earthhack.impl.modules.player.speedmine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class ListenerPacket extends ModuleListener<Speedmine, PacketEvent.Send> {

    public ListenerPacket(Speedmine module) {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send event) {
        if(!module.strict.getValue()) return;
        if(module.pos==null || module.facing==null) return;
        BlockPos position = module.pos;
        EnumFacing facing = module.facing;
        if(event.getPacket() instanceof CPacketPlayerDigging) {
            if (((CPacketPlayerDigging) event.getPacket()).getAction()== CPacketPlayerDigging.Action.RELEASE_USE_ITEM
                    || ((CPacketPlayerDigging) event.getPacket()).getAction() == CPacketPlayerDigging.Action.SWAP_HELD_ITEMS) {
                module.resetCD();
               // mc.playerController.onPlayerDamageBlock(position, facing);
                //position=null;
                //facing=null;
                return;
            }
        }
        if(event.getPacket() instanceof CPacketHeldItemChange){
            if(((CPacketHeldItemChange) event.getPacket()).getSlotId() == InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE )) return;
           // module.abortCurrentPos();
           // mc.playerController.onPlayerDamageBlock(position, facing);
           // position=null;
           // facing=null;
            module.resetCD();
            return;
        }
    }
}
