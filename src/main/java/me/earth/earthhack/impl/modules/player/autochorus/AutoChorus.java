package me.earth.earthhack.impl.modules.player.autochorus;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class AutoChorus extends Module {
    SimpleHoleManager HM = new SimpleHoleManager();

    protected Setting<Float> range = register(new NumberSetting<>("Range", 5.0f, 0.0f, 12.0f));
    public AutoChorus() {
        super("AutoChorusControl", Category.Player);
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e->{
            if(!isSafe()) return;
            if (!chorus) {
                if (mc.player.isHandActive()) {
                    ItemStack item = mc.player.getHeldItem(mc.player.getActiveHand());
                    if (item.getItem() instanceof ItemChorusFruit) {
                        int use = item.getMaxItemUseDuration() - mc.player.getItemInUseMaxCount();
                        if (use <= 1) {
                            chorus = true;
                        }
                    }
                }
            }
        }));
        this.listeners.add(new PacketReceive(this));
        this.listeners.add(new PacketSend(this));
    }
    protected boolean chorus;
    protected BlockPos teleport;
    protected int teleportID = -1;

    @Override
    public void onDisable() {
        super.onDisable();
        teleport = null;
        teleportID = -1;
        chorus = false;
    }
    protected boolean doTeleport(){
        if(teleport == null) return false;

        boolean hole = HM.getHoles().values().stream().filter(h-> mc.player.getDistance(h.getX(), h.getY(), h.getZ()) < 24).anyMatch(h-> h.contains(teleport));
        EntityPlayer enemy =  EntityUtil.getClosestEnemy();

        return (enemy != null && enemy.getDistance(teleport.getX(), teleport.getY(), teleport.getZ()) >= range.getValue()) || hole;
    }
}
