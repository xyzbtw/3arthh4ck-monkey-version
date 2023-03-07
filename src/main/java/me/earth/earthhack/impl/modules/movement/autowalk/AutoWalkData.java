package me.earth.earthhack.impl.modules.movement.autowalk;


import me.earth.earthhack.api.module.data.DefaultData;

public class AutoWalkData extends DefaultData<AutoWalk>
{
    public AutoWalkData(AutoWalk module)
    {
        super(module);
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "+W";
    }
}