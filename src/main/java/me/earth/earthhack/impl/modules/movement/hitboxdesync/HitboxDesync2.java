package me.earth.earthhack.impl.modules.movement.hitboxdesync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

// 

public class HitboxDesync2 extends Module {

    public HitboxDesync2()
    {
        super("HitboxDesync", Category.Movement);
    }


    @Override
    public void onEnable() {
        BlockPos pos = new BlockPos(mc.player.getPositionVector());
        BlockPos otherPos = validTwoBlockObiXZ(pos);
        if (otherPos != null) ifTwoBlock(pos, otherPos);
        else if ((otherPos = validTwoBlockBedrockXZ(pos)) != null) ifTwoBlock(pos, otherPos);

        System.out.println(mc.player.posX + " " + mc.player.posZ);
        disable();
    }

    // эти ютилы это пиздец + кокас паста но похуй, мио ссосать
    public void ifTwoBlock(BlockPos pos, BlockPos otherPos1) {
        BlockPos otherPos = pos.add(otherPos1.getX(),otherPos1.getY(),otherPos1.getZ());
        float x = 0.3F;
        float z = 0.3F;
        if (pos.getX() > otherPos.getX()) {
            System.out.println(1);
            x = -0.70005F;
        } else if (pos.getX() < otherPos.getX()) {
            System.out.println(2);
            x = 0.70005F;
        }
        if (pos.getZ() > otherPos.getZ()) {
            System.out.println(3);
            z = -0.70005F;
        } else if (pos.getZ() < otherPos.getZ()) {
            System.out.println(4);
            z = 0.70005F;
        }
        mc.player.setPosition(Math.floor( mc.player.posX) + x, mc.player.posY, Math.floor(mc.player.posZ) + z);

    }

    public static BlockPos validTwoBlockObiXZ(BlockPos pos) {
        if (checkObsidianOrBedrock(pos.down(),
                pos.west(),
                pos.south(),
                pos.north(),
                pos.east().down(),
                pos.east(2),
                pos.east().south(),
                pos.east().north())
                && checkMaterialAir(pos, pos.up(), pos.east(), pos.east().up(), pos.east().up(2))) {
            return validTwoBlockBedrockXZ(pos) == null ? new BlockPos(1, 0, 0) : null;
        } else if (
                checkObsidianOrBedrock(pos.down(),
                        pos.west(),
                        pos.east(),
                        pos.north(),
                        pos.south().down(),
                        pos.south(2),
                        pos.south().east(),
                        pos.south().west())
                && checkMaterialAir(pos, pos.up(), pos.south(), pos.south().up(), pos.south().up(2))) {
            return validTwoBlockBedrockXZ(pos) == null ? new BlockPos(0, 0, 1) : null;
        }
        return null;
    }


    public static BlockPos validTwoBlockBedrockXZ(BlockPos pos) {
        if (checkBedrock(pos.down(),
                pos.west(),
                pos.south(),
                pos.north(),
                pos.east().down(),
                pos.east(2),
                pos.east().south(),
                pos.east().north())
                && checkMaterialAir(pos, pos.up(), pos.east(), pos.east().up(), pos.east().up(2))) {
            return new BlockPos(1, 0, 0);
        } else if (checkBedrock(
                pos.down(),
                pos.west(),
                pos.east(),
                pos.north(),
                pos.south().down(),
                pos.south(2),
                pos.south().east(),
                pos.south().west())
                && checkMaterialAir(pos, pos.up(), pos.south(), pos.south().up(), pos.south().up(2))) {
            return new BlockPos(0, 0, 1);
        }
        return null;
    }

    private static boolean checkBedrock(BlockPos... positions) {
        for (BlockPos pos : positions) {
            Block block = mc.world.getBlockState(pos).getBlock();
            if (block != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkObsidianOrBedrock(BlockPos... positions) {
        for (BlockPos pos : positions) {
            Block block = mc.world.getBlockState(pos).getBlock();
            if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkMaterialAir(BlockPos... positions) {
        for (BlockPos pos : positions) {
            Material material = mc.world.getBlockState(pos).getMaterial();
            if (material != Material.AIR) {
                return false;
            }
        }
        return true;
    }
}
