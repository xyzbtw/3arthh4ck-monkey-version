package me.earth.earthhack.impl.modules.movement.badanchor;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;


public class BadAnchor extends Module {
    protected final Setting<Boolean> pull =
            register(new BooleanSetting("Pull", true));

    protected final Setting<Integer> pitch =
            register(new NumberSetting<>("Pitch", 60, -90, 90));
    public BadAnchor() {
        super("LegacyAnchor", Category.Movement);
        this.listeners.add(new ListenerUpdate(this));
    }


}
