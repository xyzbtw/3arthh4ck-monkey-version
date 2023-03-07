package me.earth.earthhack.impl.modules.render.crystalchams;

import me.earth.earthhack.api.module.data.DefaultData;

public class CrystalChamsData extends DefaultData<CrystalChams> {
    public CrystalChamsData(CrystalChams module) {
        super(module);
        register(module.modify, "If you want to modify the crystal bounce speed"+
                "\nAnd spin speed");
        register(module.spinnerspeed, "Crystal spin speed");
        register(module.bouncespeed, "Crystal bounce speed");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Self explanatory";
    }
}
