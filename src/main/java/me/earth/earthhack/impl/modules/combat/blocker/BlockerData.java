package me.earth.earthhack.impl.modules.combat.blocker;


import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

public class BlockerData extends BlockPlacingData<Blocker> {
    public BlockerData(Blocker module) {
        super(module);
        register(module.enemyrange, "Range from position mined to closest enemy");
        register(module.fullExtend, "Full extend (diag, face and normal)");
        register(module.extendxyz, "Diag and normal extend when fullextend is off");
        register(module.antidrew, "Surrounds the block underneath you when it gets touched\n"+
                "only on anim and anim will not work for normal surround with it");
    }
}
