package me.earth.earthhack.impl.modules.movement.nomovesneak;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class NoMoveSneak extends Module {
    protected Setting<Boolean> onlyHole = register(new BooleanSetting("OnlyHole", false));

    public NoMoveSneak() {
        super("SneakNoMove", Category.Movement);
        this.listeners.add(new LambdaListener<>(TickEvent.class, e->{
            if(!e.isSafe()) return;
            int counter=0;
            for(Entity entity : mc.world.loadedEntityList){
                if(entity instanceof EntityPlayer){
                    if(entity.getDisplayName().getFormattedText().equals(mc.player.getDisplayName().getFormattedText())){
                        counter++;
                    }
                }
            }
            if(counter>1) return;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), isInHole() && MovementUtil.noMovementKeys() && !mc.player.isRiding() && mc.player.onGround);
        }));
    }
    boolean isInHole(){
        return PlayerUtil.isInHole(mc.player) || !onlyHole.getValue();
    }



}
