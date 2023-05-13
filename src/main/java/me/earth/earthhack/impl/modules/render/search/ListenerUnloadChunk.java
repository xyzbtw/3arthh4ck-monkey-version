package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.impl.event.events.render.UnloadChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Iterator;
import java.util.Map;

final class ListenerUnloadChunk extends ModuleListener<Search, UnloadChunkEvent>
{
    public ListenerUnloadChunk(Search module)
    {
        super(module, UnloadChunkEvent.class);
    }

    @Override
    public void invoke(UnloadChunkEvent event) {
        if (module.noUnloaded.getValue() && mc.world != null) {
            ChunkPos unloadedChunk = event.getChunk().getPos();
            module.toRender.entrySet().removeIf(entry -> unloadedChunk.equals(new ChunkPos(entry.getKey())));
        }
    }

}
