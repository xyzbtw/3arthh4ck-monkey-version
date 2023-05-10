package me.earth.earthhack.impl.modules.combat.surround;

import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ListenerRender extends ModuleListener<Surround, Render3DEvent> {

    public ListenerRender(Surround module) {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event) {
        if(!module.render.getValue()) return;
        List<BlockPos> renderPos = new ArrayList<>(module.targets);
        List<BlockPos> confirmed = new ArrayList<>(module.confirmed);
        Iterator<BlockPos> iterator = renderPos.iterator();
        while (iterator.hasNext()) {
            BlockPos thing = iterator.next();
            if (thing == null) return;
            if (!BlockUtil.isAir(thing)) return;

            if (confirmed.contains(thing)) {
                iterator.remove(); // Remove the current element from the list
                continue; // Skip rendering this block, move on to the next
            }

            module.renderPos(thing);
        }
    }
}
