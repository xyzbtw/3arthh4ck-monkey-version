package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.api.module.data.DefaultData;

public class BlockerData extends DefaultData<forclown> {
    public BlockerData(forclown module) {
        super(module);
        register(module.range, "Range from position mined to closest enemy");
        register(module.fullExtend, "Full extend (diag, face and normal)");
        register(module.extendxyz, "Diag and normal extend when fullextend is off");
    }
}
