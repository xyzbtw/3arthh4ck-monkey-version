package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListenerRender extends ModuleListener<Blocker, Render3DEvent> {
    public ListenerRender(Blocker module) {
        super(module, Render3DEvent.class);
    }
    @Override
    public void invoke(Render3DEvent event) {
        ArrayList<BlockPos> renderPos = (ArrayList<BlockPos>) module.scheduledPlacements.clone();
        if(module.render.getValue() && !renderPos.isEmpty()){
                for (BlockPos thing : renderPos) {
                    if(thing == null || mc.world.getBlockState(thing).getBlock() == Blocks.BEDROCK) return;
                    if(!BlockUtil.isAir(thing)) return;
                    module.renderPos(thing);
                }
                renderPos.clear();
        }
    }
}
