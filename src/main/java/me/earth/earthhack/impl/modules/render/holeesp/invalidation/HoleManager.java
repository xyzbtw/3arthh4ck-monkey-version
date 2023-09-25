package me.earth.earthhack.impl.modules.render.holeesp.invalidation;

import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public interface HoleManager
{
    Map<BlockPos, Hole> getHoles();

    List<Hole> get1x1();

    List<Hole> get1x1Unsafe();

    List<Hole> get2x1();

    List<Hole> get2x2();

    default void reset()
    {
        getHoles().clear();
        get1x1().clear();
        get1x1Unsafe().clear();
        get2x1().clear();
        get2x2().clear();
    }
     default boolean isInHole(){
        return this.getHoles().values().stream().anyMatch(h->
                h.contains(mc.player.posX, mc.player.posY, mc.player.posZ));
    }

}
