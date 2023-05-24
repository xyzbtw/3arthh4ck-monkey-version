package me.earth.earthhack.impl.modules.movement.clip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;


public class Clip extends Module
{

    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 5, 1, 10));

    protected final Setting<Boolean> disable =
            register(new BooleanSetting("Disable", false));

    protected final Setting<Integer> updates =
            register(new NumberSetting<>("Updates", 10, 1, 30));

    protected int disabletime = 0;

    public Clip()
    {
        super("Clip", Category.Movement);
        this.listeners.add(new ListenerTick(this));
        this.setData(new ClipData(this));
    }


    @Override
    protected void onDisable()
    {
        disabletime = 0;
    }
}