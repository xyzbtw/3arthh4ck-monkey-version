package me.earth.earthhack.impl.modules.combat.forclown;


import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

public class BlockerData extends BlockPlacingData<forclown> {
    public BlockerData(forclown module) {
        super(module);
        register(module.range, "Range from position mined to closest enemy");
        register(module.fullExtend, "Full extend (diag, face and normal)");
        register(module.extendxyz, "Diag and normal extend when fullextend is off");
    }
}
