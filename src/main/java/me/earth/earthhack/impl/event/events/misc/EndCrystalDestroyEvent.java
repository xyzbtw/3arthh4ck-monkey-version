package me.earth.earthhack.impl.event.events.misc;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.util.math.BlockPos;

public class EndCrystalDestroyEvent extends Event {
    private BlockPos pos;
    public EndCrystalDestroyEvent(BlockPos pos){
        this.pos=pos;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
