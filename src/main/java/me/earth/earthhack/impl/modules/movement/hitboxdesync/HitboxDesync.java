package me.earth.earthhack.impl.modules.movement.hitboxdesync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class HitboxDesync extends Module {

    public HitboxDesync()
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
        if (
                (mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up(2)).getMaterial() == Material.AIR
                        && (mc.world.getBlockState(pos.east().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east().down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east(2)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east(2)).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east().south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east().south()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east().north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east().north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos.east()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.east().up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.east().up(2)).getMaterial() == Material.AIR
        ) {
            return validTwoBlockBedrockXZ(pos) == null ? new BlockPos(1, 0, 0) : null;
        } else if (
                (mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up(2)).getMaterial() == Material.AIR
                        && (mc.world.getBlockState(pos.south().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south().down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south(2)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south(2)).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south().east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south().east()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south().west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south().west()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos.south()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.south().up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.south().up(2)).getMaterial() == Material.AIR
        ) {
            return validTwoBlockBedrockXZ(pos) == null ? new BlockPos(0, 0, 1) : null;
        }
        return null;
    }

    public static BlockPos validTwoBlockBedrockXZ(BlockPos pos) {
        if (
                (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up(2)).getMaterial() == Material.AIR
                        && (mc.world.getBlockState(pos.east().down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east(2)).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east().south()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east().north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos.east()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.east().up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.east().up(2)).getMaterial() == Material.AIR
        ) {
            return new BlockPos(1, 0, 0);
        } else if (
                (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.up(2)).getMaterial() == Material.AIR
                        && (mc.world.getBlockState(pos.south().down()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south(2)).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south().east()).getBlock() == Blocks.BEDROCK)
                        && (mc.world.getBlockState(pos.south().west()).getBlock() == Blocks.BEDROCK)
                        && mc.world.getBlockState(pos.south()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.south().up()).getMaterial() == Material.AIR
                        && mc.world.getBlockState(pos.south().up(2)).getMaterial() == Material.AIR
        ) {
            return new BlockPos(0, 0, 1);
        }
        return null;
    }
}
