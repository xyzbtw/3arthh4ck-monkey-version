package me.earth.earthhack.impl.modules.combat.forclown2;

import me.earth.earthhack.impl.modules.combat.forclown.forclown;
import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;

public class AntiCevData extends BlockPlacingData<forclown2> {
    public AntiCevData(forclown2 module) {
        super(module);
        register(module.range, "Range from position mined to closest enemy");
        register(module.helping, "Helping blocks");
    }
}
