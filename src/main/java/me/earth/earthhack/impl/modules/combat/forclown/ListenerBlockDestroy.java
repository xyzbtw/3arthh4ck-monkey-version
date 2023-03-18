package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.impl.event.events.misc.BlockDestroyEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockDestroy extends ModuleListener<forclown, BlockDestroyEvent> {
    public ListenerBlockDestroy(forclown module) {
        super(module, BlockDestroyEvent.class);
    }

    @Override
    public void invoke(BlockDestroyEvent event) {
        if(!module.breakEvent.getValue()){
            return;
        }

        if(module.holeCheck.getValue() && !PlayerUtil.isInHoleAll(mc.player)){
            return;
        }

        BlockPos pos = event.getPos();

        //???
        if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

        BlockPos playerPos = mc.player.getPosition();
        BlockPos placePos = null;

        if (module.extend.getValue()) {
            for(EnumFacing face : EnumFacing.values()){
                if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                if (pos.equals(playerPos.offset(face)))
                    placePos = (playerPos.offset(face).offset(face));
            }
        }

        if (module.face.getValue()) {
            for(EnumFacing face : EnumFacing.values()){
                if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                if (pos.equals(playerPos.offset(face)))
                    placePos = (playerPos.offset(face).add(0, 1, 0));
            }
        }

        if (placePos != null) {
            module.placeBlock(placePos);
        }
    }
}
