package me.earth.earthhack.impl.modules.combat.privatecevbreaker;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class PrivateCevBreaker extends ObbyModule {

    public PrivateCevBreaker() {
        super("PrivateCev", Category.Combat);
        this.listeners.add(new ListenerUpdate(this));
    }
    protected Setting<Boolean> debug = register(new BooleanSetting("Debug", false));
    protected Setting<Integer> pathLength = register(new NumberSetting<>("PathLength", 3, 0, 6));
    protected Setting<Integer> clearDelay = register(new NumberSetting<>("ClearDelay", 2000, 0, 10000));

    protected StopWatch delay = new StopWatch();

    protected Vec3i[] offsetsPlace = new Vec3i[]{
            new Vec3i(1, 2, 0),
            new Vec3i(-1, 2, 0),
            new Vec3i(0, 2, 1),
            new Vec3i(0, 2, -1),
    };

}
