package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketConfirmTeleport;

public class ListenerTeleport extends ModuleListener<SmartBlockLag, PacketEvent.Post<CPacketConfirmTeleport>> {
    public ListenerTeleport(SmartBlockLag module) {
        super(module, PacketEvent.Post.class, CPacketConfirmTeleport.class);
    }

    @Override
    public void invoke(PacketEvent.Post<CPacketConfirmTeleport> event) {
        EntityPlayerSP player = mc.player;
        if (player != null
                && module.teleport.getValue()
                && !module.blockTeleporting
                && module.atechorus) {
            if(!ListenerUpdate.burrow.isEnabled()){
                ListenerUpdate.burrow.enable();
                module.atechorus=false;
                if(module.chorusdisable.getValue()
                        || !BlockUtil.isReplaceable(
                                module.pos.add(0,0.2,0)))
                    ListenerUpdate.burrow.disable();
            }
        }
    }
}
