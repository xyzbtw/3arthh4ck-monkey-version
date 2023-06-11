package me.earth.earthhack.impl.modules.combat.fastprojectiles;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class FastProjectile extends Module
{
    protected final Setting<Integer> interval =
            register(new NumberSetting<>("Interval", 25, 1, 100));
    protected final Setting<Boolean> move =
            register(new BooleanSetting("Move", false));
    protected final Setting<Boolean> eggs =
            register(new BooleanSetting("Eggs", false));
    protected final Setting<Boolean> snowballs =
            register(new BooleanSetting("Snowballs", false));
    protected final Setting<Boolean> pearls =
            register(new BooleanSetting("Pearls", true));

    public FastProjectile()
    {
        super("FastProjectile", Category.Combat);
        this.listeners.add(new ListenerPlayerTryUseItem(this));
    }
}