package me.earth.earthhack.impl.modules.movement.clip;

import me.earth.earthhack.api.module.data.DefaultData;

final class ClipData extends DefaultData<Clip>
{
    public ClipData(Clip module)
    {
        super(module);
        register(module.delay, "Delay before clipping into a corner");
        register(module.disable, "Disable the module after clipping");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Clips into blocks";
    }

}