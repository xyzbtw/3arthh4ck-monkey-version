package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent;

public class ListenerBlock extends ModuleListener<ForeverSpeedMine, DamageBlockEvent> {
    public ListenerBlock(ForeverSpeedMine module) {
        super(module, DamageBlockEvent.class);
    }

    @Override
    public void invoke(DamageBlockEvent e) {
        if (!module.canBlockBeBroken(e.getPos())) {
            e.setCancelled(true);
            return;
        }


        if (module.currentPos != null) {
            if (e.getPos() == module.currentPos) {
                if (module.mineDamage >= module.speed.getValue() && mc.world.getBlockState(module.currentPos).getBlock() != Blocks.AIR) {
                    module.swapTo();
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, module.currentPos, EnumFacing.DOWN));
                    e.setCancelled(true);
                }
                return;
            }

            if (e.getPos() != module.currentPos) {
                if(module.abort.getValue())  mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, module.currentPos, e.getFacing()));
            }
        }
        ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(false);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        for (int j = 0; j < module.spam.getValue(); j++) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, e.getPos(), e.getFacing()));
        }

        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.getPos(), EnumFacing.DOWN));
        module.currentPos = e.getPos();
        module.currentFace = e.getFacing();
        module.mineDamage = 0;
        module.strictCheck = true;
    }
}
