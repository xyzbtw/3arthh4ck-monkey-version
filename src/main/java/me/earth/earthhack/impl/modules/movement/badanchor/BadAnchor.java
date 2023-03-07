package me.earth.earthhack.impl.modules.movement.badanchor;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class BadAnchor extends Module {
    protected final Setting<Boolean> pull =
            register(new BooleanSetting("Pull", true));

    protected final Setting<Integer> pitch =
            register(new NumberSetting<>("Pitch", 60, -90, 90));
    private final ArrayList<BlockPos> holes = new ArrayList<BlockPos>();
    public static boolean AnchorING;

    public BadAnchor() {
        super("LegacyAnchor", Category.Movement);
        this.listeners.add(new ListenerUpdate(this));
    }
    @Override
    public void onDisable() {
        AnchorING = false;
    }

}
