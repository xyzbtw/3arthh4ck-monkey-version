package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.impl.core.ducks.network.IPlayerControllerMP;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class SendPacketEvent extends ModuleListener<ForeverSpeedMine, PacketEvent.Send<CPacketHeldItemChange>> {
    public SendPacketEvent(ForeverSpeedMine module) {
        super(module, PacketEvent.Send.class, CPacketHeldItemChange.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketHeldItemChange> event) {
        if (module.strict.getValue() && module.checkCurrentPos() && !module.doSyns && module.mineDamage <= module.speed.getValue() - 0.05) {
            module. sendPacket();
            module.resetProgress(true);
            module. checked = true;
            ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
            module. strictCheck = true;
        }
    }
}
