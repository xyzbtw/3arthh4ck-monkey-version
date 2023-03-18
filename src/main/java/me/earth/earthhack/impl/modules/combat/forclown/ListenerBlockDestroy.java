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
        if(!module.destroyEvent.getValue()){
            return;
        }

        if(module.hole.getValue() && !PlayerUtil.isInHoleAll(mc.player)){
            return;
        }

        BlockPos pos = event.getPos();

        //???
        if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR)) return;

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        BlockPos placePos = null;

        if (module.extend.getValue()) {

            for(EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                if (pos.equals(playerPos.offset(face))){
                    placePos = ((playerPos.offset(face)).offset(face));
                }

                if (placePos != null) {
                    module.placeBlock(placePos);
                }
            }
        }

        if (module.face.getValue()) {

            for(EnumFacing face : EnumFacing.values()) {
                if (face == EnumFacing.UP || face == EnumFacing.DOWN) continue;

                if (pos.equals(playerPos.offset(face))){
                    placePos = ((playerPos.offset(face)).offset(face));
                }

                if (placePos != null) {
                    module.placeBlock(placePos);
                }
            }
        }

        if (placePos != null) {
            module.placeBlock(placePos);
        }
    }
}


