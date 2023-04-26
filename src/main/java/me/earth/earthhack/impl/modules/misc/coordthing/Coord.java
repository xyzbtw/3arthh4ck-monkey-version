package me.earth.earthhack.impl.modules.misc.coordthing;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

public class Coord extends Module {

    public Coord() {
        super("CoordThing", Category.Misc);
        this.listeners.add(new Listener(this));
    }
    protected final Setting<Integer> startx =
            register(new NumberSetting<>("StartX", 1000, -30000000,30000000 ));
    protected final Setting<Integer> starty =
            register(new NumberSetting<>("StartY", 50, 0,256 ));
    protected final Setting<Integer> startz =
            register(new NumberSetting<>("StartZ", 1000, -30000000,30000000 ));
    protected final Setting<Integer> endx =
            register(new NumberSetting<>("EndX", 1000, -30000000,30000000 ));
    protected final Setting<Integer> endy =
            register(new NumberSetting<>("EndY", 1000, -30000000,30000000 ));
    protected final Setting<Integer> endz =
            register(new NumberSetting<>("EndZ", 1000, -30000000,30000000 ));
    protected final Setting<Integer> chunkskip =
            register(new NumberSetting<>("ChunkSkip", 1, 0,100 ));
    protected final Setting<Boolean> repeat =
            register(new BooleanSetting("Repeat", true));
}
