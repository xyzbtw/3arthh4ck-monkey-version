package me.earth.earthhack.impl.modules.combat.ccthing;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.util.math.Vec3i;

public class CCthing extends BlockPlacingModule {
    public CCthing() {
        super("CCthing", Category.Combat);
        this.listeners.add(new ListenerUpdate(this));
    }

    protected Setting<Float> range = register( new NumberSetting<>("Range", 6.0f, 0.0f, 8.0f));
    protected Setting<Integer> cycledelay  = register(new NumberSetting<>("CycleDelay", 400, 0, 3000));
    protected StopWatch delayTimer = new StopWatch();

    protected Vec3i[] offsets = new Vec3i[]{
            new Vec3i(1, 1, 1),
            new Vec3i(1, 1, -1),
            new Vec3i(-1, 1, 1),
            new Vec3i(-1, 1, -1)
    };



}
