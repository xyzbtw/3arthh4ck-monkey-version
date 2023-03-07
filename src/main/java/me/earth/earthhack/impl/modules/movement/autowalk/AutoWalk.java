package me.earth.earthhack.impl.modules.movement.autowalk;


import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.client.settings.KeyBinding;


public class AutoWalk extends Module{
    public AutoWalk() {
        super("AutoWalk", Category.Movement);
        this.listeners.add(new me.earth.earthhack.impl.modules.movement.autowalk.ListenerTick(this));
        this.setData(new AutoWalkData(this));
    }

    @Override
    public void onEnable() {
        KeyBinding.setKeyBindState(
                mc.gameSettings.keyBindForward.getKeyCode(),
                KeyBoardUtil.isKeyDown(mc.gameSettings.keyBindForward));
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
    }
}