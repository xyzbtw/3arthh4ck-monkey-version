package me.earth.earthhack.impl.modules.player.mioclip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class MioClip extends Module {

    final Setting<Integer> timeout =
            register(new NumberSetting<>("Timeout", 5, 1, 10));

    public MioClip(){
        super("MioClip", Category.Player);
        this.listeners.add(new ListenerMotion(this));
    }
}
