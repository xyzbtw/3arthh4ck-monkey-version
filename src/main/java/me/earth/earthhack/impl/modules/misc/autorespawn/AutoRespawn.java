package me.earth.earthhack.impl.modules.misc.autorespawn;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.Timer;

public class AutoRespawn extends Module
{
    protected final Setting<Boolean> coords =
            register(new BooleanSetting("Coords", false));
    protected final Setting<Float> delay =
            register(new NumberSetting<>("RespawnDelay", 1.0f, 0.0f, 60.0f));
    StopWatch respawndelay = new StopWatch();

    public AutoRespawn()
    {
        super("AutoRespawn", Category.Misc);
        this.listeners.add(new ListenerScreens(this));
        this.listeners.add(new ListenerUpdate(this));
        this.setData(new AutoRespawnData(this));
    }



}
