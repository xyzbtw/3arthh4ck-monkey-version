package me.earth.earthhack.impl.modules.misc.undead;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.event.listeners.SendListener;
import net.minecraft.network.play.client.CPacketPlayer;

public class Undead extends Module {
    protected boolean explorer;
    public Undead() {
        super("Undead", Category.Misc);
        this.listeners.add(new SendListener<>(CPacketPlayer.class, e -> {
            if(e.getPacket() != null && explorer){
                e.setCancelled(true);
            }}));
        this.listeners.add(new ListenerUpdate(this));
    }
    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.respawnPlayer();
        }
        explorer = false;
    }

}
